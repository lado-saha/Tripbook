package tech.xken.tripbook.data.sources.agency

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.*

interface AgencyDataSource {
    /**
     * Parks
     */
    suspend fun savePark(park: Park)
    suspend fun parks(): Results<List<Park>>
    suspend fun parksFromIds(ids: List<String>): Results<List<Park>>
    suspend fun townParkMapFromTown(town: String): Results<List<TownParkMap>>
    suspend fun townParkMapFromPark(park: String): Results<List<TownParkMap>>
}