package tech.xken.tripbook.data.sources.agency.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
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

@Dao
interface AgencyDao {
    @Query("select * from agency_account where agency_id = :agencyId")
    fun agencyAccount(agencyId: String): AgencyAccount
    @Query("select * from agency_email_support where agency_id = :agencyId")
    fun agencyEmailSupports(agencyId: String): List<AgencyEmailSupport>
    @Query("select * from agency_phone_support where agency_id = :agencyId")
    fun agencyPhoneSupports(agencyId: String): List<AgencyPhoneSupport>
    @Query("select * from agency_social_support where agency_id = :agencyId")
    fun agencySocialSupports(agencyId: String): AgencySocialSupport
    @Query("select * from agency_refund_policy where agency_id = :agencyId")
    fun agencyRefundPolicies(agencyId: String): List<AgencyRefundPolicy>//---------------------
    @Query("select * from agency_momo_account where agency_id = :agencyId")
    fun agencyMoMoAccounts(agencyId: String): List<AgencyMoMoAccount>
    @Query("select * from agency_om_account where agency_id = :agencyId")
    fun agencyOMAccounts(agencyId: String): List<AgencyOMAccount>
    @Query("select * from agency_paypal_account where agency_id = :agencyId")
    fun agencyPayPalAccounts(agencyId: String): List<AgencyPayPalAccount>
    @Query("select * from agency_legal_docs where agency_id = :agencyId")
    fun agencyLegalDocs(agencyId: String): AgencyLegalDocs
    @Query("select * from agency_graphics where agency_id = :agencyId")
    fun agencyGraphics(agencyId: String): AgencyGraphics


    @Query("select max(modified_on) from agency_account where agency_id = :agencyId")
    fun agencyAccountLastModifiedOn(agencyId: String):Instant?
    @Query("select max(modified_on) from agency_email_support where agency_id = :agencyId")
    fun agencyEmailSupportsLastModifiedOn(agencyId: String):Instant?
    @Query("select max(modified_on) from agency_phone_support where agency_id = :agencyId")
    fun agencyPhoneSupportsLastModifiedOn(agencyId: String):Instant?
    @Query("select max(modified_on) from agency_social_support where agency_id = :agencyId")
    fun agencySocialSupportsLastModifiedOn(agencyId: String):Instant?
    @Query("select max(modified_on) from agency_refund_policy where agency_id = :agencyId")
    fun agencyRefundPoliciesLastModifiedOn(agencyId: String):Instant?//-------------------------
    @Query("select max(modified_on) from agency_momo_account where agency_id = :agencyId")
    fun agencyMoMoAccountsLastModifiedOn(agencyId: String): Instant?
    @Query("select max(modified_on) from agency_om_account where agency_id = :agencyId")
    fun agencyOMAccountsLastModifiedOn(agencyId: String): Instant?
    @Query("select max(modified_on) from agency_paypal_account where agency_id = :agencyId")
    fun agencyPayPalAccountsLastModifiedOn(agencyId: String): Instant?
    @Query("select max(modified_on) from agency_legal_docs where agency_id = :agencyId")
    fun agencyLegalDocsLastModifiedOn(agencyId: String): Instant?
    @Query("select max(modified_on) from agency_graphics where agency_id = :agencyId")
    fun agencyGraphicsLastModifiedOn(agencyId: String): Instant?

    @Query("select count(agency_id) from agency_account where agency_id = :agencyId")
    fun agencyAccountCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_email_support where agency_id = :agencyId")
    fun agencyEmailSupportsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_phone_support where agency_id = :agencyId")
    fun agencyPhoneSupportsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_social_support where agency_id = :agencyId")
    fun agencySocialSupportsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_refund_policy where agency_id = :agencyId")
    fun agencyRefundPoliciesCount(agencyId: String): Flow<Long>//-------------------------------------
    @Query("select count(agency_id) from agency_momo_account where agency_id = :agencyId")
    fun agencyMoMoAccountsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_om_account where agency_id = :agencyId")
    fun agencyOMAccountsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_paypal_account where agency_id = :agencyId")
    fun agencyPayPalAccountsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_legal_docs where agency_id = :agencyId")
    fun agencyLegalDocsCount(agencyId: String): Flow<Long>
    @Query("select count(agency_id) from agency_graphics where agency_id = :agencyId")
    fun agencyGraphicsCount(agencyId: String): Flow<Long>

    @Query("select * from agency_account where agency_id = :agencyId")
    fun agencyAccountFlow(agencyId: String): Flow<AgencyAccount>
    @Query("select * from agency_email_support where agency_id = :agencyId")
    fun agencyEmailSupportsFlow(agencyId: String):Flow<List<AgencyEmailSupport>>
    @Query("select * from agency_phone_support where agency_id = :agencyId")
    fun agencyPhoneSupportsFlow(agencyId: String):Flow<List<AgencyPhoneSupport>>
    @Query("select * from agency_social_support where agency_id = :agencyId")
    fun agencySocialSupportFlow(agencyId: String):Flow<AgencySocialSupport>
    @Query("select * from agency_refund_policy where agency_id = :agencyId")
    fun agencyRefundPolicyFlow(agencyId: String):Flow< List<AgencyRefundPolicy>>//----------------------------
    @Query("select * from agency_momo_account where agency_id = :agencyId")
    fun agencyMoMoAccountsFlow(agencyId: String): Flow<List<AgencyMoMoAccount>>
    @Query("select * from agency_om_account where agency_id = :agencyId")
    fun agencyOMAccountsFlow(agencyId: String): Flow<List<AgencyOMAccount>>
    @Query("select * from agency_paypal_account where agency_id = :agencyId")
    fun agencyPayPalAccountsFlow(agencyId: String): Flow<List<AgencyPayPalAccount>>
    @Query("select * from agency_legal_docs where agency_id = :agencyId")
    fun agencyLegalDocsFlow(agencyId: String): Flow<AgencyLegalDocs>
    @Query("select * from agency_graphics where agency_id = :agencyId")
    fun agencyGraphicsFlow(agencyId: String): Flow<AgencyGraphics>

//    @Query("select max(modified_on) from agency_account where agency_id=:agencyId")
//    fun agencyLastModifiedOn(agencyId: String): Instant?

    @Upsert
    fun upsertAgencyAccount(account: AgencyAccount)
    @Upsert
    fun upsertEmailSupports(supports: List<AgencyEmailSupport>)
    @Upsert
    fun upsertPhoneSupports(supports: List<AgencyPhoneSupport>)
    @Upsert
    fun upsertSocialSupport(support: AgencySocialSupport)
    @Upsert
    fun upsertRefundPolicies(supports: List<AgencyRefundPolicy>)
    @Upsert
    fun upsertMoMoAccounts(accounts: List<AgencyMoMoAccount>)
    @Upsert
    fun upsertOMAccounts(accounts: List<AgencyOMAccount>)
    @Upsert
    fun upsertPayPalAccounts(accounts: List<AgencyPayPalAccount>)
    @Upsert
    fun upsertAgencyLegalDocs(legalDocs: AgencyLegalDocs)
    @Upsert
    fun upsertAgencyGraphics(graphics: AgencyGraphics)


//    @Query("delete from agency_account where agency_id = :agencyId")
//    fun deleteAgencyAccount(agencyId: String)
    @Query("delete from agency_email_support where agency_id = :agencyId and email in (:email)")
    fun deleteEmailSupports(agencyId: String,email: List<String>)
    @Query("delete from agency_phone_support where agency_id = :agencyId and phone_code in (:phoneCodes) and phone_number in (:phoneNumbers)")
    fun deletePhoneSupports(agencyId: String,phoneCodes: List<String>,  phoneNumbers: List<String>)
    @Query("delete from agency_social_support where agency_id = :agencyId")
    fun deleteSocialSupport(agencyId: String)
    @Query("delete from agency_refund_policy where agency_id = :agencyId and reason in (:reasons)")
    fun deleteRefundPolicies(agencyId: String, reasons: List<TripCancellationReason>)
    @Query("delete from agency_momo_account where agency_id = :agencyId and phone_number in (:phoneNumbers) ")
    fun deleteMoMoAccounts(agencyId: String, phoneNumbers: List<String>)
    @Query("delete from agency_om_account where agency_id = :agencyId and phone_number in (:phoneNumbers) ")
    fun deleteOMAccounts(agencyId: String, phoneNumbers: List<String>)
    @Query("delete from agency_paypal_account where agency_id = :agencyId and email in (:emails)")
    fun deletePayPalAccounts(agencyId: String, emails: List<String>)
    @Query("delete from agency_legal_docs where agency_id = :agencyId")
    fun deleteAgencyLegalDocs(agencyId: String)
    @Query("delete from agency_graphics where agency_id = :agencyId ")
    fun deleteAgencyGraphics(agencyId: String)
}
//    /**
//     * gets all scanner's associated permissions
//     */
//    @Query("SELECT * FROM StationJobs WHERE station = :station AND jobId IN (:ids)")
//    fun stationJobsFromIds(station: String, ids: List<String>): List<StationJob>
//
//    @Query("SELECT * FROM StationJobs WHERE station = :station")
//    fun stationJobs(station: String): List<StationJob>
//
//    @Query("SELECT * FROM StationJobs WHERE station = :station")
//    fun observeStationJobs(station: String): Flow<List<StationJob>>
//
//    @Insert
//    fun saveStationJobs(stationJobs: List<StationJob>)
//
//    @Query("SELECT * FROM Scanners WHERE agency_id = :agency")
//    fun scannersFromAgency(agency: String): List<Scanner>
//
//    @Query("SELECT * FROM Scanners WHERE booker_id IN (:ids)")
//    fun scannersFromIds(ids: List<String>): List<Scanner>
//
//    @Query("SELECT * FROM StationScannerMaps WHERE station = :station")
//    fun observeStationScannerMaps(station: String): Flow<List<StationScannerMap>>
//
//    @Query("SELECT * FROM StationScannerMaps WHERE station = :station")
//    fun stationScannerMaps(station: String): List<StationScannerMap>
//
//    @Insert
//    fun saveStationScannerMaps(maps: List<StationScannerMap>)
//
//    @Query("DELETE FROM StationScannerMaps WHERE station = :station AND scanner IN (:scanners)")
//    fun deleteStationScannerMap(station: String, scanners: List<String>)
//
//    /**
//     * Stations
//     */
//    @Insert(onConflict = REPLACE)
//    fun saveStations(stations: List<Station>)
//
//    @Query("UPDATE Stations SET lat = :lat, lon = :lon WHERE id = :id")
//    fun saveStationsLocation(id: String, lat: Double, lon: Double)
//
//    @Query("DELETE FROM StationTownMaps WHERE station = :station AND town IN (:towns)")
//    fun deleteStationTownMaps(station: String, towns: List<String>)
//
//    @Query("SELECT * FROM Stations WHERE id IN (:ids)")
//    fun observeStationsFromIds(ids: List<String>): Flow<List<Station>>
//
//    /**
//     * Associates [Station] to [Town] by their id
//     */
//    @Insert(onConflict = REPLACE)
//    fun saveStationTownMaps(stationTownMaps: List<StationTownMap>)
//
//    @Query("SELECT * FROM StationTownMaps WHERE station = :station")
//    fun observeStationTownMaps(station: String): Flow<List<StationTownMap>>
//
//    @Query("SELECT * FROM StationTownMaps WHERE station = :station")
//    fun stationTownMaps(station: String): List<StationTownMap>
//
//    @Query("SELECT * FROM Stations WHERE id IN (:ids)")
//    fun stationsFromIds(ids: List<String>): List<Station>
//
//    @Query("SELECT * FROM Stations")
//    fun stations(): List<Station>
//}