package si.uni_lj.fri.pbd.sensecontext.data.rules

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "matched_rule_table")
data class MatchedRule (
    @PrimaryKey(autoGenerate = true)
    var matched_rule_id: Long,
    var rule_id: Long,
    var date: Date,
    var read: Boolean,
    var name: String,
    var text: String,
    var hiking: Boolean,
    var area_id: Int
)