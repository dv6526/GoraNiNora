package si.uni_lj.fri.pbd.sensecontext.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*
@Entity(tableName = "weather_table", indices = arrayOf(Index(value = ["date", "obmocje"], unique = true)))
data class WeatherHour (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "date") val date: Date,
    val oblacnost: String,
    val vremenski_pojav: String?,
    val intenzivnost: String?,
    val t_500: Int,
    val t_1000: Int,
    val t_1500: Int,
    val t_2000: Int,
    val t_2500: Int,
    val t_3000: Int,
    val meja_snezenja: Int,
    val w_500: Int,
    val w_1000: Int,
    val w_1500: Int,
    val w_2000: Int,
    val w_2500: Int,
    val w_3000: Int,
    val obmocje: String
)