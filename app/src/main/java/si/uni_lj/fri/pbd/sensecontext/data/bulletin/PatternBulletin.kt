package si.uni_lj.fri.pbd.sensecontext.data.bulletin

import androidx.room.Entity
import androidx.room.PrimaryKey
import si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin.Pattern
import java.util.*

@Entity(tableName = "pattern_bulletin_table")
data class PatternBulletin(
    @PrimaryKey(autoGenerate = true)
    var patt_id: Long,
    var av_bulletin_id: Long,
    var pattern: Int,
    var av_area_id: Int,
    var valid_end: Date,
    var valid_start: Date
)