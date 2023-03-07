package tech.xken.tripbook.data.sources.caches

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.UnivSelection

interface CachesDataSource {
    //Univ Search results
    fun univSelections(): Flow<Results<List<UnivSelection>>>
    suspend fun saveUnivSelections(selection: List<UnivSelection>)
    suspend fun clearUnivSelections()
}