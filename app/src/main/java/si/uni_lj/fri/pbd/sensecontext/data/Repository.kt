package si.uni_lj.fri.pbd.sensecontext.data

import si.uni_lj.fri.pbd.sensecontext.data.bulletin.AvalancheBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.DangerBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.PatternBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.ProblemBulletin
import si.uni_lj.fri.pbd.sensecontext.data.rules.*
import java.util.*

class Repository(private val databaseDao: DatabaseDao) {
    val readAllDataWeather: List<WeatherHour> = databaseDao.readAllDataWeather()
    suspend fun addWeatherHour(weatherHour: WeatherHour) {
        databaseDao.add_weather_hour(weatherHour)
    }

    fun getWeatherHoursBetweenDate(from: Date, to: Date): List<WeatherHour> = databaseDao.getWeatherHoursBetweenDate(from, to)

    suspend fun addLocation(location: Location) {
        databaseDao.add_location(location)
    }

    fun getLatestLocation(): Location = databaseDao.getLatestLocation()

    fun fetchLocationsBetweenDate(from: Date, to: Date): List<Location>  = databaseDao.fetchLocationsBetweenDate(from, to)

    suspend fun addRule(rule: Rule) : Long {
        return databaseDao.add_rule(rule)
    }

    suspend fun addWeatherDescription(weatherDescription: WeatherDescription): Long {
        return databaseDao.add_weather_description(weatherDescription)
    }

    suspend fun addPatternRule(patternRule: PatternRule): Long {
        return databaseDao.addPatternRule(patternRule)
    }
    suspend fun addProblemRule(problemRule: ProblemRule): Long {
        return databaseDao.addProblemRule(problemRule)
    }
    suspend fun addDangerRule(dangerRule: DangerRule): Long {
        return databaseDao.addDangerRule(dangerRule)
    }

    suspend fun addAvalancheBulletin(avalancheBulletin: AvalancheBulletin): Long {
        return databaseDao.addAvalancheBulletin(avalancheBulletin)
    }

    suspend fun addProblem(problemBulletin: ProblemBulletin): Long {
        return databaseDao.addProblem(problemBulletin)
    }

    suspend fun addDanger(dangerBulletin: DangerBulletin): Long {
        return databaseDao.addDanger(dangerBulletin)
    }

    suspend fun addPattern(patternBulletin: PatternBulletin): Long {
        return databaseDao.addPattern(patternBulletin)
    }

    fun getRulesWithLists(): List<RuleWithLists> {
        return databaseDao.getRules()
    }

    fun getRulesNotHiking(): List<RuleWithLists> {
        return databaseDao.getRulesNotHiking()
    }

}