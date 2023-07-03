package tech.xken.tripbook.data.models

import androidx.compose.ui.geometry.Offset
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

/**
 * A town is a unit place
 */
@Entity
interface UniverseUI {
    val id: String
    val name: String?
    val parent: String?
}


/**
 * A [Town] class mainly used for UI requirements like in the Universe editor
 */
data class TownNode(
    val town: Town,
    val offset: Offset = Offset.Zero,
    val timestamp: Long = Date().time,
    val pinPoint: Offset = Offset(0f, 0f),
)

@Entity(tableName = Planet.TABLE_NAME)
data class Planet(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) : UniverseUI {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val TABLE_NAME = "Planets"
        const val TIMESTAMP = "timestamp"
    }

    override val parent: String?
        get() = id
}



@Entity(tableName = Continent.TABLE_NAME)
data class Continent(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = PLANET) val planet: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) : UniverseUI {

    override val parent: String?
        get() = planet

    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val PLANET = "planet"
        const val TABLE_NAME = "Continents"
        const val TIMESTAMP = "timestamp"
    }
}

@Entity(tableName = Country.TABLE_NAME)
data class Country(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = CONTINENT) val continent: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) : UniverseUI {

    override val parent: String?
        get() = continent
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val CONTINENT = "continent"
        const val TABLE_NAME = "Countries"
        const val TIMESTAMP = "timestamp"
    }
}

@Entity(tableName = Region.TABLE_NAME)
data class Region(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = COUNTRY) val country: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = IS_CAPITAL) val isCapital: Boolean?,
) : UniverseUI {

    override val parent: String?
        get() = country
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val COUNTRY = "country"
        const val TABLE_NAME = "Regions"
        const val TIMESTAMP = "timestamp"
        const val IS_CAPITAL = "is_capital"
    }
}

@Entity(tableName = Division.TABLE_NAME)
data class Division(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = REGION) val region: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = IS_CAPITAL) val isCapital: Boolean?,
) : UniverseUI {

    override val parent: String?
        get() = region

    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val REGION = "region"
        const val TABLE_NAME = "Divisions"
        const val TIMESTAMP = "timestamp"
        const val IS_CAPITAL = "is_capital"
    }

}

@Entity(tableName = Subdivision.TABLE_NAME)
data class Subdivision(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = DIVISION) val division: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = IS_CAPITAL) val isCapital: Boolean?,
) : UniverseUI {

    override val parent: String?
        get() = division
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val DIVISION = "division"
        const val TABLE_NAME = "Subdivisions"
        const val TIMESTAMP = "timestamp"
        const val IS_CAPITAL = "is_capital"
    }
}

@Entity(tableName = Town.TABLE_NAME)
data class Town(
    @ColumnInfo(name = ID) @PrimaryKey override val id: String,
    @ColumnInfo(name = NAME) override val name: String?,
    @ColumnInfo(name = SUBDIVISION) val subdivision: String?,
    @ColumnInfo(name = LAT) val lat: Double?,
    @ColumnInfo(name = LON) val lon: Double?,
    @ColumnInfo(name = XM) val xm: Double?,
    @ColumnInfo(name = YM) val ym: Double?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = IS_CAPITAL) val isCapital: Boolean?,
) : UniverseUI {

    override val parent: String?
        get() = subdivision
    companion object {
        const val TABLE_NAME = "Towns"
        const val IS_CAPITAL = "is_capital"
        const val ID = "id"
        const val NAME = "name"
        const val LAT = "lat"
        const val LON = "lon"
        const val SUBDIVISION = "subdivision"
        const val XM = "xm"
        const val YM = "ym"
        const val TIMESTAMP = "timestamp"
        const val all = "*"
    }
}