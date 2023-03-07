package tech.xken.tripbook.data.sources.caches.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.UnivSelection

@Dao
interface CachesDao {
    // Search Selection from
    @Query("SELECT * FROM UnivSelections")
    fun observeUnivSelections(): Flow<List<UnivSelection>>

    @Insert(onConflict = REPLACE)
    fun saveUnivSelections(selections: List<UnivSelection>)

    @Query("DELETE FROM UnivSelections")
    fun clearUnivSelections()
}