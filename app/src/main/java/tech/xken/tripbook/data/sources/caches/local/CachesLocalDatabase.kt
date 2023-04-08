package tech.xken.tripbook.data.sources.caches.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.UnivSelection

@Database(entities = [UnivSelection::class], version = 2, exportSchema = false)
abstract class CachesLocalDatabase: RoomDatabase() {

    abstract val dao: CachesDao
}