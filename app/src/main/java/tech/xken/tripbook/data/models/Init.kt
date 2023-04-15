package tech.xken.tripbook.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Job")
data class Job(
    @PrimaryKey val id: String,
    val name: String?,
    val wiki: String?,
    val timestamp: Long?,
) {
    companion object {
        enum class DefaultJobs {
            MANAGER, LUGGAGE_MASTER, SCANNER, DRIVER, JANITOR;

            val icon
                get() = when (this) {
                    MANAGER -> Icons.Outlined.SupervisorAccount
                    LUGGAGE_MASTER -> Icons.Outlined.Luggage
                    SCANNER -> Icons.Outlined.QrCodeScanner
                    DRIVER -> Icons.Outlined.DirectionsBus
                    JANITOR -> Icons.Outlined.CleaningServices
                }

            val model
                get() = when (this) {
                    MANAGER -> Job(
                        id = this.name,
                        name = "the ${this.name}",
                        wiki = "Is a manager",
                        timestamp = Date().time
                    )
                    LUGGAGE_MASTER -> Job(
                        id = this.name,
                        name = "the ${this.name}",
                        wiki = "A luggage master is responsible for the booker luggage's",
                        timestamp = Date().time
                    )
                    SCANNER -> Job(
                        id = this.name,
                        name = "the ${this.name}",
                        wiki = "A scanner is responsible for scanning the tickets of bookers",
                        timestamp = Date().time,
                    )
                    DRIVER -> Job(
                        id = this.name,
                        name ="the ${this.name}",
                        wiki = "A driver is responsible for driving a bus",
                        timestamp = Date().time,
                    )
                    JANITOR -> Job(
                        id = this.name,
                        name = "the ${this.name}",
                        wiki = "A janitor is responsible for cleaning the station",
                        timestamp = Date().time,
                    )
                }
        }
    }
}