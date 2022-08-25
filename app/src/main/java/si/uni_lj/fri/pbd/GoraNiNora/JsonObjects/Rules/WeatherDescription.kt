package si.uni_lj.fri.pbd.GoraNiNora.JsonObjects.Rules

data class WeatherDescription(
    val day_delay: Int, // -1 -> prejsni dan, 0 -> danes, 1 -> jutri
    val temp_avg_min: Double?,// min povprečna temperatura
    val temp_avg_max: Double?, // max povprečna temperatura
    val hour_min: Int, // med hour_min in hour_max mora veljati povprečna temperatura,
    val hour_max: Int,
    val oblacnost: String?, // clear, mostClear, slightCloudy, partCloudy, modCloudy, prevCloudy, overcast, FG
    val vremenski_pojav: String?, //FG, DZ, FZDZ, RA, FZRA, RASN, SN, SHRA, SHRASN, SHSN, SHGR, TS, TSRA, TSRASN, TSSN, TSGR
    val intenzivnost: String?, // light, mod, heavy
    val elevation: String? // 1000m, 1500m, 2000m, 2500m, 3000m -> temperatura na nadmorski višini
)