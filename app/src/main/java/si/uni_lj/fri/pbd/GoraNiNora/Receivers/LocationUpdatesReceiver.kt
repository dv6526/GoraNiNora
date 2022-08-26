package si.uni_lj.fri.pbd.GoraNiNora.Receivers

import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import si.uni_lj.fri.pbd.GoraNiNora.JsonObjects.Areas.Areas
import si.uni_lj.fri.pbd.GoraNiNora.MainActivity.Companion.CHANNEL_ID_WARNING
import si.uni_lj.fri.pbd.GoraNiNora.MainActivity.Companion.TAG
import si.uni_lj.fri.pbd.GoraNiNora.MatchRules
import si.uni_lj.fri.pbd.GoraNiNora.R
import si.uni_lj.fri.pbd.GoraNiNora.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.GoraNiNora.data.ApplicationDatabase
import si.uni_lj.fri.pbd.GoraNiNora.data.Location
import si.uni_lj.fri.pbd.GoraNiNora.data.Repository
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
                //if battery percantage drops below 20 and battery saving is enabled, stop location updates service
                val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val power_saving = sp.getBoolean("power_saving", false)
                if (getBatteryPercentage(context) < 20 && power_saving) {
                    val i = Intent(context, LocationUpdatesService::class.java)
                    i.action = LocationUpdatesService.ACTION_STOP
                    context.startService(i)
                }

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
                    //Toast.makeText(context, location.latitude.toString() + " " + location.longitude.toString(), Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Location update " + location.latitude.toString() + " " + location.longitude.toString())
                    var internetAvailable = checkForInternet(context)
                    var isInsideArea = isInsideMountainsFence(location.latitude, location.longitude, context)
                    if (isInsideArea && internetAvailable) {
                    //if (isInsideMountainsFence(location.latitude, location.longitude, context)) {
                        sendJobAPI(location.latitude, location.longitude, context)
                        //sendJobAPI(46.345664175687425, 13.627491787033, context) //this coordinate is inside av_area_id 2
                    } else if(isInsideArea && !internetAvailable && location.hasAltitude()) {
                        var elevation = location.altitude
                        saveLocationDatabase(location.latitude, location.latitude, elevation, null, null, context)
                        if (!LocationUpdatesService.user_is_hiking)
                            detectUserIsHiking(context)
                        else {
                            MatchRules.matchRules(context, true)
                        }
                    } /* else if (!isInsideArea) {
                        //stop location updates
                        val i = Intent(context, LocationUpdatesService::class.java)
                        i.action = LocationUpdatesService.ACTION_STOP
                        context.startService(i)
                    }
                    */
                    prevDate = curDate
                }

            }
        }
    }

    fun isInsideMountainsFence(lat: Double, lon: Double, context: Context): Boolean {

        val loc = LatLng(lat, lon)
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("areas.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            Log.d(TAG, e.toString())
        }

        val areas = Gson().fromJson(jsonString, Areas::class.java)
        var isInside = false
        for (areasItem in areas) {
            var pts: MutableList<LatLng> = ArrayList()
            for (point in areasItem.geometry) {
                pts.add(LatLng(point[1].toDouble(), point[0].toDouble()))
            }
            if (PolyUtil.containsLocation(loc, pts, true) && areasItem.av_area_id != 5) { // 5 - ostala območja
                isInside = true
                LocationUpdatesService.av_area_id = areasItem.av_area_id
            }
        }


        return isInside
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
                    /*
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            context,
                            elevation.toString() + "m elevation, " + slope.toString() + " degree slope",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                     */

                    //save location to database
                    saveLocationDatabase(lat, lon, elevation, slope, aspect, context)
                    if (!LocationUpdatesService.user_is_hiking)
                        detectUserIsHiking(context)
                    else {
                        MatchRules.matchRules(context, true)
                    }

                }
            }
        })
    }

    fun saveLocationDatabase(lat: Double, lon: Double, elevation: Double, slope: Double?, aspect: Double?, context: Context) {
        val db = ApplicationDatabase.getDatabase(context)
        val dao = db.dao()
        val repository = Repository(dao)
        val date = Calendar.getInstance().time
        val location = Location(0, date, lon, lat, slope, elevation, aspect)
        processingScope.launch  {repository.addLocation(location)  }

    }

    fun detectUserIsHiking(context: Context) {
        val db = ApplicationDatabase.getDatabase(context)
        val weatherDao = db.dao()
        val repository = Repository(weatherDao)
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, -30)
        val backTime = cal.time
        processingScope.launch  {
            val loc: List<Location> = repository.fetchLocationsBetweenDate(backTime, Calendar.getInstance().time)
            val acumulativeHeight = calcAccHeight(loc)
            if (acumulativeHeight >= 0) {
                LocationUpdatesService.user_is_hiking = true
                repository.user_hiking.postValue(true)

                //showNotification("Detected HIKING in MOUNTAINS!","Application detected that you might be HIKING.", context)
            }
            Log.d(TAG, acumulativeHeight.toString())
        }
    }

    fun calcAccHeight(locations: List<Location>): Double {
        var height: Double = 0.0
        var prevLoc: Location? = null
        for (loc in locations) {
            if (prevLoc == null) {
                prevLoc = loc
            }
            val diff = loc.elevation - prevLoc.elevation
            if (diff > 0)
                height += diff
            prevLoc = loc
        }
        return height
    }

    private fun showNotification(warningTitle:String, warningText: String, context: Context) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_WARNING).setSmallIcon(
            R.drawable.ic_launcher_foreground).setContentTitle(warningTitle).setContentText(warningText)
        with(NotificationManagerCompat.from(context)) {
            notify(DetectedTransitionReceiver.NOTIFICATION_ID, builder.build())
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }



    fun getBatteryPercentage(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= 21) {
            val bm = context.getSystemService(Service.BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        } else {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)

            val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            level / scale.toFloat().toInt()

        }
    }


}