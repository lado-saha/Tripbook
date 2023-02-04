package tech.xken.tripbook.data.sources.booking.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.booking.BookingDataSource
import kotlin.coroutines.CoroutineContext

class BookingLocalDataSourceImpl internal constructor(
    private val dao: BookingDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : BookingDataSource {
    //Current booker
    override fun getCurrentBookerStream() =  dao.currentBooker().map { Success(it) }

    override suspend fun signInBooker(currentBooker: CurrentBooker) = withContext(ioDispatcher) {
        dao.saveCurrentBooker(currentBooker)
    }

    override suspend fun signOutBooker() = withContext(ioDispatcher) {
        dao.deleteCurrentBooker()
    }

    override suspend fun saveBooker(booker: Booker) = withContext(ioDispatcher) {
        dao.saveBooker(booker)
    }

    override suspend fun bookers() = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookers())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun bookersFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookersFromIDs(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun bookersFromPhones(phones: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookersFromPhones(phones))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun bookerFromEmailCredentials(
        email: String,
        password: String,
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookerFromEmailCredentials(email, password))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun bookerFromNameCredentials(
        name: String,
        password: String,
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookerFromNameCredentials(name, password))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun bookerFromPhoneCredentials(
        phone: String,
        password: String,
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookerFromPhoneCredentials(phone, password))
        } catch (e: Exception) {
            Failure(e)
        }
    }


}