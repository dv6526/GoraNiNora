package si.uni_lj.fri.pbd.sensecontext.data.bulletin

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "problem_bulletin_table")
data class ProblemBulletin(
    @PrimaryKey(autoGenerate = true)
    var prob_id: Long,
    var av_bulletin_id: Long,
    var av_area_id: Int,
    var elevation_from: Int,
    var elevation_to: Int,
    var valid_end: Date,
    var valid_start: Date
)