package tech.xken.tripbook.data.sources.booking.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import tech.xken.tripbook.data.models.Booker

@Dao
interface BookingDao {
    //Booker
    @Insert(onConflict = REPLACE)
    fun saveBooker(booker: Booker)

    @Query("SELECT * FROM Bookers")
    fun booker(): List<Booker>

    @Query("SELECT * FROM Bookers WHERE id IN (:ids)")
    fun bookersFromIDs(ids: List<String>): List<Booker>

    @Query("SELECT * FROM Bookers WHERE phone IN (:phones)")
    fun bookersFromPhones(phones: List<String>): List<Booker>

    @Query("SELECT * FROM Bookers WHERE phone IN (:names)")
    fun bookersFromName(names: List<String>): List<Booker>

    @Query("SELECT * FROM Bookers WHERE email = :email AND password = :password")
    fun bookerFromEmailCredentials(email: String, password: String)

    @Query("SELECT * FROM Bookers WHERE phone = :phone AND password =:password")
    fun bookerFromPhoneCredentials(phone: String, password: String)
}