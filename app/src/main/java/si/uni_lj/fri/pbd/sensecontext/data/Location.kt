package si.uni_lj.fri.pbd.sensecontext.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "location_table")
data class Location (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Date,
    val lon: Double,
    val lat: Double,
    val slope: Double,
    val elevation: Double,
    val aspect: Double
)