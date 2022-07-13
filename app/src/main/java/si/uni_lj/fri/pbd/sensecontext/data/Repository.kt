package si.uni_lj.fri.pbd.sensecontext.data

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

    suspend fun addRuleWeatherDescriptionRef(ruleWeatherDescriptionRef: RuleWeatherDescriptionRef) {
        databaseDao.add_rule_weather_description_ref(ruleWeatherDescriptionRef)
    }

    fun getRulesWithWeatherDescription(): List<RuleWithWeatherDescription> {
        return databaseDao.getRulesWithWeatherDescription()
    }

    fun getRulesWithWeatherDescriptionNotHiking(): List<RuleWithWeatherDescription> {
        return databaseDao.getRulesWithWeatherDescriptionNotHiking()
    }
}