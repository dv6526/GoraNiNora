package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Rules

data class Danger(
    var av_area_id: Int,
    var check_elevation: Boolean,
    var day_delay: Int,
    var hour_max: Int,
    var hour_min: Int,
    var value: Int
)