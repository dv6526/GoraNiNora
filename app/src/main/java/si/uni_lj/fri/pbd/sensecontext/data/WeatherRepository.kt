package si.uni_lj.fri.pbd.sensecontext.data

import androidx.lifecycle.LiveData

class WeatherRepository(private val weatherDao: WeatherDao) {
    val readAllData: List<WeatherHour> = weatherDao.readAllData()
    suspend fun addWeatherHour(weatherHour: WeatherHour) {
        weatherDao.add_weather_hour(weatherHour)
    }
}