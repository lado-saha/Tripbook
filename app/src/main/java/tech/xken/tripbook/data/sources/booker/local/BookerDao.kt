package tech.xken.tripbook.data.sources.booker.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Booker


@Dao
interface BookerDao {
    //Current Booker
//    @Query("SELECT * FROM CurrentBookers")
//    fun currentBooker(): Flow<CurrentBooker?>
//
//    @Query("DELETE FROM CurrentBookers")
//    fun deleteCurrentBooker()
//
//    @Insert(onConflict = REPLACE)
//    fun saveCurrentBooker(booker: CurrentBooker)

    //Booker
    @Insert(onConflict = REPLACE)
    fun saveBooker(booker: Booker)

    @Query("SELECT * FROM Bookers")
    fun bookers(): List<Booker>

    @Query("SELECT * FROM Bookers WHERE id IN (:ids)")
    fun bookersFromIDs(ids: List<String>): List<Booker>

    @Query("SELECT * FROM Bookers WHERE name IN (:phones)")
    fun bookersFromPhones(phones: List<String>): List<Booker>

    @Query("SELECT * FROM Bookers WHERE name IN (:names)")
    fun bookersFromName(names: List<String>): List<Booker>

//    @Query("SELECT * FROM Bookers WHERE email = :email AND password = :password")
//    fun bookerFromEmailCredentials(email: String, password: String): Booker
//
//    @Query("SELECT * FROM Bookers WHERE name = :name AND password = :password")
//    fun bookerFromNameCredentials(name: String, password: String): Booker
//
//    @Query("SELECT * FROM Bookers WHERE phone = :phone AND password =:password")
//    fun bookerFromPhoneCredentials(phone: String, password: String): Booker
}