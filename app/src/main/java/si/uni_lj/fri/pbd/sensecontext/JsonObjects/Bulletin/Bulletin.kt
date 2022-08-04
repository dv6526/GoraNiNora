package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin

data class Bulletin(
    var av_bulletin_id: Long,
    var av_user: Int,
    var comments: List<Comment>,
    var dangers: List<Danger>,
    var date_created: String,
    var date_modified: String,
    var date_next_bulletin: String,
    var patterns: List<Pattern>,
    var problems: List<Problem>,
    var snowareas: List<Snowarea>,
    var snowfeatures: List<Any>,
    var status: Int,
    var texts: List<Text>,
    var valid_start: String
)