package si.uni_lj.fri.pbd.sensecontext.data.bulletin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avalanche_bulletin_table")
data class AvalancheBulletin(
    @PrimaryKey
    val av_bulletin_id: Long,
    val danger_desc: String,
    val snow_conditions_tendency: String,
    val weather_evolution: String,
    val snow_conditions: String
)