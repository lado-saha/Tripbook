package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.xken.tripbook.data.models.UnivSelection.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class UnivSelection(
    @ColumnInfo(name = PLACE) @PrimaryKey val place: Int,
    @ColumnInfo(name = SELECTION) val selections: String,
    @ColumnInfo(name = CALLER_ROUTE) val callerRoute: String
){
    companion object{
        const val TABLE_NAME = "UnivSelections"
        const val SELECTION = "selections"
        const val PLACE = "place"
        const val CALLER_ROUTE = "caller_route"
    }
}