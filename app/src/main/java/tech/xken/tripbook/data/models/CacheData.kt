package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.xken.tripbook.data.models.UnivSelection.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class UnivSelection(
    @ColumnInfo(name = PLACE) @PrimaryKey val place: Int,
    @ColumnInfo(name = SELECTION) val selections: String,
    @ColumnInfo(name = FROM_SCREEN) val fromScreen: String,
    @ColumnInfo(name = TO_SCREEN) val toScreen: String,
) {
    companion object {
        const val TABLE_NAME = "UnivSelections"
        const val SELECTION = "selections"
        const val PLACE = "place"
        const val FROM_SCREEN = "from_screen"
        const val TO_SCREEN = "to_screen"
    }
}

enum class Places {
    PLANET, CONTINENT, COUNTRY, REGION, DIVISION, SUBDIVISION, TOWN
}