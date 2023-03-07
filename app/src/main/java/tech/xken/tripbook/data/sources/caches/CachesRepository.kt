package tech.xken.tripbook.data.sources.caches

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.UnivSelection

interface CachesRepository {
    fun univSelections(): Flow<Results<List<UnivSelection>>>
    suspend fun saveUnivSelections(selections: List<UnivSelection>)
    suspend fun clearUnivSelections()
}