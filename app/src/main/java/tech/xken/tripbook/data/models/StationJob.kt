package tech.xken.tripbook.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.*
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import tech.xken.tripbook.R


@Entity(tableName = "StationJobs", primaryKeys = ["jobId", "station"])
data class StationJob(
    val jobId: String,
    val station: String,
    val salary: Double?,
    val name: String?,
    val timestamp: Long?,
    @ColumnInfo(name = "payment_schedule") val paymentSchedule: PaymentSchedule?,
) {
    @Ignore
    var job: Job? = null

    val subtitle @Ignore get() = "$salary FCFA $paymentSchedule"
}

enum class StationDefJobs {
    DRIVER, SCANNER, MANAGER, OWNER, LUGGAGE_MASTER;

    val icon
        get() = when (this) {
            DRIVER -> Icons.Outlined.DirectionsBus
            SCANNER -> Icons.Outlined.QrCodeScanner
            MANAGER -> Icons.Outlined.SupervisorAccount
            OWNER -> Icons.Outlined.AccountTree
            LUGGAGE_MASTER -> Icons.Outlined.Luggage
        }
}

enum class PaymentSchedule {
    MONTHLY, WEEKLY, DAILY, YEARLY;

    val str
        get() = when (this) {
            MONTHLY -> R.string.lb_monthly
            WEEKLY -> R.string.lb_weekly
            DAILY -> R.string.lb_daily
            YEARLY -> R.string.lb_yearly
        }
}