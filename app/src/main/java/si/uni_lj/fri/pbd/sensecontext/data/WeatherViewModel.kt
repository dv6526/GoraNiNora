package si.uni_lj.fri.pbd.sensecontext.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application): AndroidViewModel(application) {
    private val readAllData: List<WeatherHour>
    private val repository: WeatherRepository
    init {
        val weatherDao = WeatherDatabase.getDatabase(application).WeatherDao()
        repository = WeatherRepository(weatherDao)
        readAllData = repository.readAllData
    }

    fun addWeatherHour(weatherHour: WeatherHour) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWeatherHour(weatherHour)
        }
    }
}