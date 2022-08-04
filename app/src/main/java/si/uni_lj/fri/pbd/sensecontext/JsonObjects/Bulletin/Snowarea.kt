package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin

data class Snowarea(
    var av_area_id: Int,
    var snowheights: List<Snowheight>,
    var snowlevel_n: Int?,
    var snowlevel_s: Int?
)