package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.app.PendingIntent
import android.content.*
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity.ON_FOOT
import com.google.android.gms.location.DetectedActivity.WALKING
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest.*
import si.uni_lj.fri.pbd.sensecontext.Services.ActivitySamplingService
import java.util.concurrent.TimeUnit


// only registered when user is in WALKING state
class DetectedActivityReceiver : BroadcastReceiver() {

    lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {


        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DetectedActivityReceiver::class.java)
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        fun getLocationPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, LocationUpdatesBroadcastReceiver::class.java)
            intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        var startedLocationUpdates: Boolean = false

    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast
        //Toast.makeText(context, "onReceive", Toast.LENGTH_LONG).show()
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            // Toast.makeText(context, result?.mostProbableActivity.toString(), Toast.LENGTH_LONG).show()
            if (result?.mostProbableActivity?.type == WALKING || result?.mostProbableActivity?.type == ON_FOOT) {
                val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                val timeStamp = pref.getLong("timestamp", 0)
                // when user is WALKING for more than t seconds, start Background Location Updates
                if (System.currentTimeMillis() - timeStamp >= TimeUnit.SECONDS.toMillis(10)) {
                    if (!startedLocationUpdates) {
                        Toast.makeText(context, "You are walking for more than 20 seconds!", Toast.LENGTH_LONG).show()
                        startLocationUpdates(context)
                        startedLocationUpdates = true
                    }



                }
            }
        }
    }

    fun startLocationUpdates(context: Context) {
        val locationRequest = create().apply {
            interval = TimeUnit.SECONDS.toMillis(30)
            fastestInterval = TimeUnit.SECONDS.toMillis(15)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = PRIORITY_HIGH_ACCURACY
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                getLocationPendingIntent(context)
            )
        } catch (error: SecurityException) {

        }

    }



}
