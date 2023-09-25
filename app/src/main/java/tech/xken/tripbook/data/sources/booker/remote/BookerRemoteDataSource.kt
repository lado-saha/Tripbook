package tech.xken.tripbook.data.sources.booker.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Returning
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.Schema
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount
import tech.xken.tripbook.data.sources.booker.BookerDataSource
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class BookerRemoteDataSource @Inject constructor(
    val ioDispatcher: CoroutineContext,
    val client: SupabaseClient,
) : BookerDataSource {

//    val channel = client.realtime.createChannel("booking")

    private val parser = Json {
        coerceInputValues = true
        encodeDefaults = true
    }

    override suspend fun createBooker(booker: Booker) = withContext(ioDispatcher) {
        try {
            val result = client.postgrest[Schema.BOOKER, Booker.NAME].insert(
                value = booker,
                returning = Returning.REPRESENTATION
            )
            Success(parser.decodeFromJsonElement<Booker>(result.body!!))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateBooker(booker: Booker) =
        withContext(ioDispatcher) {
            try {
                val result = client.postgrest[Schema.BOOKER, Booker.NAME].update(
                    value = booker,
                    returning = Returning.REPRESENTATION
                ) {
                    Booker::bookerId eq booker.bookerId
                }
                Success(parser.decodeFromJsonElement<List<Booker>>(result.body!!)[0])
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun bookerFromId(bookerId: String, columns: List<String>): Results<Booker?> =
        withContext(ioDispatcher) {
            try {
                val jsonRespond = client.postgrest[Schema.BOOKER, Booker.NAME].select(
                    if (columns.isEmpty()) Columns.ALL else Columns.list(columns)
                ) {
                    Booker::bookerId eq UUID.fromString(bookerId)
                }
                try {
                    Success(parser.decodeFromJsonElement<List<Booker>>(jsonRespond.body!!)[0])
                } catch (e: Exception) {
                    Success(null)
                }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteBooker(bookerId: String) = withContext(ioDispatcher) {
        try {
            val result = client.postgrest[Schema.BOOKER, Booker.NAME].delete {
                Booker::bookerId eq bookerId
            }
            Success(parser.decodeFromJsonElement<Booker>(result.body!!))
        } catch (e: Exception) {
            Failure(e)
        }
    }


    override suspend fun bookerMoMoAccounts(bookerId: String) = withContext(ioDispatcher) {
        try {
            val result =
                client.postgrest[Schema.BOOKER, BookerMoMoAccount.NAME].select(Columns.raw("*")) {
                    Booker::bookerId eq bookerId
                }
            Success(parser.decodeFromJsonElement<List<BookerMoMoAccount>>(result.body!!))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    /*    override suspend fun bookerFromPhone(phone: String, columns: List<String>) =
            withContext(ioDispatcher) {
                 try {
                    val bookerColumns = columns.foldIndexed("") { index, acc, s ->
                        if (index == 0) s else "$acc,$s"
                    }
                    Success(
                        parser.decodeFromJsonElement<BookerWithPhone>(
                            client.postgrest["auth", "users"].select(
                                columns = Columns.raw(
                                    """
                                phone,
                                booker: id($bookerColumns)
                                """.trimIndent()
                                )
                            ).body!!
                        )
                    )
                } catch (e: Exception) {
                    Failure(e)
                }
            }*/

    override suspend fun createBookerMoMoAccounts(accounts: List<BookerMoMoAccount>): Results<List<BookerMoMoAccount?>> =
        withContext(ioDispatcher) {
            try {
                val result = client.postgrest[Schema.BOOKER, BookerMoMoAccount.NAME].insert(
                    accounts,
                    upsert = false
                )
                Success(parser.decodeFromJsonElement<List<BookerMoMoAccount>>(result.body!!))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun createBookerOMAccounts(accounts: List<BookerOMAccount>) =
        withContext(ioDispatcher) {
            try {
                val result = client.postgrest[Schema.BOOKER, BookerOMAccount.NAME].insert(
                    accounts,
                    upsert = false
                )
                Success(parser.decodeFromJsonElement<List<BookerOMAccount>>(result.body!!))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateBookerMoMoAccount(account: BookerMoMoAccount) =
        withContext(ioDispatcher) {
            try {
                val result = client.postgrest[Schema.BOOKER, BookerMoMoAccount.NAME].update(account) {
                    BookerMoMoAccount::bookerId eq account.bookerId
                    BookerMoMoAccount::phoneNumber eq account.phoneNumber
                }
                Success(
                    parser.decodeFromJsonElement<List<BookerMoMoAccount>>(result.body!!).first()
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateBookerOMAccount(account: BookerOMAccount) =
        withContext(ioDispatcher) {
            try {
                val result = client.postgrest[Schema.BOOKER, BookerOMAccount.NAME].update(account) {
                    BookerOMAccount::bookerId eq account.bookerId
                    BookerOMAccount::phoneNumber eq account.phoneNumber
                }
                Success(parser.decodeFromJsonElement<List<BookerOMAccount>>(result.body!!).first())
            } catch (e: Exception) {
                Failure(e)
            }
        }


    override suspend fun countBookerMoMoAccounts(bookerId: String) = withContext(ioDispatcher) {
        try {
            val result = client.postgrest[Schema.BOOKER, BookerMoMoAccount.NAME].select(
                Columns.raw("*"),
                true,
                Count.EXACT
            ) {
                Booker::bookerId eq bookerId
            }
            Success(result.count()!!)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countBookerOMAccounts(bookerId: String) = withContext(ioDispatcher) {
        try {
            val result = client.postgrest[Schema.BOOKER, BookerOMAccount.NAME].select(
                Columns.raw("*"),
                true,
                Count.EXACT
            ) {
                Booker::bookerId eq bookerId
            }
            Success(result.count()!!)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countBookerMoMoAccountsStream(bookerId: String) = TODO("Not implemented")
    override fun countBookerOMAccountsStream(bookerId: String) = TODO("Not implemented")

    override fun bookerMoMoAccountsStream(bookerId: String) = TODO("Not implemented")

    override fun bookerOMAccountsStream(bookerId: String) = TODO("Not implemented")

    override suspend fun bookerOMAccounts(bookerId: String) = withContext(ioDispatcher) {
        try {
            val result =
                client.postgrest[Schema.BOOKER, BookerOMAccount.NAME].select(Columns.raw("*")) {
                    Booker::bookerId eq bookerId
                }
            Success(parser.decodeFromJsonElement<List<BookerOMAccount>>(result.body!!))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteBookerMoMoAccounts(
        bookerId: String,
        phoneNumbers: List<String>
    ) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.BOOKER, BookerMoMoAccount.NAME].delete {
                    BookerMoMoAccount::bookerId eq bookerId
                    BookerMoMoAccount::phoneNumber isIn phoneNumbers
                }.run {
                    Success(Unit)
                }

            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteBookerOMAccounts(bookerId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                val result = client.postgrest[Schema.BOOKER, BookerOMAccount.NAME].delete {
                    BookerOMAccount::bookerId eq bookerId
                    BookerOMAccount::phoneNumber isIn phoneNumbers
                }
                Success(Unit)
            } catch (e: Exception) {
                Failure(e)
            }
        }
}