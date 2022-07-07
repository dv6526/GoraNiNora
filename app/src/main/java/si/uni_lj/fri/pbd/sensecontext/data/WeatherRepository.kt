package si.uni_lj.fri.pbd.sensecontext.data

import androidx.lifecycle.LiveData

class WeatherRepository(private val weatherDao: WeatherDao) {
    val readAllDataWeather: List<WeatherHour> = weatherDao.readAllDataWeather()
    suspend fun addWeatherHour(weatherHour: WeatherHour) {
        weatherDao.add_weather_hour(weatherHour)
    }

    suspend fun addLocation(location: Location) {
        weatherDao.add_location(location)
    }
}