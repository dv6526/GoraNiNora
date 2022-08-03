package si.uni_lj.fri.pbd.sensecontext.data

import androidx.room.Embedded
import androidx.room.Relation

data class RuleWithLists (
    @Embedded val rule: Rule,
    @Relation(
        parentColumn = "rule_id",
        entityColumn = "rule_id"
    )

    val weather_descriptions: List<WeatherDescription>,
    @Relation(
        parentColumn = "rule_id",
        entityColumn = "rule_id"
    )
    val patternRules: List<PatternRule>,
    @Relation(
        parentColumn = "rule_id",
        entityColumn = "rule_id"
    )
    val problemRules: List<ProblemRule>,
    @Relation(
        parentColumn = "rule_id",
        entityColumn = "rule_id"
    )
    val dangerRules: List<DangerRule>
)