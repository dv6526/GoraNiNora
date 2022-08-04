package si.uni_lj.fri.pbd.sensecontext.data

import androidx.room.*
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.AvalancheBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.DangerBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.PatternBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.ProblemBulletin
import si.uni_lj.fri.pbd.sensecontext.data.rules.*
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

    @Query("SELECT * FROM location_table ORDER BY id DESC LIMIT 1")
    fun getLatestLocation(): Location

    @Query("SELECT * FROM location_table WHERE date BETWEEN :from AND :to")
    fun fetchLocationsBetweenDate(from: Date, to: Date): List<Location>

    @Query("SELECT * FROM weather_table WHERE date BETWEEN :from AND :to")
    fun getWeatherHoursBetweenDate(from: Date, to: Date): List<WeatherHour>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_rule(rule: Rule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_weather_description(weatherDescription: WeatherDescription): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPatternRule(patternRule: PatternRule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProblemRule(problemRule: ProblemRule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDangerRule(dangerRule: DangerRule): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAvalancheBulletin(avalancheBulletin: AvalancheBulletin): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProblem(problemBulletin: ProblemBulletin): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDanger(dangerBulletin: DangerBulletin): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPattern(patternBulletin: PatternBulletin): Long


    @Transaction
    @Query("SELECT * FROM rule_table")
    fun getRules(): List<RuleWithLists>

    @Transaction
    @Query("SELECT * FROM rule_table WHERE NOT user_hiking")
    fun getRulesNotHiking(): List<RuleWithLists>

    @Transaction
    @Query("SELECT * FROM rule_table WHERE user_hiking")
    fun getRulesHiking(): List<RuleWithLists>
}


// if user is HIKING and the PROBLEM is present in av_area_id
// true -> we check the aspect of the user HIKING
// user is located in elevation_from - elevation_to which is defined by PROBLEM
//
