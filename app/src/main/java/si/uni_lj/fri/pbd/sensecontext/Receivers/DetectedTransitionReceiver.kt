package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService.Companion.ACTION_START
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService.Companion.ACTION_STOP
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DetectedTransitionReceiver : BroadcastReceiver() {


    private val processingScope = kotlinx.coroutines.GlobalScope

    companion object {
        var activityState:String? = null
        const val CHANNEL_ID="si.uni_lj.fri.pbd.sensecontext.NEWS"
        const val NOTIFICATION_ID = 18
        var waitBeforeLocationUpdates = 15L
        var locationUpdatesInterval = 15L
    }


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
                    // start ActivitySampling service when WALKING enter
                    if (event.activityType == DetectedActivity.WALKING && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        //activityState = "WALKING" does not work because everytime onReceive is called new instance is created
                        activityState = "WALKING"

                        // wait for t time and if still in WALKING state, then start activity sampling service
                        processingScope.launch { waitTTime(TimeUnit.SECONDS.toMillis(waitBeforeLocationUpdates), context) }


                    // stop LocationUpdatesService when WALKING exit
                    // sometimes only recognized STILL ENTER and not both
                    } else if (event.activityType == DetectedActivity.WALKING && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT || event.activityType == DetectedActivity.STILL && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        activityState = "NOT WALKING"
                        if (LocationUpdatesService.IS_RUNNING) {
                            Toast.makeText(context, "Stopped Location Updates Service from transition receiver", Toast.LENGTH_LONG).show()
                            val i = Intent(context, LocationUpdatesService::class.java)
                            i.action = ACTION_STOP
                            context.startService(i)
                        }
                    }
                }
                Log.d(TAG, "Detected activity transition " + output.toString())
                showNotification(output.toString(), context)
            }
        }
    }
    private fun waitTTime(waitTime: Long, context: Context) {
        Thread.sleep(waitTime)
        //val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        //val activityState = pref.getString("activityState", null)
        if (activityState == "WALKING") {
            val i = Intent(context, LocationUpdatesService::class.java)
            i.putExtra("locationUpdatesInterval", locationUpdatesInterval)
            i.action = ACTION_START
            if (!LocationUpdatesService.IS_RUNNING) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i)
                } else {
                    context.startService(i)
                }
            }
        }
        Log.d(TAG, "After $waitBeforeLocationUpdates. seconds. $activityState")

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