package tech.xken.tripbook.data.models.agency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import tech.xken.tripbook.R


data class StationJob(
    val jobId: String,
    val station: String,
    val salary: Long,
    val name: String,
    val paymentSchedule: PaymentSchedule,
    val timestamp: Long,
    val description: String
) {
    @Ignore
    var job: Job? = null
    val subtitle @Ignore get() = "$salary FCFA $paymentSchedule"
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

data class Job(
    val id: String,
    val name: String,
    val addedOn: Long?,
    val description: String,
) {
    companion object {
//        const val ID_MANAGER = "init_job_manager"
//        const val ID_SCANNER = "init_job_scanner"
//        const val ID_DRIVER = "init_job_driver"
//        const val ID_LUGGAGE_MASTER = "init_job_luggage_master"
//        const val ID_JANITOR = "init_job_janitor"
//
//        val jobIcon = mapOf(
//            ID_MANAGER to Icons.Outlined.SupervisorAccount,
//            ID_LUGGAGE_MASTER to Icons.Outlined.Luggage,
//            ID_SCANNER to Icons.Outlined.QrCodeScanner,
//            ID_DRIVER to Icons.Outlined.DirectionsBus,
//            ID_JANITOR to Icons.Outlined.CleaningServices
//        )
//
//        val defaultJobs = mapOf(
//            ID_MANAGER to Job(ID_MANAGER, Date().time).apply {
//                name = R.string.lb_the_manager
//                shortWiki = R.string.lb_wiki_short_manager
//                longWiki = R.string.lb_wiki_long_manager
//            },
//            ID_LUGGAGE_MASTER to Job(ID_LUGGAGE_MASTER, Date().time).apply {
//                name = R.string.lb_the_luggage_master
//                shortWiki = R.string.lb_wiki_short_luggage_master
//                longWiki = R.string.lb_wiki_long_luggage_master
//            },
//            ID_SCANNER to Job(ID_SCANNER, Date().time).apply {
//                name = R.string.lb_the_scanner
//                shortWiki = R.string.lb_wiki_short_scanner
//                longWiki = R.string.lb_wiki_long_scanner
//            },
//            ID_DRIVER to Job(ID_DRIVER, Date().time).apply {
//                name = R.string.lb_the_driver
//                shortWiki = R.string.lb_wiki_short_driver
//                longWiki = R.string.lb_wiki_long_driver
//            },
//            ID_JANITOR to Job(ID_JANITOR, Date().time).apply {
//                name = R.string.lb_the_janitor
//                shortWiki = R.string.lb_wiki_short_janitor
//                longWiki = R.string.lb_wiki_long_janitor
//            },
//        )
    }
}