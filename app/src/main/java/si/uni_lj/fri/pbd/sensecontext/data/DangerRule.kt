package si.uni_lj.fri.pbd.sensecontext.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "danger_rule_table")
data class DangerRule(
    @PrimaryKey(autoGenerate = true)
    var dang_id: Long,
    val rule_id: Long,
    var av_area_id: Int,
    var check_elevation: Boolean,
    var day_delay: Int,
    var hour_max: Int,
    var hour_min: Int,
    var value: Int
)