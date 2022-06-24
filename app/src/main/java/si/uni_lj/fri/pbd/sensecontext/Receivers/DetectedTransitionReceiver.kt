package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.Services.ActivitySamplingService
import java.text.SimpleDateFormat
import java.util.*

class DetectedTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null) {

                val output = StringBuilder()

                for (event in result.transitionEvents) {
                    val info: String = "Transition: ${fromActivityType(event.activityType)}" +
                            " (${fromTransitionType(event.transitionType)}) " +
                            SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                    output.append(info)
                    // when user is walking request Activity Sampling API updates
                    if (event.activityType == DetectedActivity.WALKING && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        val sharedPref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putLong("timestamp", System.currentTimeMillis())
                            apply()
                        }
                        context.startService(Intent(context, ActivitySamplingService::class.java).putExtra("millis", 100000L))
                    } else if (event.activityType == DetectedActivity.WALKING && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                        context.stopService(Intent(context, ActivitySamplingService::class.java))
                    }
                }
                print(output.toString())
                showNotification(output.toString(), context)
            }
        }
    }
    private fun handleDetectedActivities(detectedActivities: List<DetectedActivity>,
                                         context: Context) {

    }

    companion object {

        const val CHANNEL_ID="si.uni_lj.fri.pbd.sensecontext.NEWS"
        const val NOTIFICATION_ID = 18
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.resources.getString(R.string.channel_name)
            val descriptionText = context.resources.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(detectedActivity: String, context: Context) {
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Detected activity!").setContentText(detectedActivity)
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun fromActivityType(type: Int): String = when (type) {
        DetectedActivity.STILL -> "STILL"
        DetectedActivity.WALKING -> "WALKING"
        DetectedActivity.RUNNING -> "RUNNING"
        else -> "NOT SUPPORTED"
    }

    private fun fromTransitionType(transition: Int): String {
        return when(transition) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "ENTER"
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "EXIT"
            else -> "UNKNOWN"
        }
    }




}