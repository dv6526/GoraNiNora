package si.uni_lj.fri.pbd.sensecontext.Weather

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.internal.filter.ValueNodes.JsonNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin.*
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.data.WeatherHour
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.AvalancheBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.DangerBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.PatternBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.ProblemBulletin
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class AvalancheBulletinWorker (ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {

    val db = ApplicationDatabase.getDatabase(applicationContext)
    val dao = db.dao()
    val repository = Repository(dao)
    val processingScope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val client = OkHttpClient()
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("vreme.arso.gov.si")
            .addPathSegments("api/1.0/avalanche_bulletin/")
            .addQueryParameter("format", "json")
            .build()
        val getRequest: Request = Request.Builder()
            .url(urlBuilder)
            .addHeader("content-type", "application/json")
            .build()

        try {
            val response: Response = client.newCall(getRequest).execute()
            //val cont = JsonPath.parse(response.body!!.string())
            val jsonString = response.body!!.string()
            val bulletin = Gson().fromJson(jsonString, Bulletin::class.java)
            print(bulletin)
            val av_bulletin = AvalancheBulletin(bulletin.av_bulletin_id, bulletin.texts[0].danger_description, bulletin.texts[0].snow_conditions_tendency, bulletin.texts[0].weather_evolution,
                bulletin.texts[0].snow_conditions)

            val av_id = repository.addAvalancheBulletin(av_bulletin)

            if (av_id != -1L) {
                for (danger in bulletin.dangers) {
                    repository.addDanger(
                        DangerBulletin(
                            0L,
                            av_id,
                            danger.aspects,
                            danger.av_area_id,
                            danger.elevation_from,
                            danger.elevation_to,
                            danger.treeline,
                            danger.treeline_above,
                            Date.from(Instant.parse(danger.valid_end)),
                            Date.from(Instant.parse(danger.valid_start)),
                            danger.value
                        )
                    )
                }

                for (pattern in bulletin.patterns) {
                    repository.addPattern(
                        PatternBulletin(
                            0L,
                            av_id,
                            pattern.pattern,
                            pattern.av_area_id,
                            Date.from(Instant.parse(pattern.valid_end)),
                            Date.from(Instant.parse(pattern.valid_start))
                        )
                    )
                }

                for (problem in bulletin.problems) {
                    repository.addProblem(
                        ProblemBulletin(
                            0L,
                            av_id,
                            problem.problem_id,
                            problem.av_area_id,
                            problem.elevation_from,
                            problem.elevation_to,
                            Date.from(Instant.parse(problem.valid_end)),
                            Date.from(Instant.parse(problem.valid_start))
                        )
                    )
                }
            }






            return Result.success()
        } catch (e: Exception) {
            print(e.toString())
            return Result.failure()
        }
    }


    suspend fun updateWeatherDatabase(weather: ArrayList<WeatherHour>) {

        for (item in weather) {
            withContext(Dispatchers.IO) {
                repository.addWeatherHour(item)
            }

        }
        //repository.addWeatherHour()
    }


}