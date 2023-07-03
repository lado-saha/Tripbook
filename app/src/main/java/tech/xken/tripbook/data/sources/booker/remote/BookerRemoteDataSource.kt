package tech.xken.tripbook.data.sources.booker.remote


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Book
import tech.xken.tripbook.data.models.Booker

import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.sources.booker.BookerDataSource
import kotlin.coroutines.CoroutineContext

object BookerRemoteDataSource: BookerDataSource {
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
//    private lateinit var client: SupabaseClient
//    var db = client.postgrest

    override suspend fun saveBooker(booker: Booker): Unit = withContext(ioDispatcher) {
        try {
            Results.Success(
                Unit
            )
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    override suspend fun bookers(): Results<List<Booker>> {
        TODO("Not yet implemented")
    }

    override suspend fun bookersFromIds(ids: List<String>): Results<List<Booker>> {
        TODO("Not yet implemented")
    }

    override suspend fun bookersFromPhones(phones: List<String>): Results<List<Booker>> {
        TODO("Not yet implemented")
    }

    override suspend fun bookerFromEmailCredentials(
        email: String,
        password: String
    ): Results<Booker> {
        TODO("Not yet implemented")
    }

    override suspend fun bookerFromNameCredentials(
        name: String,
        password: String
    ): Results<Booker> {
        TODO("Not yet implemented")
    }

    override suspend fun bookerFromPhoneCredentials(
        phone: String,
        password: String
    ): Results<Booker> {
        TODO("Not yet implemented")
    }

//    override suspend fun signInBooker(currentBooker: CurrentBooker) {
//        TODO("Not yet implemented")
//    }

    override suspend fun signOutBooker() {
        TODO("Not yet implemented")
    }

//    override fun getCurrentBookerStream(): Flow<Results<CurrentBooker?>> {
//        TODO("Not yet implemented")
//    }
}