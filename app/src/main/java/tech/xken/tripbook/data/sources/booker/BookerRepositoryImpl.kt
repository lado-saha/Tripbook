package tech.xken.tripbook.data.sources.booker

import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount
import tech.xken.tripbook.domain.NetworkState
import tech.xken.tripbook.ui.CacheSyncUiState

class BookerRepositoryImpl(
    private val localDS: BookerDataSource,
    private val remoteDS: BookerDataSource,
    private val authRepo: AuthRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkState: NetworkState,
) : BookerRepository {

    //    private val momoAccounts = channel.postgresChangeFlow<PostgresAction.Insert>("sc_booker") {
//        table = "booker_momo_account"
//    }.map {
//        it.serializer.decode(it.record.jsonObject)
//    }
    override suspend fun syncCache(
        onAccountComplete: (Results<Booker?>) -> Unit,
        onMoMoAccountComplete: (Results<List<BookerMoMoAccount?>>) -> Unit,
        onOMAccountComplete: (Results<List<BookerOMAccount?>>) -> Unit,
        syncUis: CacheSyncUiState
    ) {
        if (!syncUis.syncAccountDone && syncUis.hasSyncAccount != true) onAccountComplete(
            remoteDS.bookerFromId(authRepo.bookerId!!).run {
                when (this) {
                    is Failure -> Failure(exception)
                    is Success -> {
                        localDS.createBooker(data)
                    }
                }
            }
        )

        if (!syncUis.syncOMDone && syncUis.hasSyncOM != true) onOMAccountComplete(
            remoteDS.bookerOMAccounts(authRepo.bookerId!!).run {
                when (this) {
                    is Failure -> Failure(exception)
                    is Success -> {
                        localDS.createBookerOMAccounts(data)
                    }
                }
            }
        )

        if (!syncUis.syncMoMoDone && syncUis.hasSyncMoMo != true) onMoMoAccountComplete(
            remoteDS.bookerMoMoAccounts(authRepo.bookerId!!).run {
                when (this) {
                    is Failure -> Failure(exception)
                    is Success -> {
                        localDS.createBookerMoMoAccounts(data)
                    }
                }
            }
        )

    }

    override suspend fun createBooker(booker: Booker) =
        when (val rBooker = remoteDS.createBooker(booker)) {
            is Success -> {
                localDS.createBooker(rBooker.data!!)
                rBooker
            }

            else -> rBooker
        }

    override suspend fun updateBooker(booker: Booker) =
        when (val rBooker = remoteDS.updateBooker(booker)) {
            is Success -> {
                localDS.updateBooker(rBooker.data!!)
                rBooker
            }

            is Failure -> rBooker
        }

    override suspend fun bookerFromId(bookerId: String, columns: List<String>) =
        localDS.bookerFromId(bookerId)

    override suspend fun deleteBooker(bookerId: String) =
        when (val rBooker = remoteDS.deleteBooker(bookerId)) {
            is Success -> {
                localDS.deleteBooker(bookerId)
                rBooker
            }

            else -> rBooker
        }

    override suspend fun createBookerMoMoAccounts(accounts: List<BookerMoMoAccount>) =
        when (val result = remoteDS.createBookerMoMoAccounts(accounts)) {
            is Success -> {
                localDS.createBookerMoMoAccounts(accounts)
                result
            }

            else -> result
        }

    override suspend fun createBookerOMAccounts(accounts: List<BookerOMAccount>) =
        when (val result = remoteDS.createBookerOMAccounts(accounts)) {
            is Success -> {
                localDS.createBookerOMAccounts(accounts)
                result
            }

            else -> result
        }

    override suspend fun updateBookerMoMoAccount(account: BookerMoMoAccount) =
        when (val result = remoteDS.updateBookerMoMoAccount(account)) {
            is Success -> {
                localDS.updateBookerMoMoAccount(result.data!!)
                result
            }

            else -> result
        }

    override suspend fun updateBookerOMAccount(account: BookerOMAccount) =
        when (val result = remoteDS.updateBookerOMAccount(account)) {
            is Success -> {
                localDS.updateBookerOMAccount(account)
                result
            }

            else -> result
        }

    override fun countBookerMoMoAccounts(bookerId: String) =
        localDS.countBookerMoMoAccountsStream(bookerId)

    override fun countBookerOMAccounts(bookerId: String) =
        localDS.countBookerOMAccountsStream(bookerId)

    override fun bookerMoMoAccounts(bookerId: String) =
        localDS.bookerMoMoAccountsStream(bookerId)

    override fun bookerOMAccounts(bookerId: String) =
        localDS.bookerOMAccountsStream(bookerId)

    override suspend fun deleteBookerMoMoAccounts(
        bookerId: String, phoneNumbers: List<String>
    ) = when (val result = remoteDS.deleteBookerMoMoAccounts(bookerId, phoneNumbers)) {
        is Success -> {
            localDS.deleteBookerMoMoAccounts(bookerId, phoneNumbers)
            result
        }

        else -> result
    }

    override suspend fun deleteBookerOMAccounts(
        bookerId: String, phoneNumbers: List<String>
    ) = when (val result = remoteDS.deleteBookerOMAccounts(bookerId, phoneNumbers)) {
        is Success -> {
            localDS.deleteBookerOMAccounts(bookerId, phoneNumbers)
            result
        }

        else -> result
    }


}