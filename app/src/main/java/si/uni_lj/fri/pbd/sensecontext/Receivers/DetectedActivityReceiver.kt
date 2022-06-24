package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity.ON_FOOT
import com.google.android.gms.location.DetectedActivity.WALKING


class DetectedActivityReceiver : BroadcastReceiver() {



    companion object {

        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DetectedActivityReceiver::class.java)
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast
        //Toast.makeText(context, "onReceive", Toast.LENGTH_LONG).show()
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            Toast.makeText(context, result?.mostProbableActivity.toString(), Toast.LENGTH_LONG).show()
            if (result?.mostProbableActivity?.type == WALKING || result?.mostProbableActivity?.type == ON_FOOT) {
                val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                val timeStamp = pref.getLong("timestamp", 0)
                if (System.currentTimeMillis() - timeStamp >= 20 * 1000) {
                    Toast.makeText(context, "You are walking for more than 20 seconds!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}