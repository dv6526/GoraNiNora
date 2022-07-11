package si.uni_lj.fri.pbd.sensecontext.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_weather_hour(weatherHour: WeatherHour)

    @Query("SELECT * FROM weather_table ORDER BY id ASC")
    fun readAllDataWeather(): List<WeatherHour>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_location(location: Location)

    @Query("SELECT * FROM location_table ORDER BY id ASC")
    fun readAllDataLocation(): List<Location>

    @Query("SELECT * FROM location_table WHERE date BETWEEN :from AND :to")
    fun fetchLocationsBetweenDate(from: Date, to: Date): List<Location>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add_rule(rule: Rule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add_weather_description(weatherDescription: WeatherDescription): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add_rule_weather_description_ref(ruleWeatherDescriptionRef: RuleWeatherDescriptionRef)

    @Transaction
    @Query("SELECT * FROM rule_table")
    fun getRulesWithWeatherDescription(): List<RuleWithWeatherDescription>
}