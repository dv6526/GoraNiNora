package si.uni_lj.fri.pbd.sensecontext.data

import java.util.*

class Repository(private val databaseDao: DatabaseDao) {
    val readAllDataWeather: List<WeatherHour> = databaseDao.readAllDataWeather()
    suspend fun addWeatherHour(weatherHour: WeatherHour) {
        databaseDao.add_weather_hour(weatherHour)
    }

    suspend fun addLocation(location: Location) {
        databaseDao.add_location(location)
    }

    fun fetchLocationsBetweenDate(from: Date, to: Date): List<Location>  = databaseDao.fetchLocationsBetweenDate(from, to)
}