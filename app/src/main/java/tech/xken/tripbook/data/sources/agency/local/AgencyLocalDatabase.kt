package tech.xken.tripbook.data.sources.agency.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.*

@Database(
    entities = [Station::class, StationTownMap::class, StationScannerMap::class, Scanner::class, StationJob::class],
    version = 7,
    exportSchema = false
)
abstract class AgencyLocalDatabase : RoomDatabase() {
    abstract val dao: AgencyDao
}