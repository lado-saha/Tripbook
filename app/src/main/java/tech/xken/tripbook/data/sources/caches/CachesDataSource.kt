package tech.xken.tripbook.data.sources.caches

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.UnivSelection

interface CachesDataSource {

    suspend fun univSelections(fromScreen: String, toScreen: String): Results<List<UnivSelection>>//Univ Search results
    fun observeUnivSelections(fromScreen: String, toScreen: String): Flow<Results<List<UnivSelection>>>
    suspend fun saveUnivSelections(selection: List<UnivSelection>)
    suspend fun clearUnivSelections()
}