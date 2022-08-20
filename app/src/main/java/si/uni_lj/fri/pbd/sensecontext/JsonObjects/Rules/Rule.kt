package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Rules

data class Rule(
    var av_area_id: Int?,
    val aspect: String?, // usmerjenost pobočja (N,NE,E,SE,S,SW,W,NW)
    val min_slope: Double?, // min naklon, na katerem se uporabnik nahaja
    val max_slope: Double?, // max naklon, na katerem se uporabnik nahaja
    val elevation_min: Double?, // min nadmorska višina, na kateri se uporabnik nahaja
    val elevation_max: Double?, // max nadmorska višina, na kateri se uporabnik nahaja
    val user_hiking: Boolean, // ali se obvestilo se prikaze, ko je uporabnik na turi?
    val hour_min: Int?,
    val hour_max: Int?,
    val notification_name: String,
    val notification_text: String,
    var weather_descriptions: List<WeatherDescription?>, // kateri pogoji glede vremena morajo veljati
    var patterns: List<Pattern?>,
    var problems: List<Problem?>,
    var dangers: List<Danger?>,
)