package si.uni_lj.fri.pbd.sensecontext.data;

import androidx.room.Entity;

@Entity(primaryKeys = ["rId", "wId"])
data class RuleWeatherDescriptionRef (
    val rId: Long,
    val wId: Long
)
