package si.uni_lj.fri.pbd.sensecontext.JsonObjects

data class Rule(
    val aspect: String?,
    val min_slope: Double?,
    val max_slope: Double?,
    val elevation_min: Double?,
    val elevation_max: Double?,
    val user_hiking: Boolean,
    var weather_descriptions: List<WeatherDescription?>
)