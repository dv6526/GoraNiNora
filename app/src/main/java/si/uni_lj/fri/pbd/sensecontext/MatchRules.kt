package si.uni_lj.fri.pbd.sensecontext

import android.content.Context
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.data.rules.RuleWithLists
import java.text.SimpleDateFormat
import java.util.*

class MatchRules {

    companion object {
        fun matchRules(context: Context, user_hiking: Boolean) {
            val db = ApplicationDatabase.getDatabase(context)
            val dao = db.dao()
            val repository = Repository(dao)

            val loc =repository.getLatestLocation()
            var rwd: List<RuleWithLists>? = null
            if (user_hiking) {
                rwd = repository.getRulesHiking()
            } else {
                rwd = repository.getRulesNotHiking()
            }

            var area_id = LocationUpdatesService.av_area_id!! // trenutno opozorilo -> gledamo dangerje, patterne in probleme glede na trenutno lokacijo
            var matched_rule = false // če smo imeli ujemanje vsaj z enim rule-om
            var ids: MutableList<Long> = mutableListOf()
            var rules_names: MutableList<String> = mutableListOf()
            var rules_texts: MutableList<String> = mutableListOf()

            for (rule in rwd) {
                var rule_is_match = true
                val aspects = rule.rule.aspect
                if (aspects != null) {
                    for (aspect in aspects.split(",")) {
                        when (aspect) {
                            "N" -> {
                                if (loc.aspect in 0.0..90.0 || loc.aspect in 270.0..360.0)
                                    break
                                else
                                    rule_is_match = false
                            }
                            "S" -> {
                                if (loc.aspect in 90.0..270.0)
                                    break
                                else
                                    rule_is_match = false
                            }
                        }
                    }
                }

                val min_slope = rule.rule.min_slope
                if (min_slope != null && loc.slope < min_slope) {
                    rule_is_match = false
                }

                val max_slope = rule.rule.max_slope
                if (max_slope != null && loc.slope > max_slope) {
                    rule_is_match = false
                }

                val elevation_min = rule.rule.elevation_min
                if (elevation_min != null && loc.elevation < elevation_min) {
                    rule_is_match = false
                }

                val elevation_max = rule.rule.elevation_max
                if (elevation_max != null && loc.elevation > elevation_max) {
                    rule_is_match = false
                }


                //preveri, če se ujemajo vsa pravila za vreme
                val wds = rule.weather_descriptions
                for (wd in wds) {
                    val cal1 = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cal1.add(Calendar.DATE, wd.day_delay)
                    cal1.set(Calendar.HOUR_OF_DAY, wd.hour_min)
                    cal1.set(Calendar.MINUTE, 0)
                    cal1.set(Calendar.SECOND, 0)
                    cal1.set(Calendar.MILLISECOND, 0)
                    val str1 = sdf.format(cal1.time)
                    val cal2 = Calendar.getInstance()
                    cal2.add(Calendar.DATE, wd.day_delay)
                    cal2.set(Calendar.HOUR_OF_DAY, wd.hour_max)
                    cal2.set(Calendar.MINUTE, 0)
                    cal2.set(Calendar.SECOND, 0)
                    cal2.set(Calendar.MILLISECOND, 0)
                    val str2 = sdf.format(cal2.time)
                    val whs = repository.getWeatherHoursBetweenDate(cal1.time, cal2.time)
                    //check if weather description matches weather from ARSO

                    // AVG TEMP
                    var vremenski_pojav_occured = false
                    var oblacnost_occured = false
                    var temp = 0
                    for (wh in whs) {
                        when (wd.elevation) {
                            "1000" -> temp += wh.t_1000
                            "1500" -> temp += wh.t_1500
                            "2000" -> temp += wh.t_2000
                            "2500" -> temp += wh.t_2500
                            "3000" -> temp += wh.t_3000
                        }

                        if (wd.vremenski_pojav != null && wd.vremenski_pojav.equals(wh.vremenski_pojav)) {
                            if (wd.intenzivnost != null && wd.intenzivnost.equals(wh.intenzivnost)) {
                                vremenski_pojav_occured = true
                            } else {
                                vremenski_pojav_occured = true
                            }
                        }

                        if (wd.oblacnost != null && wd.oblacnost.equals(wh.oblacnost)) {
                            oblacnost_occured = true
                        }
                    }

                    if (wd.vremenski_pojav != null && !vremenski_pojav_occured) {
                        rule_is_match = false
                    }

                    if (wd.oblacnost != null && !oblacnost_occured) {
                        rule_is_match = false
                    }

                    if (whs.size > 0)
                        temp = temp/whs.size
                    if (wd.temp_avg_min != null && temp < wd.temp_avg_min)
                        rule_is_match = false
                    if (wd.temp_avg_max != null && temp > wd.temp_avg_max)
                        rule_is_match = false
                }

                //preveri, če se ujemajo vsi patterni
                val pts = rule.patternRules
                for (pt in pts) {
                    val cal1 = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cal1.add(Calendar.DATE, pt.day_delay)
                    //cal1.add(Calendar.DATE, -86)
                    cal1.set(Calendar.HOUR_OF_DAY, pt.hour_min)
                    cal1.set(Calendar.MINUTE, 0)
                    cal1.set(Calendar.SECOND, 0)
                    cal1.set(Calendar.MILLISECOND, 0)
                    val str1 = sdf.format(cal1.time)
                    val cal2 = Calendar.getInstance()
                    cal2.add(Calendar.DATE, pt.day_delay)
                    cal2.set(Calendar.HOUR_OF_DAY, pt.hour_max)
                    cal2.set(Calendar.MINUTE, 0)
                    cal2.set(Calendar.SECOND, 0)
                    cal2.set(Calendar.MILLISECOND, 0)
                    val str2 = sdf.format(cal2.time)
                    //vzames bilten
                    val bulletin = repository.getLatestBulletin()
                    //val pts2 = repository.getPatternsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, pt.pattern_id, 1)

                    if (!user_hiking) {
                        area_id = pt.av_area_id!! // vemo, da imamo rule, ki so za splošna opozorila in mora obstajati območje.
                    }
                    val pts2 = repository.getPatternsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, pt.pattern_id, area_id)

                    if (pts2.size == 0) {
                        //ni patterna za trenutno območje na katerem se nahajamo
                        rule_is_match = false
                    }
                }


                //preveri, če se ujemajo vsi dangerji
                val dangers = rule.dangerRules
                for (danger in dangers) {
                    var hour_min = 0
                    var hour_max = 12
                    if (!danger.am) {
                        hour_min = 12
                        hour_max = 24
                    }

                    val cal1 = Calendar.getInstance()
                    cal1.add(Calendar.DATE, danger.day_delay)
                    cal1.set(Calendar.HOUR_OF_DAY, hour_min)
                    cal1.set(Calendar.MINUTE, 0)
                    cal1.set(Calendar.SECOND, 0)
                    cal1.set(Calendar.MILLISECOND, 0)
                    val cal2 = Calendar.getInstance()
                    cal2.add(Calendar.DATE, danger.day_delay)
                    cal2.set(Calendar.HOUR_OF_DAY, hour_max)
                    cal2.set(Calendar.MINUTE, 0)
                    cal2.set(Calendar.SECOND, 0)
                    cal2.set(Calendar.MILLISECOND, 0)
                    val bulletin = repository.getLatestBulletin()
                    if (!user_hiking) {
                        area_id = danger.av_area_id!! // vemo, da imamo rule, ki so za splošna opozorila in mora obstajati območje.
                    }
                    val dangers2 = repository.getDangersForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, area_id, loc.elevation, danger.value)
                    //val dangers2 = repository.getDangersForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, 1, loc.elevation, 2)
                    if (dangers2.size == 0) {
                        rule_is_match = false
                    }
                }

                //preveri, če se ujemajo vsi problemi
                val problems = rule.problemRules
                for (problem in problems) {
                    val cal1 = Calendar.getInstance()
                    cal1.add(Calendar.DATE, problem.day_delay)
                    cal1.set(Calendar.HOUR_OF_DAY, problem.hour_min)
                    cal1.set(Calendar.MINUTE, 0)
                    cal1.set(Calendar.SECOND, 0)
                    cal1.set(Calendar.MILLISECOND, 0)
                    val cal2 = Calendar.getInstance()
                    cal2.add(Calendar.DATE, problem.day_delay)
                    cal2.set(Calendar.HOUR_OF_DAY, problem.hour_max)
                    cal2.set(Calendar.MINUTE, 0)
                    cal2.set(Calendar.SECOND, 0)
                    cal2.set(Calendar.MILLISECOND, 0)
                    val bulletin = repository.getLatestBulletin()
                    if (!user_hiking) {
                        area_id = problem.av_area_id!! // vemo, da imamo rule, ki so za splošna opozorila in mora obstajati območje.
                    }
                    val problems2 = repository.getProblemsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, area_id, problem.problem_id, loc.elevation)
                    //val problems2 = repository.getProblemsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, 1 , 3, 2500.0)
                    if (problems2.size == 0) {
                        rule_is_match = false
                    }
                }

                if(rule_is_match) {

                    ids.add(rule.rule.rule_id)
                    rules_names.add(rule.rule.notification_name)
                    rules_texts.add(rule.rule.notification_text)

                    //showNotification(rule.rule.notification_name, rule.rule.notification_text, context)
                }
            }

            processingScope.launch {
                ids.forEachIndexed { index, id ->
                    // rule shrani, če danes še ni bilo ujemanja
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.HOUR_OF_DAY, 0)
                    cal1.set(Calendar.MINUTE, 0)
                    cal1.set(Calendar.SECOND, 0)
                    cal1.set(Calendar.MILLISECOND, 0)
                    val matched_rules = repository.getRuleByIdNewerThan(id, cal1.time)
                    // danes še nisi izdal opozorila za rule_id
                    if (matched_rules.size == 0) {
                        repository.addMatchedRule(
                            MatchedRule(0L, id, Calendar.getInstance().time, false, rules_names[index], rules_texts[index], true)
                        )
                    }
                }

                if (ids.size != 0 && user_hiking) {
                    //pošlji notification za matchana pravila
                }



            }

        }
        //poglej katera pravila so se matchala, shrani si idje
        //prenesi vsa pravila od danes
        //poglej, če se je pojavilo kaksno novo pravilo
        //poslji notification
        //ko odpre notificaiton se odpre stran s pravili
    }
}