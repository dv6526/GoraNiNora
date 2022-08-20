package si.uni_lj.fri.pbd.sensecontext

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.sensecontext.TrackingHelper.Companion.processingScope
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.data.WeatherHour
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.data.rules.RuleWithLists
import si.uni_lj.fri.pbd.sensecontext.ui.HikingWarningsActivity
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MatchRules {

    companion object {
        fun matchRules(context: Context, user_hiking: Boolean) {
            val db = ApplicationDatabase.getDatabase(context)
            val dao = db.dao()
            val repository = Repository(dao)
            var area_id: Int? = null

            val loc =repository.getLatestLocation()
            var rwd: List<RuleWithLists>? = null
            if (user_hiking) {
                rwd = repository.getRulesHiking()
                area_id = LocationUpdatesService.av_area_id!! // trenutno opozorilo -> gledamo dangerje, patterne in probleme glede na trenutno lokacijo
            } else {
                rwd = repository.getRulesNotHiking()
            }


            var ids: MutableList<Long> = mutableListOf()
            var rules_names: MutableList<String> = mutableListOf()
            var rules_texts: MutableList<String> = mutableListOf()

            for (rule in rwd) {
                var rule_is_match = true
                val aspects = rule.rule.aspect
                if (!user_hiking)
                    area_id = rule.rule.av_area_id //all problem, patterns, dangers, weather for this area_id
                if (aspects != null && user_hiking && loc.aspect != null) {
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
                            "W" -> {
                                if (loc.aspect in 180.0..360.0)
                                   break
                                else
                                    rule_is_match = false
                            }
                            "E" -> {
                                if (loc.aspect in 0.0..180.0)
                                    break
                                else
                                    rule_is_match = false
                            }
                        }
                    }
                }

                val min_slope = rule.rule.min_slope
                if (min_slope != null && user_hiking && loc.slope != null  && loc.slope < min_slope) {
                    rule_is_match = false
                }

                val max_slope = rule.rule.max_slope
                if (max_slope != null && user_hiking && loc.slope != null && loc.slope > max_slope) {
                    rule_is_match = false
                }

                val elevation_min = rule.rule.elevation_min
                if (elevation_min != null && user_hiking && loc.elevation != null && loc.elevation < elevation_min) {
                    rule_is_match = false
                }

                val elevation_max = rule.rule.elevation_max
                if (elevation_max != null && user_hiking && loc.elevation != null && loc.elevation > elevation_max) {
                    rule_is_match = false
                }

                val cal = Calendar.getInstance()
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val hour_min = rule.rule.hour_min
                if (hour_min != null && hour < hour_min) {
                    rule_is_match = false
                }

                val hour_max = rule.rule.hour_max
                if (hour_max != null && hour > hour_max) {
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


                    val whs = weatherHoursForAvalancheArea(area_id, context, repository, cal1, cal2)
                    //check if weather description matches weather from ARSO

                    if (whs.size == 0) {
                        rule_is_match = false
                    }


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
                    if (wd.temp_avg_min != null && temp <= wd.temp_avg_min)
                        rule_is_match = false
                    if (wd.temp_avg_max != null && temp >= wd.temp_avg_max)
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


                    val pts2 = repository.getPatternsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, pt.pattern_type,
                        area_id!!
                    )

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
                    val dangers2 = repository.getDangersForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, area_id!!, loc.elevation, danger.value)
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
                    val problems2 = repository.getProblemsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, area_id!!, problem.problem_type, loc.elevation)
                    //val problems2 = repository.getProblemsForDate(bulletin.av_bulletin_id, cal1.time, cal2.time, 1 , 3, 2500.0)
                    if (problems2.size == 0) {
                        rule_is_match = false
                    }
                }

                // če za opozorilo danes še nismo poslali notificationa
                val cal1 = Calendar.getInstance()
                cal1.set(Calendar.HOUR_OF_DAY, 0)
                cal1.set(Calendar.MINUTE, 0)
                cal1.set(Calendar.SECOND, 0)
                cal1.set(Calendar.MILLISECOND, 0)
                val matchedRule = repository.getMatchedRuleByIdDate(cal1.time, rule.rule.rule_id)

                if(rule_is_match && matchedRule == null) {
                    ids.add(rule.rule.rule_id)
                    rules_names.add(rule.rule.notification_name)
                    rules_texts.add(rule.rule.notification_text)
                }
            }


            processingScope.launch {

                ids.forEachIndexed { index, id ->
                    repository.addMatchedRule(
                        MatchedRule(
                            0L,
                            id,
                            Calendar.getInstance().time,
                            false,
                            rules_names[index],
                            rules_texts[index],
                            user_hiking,
                            area_id!!
                        )
                    )
                }
                val desc = StringBuilder()
                rules_names.forEachIndexed { idx, rule ->
                    if (idx == rules_names.size - 1)
                        desc.append(rule)
                    else
                        desc.append(rule + ", ")
                }
                var title: String? = null
                when (rules_names.size) {
                    1 -> title = "Zaznano 1 opozorilo!"
                    2 -> title = "Zaznana 2 opozorila"
                    3 -> title = "Zaznana 3 opozorila"
                    3 -> title = "Zaznana 4 opozorila"
                    else -> "Zaznanih " + rules_names.size + " opozoril!"
                }
                if (title != null && user_hiking) {
                    showNotification(title, desc.toString(), context)
                }
            }


        }
        //poglej katera pravila so se matchala, shrani si idje
        //prenesi vsa pravila od danes
        //poglej, če se je pojavilo kaksno novo pravilo
        //poslji notification
        //ko odpre notificaiton se odpre stran s pravili
        private fun showNotification(warningTitle:String, warningText: String, context: Context) {

            // Create an Intent for the activity you want to start
            val intent = Intent(context, HikingWarningsActivity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID_NOTIFY).setSmallIcon(
                R.drawable.ic_launcher_foreground).setContentTitle(warningTitle).setContentText(warningText).setContentIntent(pendingIntent)
            with(NotificationManagerCompat.from(context)) {
                notify(13, builder.build())
            }
        }

        private fun weatherHoursForAvalancheArea(av_area_id: Int?, context: Context, repository: Repository, cal1: Calendar, cal2: Calendar): List<WeatherHour> {
            val whs = mutableListOf<WeatherHour>()
            when (av_area_id) {
                2 -> {
                    whs.addAll(repository.getWeatherHoursBetweenDate(cal1.time, cal2.time, context.resources.getString(R.string.weather_area_2)))
                }
                3 -> {
                    whs.addAll(repository.getWeatherHoursBetweenDate(cal1.time, cal2.time, context.resources.getString(R.string.weather_area_1)))
                    whs.addAll(repository.getWeatherHoursBetweenDate(cal1.time, cal2.time, context.resources.getString(R.string.weather_area_4)))
                }
                4 -> {
                    whs.addAll(repository.getWeatherHoursBetweenDate(cal1.time, cal2.time, context.resources.getString(R.string.weather_area_3)))
                    whs.addAll(repository.getWeatherHoursBetweenDate(cal1.time, cal2.time, context.resources.getString(R.string.weather_area_4)))
                }
            }
            return whs.toList()
        }
    }


}