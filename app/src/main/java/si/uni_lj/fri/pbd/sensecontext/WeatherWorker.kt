package si.uni_lj.fri.pbd.sensecontext

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import si.uni_lj.fri.pbd.sensecontext.data.WeatherDatabase
import si.uni_lj.fri.pbd.sensecontext.data.WeatherHour
import si.uni_lj.fri.pbd.sensecontext.data.WeatherRepository
import java.lang.Exception


class WeatherWorker (ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val client = OkHttpClient()
        val getRequest: Request = Request.Builder()
            .url("https://meteo.arso.gov.si/uploads/probase/www/fproduct/text/sl/forecast_SI_JULIAN-ALPS_latest.xml")
            .build()

        try {
            val response: Response = client.newCall(getRequest).execute()
            val stream = response.body?.byteStream()
            //val stream = applicationContext.assets.open("forecast.xml")
            if (stream != null) {
                val parser = ParseWeatherXML()
                val weather = stream.let { parser.parse(it) }
                updateWeatherDatabase(weather)
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }


    suspend fun updateWeatherDatabase(weather: ArrayList<WeatherHour>) {
        val db = WeatherDatabase.getDatabase(applicationContext)
        val weatherDao = db.WeatherDao()
        val repository = WeatherRepository(weatherDao)
        for (item in weather) {
            withContext(Dispatchers.IO) {
                repository.addWeatherHour(item)
            }

        }
        //repository.addWeatherHour()
    }
}