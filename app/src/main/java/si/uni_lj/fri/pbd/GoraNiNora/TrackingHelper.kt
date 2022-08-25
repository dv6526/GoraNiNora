package si.uni_lj.fri.pbd.GoraNiNora

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import si.uni_lj.fri.pbd.GoraNiNora.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.GoraNiNora.Receivers.DetectedTransitionReceiver
import java.lang.Exception


class TrackingHelper {

    companion object {

        lateinit var pendingIntent: PendingIntent
        val processingScope = CoroutineScope(Dispatchers.IO)


        private fun getTransitions(): List<ActivityTransition> {
            val transitions = mutableListOf<ActivityTransition>()
            transitions +=
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.WALKING)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()

            transitions +=
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.WALKING)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()

            transitions +=
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.STILL)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()


            transitions +=
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.STILL)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()

            return transitions
        }

        fun requestActivityTransitionUpdates(context: Context) {
            // interested
            val request = ActivityTransitionRequest(getTransitions())
            val intent = Intent(context, DetectedTransitionReceiver::class.java)
            // android 12 crashes if flags is not included in pending intent
            val flags = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
                else -> FLAG_UPDATE_CURRENT
            }
            // pending intent to receive callbacks
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            val task =
                ActivityRecognitionClient(context).requestActivityTransitionUpdates(
                    request,
                    pendingIntent
                )

            task.run {
                addOnSuccessListener { Log.d(TAG, "Transitions API registered") }
                addOnFailureListener { e: Exception ->
                    Log.d(TAG, "Transition API could not be registered!")
                }
            }
        }

        fun removeActivityTransitionUpdates(activity: Activity) {
            val task =
                ActivityRecognitionClient(activity).removeActivityTransitionUpdates(pendingIntent)
            task.run {
                addOnSuccessListener { Log.d(TAG, "Transitions API removed succesfully") }
                addOnFailureListener { e: Exception ->
                    Log.d(TAG, "Transition API could not be removed!")
                }
            }
        }
    }
}


