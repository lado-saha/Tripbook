package tech.xken.tripbook.data.sources.agency

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.*

interface AgencyRepository {
    /**
     * Park
     */
    suspend fun savePark(park: Park)
    suspend fun parks(): Results<List<Park>>
    suspend fun parksFromIds(ids: List<String>): Results<List<Park>>
    suspend fun parksFromTown(town: String): Results<List<Park>>
    suspend fun townFromPark(park: String): Results<List<Town>>
}