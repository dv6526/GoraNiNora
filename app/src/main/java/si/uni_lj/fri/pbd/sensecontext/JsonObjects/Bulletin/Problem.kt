package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin

data class Problem(
    var additional_load: Int,
    var aspects: String,
    var av_area_id: Int,
    var avalanchetypes: List<Avalanchetype>,
    var elevation_from: Double,
    var elevation_to: Double,
    var primary_days: String,
    var problem_id: Int,
    var s_reason_id: Int,
    var treeline: Boolean,
    var treeline_above: Boolean,
    var trends: String,
    var valid_end: String,
    var valid_start: String
)