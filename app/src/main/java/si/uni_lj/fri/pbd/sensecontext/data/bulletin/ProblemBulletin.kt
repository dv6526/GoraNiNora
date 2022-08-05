package si.uni_lj.fri.pbd.sensecontext.data.bulletin

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "problem_bulletin_table")
data class ProblemBulletin(
    @PrimaryKey(autoGenerate = true)
    var prob_id: Long,
    var av_bulletin_id: Long,
    var problem: Int,
    var av_area_id: Int,
    var elevation_from: Double,
    var elevation_to: Double,
    var valid_end: Date,
    var valid_start: Date
)