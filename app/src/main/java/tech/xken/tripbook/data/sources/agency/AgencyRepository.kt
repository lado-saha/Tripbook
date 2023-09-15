package tech.xken.tripbook.data.sources.agency

import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.agency.AgencyAccount
import tech.xken.tripbook.data.models.agency.AgencyEmailSupport
import tech.xken.tripbook.data.models.agency.AgencyPhoneSupport
import tech.xken.tripbook.data.models.agency.AgencyRefundPolicy
import tech.xken.tripbook.data.models.agency.AgencySocialSupport
import tech.xken.tripbook.data.models.agency.TripCancellationReason

interface AgencyRepository {
    val channel: RealtimeChannel
    //    Agency account
    suspend fun agencyAccountFullSync(agencyId: String): Results<Unit>
    suspend fun agencyAccount(agencyId: String): Results<AgencyAccount?>
    /**
     * The so called flow sync for agencyAccount
     */
    fun agencyAccountLogFlow(agencyId: String): Flow<Results<AgencyAccount.Log>>
    fun agencyAccountFlow(agencyId: String): Flow<Results<AgencyAccount>>
    suspend fun createAgencyAccount(
        offline: Boolean?,
        account: AgencyAccount,
    ): Results<AgencyAccount?>

    suspend fun updateAgencyAccount(
        agencyId: String, account: AgencyAccount
    ): Results<AgencyAccount?>

    suspend fun countAgencyAccount(agencyId: String): Flow<Results<Long>>

    // Agency Email support
    suspend fun emailSupportsFullSync(agencyId: String): Results<Unit>
    suspend fun emailSupports(agencyId: String, emails: List<String>): Results<List<AgencyEmailSupport>>
    /**
     * The so called flow sync for emailSupports
     */
    fun emailSupportsLogFlow(agencyId: String): Flow<Results<AgencyEmailSupport.Log>>
    fun emailSupportsFlow(agencyId: String): Flow<Results<List<AgencyEmailSupport>>>
    suspend fun createEmailSupports(
        offline: Boolean?,
        supports: List<AgencyEmailSupport>
    ): Results<List<AgencyEmailSupport?>>

    suspend fun updateEmailSupports(
        agencyId: String, supports: List<AgencyEmailSupport>
    ): Results<List<AgencyEmailSupport?>>

    suspend fun deleteEmailSupports(agencyId: String, emails: List<String>): Results<Unit>
    suspend fun countEmailSupports(agencyId: String): Flow<Results<Long>>

    // Agency Phone support
    suspend fun phoneSupportsFullSync(agencyId: String): Results<Unit>
    suspend fun phoneSupports(agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>): Results<List<AgencyPhoneSupport>>
    /**
     * The so called flow sync for phoneSupports
     */
    fun phoneSupportsLogFlow(agencyId: String): Flow<Results<AgencyPhoneSupport.Log>>
    fun phoneSupportsFlow(agencyId: String): Flow<Results<List<AgencyPhoneSupport>>>
    suspend fun createPhoneSupports(
        offline: Boolean?,
        supports: List<AgencyPhoneSupport>
    ): Results<List<AgencyPhoneSupport?>>

    suspend fun updatePhoneSupports(
        agencyId: String, supports: List<AgencyPhoneSupport>
    ): Results<List<AgencyPhoneSupport?>>

    suspend fun deletePhoneSupports(agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>): Results<Unit>
    suspend fun countPhoneSupports(agencyId: String): Flow<Results<Long>>

    // Agency Social support
    suspend fun socialSupportFullSync(agencyId: String): Results<Unit>
    suspend fun socialSupport(agencyId: String): Results<AgencySocialSupport?>
    /**
     * The so called flow sync for socialSupport
     */
    fun socialSupportLogFlow(agencyId: String): Flow<Results<AgencySocialSupport.Log>>
    fun socialSupportFlow(agencyId: String): Flow<Results<AgencySocialSupport>>
    suspend fun createSocialSupport(
        offline: Boolean?,
        support: AgencySocialSupport
    ): Results<AgencySocialSupport?>

    suspend fun updateSocialSupports(
        agencyId: String, support: AgencySocialSupport
    ): Results<AgencySocialSupport?>

    suspend fun deleteSocialSupport(agencyId: String): Results<Unit>
    suspend fun countSocialAccount(agencyId: String): Flow<Results<Long>>

    // Agency Refund policies
    suspend fun refundPoliciesFullSync(agencyId: String): Results<Unit>
    suspend fun refundPolicies(agencyId: String, reasons: List<TripCancellationReason>): Results<List<AgencyRefundPolicy>>
    /**
     * The so called flow sync for refundPolicies
     */
    fun refundPoliciesLogFlow(agencyId: String): Flow<Results<AgencyRefundPolicy.Log>>
    fun refundPoliciesFlow(agencyId: String): Flow<Results<List<AgencyRefundPolicy>>>
    suspend fun createRefundPolicies(
        offline: Boolean?,
        policies: List<AgencyRefundPolicy>
    ): Results<List<AgencyRefundPolicy?>>

    suspend fun updateRefundPolicies(
        agencyId: String, policies: List<AgencyRefundPolicy>
    ): Results<List<AgencyRefundPolicy?>>
    suspend fun deleteRefundPolicies(agencyId: String, reasons: List<TripCancellationReason>): Results<Unit>
    suspend fun countRefundPolicies(agencyId: String): Flow<Results<Long>>


//    /**
//     * @param agency is the scanner's employer
//     */
//    suspend fun scanners(
//        agency: String,
//        station: String,
//        getBookers: Boolean,
//        getJobs: Boolean,
//    ): Results<Map<String, Scanner>>
//
//    suspend fun scannersFromIds(
//        ids: List<String>,
//        station: String,
//        getBookers: Boolean,
//        getJobs: Boolean,
//    ): Results<Map<String, Scanner>>
//
//    suspend fun saveStationJobs(stationJobs: List<StationJob>)
//    fun observeStationJobs(station: String): Flow<Results<List<StationJob>>>
//    suspend fun stationJobsFromIds(station: String, ids: List<String>): Results<List<StationJob>>
//    suspend fun stationJobs(station: String): Results<List<StationJob>>
//    fun observeStationScannerMaps(station: String): Flow<Results<List<StationScannerMap>>>
//    suspend fun stationScannerMaps(
//        station: String,
//        scanners: List<String>,
//    ): Results<List<StationScannerMap>>
//    suspend fun saveStationScannerMaps(maps: List<StationScannerMap>)
//    suspend fun deleteStationScannerMap(station: String, scanners: List<String>)
//    fun observeStationsFromIds(ids: List<String>): Flow<Results<List<Station>>>
//    suspend fun saveStations(stations: List<Station>)
//    suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>)
//    suspend fun deleteStationTownMaps(station: String, towns: List<String>)
//    fun observeStationTownMaps(station: String): Flow<Results<List<StationTownMap>>>
//    suspend fun stationTownMaps(station: String): Results<List<StationTownMap>>
//    suspend fun stations(): Results<List<Station>>
//    suspend fun stationsFromIds(ids: List<String>): Results<List<Station>>
//    suspend fun saveStationLocation(id: String, lat: Double, lon: Double)
//
}