package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Rules

data class Danger(
    var av_area_id: Int,
    var check_elevation: Boolean,
    var day_delay: Int,
    var hour_max: Int, // v biltenu so nevarnosti na 12 ur, od 00-12h in 12h do 24h
    var hour_min: Int,
    var value: Int
)