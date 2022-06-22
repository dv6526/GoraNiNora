package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.ActivityTransitionResult
import si.uni_lj.fri.pbd.sensecontext.pendingIntent
import si.uni_lj.fri.pbd.sensecontext.requestActivityTransitionUpdates

class DetectedTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context, "Transitions intent $intent", Toast.LENGTH_SHORT).show()

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            Toast.makeText(context, intent?.getStringExtra("transitions"), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DetectedTransitionReceiver::class.java)
            // pending intent to receive callbacks
             return PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }


}