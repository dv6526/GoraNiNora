package si.uni_lj.fri.pbd.sensecontext.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.AvalancheBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.DangerBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.PatternBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.ProblemBulletin
import si.uni_lj.fri.pbd.sensecontext.data.rules.*
import java.util.*


class Repository(private val databaseDao: DatabaseDao) {
    val readAllDataWeather: List<WeatherHour> = databaseDao.readAllDataWeather()
    fun matchedRulesToday(date: Date): LiveData<List<MatchedRule>> = databaseDao.getMatchedRulesByDate(date)
    val user_hiking: MutableLiveData<Boolean> = MutableLiveData()

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

    suspend fun addMatchedRule(matchedRule: MatchedRule): Long {
        return databaseDao.addMatchedRule(matchedRule)
    }

    fun getRulesWithLists(): List<RuleWithLists> {
        return databaseDao.getRules()
    }

    fun getRulesNotHiking(): List<RuleWithLists> {
        return databaseDao.getRulesNotHiking()
    }

    fun getRulesHiking(): List<RuleWithLists> {
        return databaseDao.getRulesHiking()
    }

    fun getPatternsForDate(av_bulletin_id: Long, start: Date, end: Date, pattern_id: Int, av_area_id: Int): List<PatternBulletin> {
        return databaseDao.getPatternsForDate(av_bulletin_id, start, end, pattern_id, av_area_id)
    }

    fun getDangersForDate(av_bulletin_id: Long, start: Date, end: Date, av_area_id: Int, elevation: Double, value: Int): List<DangerBulletin> {
        return databaseDao.getDangersForDate(av_bulletin_id, start, end, av_area_id, elevation, value)
    }

    fun getProblemsForDate(av_bulletin_id: Long, start: Date, end: Date, av_area_id: Int, problem: Int, elevation: Double): List<ProblemBulletin> {
        return databaseDao.getProblemsForDate(av_bulletin_id, start, end, av_area_id, problem, elevation)
    }

    fun getLatestBulletin(): AvalancheBulletin {
        return databaseDao.getLatestBulletin()
    }

    fun getRuleByIdNewerThan(rule_id: Long, date: Date): List<MatchedRule> {
        return databaseDao.getRuleByIdNewerThan(rule_id, date)
    }

    fun getMatchedRulesByDate(date: Date, hiking: Boolean): List<MatchedRule> {
        return databaseDao.getMatchedRulesByDate(date, hiking)
    }

    fun getMatchedRuleByIdDate(date: Date, rule_id: Long): MatchedRule {
        return databaseDao.getMatchedRuleByIdDate(date, rule_id)
    }



}