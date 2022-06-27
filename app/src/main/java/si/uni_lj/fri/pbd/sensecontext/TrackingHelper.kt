package si.uni_lj.fri.pbd.sensecontext

import android.app.DownloadManager
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
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.sensecontext.Receivers.DetectedTransitionReceiver
import java.io.IOException
import java.lang.Exception


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


fun MainActivity.sendJobAPI() {
    val client = OkHttpClient()

    val urlBuilder = HttpUrl.Builder()
        .scheme("https")
        .host("elevation.arcgis.com")
        .addPathSegments("arcgis/rest/services/Tools/Elevation/GPServer/SummarizeElevation/submitJob")
        .addQueryParameter("f", "json")
        .addQueryParameter("token", "AAPKdd71a846aa114aa99a41c42d20b3b56axTyOGS9vd-DdXMptK5a0vWNL56_uajIO5SEZZ_oMtaQXFK5gsmw8NgjNVZ7lyfXP")
        .addQueryParameter("InputFeatures", "{     \"geometryType\": \"esriGeometryPoint\",     \"spatialReference\": {         \"wkid\": 4326     },     \"features\": [         {             \"geometry\": {                 \"x\": 14.33030882390553,                 \"y\": 46.327262784885846             }         }     ] }")
        .addQueryParameter("IncludeSlopeAspect", "true")
        .addQueryParameter("DEMResolution", "FINEST")
        .build()
    val request = Request.Builder()
        .url(urlBuilder)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val context = JsonPath.parse(response.body!!.string())
                val job_id = context.read<String>("jobId")
                processingScope.launch { getJobStatus(job_id) }

            }
        }
    })
}


suspend fun MainActivity.getJobStatus(job_id: String) {
    var jobCompleted: Boolean = false
    val client = OkHttpClient()
    val urlBuilder = HttpUrl.Builder()
        .scheme("https")
        .host("elevation.arcgis.com")
        .addPathSegments("arcgis/rest/services/Tools/Elevation/GPServer/SummarizeElevation/jobs/"+job_id)
        .addQueryParameter("f", "json")
        .addQueryParameter("token", "AAPKdd71a846aa114aa99a41c42d20b3b56axTyOGS9vd-DdXMptK5a0vWNL56_uajIO5SEZZ_oMtaQXFK5gsmw8NgjNVZ7lyfXP")
        .build()


    while (!jobCompleted) {
        Thread.sleep(1000)
        val request = Request.Builder()
            .url(urlBuilder)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            //var sg1t = (response.body().string())
            val context = JsonPath.parse(response.body!!.string())
            val job_status = context.read<String>("jobStatus")
            if (job_status == "esriJobSucceeded") jobCompleted = true
        }
    }

    getResults(job_id)
}

fun MainActivity.getResults(job_id: String) {
    val client = OkHttpClient()
    val urlBuilder = HttpUrl.Builder()
        .scheme("https")
        .host("elevation.arcgis.com")
        .addPathSegments("arcgis/rest/services/Tools/Elevation/GPServer/SummarizeElevation/jobs/"+job_id+"/results/OutputSummary")
        .addQueryParameter("f", "json")
        .addQueryParameter("token", "AAPKdd71a846aa114aa99a41c42d20b3b56axTyOGS9vd-DdXMptK5a0vWNL56_uajIO5SEZZ_oMtaQXFK5gsmw8NgjNVZ7lyfXP")
        .addQueryParameter("returnType", "data")
        .build()

    val request = Request.Builder()
        .url(urlBuilder)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val context = JsonPath.parse(response.body!!.string())
                val slope = context.read<Double>("$.value.features[0].attributes.MeanSlope")
                val elevation = context.read<Double>("$.value.features[0].attributes.MeanElevation")

            }
        }
    })
}
