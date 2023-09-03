package tech.xken.tripbook.data.sources

//import tech.xken.tripbook.data.models.Scanner
//import tech.xken.tripbook.data.sources.agency.local.AgencyDao
//import tech.xken.tripbook.data.sources.init.local.InitDao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import tech.xken.tripbook.data.models.Country
import tech.xken.tripbook.data.models.Division
import tech.xken.tripbook.data.models.GeoPoint
import tech.xken.tripbook.data.models.Region
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Subdivision
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.data.models.TownPair
import tech.xken.tripbook.data.models.UnivSelection
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.booker.BookerOMAccount
import tech.xken.tripbook.data.sources.booker.local.BookerDao
import tech.xken.tripbook.data.sources.caches.local.CachesDao
import java.util.UUID


@Database(
    entities = [/*Station::class, StationTownMap::class, StationScannerMap::class, *//*Scanner::class,*//* StationJob::class,*/ BookerMoMoAccount::class, BookerOMAccount::class, Booker::class, Country::class, Region::class, Division::class, Subdivision::class, Town::class, Road::class, TownPair::class, UnivSelection::class /*Job::class*/],
    version = 4,
    exportSchema = false
)
@TypeConverters(LocalConverters::class)
abstract class LocalDatabase : RoomDatabase() {
    //    abstract val agencyDao: AgencyDao
    abstract val bookerDao: BookerDao


    //    abstract val universeDao: UniverseDao
//    abstract val initDao: InitDao
    abstract val cachesDao: CachesDao
}

class LocalConverters {
    @TypeConverter
    fun fromUUID(value: UUID): String {
        return value.toString()
    }

    @TypeConverter
    fun toUUID(value: String): UUID {
        return UUID.fromString(value)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.foldIndexed("") { i, acc, s ->
            if (i == 0) "$s," else "$acc,$s"
        }
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromGeoPoint(value: GeoPoint): String {
        return "${value.x} ${value.y}"
    }

    @TypeConverter
    fun toGeoPoint(value: String): GeoPoint {
        return value.split(" ").run {
            GeoPoint(this[0].toDouble(), this[1].toDouble())
        }
    }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Int? {
        return value?.toEpochDays()
    }

    @TypeConverter
    fun toLocalDate(value: Int?): LocalDate? {
        return value?.let { LocalDate.fromEpochDays(value) }
    }
}
