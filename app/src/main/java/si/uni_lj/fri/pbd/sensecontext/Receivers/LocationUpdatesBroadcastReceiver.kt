package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import si.uni_lj.fri.pbd.sensecontext.MainActivity
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.sensecontext.processingScope
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "si.uni_lj.fri.pbd.sensecontext.Receivers.LocationUpdatesBroadcastReceiver." +
                    "PROCESS_UPDATES"
    }
    val processingScope = CoroutineScope(Dispatchers.IO)
    lateinit var context1: Context



    override fun onReceive(context: Context, intent: Intent) {
        context1 = context
        if (intent.action == ACTION_PROCESS_UPDATES) {
            // Checks for location availability changes.
            LocationAvailability.extractLocationAvailability(intent)?.let { locationAvailability ->
                if (!locationAvailability.isLocationAvailable) {
                    Log.d(TAG, "Location services are no longer available!")
                }
            }

            LocationResult.extractResult(intent)?.let { locationResult ->
                val locations = locationResult.locations.map { location ->
                    sendJobAPI(location.latitude, location.longitude, context)
                    Toast.makeText(context, location.latitude.toString() + " " + location.longitude.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    fun sendJobAPI(lat: Double, lon: Double, context: Context) {
        val client = OkHttpClient()

        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("elevation.arcgis.com")
            .addPathSegments("arcgis/rest/services/Tools/Elevation/GPServer/SummarizeElevation/submitJob")
            .addQueryParameter("f", "json")
            .addQueryParameter("token", "AAPKdd71a846aa114aa99a41c42d20b3b56axTyOGS9vd-DdXMptK5a0vWNL56_uajIO5SEZZ_oMtaQXFK5gsmw8NgjNVZ7lyfXP")
            //.addQueryParameter("InputFeatures", "{     \"geometryType\": \"esriGeometryPoint\", \"spatialReference\": {\"wkid\": 4326     }, \"features\": [  {  \"geometry\": { \"x\": " + lon.toString() + ", \"y\": " + lat.toString() + "  }}]}")
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
                    val cont = JsonPath.parse(response.body!!.string())
                    val job_id = cont.read<String>("jobId")
                    processingScope.launch { getJobStatus(job_id, context) }

                }
            }
        })
    }


    suspend fun getJobStatus(job_id: String, context: Context) {
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

        getResults(job_id, context)
    }

    fun getResults(job_id: String, context: Context) {
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
                    val cont = JsonPath.parse(response.body!!.string())
                    val slope = cont.read<Double>("$.value.features[0].attributes.MeanSlope")
                    val elevation = cont.read<Double>("$.value.features[0].attributes.MeanElevation")
                    //Toast.makeText(context, elevation.toString() + "m elevation, " + slope.toString() + " degree slope", Toast.LENGTH_LONG).show()

                }
            }
        })
    }

}