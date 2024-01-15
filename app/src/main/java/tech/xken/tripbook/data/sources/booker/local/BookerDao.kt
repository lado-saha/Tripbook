package tech.xken.tripbook.data.sources.booker.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount

@Dao
interface BookerDao {
    @Insert(onConflict = REPLACE)
    fun createBooker(booker: Booker)

    @Update
    fun updateBooker(booker: Booker)

    @Query("SELECT * FROM booker WHERE booker_id = :id")
    fun bookerFromId(id: String): Booker?

    @Query("DELETE FROM booker WHERE booker_id = :bookerId")
    fun deleteBooker(bookerId: String)

    @Insert(onConflict = REPLACE)
    fun createBookerMoMoAccounts(accounts: List<BookerMoMoAccount>)

    @Update
    fun updateBookerMoMoAccount(account: BookerMoMoAccount)

    @Insert(onConflict = REPLACE)
    fun createBookerOMAccount(accounts: List<BookerOMAccount>)

    @Update
    fun updateBookerOMAccount(account: BookerOMAccount)

    @Query("SELECT COUNT(booker_id) FROM booker WHERE booker_id = :bookerId")
    fun countBookerAccount(bookerId: String): Flow<Long>

    @Query("SELECT COUNT(phone_number) FROM booker_momo_account WHERE booker_id = :bookerId")
    fun countBookerMoMoAccounts(bookerId: String): Flow<Long>

    @Query("SELECT COUNT(phone_number) FROM booker_om_account WHERE booker_id = :bookerId")
    fun countBookerOMAccounts(bookerId: String): Flow<Long>

    @Query("SELECT * FROM booker_momo_account WHERE booker_id = :bookerId")
    fun bookerMoMoAccounts(bookerId: String): Flow<List<BookerMoMoAccount>>

    @Query("SELECT * FROM booker_om_account WHERE booker_id = :bookerId")
    fun bookerOMAccounts(bookerId: String): Flow<List<BookerOMAccount>>

    @Query("DELETE FROM booker_momo_account WHERE booker_id = :bookerId AND phone_number IN (:phoneNumbers)")
    suspend fun deleteBookerMoMoAccounts(bookerId: String, phoneNumbers: List<String>)

    @Query("DELETE FROM booker_om_account WHERE booker_id = :bookerId AND phone_number IN (:phoneNumbers)")
    suspend fun deleteBookerOMAccounts(bookerId: String, phoneNumbers: List<String>)
}

