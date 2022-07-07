package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class LocationUpdatesReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "si.uni_lj.fri.pbd.sensecontext.Receivers.LocationUpdatesBroadcastReceiver." +
                    "PROCESS_UPDATES"
        var prevDate: Date? = null
        fun getLocationPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, LocationUpdatesReceiver::class.java)
            intent.action = LocationUpdatesReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

    }
    val processingScope = CoroutineScope(Dispatchers.IO)
    val timeBetweenUpdates = LocationUpdatesService.locationUpdatesInterval // if seconds elapsed since last location, we call API


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_PROCESS_UPDATES) {
            // Checks for location availability changes.

            LocationResult.extractResult(intent)?.let { locationResult ->
                //only take first location
                val location = locationResult.locations[0]
                val curDate = Date(location.time)
                val millis1: Long = curDate.time
                val millis2: Long? = prevDate?.time
                var timeDiff = TimeUnit.SECONDS.toMillis(timeBetweenUpdates)
                if (millis2 != null)
                    timeDiff = millis1 - millis2
                if (timeDiff >= TimeUnit.SECONDS.toMillis(timeBetweenUpdates)) {
                    //Log.d(TAG, "Prev date $prevDate")
                    //Log.d(TAG, "Cur date $curDate")
                    Toast.makeText(context, location.latitude.toString() + " " + location.longitude.toString(), Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Location update " + location.latitude.toString() + " " + location.longitude.toString())
                    if (isInsideMountainsFence(location.latitude, location.longitude)) {
                        sendJobAPI(location.latitude, location.longitude, context)
                    }
                    prevDate = curDate
                }

            }
        }
    }

    fun isInsideMountainsFence(lat: Double, lon: Double): Boolean {
        val pts: MutableList<LatLng> = ArrayList()
        /* Home location
        pts.add(LatLng(46.333164269796875, 14.328845691769393))
        pts.add(LatLng(46.32889716839761, 14.342621517269881))
        pts.add(LatLng(46.31736840370528, 14.334853839962753))
        pts.add(LatLng(46.321814230575065, 14.321464252560409))
         */

        pts.add(LatLng(46.32455349101422,14.339970970425394 ))
        pts.add(LatLng(46.319752222784885,14.341129684719828 ))
        pts.add(LatLng(46.3209377602873,14.337138557705668 ))
        val loc = LatLng(lat, lon)
        return PolyUtil.containsLocation(loc, pts, true);
    }

    fun sendJobAPI(lat: Double, lon: Double, context: Context) {
        val client = OkHttpClient()

        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("elevation.arcgis.com")
            .addPathSegments("arcgis/rest/services/Tools/Elevation/GPServer/SummarizeElevation/submitJob")
            .addQueryParameter("f", "json")
            .addQueryParameter("token", "AAPKdd71a846aa114aa99a41c42d20b3b56axTyOGS9vd-DdXMptK5a0vWNL56_uajIO5SEZZ_oMtaQXFK5gsmw8NgjNVZ7lyfXP")
            .addQueryParameter("InputFeatures", "{     \"geometryType\": \"esriGeometryPoint\", \"spatialReference\": {\"wkid\": 4326     }, \"features\": [  {  \"geometry\": { \"x\": " + lon.toString() + ", \"y\": " + lat.toString() + "  }}]}")
            //.addQueryParameter("InputFeatures", "{     \"geometryType\": \"esriGeometryPoint\",     \"spatialReference\": {         \"wkid\": 4326     },     \"features\": [         {             \"geometry\": {                 \"x\": 14.33030882390553,                 \"y\": 46.327262784885846             }         }     ] }")
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
                    processingScope.launch { getJobStatus(job_id, lat, lon, context) }

                }
            }
        })
    }


    suspend fun getJobStatus(job_id: String, lat: Double, lon: Double, context: Context) {
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

        getResults(job_id, lat, lon, context)
    }

    fun getResults(job_id: String, lat: Double, lon: Double, context: Context) {
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
                    val aspect = cont.read<Double>("$.value.features[0].attributes.MeanAspect")
                    Log.d(TAG, "ArcgisAPI update " + elevation.toString() + "m elevation, " + slope.toString() + " degree slope " + aspect.toString() + " degree aspect")
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            context,
                            elevation.toString() + "m elevation, " + slope.toString() + " degree slope",
                            Toast.LENGTH_LONG
                        ).show()
                    })

                }
            }
        })
    }

}