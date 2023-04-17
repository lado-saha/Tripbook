package tech.xken.tripbook.data.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import tech.xken.tripbook.R
import java.util.*

@Entity(tableName = "Job")
data class Job(
    @PrimaryKey val id: String,
    val timestamp: Long?,
) {
    val icon get() = jobIcon[id]

    @Ignore
    @StringRes
    var name: Int? = null

    @Ignore
    @StringRes
    var shortWiki: Int? = null

    @Ignore
    @StringRes
    var longWiki: Int? = null

    companion object {
        const val ID_MANAGER = "init_job_manager"
        const val ID_SCANNER = "init_job_scanner"
        const val ID_DRIVER = "init_job_driver"
        const val ID_LUGGAGE_MASTER = "init_job_luggage_master"
        const val ID_JANITOR = "init_job_janitor"

        val jobIcon = mapOf(
            ID_MANAGER to Icons.Outlined.SupervisorAccount,
            ID_LUGGAGE_MASTER to Icons.Outlined.Luggage,
            ID_SCANNER to Icons.Outlined.QrCodeScanner,
            ID_DRIVER to Icons.Outlined.DirectionsBus,
            ID_JANITOR to Icons.Outlined.CleaningServices
        )

        val defaultJobs = mapOf(
            ID_MANAGER to Job(ID_MANAGER, Date().time).apply {
                name = R.string.lb_the_manager
                shortWiki = R.string.lb_wiki_short_manager
                longWiki = R.string.lb_wiki_long_manager
            },
            ID_LUGGAGE_MASTER to Job(ID_LUGGAGE_MASTER, Date().time).apply {
                name = R.string.lb_the_luggage_master
                shortWiki = R.string.lb_wiki_short_luggage_master
                longWiki = R.string.lb_wiki_long_luggage_master
            },
            ID_SCANNER to Job(ID_SCANNER, Date().time).apply {
                name = R.string.lb_the_scanner
                shortWiki = R.string.lb_wiki_short_scanner
                longWiki = R.string.lb_wiki_long_scanner
            },
            ID_DRIVER to Job(ID_DRIVER, Date().time).apply {
                name = R.string.lb_the_driver
                shortWiki = R.string.lb_wiki_short_driver
                longWiki = R.string.lb_wiki_long_driver
            },
            ID_JANITOR to Job(ID_JANITOR, Date().time).apply {
                name = R.string.lb_the_janitor
                shortWiki = R.string.lb_wiki_short_janitor
                longWiki = R.string.lb_wiki_long_janitor
            },
        )
    }
}
