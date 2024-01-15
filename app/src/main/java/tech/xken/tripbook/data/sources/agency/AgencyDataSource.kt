package tech.xken.tripbook.data.sources.agency


import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.agency.AgencyAccount
import tech.xken.tripbook.data.models.agency.AgencyEmailSupport
import tech.xken.tripbook.data.models.agency.AgencyGraphics
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs
import tech.xken.tripbook.data.models.agency.AgencyMoMoAccount
import tech.xken.tripbook.data.models.agency.AgencyOMAccount
import tech.xken.tripbook.data.models.agency.AgencyPayPalAccount
import tech.xken.tripbook.data.models.agency.AgencyPhoneSupport
import tech.xken.tripbook.data.models.agency.AgencyRefundPolicy
import tech.xken.tripbook.data.models.agency.AgencySocialSupport
import tech.xken.tripbook.data.models.agency.TripCancellationReason

interface AgencyDataSource {
    val channel: RealtimeChannel?

    suspend fun agencyAccountLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyEmailSupportsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyPhoneSupportsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencySocialSupportsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyRefundPoliciesLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyOMAccountsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyMoMoAccountsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyPayPalAccountsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyLegalDocsLastModifiedOn(agencyId: String):Results<Instant?>
    suspend fun agencyGraphicsLastModifiedOn(agencyId: String):Results<Instant?>

    //    Agency account
    suspend fun agencyAccount(agencyId: String): Results<AgencyAccount?>
    suspend fun agencyAccountLatestLog(agencyId: String, fromInstant: Instant?): Results<AgencyAccount.Log>
    fun agencyAccountLogFlow(agencyId: String): Flow<Results<AgencyAccount.Log>>
    fun agencyAccountFlow(agencyId: String): Flow<Results<AgencyAccount>>
    suspend fun createAgencyAccount(account: AgencyAccount): Results<AgencyAccount?>
    suspend fun updateAgencyAccount(agencyId: String, account: AgencyAccount): Results<AgencyAccount?>
    fun countAgencyAccount(agencyId: String): Flow<Results<Long>>

    //    Agency Graphics
    suspend fun agencyGraphics(agencyId: String): Results<AgencyGraphics?>
    suspend fun agencyGraphicsLatestLog(agencyId: String, fromInstant: Instant?): Results<AgencyGraphics.Log>
    fun agencyGraphicsLogFlow(agencyId: String): Flow<Results<AgencyGraphics.Log>>
    fun agencyGraphicsFlow(agencyId: String): Flow<Results<AgencyGraphics>>

    suspend fun createAgencyGraphics(graphics: AgencyGraphics): Results<AgencyGraphics?>
    /**
     * Special case. Since this is just a wrapper for storage functions, we donot need to upload the function for n
     */
    suspend fun updateAgencyGraphics(agencyId: String, graphics: AgencyGraphics): Results<AgencyGraphics?>
    suspend fun deleteAgencyGraphics(agencyId: String): Results<Unit>
    fun countAgencyGraphics(agencyId: String): Flow<Results<Long>>

    // Agency Legal Documents
    suspend fun legalDocs(agencyId: String): Results<AgencyLegalDocs?>
    suspend fun legalDocsLatestLog(agencyId: String, fromInstant: Instant?): Results<AgencyLegalDocs.Log>
    fun legalDocsLogFlow(agencyId: String): Flow<Results<AgencyLegalDocs.Log>>
    fun legalDocsFlow(agencyId: String): Flow<Results<AgencyLegalDocs>>
    suspend fun createLegalDocs(docs: AgencyLegalDocs): Results<AgencyLegalDocs?>
    suspend fun updateLegalDocs(agencyId: String, docs: AgencyLegalDocs): Results<AgencyLegalDocs?>
    suspend fun deleteLegalDocs(agencyId: String): Results<Unit>
    fun countLegalDocs(agencyId: String): Flow<Results<Long>>


    // Agency Email support
    suspend fun emailSupports(agencyId: String, emails: List<String>): Results<List<AgencyEmailSupport>>
    suspend fun emailSupportLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyEmailSupport.Log>>
    fun emailSupportsLogFlow(agencyId: String): Flow<Results<AgencyEmailSupport.Log>>
    fun emailSupportsFlow(agencyId: String): Flow<Results<List<AgencyEmailSupport>>>
    suspend fun createEmailSupports(supports: List<AgencyEmailSupport>): Results<List<AgencyEmailSupport?>>
    suspend fun updateEmailSupports(agencyId: String, supports: List<AgencyEmailSupport>): Results<List<AgencyEmailSupport?>>
    suspend fun deleteEmailSupports(agencyId: String, emails: List<String>): Results<Unit>
    fun countEmailSupports(agencyId: String): Flow<Results<Long>>

    // Agency Phone support
    suspend fun phoneSupports(agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>): Results<List<AgencyPhoneSupport>>
    suspend fun phoneSupportsLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyPhoneSupport.Log>>
    fun phoneSupportsLogFlow(agencyId: String): Flow<Results<AgencyPhoneSupport.Log>>
    fun phoneSupportsFlow(agencyId: String): Flow<Results<List<AgencyPhoneSupport>>>
    suspend fun createPhoneSupports(supports: List<AgencyPhoneSupport>): Results<List<AgencyPhoneSupport?>>
    suspend fun updatePhoneSupports(agencyId: String, supports: List<AgencyPhoneSupport>): Results<List<AgencyPhoneSupport?>>
    suspend fun deletePhoneSupports(agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>): Results<Unit>
    fun countPhoneSupports(agencyId: String): Flow<Results<Long>>

    // Agency Social support
    suspend fun socialSupport(agencyId: String): Results<AgencySocialSupport?>
    suspend fun socialSupportLatestLogs(agencyId: String, fromInstant: Instant?): Results<AgencySocialSupport.Log>
    fun socialSupportLogFlow(agencyId: String): Flow<Results<AgencySocialSupport.Log>>
    fun socialSupportFlow(agencyId: String): Flow<Results<AgencySocialSupport>>
    suspend fun createSocialSupport(support: AgencySocialSupport): Results<AgencySocialSupport?>
    suspend fun updateSocialSupport(agencyId: String, supports: AgencySocialSupport): Results<AgencySocialSupport?>
    suspend fun deleteSocialSupport(agencyId: String): Results<Unit>
    fun countSocialAccount(agencyId: String): Flow<Results<Long>>

    // Agency Refund policies
    suspend fun refundPolicies(agencyId: String, reasons: List<TripCancellationReason>): Results<List<AgencyRefundPolicy>>
    suspend fun refundPolicyLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyRefundPolicy.Log>>
    fun refundPoliciesLogFlow(agencyId: String): Flow<Results<AgencyRefundPolicy.Log>>
    fun refundPoliciesFlow(agencyId: String): Flow<Results<List<AgencyRefundPolicy>>>
    suspend fun createRefundPolicies(policies: List<AgencyRefundPolicy>): Results<List<AgencyRefundPolicy?>>
    suspend fun updateRefundPolicies(agencyId: String, policies: List<AgencyRefundPolicy>): Results<List<AgencyRefundPolicy?>>
    suspend fun deleteRefundPolicies(agencyId: String, reasons: List<TripCancellationReason>): Results<Unit>
    fun countRefundPolicies(agencyId: String): Flow<Results<Long>>

    // MoMo Accounts
    suspend fun moMoAccounts(agencyId: String, phoneNumbers: List<String>): Results<List<AgencyMoMoAccount>>
    suspend fun moMoAccountsLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyMoMoAccount.Log>>
    fun moMoAccountsLogFlow(agencyId: String): Flow<Results<AgencyMoMoAccount.Log>>
    fun moMoAccountsFlow(agencyId: String): Flow<Results<List<AgencyMoMoAccount>>>
    suspend fun createMoMoAccounts(accounts: List<AgencyMoMoAccount>): Results<List<AgencyMoMoAccount?>>
    suspend fun updateMoMoAccounts(agencyId: String, accounts: List<AgencyMoMoAccount>): Results<List<AgencyMoMoAccount?>>
    suspend fun deleteMoMoAccounts(agencyId: String, phoneNumbers: List<String>): Results<Unit>
    suspend fun countMoMoAccounts(agencyId: String): Flow<Results<Long>>

    // OM Accounts
    suspend fun oMAccounts(agencyId: String, phoneNumbers: List<String>): Results<List<AgencyOMAccount>>
    suspend fun oMAccountsLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyOMAccount.Log>>
    fun oMAccountsLogFlow(agencyId: String): Flow<Results<AgencyOMAccount.Log>>
    fun oMAccountsFlow(agencyId: String): Flow<Results<List<AgencyOMAccount>>>
    suspend fun createOMAccounts(accounts: List<AgencyOMAccount>): Results<List<AgencyOMAccount?>>
    suspend fun updateOMAccounts(agencyId: String, accounts: List<AgencyOMAccount>): Results<List<AgencyOMAccount?>>
    suspend fun deleteOMAccounts(agencyId: String, phoneNumbers: List<String>): Results<Unit>
    suspend fun countOMAccounts(agencyId: String): Flow<Results<Long>>

    // PayPal Accounts
    suspend fun payPalAccounts(agencyId: String, email: List<String>): Results<List<AgencyPayPalAccount>>
    suspend fun payPalAccountsLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyPayPalAccount.Log>>
    fun payPalAccountsLogFlow(agencyId: String): Flow<Results<AgencyPayPalAccount.Log>>
    fun payPalAccountsFlow(agencyId: String): Flow<Results<List<AgencyPayPalAccount>>>
    suspend fun createPayPalAccounts(accounts: List<AgencyPayPalAccount>): Results<List<AgencyPayPalAccount?>>
    suspend fun updatePayPalAccounts(agencyId: String, accounts: List<AgencyPayPalAccount>): Results<List<AgencyPayPalAccount?>>
    suspend fun deletePayPalAccounts(agencyId: String, emails: List<String>): Results<Unit>
    suspend fun countPayPalAccounts(agencyId: String): Flow<Results<Long>>

    //

//    suspend fun phoneSupports(agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>, fromInstant: Instant?): Results<List<AgencyPhoneSupport>>
//    suspend fun phoneSupportsLatestLogs(agencyId: String, fromInstant: Instant?): Results<List<AgencyPhoneSupport.Log>>
//    fun phoneSupportsLogFlow(agencyId: String): Flow<Results<AgencyPhoneSupport.Log>>
//    fun phoneSupportsFlow(agencyId: String): Flow<Results<List<AgencyPhoneSupport>>>
//    suspend fun createPhoneSupports(supports: List<AgencyPhoneSupport>): Results<List<AgencyPhoneSupport?>>
//    suspend fun updatePhoneSupports(agencyId: String, supports: List<AgencyPhoneSupport>): Results<List<AgencyPhoneSupport?>>
//    suspend fun deletePhoneSupports(agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>): Results<Unit> suspend fun countPhoneSupports(agencyId: String): Flow<Results<Long>>


//    suspend fun stationScannerMapsFromStation(station: String)
//    suspend fun stationJobsFromIds(station: String, ids: List<String>): Results<List<StationJob>>
//    suspend fun stationJobs(station: String): Results<List<StationJob>>
//    fun observeStationJobs(station: String): Flow<Results<List<StationJob>>>
//    suspend fun saveStationJobs(stationJobs: List<StationJob>)
//    suspend fun scanners(agency: String): Results<List<Scanner>>
//    suspend fun scannersFromIds(ids: List<String>): Results<List<Scanner>>
//    fun observeStationScannerMaps(station: String): Flow<Results<List<StationScannerMap>>>
//
//    suspend fun stationScannerMaps(station: String, scanners: List<String>): Results<List<StationScannerMap>>
//
//    suspend fun saveStationScannerMaps(maps: List<StationScannerMap>)
//    suspend fun deleteStationScannerMap(station: String, scanners: List<String>)
//    suspend fun saveStations(stations: List<Station>)
//    suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>)
//    fun observeStationTownMaps(station: String): Flow<Results<List<StationTownMap>>>
//    suspend fun stationTownMaps(station: String): Results<List<StationTownMap>>
//    suspend fun stations(): Results<List<Station>>
//    suspend fun stationsFromIds(ids: List<String>): Results<List<Station>>
//    fun observeStationsFromIds(ids: List<String>): Flow<Results<List<Station>>>
//
//    suspend fun deleteStationTownMaps(station: String, towns: List<String>)
//
//    suspend fun saveStationLocation(id: String, lat: Double, lon: Double)
}