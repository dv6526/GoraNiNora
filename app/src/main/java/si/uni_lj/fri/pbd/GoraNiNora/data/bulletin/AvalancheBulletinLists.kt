package si.uni_lj.fri.pbd.GoraNiNora.data.bulletin

import androidx.room.Embedded
import androidx.room.Relation
import si.uni_lj.fri.pbd.GoraNiNora.data.rules.DangerRule

data class AvalancheBulletinLists (
    @Embedded val avalancheBulletin: AvalancheBulletin,
    @Relation(
            parentColumn = "av_bulletin_id",
            entityColumn = "av_bulletin_id"
    )
    val dangers: List<DangerRule>
)