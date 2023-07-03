package tech.xken.tripbook.data.sources.booker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Booker
//import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Results
import java.util.*

class BookerRepositoryImpl(
    private val localDataSource: BookerDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : BookerRepository {

//    override suspend fun saveCurrentBooker(currentBooker: CurrentBooker) =
//        localDataSource.signInBooker(currentBooker)

    override suspend fun signOutBooker() = localDataSource.signOutBooker()

//    override fun getCurrentBookerStream(): Flow<Results<CurrentBooker?>> = localDataSource.getCurrentBookerStream()

    override suspend fun saveBooker(booker: Booker) = localDataSource.saveBooker(booker)

    override suspend fun bookers() = localDataSource.bookers()

    override suspend fun bookersFromIds(ids: List<String>) = localDataSource.bookersFromIds(ids)

    override suspend fun bookersFromPhones(phones: List<String>) =
        localDataSource.bookersFromPhones(phones)

    override suspend fun signInBookerFromNameCredentials(name: String, password: String) {
        TODO("Not yet implemented")
    }

    /*override suspend fun signInBookerFromEmailCredentials(email: String, password: String) =
        localDataSource.bookerFromEmailCredentials(email, password)
    override suspend fun signInBookerFromPhoneCredentials(phone: String, password: String) =
        localDataSource.bookerFromPhoneCredentials(phone, password)*/

    /**
     * We could logout first in case there is any current user which is not required as the signIn screen
     * only shows after logout signOutBooker()
     * Then we get the booker which matches the credentials
     */
//    override suspend fun signInBookerFromNameCredentials(name: String, password: String) =
//        when (val booker = localDataSource.bookerFromNameCredentials(name, password)) {
//            //If the user exists, we save it as the current booker
//            is Results.Success -> {
//                saveCurrentBooker(CurrentBooker(id = booker.data.id, signInTimestamp = Date().time))
//            }
//            //Else we throw the exception and cancel the signIn process. One cause may be the fact that
//            // this booker has not yet created an account
//            is Results.Failure -> {
//                throw booker.exception
//            }
//        }

}