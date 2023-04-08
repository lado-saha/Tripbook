package tech.xken.tripbook.data.sources.init.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Job
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.booking.BookingDataSource
import tech.xken.tripbook.data.sources.init.InitDataSource
import kotlin.coroutines.CoroutineContext

class InitLocalDataSourceImpl internal constructor(
    private val dao: InitDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : InitDataSource {
    //Current booker
//    override suspend fun bookerFromPhoneCredentials(
    override suspend fun saveJobs(jobs: List<Job>) = withContext(ioDispatcher) {
        dao.saveJobs(jobs)
    }

    override suspend fun jobs(): Results<List<Job>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.jobs())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun jobsFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.jobsFromIds(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }
//        phone: String,
//        password: String,
//    ) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.bookerFromPhoneCredentials(phone, password))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }


}