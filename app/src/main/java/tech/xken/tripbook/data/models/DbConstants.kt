package tech.xken.tripbook.data.models

import kotlinx.serialization.Serializable

object Schema {
    const val AGENCY = "sc_agency"
    const val BOOKER = "sc_booker"
    const val AGENCY_LOG = "sc_agency_log"
    const val BOOKER_LOG = "sc_booker_log"
}

object Channel{
    const val AGENCY = "ch_agency"
}

@Serializable
enum class DbAction {
    INSERT, UPDATE, DELETE
}