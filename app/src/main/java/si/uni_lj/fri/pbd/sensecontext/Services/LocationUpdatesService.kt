package si.uni_lj.fri.pbd.sensecontext.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.CHANNEL_ID_WARNING
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.Receivers.LocationUpdatesReceiver
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import java.util.*
import java.util.concurrent.TimeUnit

class LocationUpdatesService : Service() {

    companion object {
        const val STOP_BACKGROUND_SENSING = "si.uni_lj.fri.pbd.sensecontext.Services.ActivitySamplingService.stop_background_service"
        const val ACTION_STOP = "action_stop"
        const val ACTION_STOP_FOR_1H = "action_stop_hour"
        const val ACTION_START_BUTTON = "action_start_button"
        const val ACTION_START_RECEIVER = "action_start_receiver"

        var IS_RUNNING: Boolean = false // if service is running
        const val NOTIFICATION_ID = 12
        var locationUpdatesInterval: Long = 0
        var user_is_hiking = false // when LocationUpdatesReceiver determines user is hiking based on GPS coordinates, user_is_hiking is set to TRUE
        var av_area_id: Int? = null
    }

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var repository: Repository



    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bm = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
        // Get the battery percentage and store it in a INT variable
        val batLevel = getBatteryPercentage(this)
        val power_saving = sp.getBoolean("power_saving", false)
        if (intent.action.equals(ACTION_STOP) || (power_saving && batLevel < 20)) {
            stopService()
        } else if (intent.action.equals(ACTION_START_BUTTON)) {
            locationUpdatesInterval = intent?.getSerializableExtra("locationUpdatesInterval") as Long
        } else if (intent.action.equals(ACTION_START_RECEIVER)) {
            val time1 = Date(sp.getLong("last_stopped", 0)).time
            val time2 = Date().time
            val diff = time2 - time1
            if ( TimeUnit.MILLISECONDS.toMinutes(diff) < 1) {
                stopService()
            } else {
                locationUpdatesInterval = intent?.getSerializableExtra("locationUpdatesInterval") as Long
            }
        } else if (intent.action.equals(ACTION_STOP_FOR_1H)) {

            val time2 = Date().time
            with (sp.edit()) {
                putLong("last_stopped", time2)
                apply()
            }
            stopService()

        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val db = ApplicationDatabase.getDatabase(applicationContext)
        val dao = db.dao()
        repository = Repository(dao)
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates(this)
        IS_RUNNING = true
        Toast.makeText(this, "Started Location GPS Updates", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Stopped Location GPS Updates", Toast.LENGTH_LONG).show()
        removeLocationUpdates(this)
        user_is_hiking = false
        repository.user_hiking.postValue(false)
        IS_RUNNING = false
    }



    private fun createNotification(): Notification {
        val i = Intent(applicationContext, LocationUpdatesService::class.java)
        i.action = LocationUpdatesService.ACTION_STOP_FOR_1H
        val pendingIntent = PendingIntent.getService(applicationContext, 0, i, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_WARNING)
            .setContentTitle("GoraNiNora uporablja lokacijo GPS")
            .setContentText("Ugotavlja, če se nahajate v hribih.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(R.drawable.ic_opozorila, "IZKLOPI LOKACIJO ZA 1h",pendingIntent)

        return builder.build()
    }

    private fun stopService() {
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
        IS_RUNNING = false
    }

    fun startLocationUpdates(context: Context) {
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(locationUpdatesInterval)
            fastestInterval = TimeUnit.SECONDS.toMillis(locationUpdatesInterval)
            maxWaitTime = TimeUnit.MINUTES.toMillis(locationUpdatesInterval)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                LocationUpdatesReceiver.getLocationPendingIntent(context)
            )
        } catch (error: SecurityException) {

        }

    }

    private fun removeLocationUpdates(context: Context) {

        // remove background location updates
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.removeLocationUpdates(LocationUpdatesReceiver.getLocationPendingIntent(context))

    }

    fun getBatteryPercentage(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= 21) {
            val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        } else {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = registerReceiver(null, ifilter)

            val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            level / scale.toFloat().toInt()

        }
    }


}