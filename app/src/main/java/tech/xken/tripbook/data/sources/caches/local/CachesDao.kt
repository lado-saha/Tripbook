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
    @Query("SELECT * FROM UnivSelections WHERE from_screen = :fromScreen AND to_screen = :toScreen")
    fun univSelections(fromScreen: String, toScreen: String): List<UnivSelection>

    @Query("SELECT * FROM UnivSelections WHERE from_screen = :fromScreen AND to_screen = :toScreen")
    fun observeUnivSelections(fromScreen: String, toScreen: String): Flow<List<UnivSelection>>

    @Insert(onConflict = REPLACE)
    fun saveUnivSelections(selections: List<UnivSelection>)

    @Query("DELETE FROM UnivSelections")
    fun clearUnivSelections()
}