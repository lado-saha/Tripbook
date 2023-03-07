package tech.xken.tripbook.data.sources.caches.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.UnivSelection
import tech.xken.tripbook.data.sources.caches.CachesDataSource
import kotlin.coroutines.CoroutineContext

class CachesLocalDataSourceImpl(
    private val dao: CachesDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : CachesDataSource {
    override fun univSelections() =
        dao.observeUnivSelections().map { Results.Success(it) }

    override suspend fun saveUnivSelections(selection: List<UnivSelection>) =
        withContext(ioDispatcher) {
            dao.saveUnivSelections(selection)
        }

    override suspend fun clearUnivSelections() = withContext(ioDispatcher){
        dao.clearUnivSelections()
    }
}