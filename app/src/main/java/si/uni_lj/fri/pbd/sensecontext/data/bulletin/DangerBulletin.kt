package si.uni_lj.fri.pbd.sensecontext.data.bulletin

import androidx.room.Entity
import androidx.room.PrimaryKey
import si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin.Danger
import java.util.*

@Entity(tableName = "danger_bulletin_table")
data class DangerBulletin(
    @PrimaryKey(autoGenerate = true)
    var dang_id: Long,
    val av_bulletin_id: Long,
    var aspects: String,
    var av_area_id: Int,
    var elevation_from: Int,
    var elevation_to: Int,
    var treeline: Boolean,
    var treeline_above: Boolean,
    var valid_end: Date,
    var valid_start: Date,
    var value: Int
)