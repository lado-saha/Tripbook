package tech.xken.tripbook.data.sources.booker

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount

interface BookerDataSource {
    suspend fun createBooker(booker: Booker): Results<Booker?>
    suspend fun updateBooker(booker: Booker): Results<Booker?>
    suspend fun bookerFromId(bookerId: String, columns: List<String> = listOf()): Results<Booker>
    suspend fun deleteBooker(bookerId: String): Results<Booker?>

    fun bookerMoMoAccountsStream(bookerId: String): Flow<Results<List<BookerMoMoAccount>>>
    suspend fun bookerMoMoAccounts(bookerId: String): Results<List<BookerMoMoAccount>>
    fun countBookerMoMoAccountsStream(bookerId: String): Flow<Results<Long>>
    suspend fun countBookerMoMoAccounts(bookerId: String): Results<Long>
    suspend fun createBookerMoMoAccounts(accounts: List<BookerMoMoAccount>): Results<List<BookerMoMoAccount?>>
    suspend fun updateBookerMoMoAccount(account: BookerMoMoAccount): Results<BookerMoMoAccount?>
    suspend fun deleteBookerMoMoAccounts(
        bookerId: String,
        phoneNumbers: List<String>
    ): Results<Unit>

    fun bookerOMAccountsStream(bookerId: String): Flow<Results<List<BookerOMAccount>>>
    suspend fun bookerOMAccounts(bookerId: String): Results<List<BookerOMAccount>>
    fun countBookerOMAccountsStream(bookerId: String): Flow<Results<Long>>
    suspend fun countBookerOMAccounts(bookerId: String): Results<Long>
    suspend fun createBookerOMAccounts(accounts: List<BookerOMAccount>): Results<List<BookerOMAccount?>>
    suspend fun updateBookerOMAccount(account: BookerOMAccount): Results<BookerOMAccount?>
    suspend fun deleteBookerOMAccounts(bookerId: String, phoneNumbers: List<String>): Results<Unit>
}