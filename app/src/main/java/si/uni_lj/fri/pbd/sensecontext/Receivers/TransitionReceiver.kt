package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.ActivityTransitionResult

class TransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context, "Transitions intent $intent", Toast.LENGTH_SHORT).show()

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            Toast.makeText(context, intent?.getStringExtra("transitions_exit"), Toast.LENGTH_SHORT).show()
        }
    }


}