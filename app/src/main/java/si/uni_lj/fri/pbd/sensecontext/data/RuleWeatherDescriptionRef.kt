package si.uni_lj.fri.pbd.sensecontext.data;

import androidx.room.Entity;

@Entity(primaryKeys = ["rId", "wId"], tableName = "rule_with_weather_description_ref")
data class RuleWeatherDescriptionRef (
    val rId: Long,
    val wId: Long
)
