package tech.xken.tripbook.data.sources.caches.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.UnivSelection
import tech.xken.tripbook.data.sources.caches.CachesDataSource
import kotlin.coroutines.CoroutineContext

class CachesLocalDataSourceImpl(
    private val dao: CachesDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : CachesDataSource {
    override suspend fun univSelections(fromScreen: String, toScreen: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.univSelections(fromScreen, toScreen))
            } catch (e: Exception) {
                Results.Failure(e)
            }
        }

    override fun observeUnivSelections(fromScreen: String, toScreen: String) =
        dao.observeUnivSelections(fromScreen, toScreen).map { Success(it) }

    override suspend fun saveUnivSelections(selection: List<UnivSelection>) =
        withContext(ioDispatcher) {
            dao.saveUnivSelections(selection)
        }

    override suspend fun clearUnivSelections() = withContext(ioDispatcher) {
        dao.clearUnivSelections()
    }
}