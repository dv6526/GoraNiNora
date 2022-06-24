package si.uni_lj.fri.pbd.sensecontext.Services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionClient
import si.uni_lj.fri.pbd.sensecontext.Receivers.DetectedActivityReceiver

class ActivitySamplingService : Service() {

    private var millis: Long = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        millis = intent?.getSerializableExtra("millis") as Long
        Log.d("tag", millis.toString())
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Started Activity Sampling Service", Toast.LENGTH_LONG).show()
        requestActivityUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Stopped Activity Sampling Service", Toast.LENGTH_LONG).show()
        removeActivityUpdates()
    }

    private fun requestActivityUpdates() {
        val task = ActivityRecognitionClient(this).requestActivityUpdates(millis, DetectedActivityReceiver.getPendingIntent(this))
        task.run {
            addOnSuccessListener {
                Log.d("ActivitySamplingService", "Sampling API started!")
            }
            addOnFailureListener {
                Log.d("ActivitySamplingService", "Sampling API cannot be started!")
            }
        }
    }

    private fun removeActivityUpdates() {
        val task = ActivityRecognitionClient(this).removeActivityUpdates(DetectedActivityReceiver.getPendingIntent(this))
        task.run {
            addOnSuccessListener { Log.d("ActivitySamplingService", "Sampling API stopped!") }
            addOnFailureListener { Log.d("ActivitySamplingService", "Sampling API cannot be stopped!") }
        }
    }
}