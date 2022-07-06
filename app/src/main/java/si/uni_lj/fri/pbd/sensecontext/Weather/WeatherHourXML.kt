package si.uni_lj.fri.pbd.sensecontext.Weather

import java.util.*

class WeatherHourXML {
    var date: Date? = null
    var oblacnost: String? = null
    var vremenski_pojav: String? = null // ce je vremenski pojav null, potem je tudi intenzivnost null
    var intenzivnost: String? = null
    var t_1000: Int? = null
    var t_1500: Int? = null
    var t_2000: Int? = null
    var t_2500: Int? = null
    var t_3000: Int? = null
    var meja_snezenja: Int? = null
    var w_1000: Int? = null
    var w_1500: Int? = null
    var w_2000: Int? = null
    var w_2500: Int? = null
    var w_3000: Int? = null
}