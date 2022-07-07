package si.uni_lj.fri.pbd.sensecontext.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import si.uni_lj.fri.pbd.sensecontext.ForegroundService
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.Receivers.LocationUpdatesReceiver
import java.util.concurrent.TimeUnit

class LocationUpdatesService : Service() {

    companion object {
        const val STOP_BACKGROUND_SENSING = "si.uni_lj.fri.pbd.sensecontext.Services.ActivitySamplingService.stop_background_service"
        const val ACTION_STOP = "action_stop"
        const val ACTION_START = "action_start"
        var IS_RUNNING: Boolean = false // if service is running
        const val NOTIFICATION_ID = 12
        private const val CHANNEL_ID: String = "Sensor Data"
        var locationUpdatesInterval: Long = 0
    }

    lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent.action.equals(ACTION_STOP)) {
            stopService()
        } else if (intent.action.equals(ACTION_START)) {
            locationUpdatesInterval = intent?.getSerializableExtra("locationUpdatesInterval") as Long
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates(this)
        IS_RUNNING = true
        Toast.makeText(this, "Started Location GPS Updates", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Stopped Location GPS Updates", Toast.LENGTH_LONG).show()
        removeLocationUpdates(this)
        IS_RUNNING = false
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(ForegroundService.CHANNEL_ID, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun createNotification(): Notification {

        val builder = NotificationCompat.Builder(this, ForegroundService.CHANNEL_ID)
            .setContentTitle("Sampling API running")
            .setContentText("running in foreground")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setChannelId(ForegroundService.CHANNEL_ID)

        return builder.build()
    }

    private fun stopService() {
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
        ForegroundService.IS_RUNNING = false
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


}