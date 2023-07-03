package tech.xken.tripbook.data.sources.caches.remote

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.UnivSelection
import tech.xken.tripbook.data.sources.caches.CachesDataSource

object CachesRemoteDataSource: CachesDataSource {
    override suspend fun univSelections(
        fromScreen: String,
        toScreen: String
    ): Results<List<UnivSelection>> {
        TODO("Not yet implemented")
    }

    override fun observeUnivSelections(
        fromScreen: String,
        toScreen: String
    ): Flow<Results<List<UnivSelection>>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUnivSelections(selection: List<UnivSelection>) {
        TODO("Not yet implemented")
    }

    override suspend fun clearUnivSelections() {
        TODO("Not yet implemented")
    }

}