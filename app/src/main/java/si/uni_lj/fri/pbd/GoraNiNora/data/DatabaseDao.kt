package si.uni_lj.fri.pbd.GoraNiNora.data

import androidx.lifecycle.LiveData
import androidx.room.*
import si.uni_lj.fri.pbd.GoraNiNora.data.bulletin.AvalancheBulletin
import si.uni_lj.fri.pbd.GoraNiNora.data.bulletin.DangerBulletin
import si.uni_lj.fri.pbd.GoraNiNora.data.bulletin.PatternBulletin
import si.uni_lj.fri.pbd.GoraNiNora.data.bulletin.ProblemBulletin
import si.uni_lj.fri.pbd.GoraNiNora.data.rules.*
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

    @Query("SELECT * FROM weather_table WHERE date BETWEEN :from AND :to AND area=:av_area")
    fun getWeatherHoursBetweenDate(from: Date, to: Date, av_area: String): List<WeatherHour>

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

    @Transaction
    @Query("SELECT * FROM matched_rule_table WHERE date >= :date ")
    fun getMatchedRulesByDate(date: Date): LiveData<List<MatchedRule>>

    @Transaction
    @Query("SELECT * FROM matched_rule_table WHERE date >= :date AND :hiking=hiking")
    fun getMatchedRulesByDate(date: Date, hiking: Boolean): List<MatchedRule>

    @Transaction
    @Query("SELECT * FROM matched_rule_table WHERE date >= :date AND :rule_id=rule_id")
    fun getMatchedRuleByIdDate(date: Date, rule_id: Long): MatchedRule

    @Transaction
    @Query("UPDATE matched_rule_table SET read=1 WHERE :rule_id=rule_id")
    fun updateMatchedRuleIsRead(rule_id: Long)

    @Transaction
    @Query("SELECT * FROM matched_rule_table WHERE date <= :date")
    fun getMatchedRulesOlderThan(date: Date): LiveData<List<MatchedRule>>
}


// if user is HIKING and the PROBLEM is present in av_area_id
// true -> we check the aspect of the user HIKING
// user is located in elevation_from - elevation_to which is defined by PROBLEM
//
