package tech.xken.tripbook.data.sources.booking

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Results

interface BookingRepository {
    suspend fun saveBooker(booker: Booker)
    suspend fun bookers(): Results<List<Booker>>
    suspend fun bookersFromIds(ids: List<String>): Results<List<Booker>>
    suspend fun bookersFromPhones(phones: List<String>): Results<List<Booker>>
//    suspend fun signInBookerFromEmailCredentials(email: String, password: String): Results<Booker>
//    suspend fun signInBookerFromPhoneCredentials(phone: String, password: String): Results<Booker>
    suspend fun signInBookerFromNameCredentials(name: String, password: String)

    //Current Booker
    suspend fun saveCurrentBooker(currentBooker: CurrentBooker)
    suspend fun signOutBooker()
    fun getCurrentBookerStream(): Flow<Results<CurrentBooker?>>
    //TODO: Current user
}