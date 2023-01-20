package tech.xken.tripbook.data.sources.booking

import tech.xken.tripbook.data.models.Booker

interface BookingDataSource {
    suspend fun saveBooker(booker: Booker)
    suspend fun bookers(): Result<List<Booker>>
    suspend fun bookersFromIds(ids: List<String>)
    suspend fun bookersFromPhones(phones: List<String>)
    suspend fun bookerFromEmailCredentials(email: String, password: String)
    suspend fun bookerFromPhoneCredentials(phone: String, password: String)
}