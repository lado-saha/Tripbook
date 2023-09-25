//package tech.xken.tripbook.data.sources.booker
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import tech.xken.tripbook.data.models.Results.*
//import tech.xken.tripbook.data.models.offline_logs.BookingLogs
//import tech.xken.tripbook.data.models.offline_logs.BookingLogs.*
//import kotlin.coroutines.CoroutineContext
//
//@Dao
//interface LogBookingDao {
//    //    Booker
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun logBooker(booker: LogBooker)
//
//    @Query("SELECT * FROM log_booker WHERE op_type = 'CREATE'")
//    fun toCreateBookers(): List<LogBooker>
//
//    @Query("SELECT * FROM log_booker WHERE op_type = 'UPDATE'")
//    fun toDeleteBookers(): List<LogBooker>
//
//    @Query("SELECT * FROM log_booker WHERE op_type = 'DELETE'")
//    fun toUpdateBookers(): List<LogBooker>
//
//    //    Momo account
//    fun logBookerMoMoAccount(booker: LogBookerMoMoAccount)
//
//    @Query("SELECT * FROM log_booker_momo_account WHERE op_type = 'CREATE'")
//    fun toCreateBookerMoMoAccount(): List<LogBookerMoMoAccount>
//
//    @Query("SELECT * FROM log_booker_momo_account WHERE op_type = 'UPDATE'")
//    fun toDeleteBookerMoMoAccount(): List<LogBookerMoMoAccount>
//
//    @Query("SELECT * FROM log_booker_momo_account WHERE op_type = 'DELETE'")
//    fun toUpdateBookerMoMoAccount(): List<LogBookerOMAccount>
//
//    //    Momo account
//    fun logBookerOMAccount(booker: LogBookerOMAccount)
//
//    @Query("SELECT * FROM log_booker_om_account WHERE op_type = 'CREATE'")
//    fun toCreateBookerOMAccount(): List<LogBookerOMAccount>
//
//    @Query("SELECT * FROM log_booker_om_account WHERE op_type = 'UPDATE'")
//    fun toDeleteBookerOMAccount(): List<LogBookerOMAccount>
//
//    @Query("SELECT * FROM log_booker_om_account WHERE op_type = 'DELETE'")
//    fun toUpdateBookerOMAccount(): List<LogBookerOMAccount>
//
//}
//
//class LogBookingDataSource internal constructor(
//    private val dao: LogBookingDao,
//    private val ioDispatcher: CoroutineContext = Dispatchers.IO
//) {
//    //    Booker
//    suspend fun logBooking(log: BookingLogs) = withContext(ioDispatcher) {
//        return@withContext try {
//            val result = when (log) {
//                is LogBooker -> {
//                    dao.logBooker(log)
//                }
//
//                is LogBookerMoMoAccount -> {
//                    dao.logBookerMoMoAccount(log)
//                }
//
//                is LogBookerOMAccount -> {
//                    dao.logBookerOMAccount(log)
//                }
//            }
//            Success(result)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    suspend fun BookingLogs.toCreateLogs() = withContext(ioDispatcher) {
//        return@withContext try {
//            val results = when (this@toCreateLogs) {
//                is LogBooker -> {
//                    dao.toCreateBookers()
//                }
//
//                is LogBookerMoMoAccount -> {
//                    dao.toCreateBookerMoMoAccount()
//                }
//
//                is LogBookerOMAccount -> {
//                    dao.toCreateBookerOMAccount()
//                }
//            }
//            Success(results)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    suspend fun BookingLogs.toUpdateLogs() = withContext(ioDispatcher) {
//        return@withContext try {
//            val results = when (this@toUpdateLogs) {
//                is LogBooker -> {
//                    dao.toUpdateBookers()
//                }
//
//                is LogBookerMoMoAccount -> {
//                    dao.toUpdateBookerMoMoAccount()
//                }
//
//                is LogBookerOMAccount -> {
//                    dao.toUpdateBookerOMAccount()
//                }
//            }
//            Success(results)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    suspend fun BookingLogs.toDeleteLogs() = withContext(ioDispatcher) {
//        return@withContext try {
//            val results = when (this@toDeleteLogs) {
//                is LogBooker -> {
//                    dao.toDeleteBookers()
//                }
//
//                is LogBookerMoMoAccount -> {
//                    dao.toDeleteBookerMoMoAccount()
//                }
//
//                is LogBookerOMAccount -> {
//                    dao.toUpdateBookerOMAccount()
//                }
//            }
//            Success(results)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//
//}