package tech.xken.tripbook.data.sources.agency.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import kotlin.coroutines.CoroutineContext

class AgencyLocalDataSourceImpl internal constructor(
    private val dao: AgencyDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : AgencyDataSource {
    /**
     * Parks
     */
    override suspend fun savePark(park: Park) = withContext(ioDispatcher) {
        dao.savePark(park)
    }

    override suspend fun parks(): Results<List<Park>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.parks())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun parksFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.parksFromIds(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun townParkMapFromTown(town: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townParkLinksFromTown(town))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun townParkMapFromPark(park: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townParkLinksFromPark(park))
        } catch (e: Exception) {
            Failure(e)
        }
    }

}