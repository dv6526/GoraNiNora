package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Rules

data class Danger(
    var check_elevation: Boolean,
    var day_delay: Int,
    var am: Boolean,
    var value: Int
)