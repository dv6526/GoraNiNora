package si.uni_lj.fri.pbd.sensecontext

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.sensecontext.Receivers.DetectedTransitionReceiver
import java.lang.Exception

lateinit var pendingIntent: PendingIntent

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

fun MainActivity.requestActivityTransitionUpdates() {
    // interested
    val request = ActivityTransitionRequest(getTransitions())
    val intent = Intent(this, DetectedTransitionReceiver::class.java)
    // android 12 crashes if flags is not included in pending intent
    val flags = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        else -> FLAG_UPDATE_CURRENT
    }
    // pending intent to receive callbacks
    pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

    val task = ActivityRecognitionClient(this).requestActivityTransitionUpdates(request, pendingIntent)

    task.run {
        addOnSuccessListener { Log.d(TAG, "Transitions API registered") }
        addOnFailureListener { e:Exception ->
            Log.d(TAG, "Transition API could not be registered!")
        }
    }
}

fun MainActivity.removeActivityTransitionUpdates() {
    val task = ActivityRecognitionClient(this).removeActivityTransitionUpdates(pendingIntent)
    task.run {
        addOnSuccessListener { Log.d(TAG, "Transitions API removed succesfully") }
        addOnFailureListener { e:Exception ->
            Log.d(TAG, "Transition API could not be removed!")
        }
    }
}