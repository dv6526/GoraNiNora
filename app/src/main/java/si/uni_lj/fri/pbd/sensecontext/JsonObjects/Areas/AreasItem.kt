package si.uni_lj.fri.pbd.sensecontext.JsonObjects.Areas

data class AreasItem(
    var av_area_id: Int,
    var dangeranchor: List<String>,
    var forecast: Boolean,
    var geometry: List<List<String>>,
    var snow: String,
    var snowanchor: List<String>,
    var texts: List<Text>
)