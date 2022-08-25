package si.uni_lj.fri.pbd.GoraNiNora.JsonObjects.Rules

data class Problem(
    var check_elevation: Boolean,
    var day_delay: Int,
    var hour_max: Int,
    var hour_min: Int,
    var problem_id: Int
)