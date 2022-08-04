package si.uni_lj.fri.pbd.sensecontext.data.rules

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pattern_rule_table")
data class PatternRule(
    @PrimaryKey(autoGenerate = true)
    var patt_id: Long,
    val rule_id: Long,
    var av_area_id: Int,
    var day_delay: Int,
    var hour_max: Int,
    var hour_min: Int,
    var pattern_id: Int
)