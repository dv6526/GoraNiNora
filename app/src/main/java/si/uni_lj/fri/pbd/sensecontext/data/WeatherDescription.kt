package si.uni_lj.fri.pbd.sensecontext.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity(tableName = "weather_description_table")
data class WeatherDescription(
        @PrimaryKey(autoGenerate = true)
        val weather_description_id: Long,
        val rule_id: Long,
        val day_delay: Int,
        val temp_avg_min: Double?,
        val temp_avg_max: Double?,
        val hour_min: Int,
        val hour_max: Int,
        val oblacnost: String?,
        val vremenski_pojav: String?,
        val intenzivnost: String?,
        val elevation: String?
)
