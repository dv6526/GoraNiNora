package si.uni_lj.fri.pbd.sensecontext.JsonObjects

data class Rule(
    val aspect: String?, // usmerjenost pobočja (S, J, Z, V)
    val min_slope: Double?, // min naklon, na katerem se uporabnik nahaja
    val max_slope: Double?, // max naklon, na katerem se uporabnik nahaja
    val elevation_min: Double?, // min nadmorska višina, na kateri se uporabnik nahaja
    val elevation_max: Double?, // max nadmorska višina, na kateri se uporabnik nahaja
    val user_hiking: Boolean, // ali se obvestilo se prikaze, ko je uporabnik na turi?
    val notification_name: String,
    val notification_text: String,
    var weather_descriptions: List<WeatherDescription?> // kateri pogoji glede vremena morajo veljati
)