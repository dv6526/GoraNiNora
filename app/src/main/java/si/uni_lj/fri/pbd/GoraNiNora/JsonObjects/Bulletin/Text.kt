package si.uni_lj.fri.pbd.GoraNiNora.JsonObjects.Bulletin

data class Text(
    var danger_description: String,
    var s_language_id: Int,
    var snow_conditions: String,
    var snow_conditions_tendency: String,
    var weather_evolution: String
)