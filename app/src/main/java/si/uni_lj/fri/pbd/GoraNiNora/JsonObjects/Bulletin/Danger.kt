package si.uni_lj.fri.pbd.GoraNiNora.JsonObjects.Bulletin

data class Danger(
    var aspects: String,
    var av_area_id: Int,
    var elevation_from: Int,
    var elevation_to: Int,
    var treeline: Boolean,
    var treeline_above: Boolean,
    var valid_end: String,
    var valid_start: String,
    var value: Int
)