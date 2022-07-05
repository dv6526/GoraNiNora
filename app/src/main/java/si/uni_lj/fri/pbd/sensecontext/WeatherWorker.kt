package si.uni_lj.fri.pbd.sensecontext

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayInputStream
import java.io.IOException
import java.lang.Exception


class WeatherWorker (ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val client = OkHttpClient()

        val getRequest: Request = Request.Builder()
            .url("https://meteo.arso.gov.si/uploads/probase/www/fproduct/text/sl/forecast_SI_JULIAN-ALPS_latest.xml")
            .build()

        try {
            val response: Response = client.newCall(getRequest).execute()
            val stream = response.body?.byteStream()
            val parser = ParseWeatherXML()
            val weather = stream?.let { parser.parse(it) }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}