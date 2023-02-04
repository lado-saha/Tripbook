package tech.xken.tripbook.data.sources.booking

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Results

interface BookingDataSource {
    suspend fun saveBooker(booker: Booker)
    suspend fun bookers(): Results<List<Booker>>
    suspend fun bookersFromIds(ids: List<String>): Results<List<Booker>>
    suspend fun bookersFromPhones(phones: List<String>): Results<List<Booker>>
    suspend fun bookerFromEmailCredentials(email: String, password: String): Results<Booker>
    suspend fun bookerFromNameCredentials(name: String, password: String): Results<Booker>
    suspend fun bookerFromPhoneCredentials(phone: String, password: String): Results<Booker>

    //Current booker
    suspend fun signInBooker(currentBooker: CurrentBooker)
    suspend fun signOutBooker()
    fun getCurrentBookerStream(): Flow<Results<CurrentBooker?>>

}