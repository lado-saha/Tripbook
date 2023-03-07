package tech.xken.tripbook.data.sources.agency.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.Park
import tech.xken.tripbook.data.models.TownParkMap

@Database(entities = [Park::class, TownParkMap::class], version = 1, exportSchema = false)
abstract class AgencyLocalDatabase: RoomDatabase() {
    abstract val dao: AgencyDao
}