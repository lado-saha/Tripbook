package tech.xken.tripbook.data.sources.booking.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.Book
import tech.xken.tripbook.data.models.Booker

@Database(entities = [Booker::class], version = 1, exportSchema = false)
abstract class BookingLocalDatabase: RoomDatabase() {
    abstract val dao: BookingDao
}