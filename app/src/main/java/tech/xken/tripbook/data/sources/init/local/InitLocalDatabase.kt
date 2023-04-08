package tech.xken.tripbook.data.sources.init.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.Job

@Database(entities = [Job::class], version = 2, exportSchema = false)
abstract class InitLocalDatabase: RoomDatabase() {
    abstract val dao: InitDao
}