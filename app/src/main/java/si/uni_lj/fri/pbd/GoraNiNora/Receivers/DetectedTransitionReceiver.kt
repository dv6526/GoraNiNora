package si.uni_lj.fri.pbd.GoraNiNora.Receivers

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
import si.uni_lj.fri.pbd.GoraNiNora.MainActivity.Companion.CHANNEL_ID_ACTIVITY_TRANSITION
import si.uni_lj.fri.pbd.GoraNiNora.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.GoraNiNora.R
import si.uni_lj.fri.pbd.GoraNiNora.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.GoraNiNora.Services.LocationUpdatesService.Companion.ACTION_START_RECEIVER
import si.uni_lj.fri.pbd.GoraNiNora.Services.LocationUpdatesService.Companion.ACTION_STOP
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DetectedTransitionReceiver : BroadcastReceiver() {


    private val processingScope = kotlinx.coroutines.GlobalScope

    companion object {
        var activityState:String? = null
        const val CHANNEL_ID="si.uni_lj.fri.pbd.sensecontext.NEWS"
        const val NOTIFICATION_ID = 18
        // in seconds
        var waitBeforeLocationUpdates = 60L
        var locationUpdatesInterval = 60L
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
                    // start Location Updates Service when WALKING enter
                    if (event.activityType == DetectedActivity.WALKING && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        activityState = "WALKING"


                        // wait for t time and if still in WALKING state, then start activity sampling service
                        processingScope.launch { waitTTime(TimeUnit.SECONDS.toMillis(waitBeforeLocationUpdates), context) }


                    // stop LocationUpdatesService when WALKING exit
                    // sometimes only recognized STILL ENTER and not both
                    } else if (event.activityType == DetectedActivity.WALKING && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT || event.activityType == DetectedActivity.STILL && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        activityState = "NOT WALKING"
                        if (LocationUpdatesService.IS_RUNNING) {
                            //Toast.makeText(context, "Stopped Location Updates Service from transition receiver", Toast.LENGTH_LONG).show()
                            val i = Intent(context, LocationUpdatesService::class.java)
                            i.action = ACTION_STOP
                            context.startService(i)
                        }
                    }
                }
                Log.d(TAG, "Detected activity transition " + output.toString())
                //showNotification(output.toString(), context)
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
            i.action = ACTION_START_RECEIVER
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




    private fun showNotification(detectedActivity: String, context: Context) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ACTIVITY_TRANSITION).setSmallIcon(
            R.drawable.ic_launcher_foreground).setContentTitle("Detected activity!").setContentText(detectedActivity)
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