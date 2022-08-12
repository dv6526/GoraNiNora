package si.uni_lj.fri.pbd.sensecontext.data.rules

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "problem_rule_table")
data class ProblemRule(
    @PrimaryKey(autoGenerate = true)
    var prob_id: Long,
    val rule_id: Long,
    var av_area_id: Int?,
    var check_elevation: Boolean,
    var day_delay: Int,
    var hour_max: Int,
    var hour_min: Int,
    var problem_type: Int
)