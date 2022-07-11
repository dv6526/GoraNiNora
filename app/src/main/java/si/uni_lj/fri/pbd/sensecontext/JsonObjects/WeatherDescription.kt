package si.uni_lj.fri.pbd.sensecontext.JsonObjects

data class WeatherDescription(
    val weather_description_id: Long,
    val day_delay: Int,
    val temp_avg_min: Double?,
    val temp_avg_max: Double?,
    val hour_min: Int,
    val hour_max: Int,
    val oblacnost: String?,
    val vremenski_pojav: String?,
    val intenzivnost: String?,
    val elevation: String?
)