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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMatchedRule(matchedRule: MatchedRule): Long


    @Transaction
    @Query("SELECT * FROM rule_table")
    fun getRules(): List<RuleWithLists>

    @Transaction
    @Query("SELECT * FROM rule_table WHERE NOT user_hiking")
    fun getRulesNotHiking(): List<RuleWithLists>

    @Transaction
    @Query("SELECT * FROM rule_table WHERE user_hiking")
    fun getRulesHiking(): List<RuleWithLists>

    @Transaction
    @Query("SELECT * FROM pattern_bulletin_table WHERE :av_bulletin_id=av_bulletin_id AND :start >= valid_start AND :end <= valid_end AND :av_area_id = av_area_id AND :pattern_id = pattern AND :av_area_id = av_area_id")
    fun getPatternsForDate(av_bulletin_id: Long, start: Date, end: Date, pattern_id: Int, av_area_id: Int): List<PatternBulletin>

    @Transaction
    @Query("SELECT * FROM avalanche_bulletin_table ORDER BY av_bulletin_id DESC LIMIT 1")
    fun getLatestBulletin(): AvalancheBulletin

    @Transaction
    @Query("SELECT * FROM danger_bulletin_table WHERE :av_bulletin_id=av_bulletin_id AND :start >= valid_start AND :end <= valid_end AND :av_area_id = av_area_id AND :value = value AND :elevation BETWEEN elevation_from AND elevation_to")
    fun getDangersForDate(av_bulletin_id: Long, start: Date, end: Date, av_area_id: Int, elevation: Double, value: Int): List<DangerBulletin>

    @Transaction
    @Query("SELECT * FROM problem_bulletin_table WHERE :av_bulletin_id=av_bulletin_id AND :start >= valid_start AND :end <= valid_end AND :av_area_id = av_area_id AND :problem = problem AND :elevation BETWEEN elevation_from AND elevation_to")
    fun getProblemsForDate(av_bulletin_id: Long, start: Date, end: Date, av_area_id: Int, problem: Int, elevation: Double): List<ProblemBulletin>

    @Transaction
    @Query("SELECT * FROM matched_rule_table WHERE :rule_id=rule_id AND date >= :date ")
    fun getRuleByIdNewerThan(rule_id: Long, date: Date): List<MatchedRule>

}


// if user is HIKING and the PROBLEM is present in av_area_id
// true -> we check the aspect of the user HIKING
// user is located in elevation_from - elevation_to which is defined by PROBLEM
//
