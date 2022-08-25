package si.uni_lj.fri.pbd.GoraNiNora.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "location_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Date,
    val lon: Double,
    val lat: Double,
    val slope: Double?,
    val elevation: Double,
    val aspect: Double?
)