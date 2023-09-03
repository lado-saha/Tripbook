package tech.xken.tripbook.data.sources.booker

import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount
import tech.xken.tripbook.ui.CacheSyncUiState

interface BookerRepository {
    suspend fun createBooker(booker: Booker): Results<Booker?>
    suspend fun updateBooker(booker: Booker): Results<Booker?>
    suspend fun bookerFromId(bookerId: String, columns: List<String> = listOf()): Results<Booker?>
    suspend fun deleteBooker(bookerId: String): Results<Booker?>

    fun bookerMoMoAccounts(bookerId: String): Flow<Results<List<BookerMoMoAccount>>>

    //    suspend fun bookerMoMoAccounts(bookerId: String): Results<List<BookerMoMoAccount>>
    fun countBookerMoMoAccounts(bookerId: String): Flow<Results<Long>>
    suspend fun createBookerMoMoAccounts(accounts: List<BookerMoMoAccount>): Results<List<BookerMoMoAccount?>>
    suspend fun updateBookerMoMoAccount(account: BookerMoMoAccount): Results<BookerMoMoAccount?>
    suspend fun deleteBookerMoMoAccounts(
        bookerId: String,
        phoneNumbers: List<String>
    ): Results<Unit>

    fun bookerOMAccounts(bookerId: String): Flow<Results<List<BookerOMAccount>>>

    //    suspend fun bookerOMAccounts(bookerId: String): Results<List<BookerOMAccount>>
    fun countBookerOMAccounts(bookerId: String): Flow<Results<Long>>
    suspend fun createBookerOMAccounts(accounts: List<BookerOMAccount>): Results<List<BookerOMAccount?>>
    suspend fun updateBookerOMAccount(account: BookerOMAccount): Results<BookerOMAccount?>
    suspend fun deleteBookerOMAccounts(bookerId: String, phoneNumbers: List<String>): Results<Unit>

    suspend fun syncCache(
        onAccountComplete: (Results<Booker?>) -> Unit,
        onMoMoAccountComplete: (Results<List<BookerMoMoAccount?>>) -> Unit,
        onOMAccountComplete: (Results<List<BookerOMAccount?>>) -> Unit,
        syncUis: CacheSyncUiState,
    )
}