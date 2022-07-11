package si.uni_lj.fri.pbd.sensecontext.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RuleWithWeatherDescription (
    @Embedded val rule: Rule,
    @Relation(
        parentColumn = "rule_id",
        entityColumn = "weather_description_id",
        associateBy = Junction(
            value = RuleWeatherDescriptionRef::class,
            parentColumn = "rId",
            entityColumn = "wId"
        )
    )
    val weather_descriptions: List<WeatherDescription>
)