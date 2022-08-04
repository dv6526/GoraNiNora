package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin

data class Comment(
    var av_area_id: Int,
    var contents: String,
    var heading_code: String,
    var subareas: List<Any>
)