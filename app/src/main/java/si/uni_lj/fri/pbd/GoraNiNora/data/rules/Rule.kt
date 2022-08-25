package si.uni_lj.fri.pbd.GoraNiNora.data.rules

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rule_table")
data class Rule (
    @PrimaryKey(autoGenerate = true)
    val rule_id: Long,
    val aspect: String?,
    val min_slope: Double?,
    val max_slope: Double?,
    val elevation_min: Double?,
    val elevation_max: Double?,
    val hour_min: Int?,
    val hour_max: Int?,
    val user_hiking: Boolean, // if notification is shown when user is hiking, otherwise notification can be shown anytime,
    val av_area_id: Int?, // if user_hiking == true, then av_area_id == null
    val notification_name: String,
    val notification_text: String
)