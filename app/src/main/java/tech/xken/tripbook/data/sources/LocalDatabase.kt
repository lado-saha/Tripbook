package tech.xken.tripbook.data.sources

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.Continent
import tech.xken.tripbook.data.models.Country
import tech.xken.tripbook.data.models.Division
import tech.xken.tripbook.data.models.Job
import tech.xken.tripbook.data.models.Planet
import tech.xken.tripbook.data.models.Region
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Scanner
import tech.xken.tripbook.data.models.Station
import tech.xken.tripbook.data.models.StationJob
import tech.xken.tripbook.data.models.StationScannerMap
import tech.xken.tripbook.data.models.StationTownMap
import tech.xken.tripbook.data.models.Subdivision
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.data.models.TownPair
import tech.xken.tripbook.data.models.UnivSelection
import tech.xken.tripbook.data.sources.agency.local.AgencyDao
import tech.xken.tripbook.data.sources.booker.local.BookerDao
import tech.xken.tripbook.data.sources.caches.local.CachesDao
import tech.xken.tripbook.data.sources.init.local.InitDao
import tech.xken.tripbook.data.sources.universe.local.UniverseDao


@Database(
    entities = [Station::class, StationTownMap::class, StationScannerMap::class, Scanner::class, StationJob::class, Booker::class, Planet::class, Continent::class, Country::class, Region::class, Division::class, Subdivision::class, Town::class, Road::class, TownPair::class, UnivSelection::class, Job::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract val agencyDao: AgencyDao
    abstract val bookerDao: BookerDao
    abstract val universeDao: UniverseDao
    abstract val initDao: InitDao
    abstract val cachesDao: CachesDao
}
