package tech.xken.tripbook.data.sources.universe.local


import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.*

/**
 * The current only database for the project and will remain the only for some time
 * SQLITE
 * For now we have the [Town], [Road] and the [TownPair]
 */
@Database(
    entities = [Planet::class, Continent::class, Country::class, Region::class, Division::class, Subdivision::class, Town::class, Road::class, TownPair::class],
    version = 1,
    exportSchema = false
)
abstract class UniverseLocalDatabase : RoomDatabase() {
    abstract val dao: UniverseDao

}