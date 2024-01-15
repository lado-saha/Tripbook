package tech.xken.tripbook.data.sources.booker.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount
import tech.xken.tripbook.data.sources.booker.BookerDataSource
import kotlin.coroutines.CoroutineContext

class BookerLocalDataSource internal constructor(
    private val dao: BookerDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : BookerDataSource {

    override suspend fun createBooker(booker: Booker) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.createBooker(booker).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateBooker(booker: Booker) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.updateBooker(booker).run { null })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun bookerFromId(
        bookerId: String,
        columns: List<String>
    ): Results<Booker?> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.bookerFromId(bookerId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteBooker(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.deleteBooker(bookerId).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }


    override suspend fun createBookerMoMoAccounts(accounts: List<BookerMoMoAccount>): Results<List<BookerMoMoAccount?>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.createBookerMoMoAccounts(accounts).run { listOf() })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun createBookerOMAccounts(accounts: List<BookerOMAccount>) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.createBookerOMAccount(accounts).run { listOf(null) })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateBookerMoMoAccount(account: BookerMoMoAccount) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.updateBookerMoMoAccount(account).run { null })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateBookerOMAccount(account: BookerOMAccount) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.updateBookerOMAccount(account).run { null })
            } catch (e: Exception) {
                Failure(e)
            }
        }


    override fun countBookerMoMoAccountsStream(bookerId: String) =
        dao.countBookerMoMoAccounts(bookerId).map {
            try {
                Success(it)
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countBookerMoMoAccounts(bookerId: String) =
        TODO("Not yet implemented")


    override fun countBookerOMAccountsStream(bookerId: String) =
        dao.countBookerOMAccounts(bookerId).map {
            try {
                Success(it)
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun countBookerAccountStream(bookerId: String) =
        dao.countBookerAccount(bookerId).map {
            try {
                Success(it)
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countBookerOMAccounts(bookerId: String) =
        TODO("Not yet implemented")

    override suspend fun countBookerAccount(bookerId: String) =
        TODO("Not yet implemented")

    override fun bookerMoMoAccountsStream(bookerId: String) =
        dao.bookerMoMoAccounts(bookerId).map {
            try {
                Success(it)
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun bookerMoMoAccounts(bookerId: String): Results<List<BookerMoMoAccount>> {
        TODO("Not yet implemented")
    }


    override fun bookerOMAccountsStream(bookerId: String) =
        dao.bookerOMAccounts(bookerId).map {
            try {
                Success(it)
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun bookerOMAccounts(bookerId: String): Results<List<BookerOMAccount>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBookerMoMoAccounts(
        bookerId: String,
        phoneNumbers: List<String>
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.deleteBookerMoMoAccounts(bookerId, phoneNumbers))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteBookerOMAccounts(
        bookerId: String,
        phoneNumbers: List<String>
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.deleteBookerOMAccounts(bookerId, phoneNumbers))
        } catch (e: Exception) {
            Failure(e)
        }
    }

}