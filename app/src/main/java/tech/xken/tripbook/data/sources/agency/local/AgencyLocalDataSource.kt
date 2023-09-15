package tech.xken.tripbook.data.sources.agency.local

import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.agency.AgencyAccount
import tech.xken.tripbook.data.models.agency.AgencyEmailSupport
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs
import tech.xken.tripbook.data.models.agency.AgencyPhoneSupport
import tech.xken.tripbook.data.models.agency.AgencyRefundPolicy
import tech.xken.tripbook.data.models.agency.AgencySocialSupport
import tech.xken.tripbook.data.models.agency.TripCancellationReason
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import kotlin.coroutines.CoroutineContext


class AgencyLocalDataSource internal constructor(
    private val dao: AgencyDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : AgencyDataSource {
    override val channel: RealtimeChannel? = null


    override suspend fun agencyAccountLastModifiedOn(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(dao.agencyAccountLastModifiedOn(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun agencyEmailSupportsLastModifiedOn(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyEmailSupportsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyPhoneSupportsLastModifiedOn(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyPhoneSupportsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencySocialSupportsLastModifiedOn(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencySocialSupportsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyRefundPoliciesLastModifiedOn(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyRefundPoliciesLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyAccount(
        agencyId: String,
    ): Results<AgencyAccount?> = withContext(ioDispatcher) {
        try {
            Success(dao.agencyAccount(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun agencyAccountLatestLog(
        agencyId: String,
        fromInstant: Instant?
    ): Results<AgencyAccount.Log> {
        TODO("Not to be called from remote.")
    }

    override fun agencyAccountLogFlow(agencyId: String) =
        TODO("Not to be called from remote.")


    override fun agencyAccountFlow(agencyId: String) =
        dao.agencyAccountFlow(agencyId).map { Success(it) }

    override suspend fun createAgencyAccount(
        account: AgencyAccount
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.createAgencyAccount(account).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateAgencyAccount(
        agencyId: String,
        account: AgencyAccount
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.updateAgencyAccount(account).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countAgencyAccount(agencyId: String) =
        dao.agencyAccountCount(agencyId).map { Success(it) }

    override suspend fun emailSupports(
        agencyId: String,
        emails: List<String>
    ): Results<List<AgencyEmailSupport>> =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyEmailSupports(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun emailSupportLatestLogs(
        agencyId: String,
        fromInstant: Instant?
    ): Results<List<AgencyEmailSupport.Log>> {
        TODO("Not to be called from remote.")
    }

    override fun emailSupportsLogFlow(agencyId: String) = TODO("Not to be called from remote.")

    override fun emailSupportsFlow(agencyId: String) =
        dao.agencyEmailSupportsFlow(agencyId).map { Success(it) }

    override suspend fun createEmailSupports(
        supports: List<AgencyEmailSupport>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.createEmailSupports(supports).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateEmailSupports(
        agencyId: String,
        supports: List<AgencyEmailSupport>
    ): Results<List<AgencyEmailSupport?>> = withContext(ioDispatcher) {
        try {
            Success(dao.updateEmailSupports(supports).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteEmailSupports(
        agencyId: String,
        emails: List<String>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.deleteEmailSupports(agencyId, emails))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countEmailSupports(agencyId: String) =
        dao.agencyEmailSupportsCount(agencyId).map { Success(it) }

    override suspend fun phoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ): Results<List<AgencyPhoneSupport>> {
        TODO("Not to be called from remote.")
    }

    override suspend fun phoneSupportsLatestLogs(
        agencyId: String,
        fromInstant: Instant?
    ): Results<List<AgencyPhoneSupport.Log>> {
        TODO("Not to be called from remote.")
    }

    override fun phoneSupportsLogFlow(agencyId: String) =
        TODO("Not to be called from remote.")


    override fun phoneSupportsFlow(agencyId: String): Flow<Results<List<AgencyPhoneSupport>>> =
        dao.agencyPhoneSupportsFlow(agencyId).map { Success(it) }

    override suspend fun createPhoneSupports(
        supports: List<AgencyPhoneSupport>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.createPhoneSupports(supports).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updatePhoneSupports(
        agencyId: String,
        supports: List<AgencyPhoneSupport>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.updatePhoneSupports(supports).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deletePhoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.deletePhoneSupports(agencyId, phoneCodes, phoneNumbers))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countPhoneSupports(agencyId: String) =
        dao.agencyEmailSupportsCount(agencyId).map { Success(it) }

    override suspend fun socialSupport(
        agencyId: String,
    ): Results<AgencySocialSupport?> = withContext(ioDispatcher) {
        try {
            Success(dao.agencySocialSupports(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun socialSupportLatestLogs(
        agencyId: String,
        fromInstant: Instant?
    ): Results<AgencySocialSupport.Log> {
        TODO("Not to be called from remote.")
    }

    override fun socialSupportLogFlow(agencyId: String)= TODO("Not to be called from remote.")


    override fun socialSupportFlow(agencyId: String) =
        dao.agencySocialSupportFlow(agencyId).map { Success(it) }

    override suspend fun createSocialSupport(
        support: AgencySocialSupport
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.createSocialSupport(support).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateSocialSupports(
        agencyId: String,
        supports: AgencySocialSupport
    ): Results<AgencySocialSupport?> = withContext(ioDispatcher) {
        try {
            Success(dao.updateSocialSupport(supports).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteSocialSupport(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(dao.deleteSocialSupport(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countSocialAccount(agencyId: String): Flow<Results<Long>> =
        dao.agencySocialSupportsCount(agencyId).map { Success(it) }

    override suspend fun refundPolicies(
        agencyId: String,
        reasons: List<TripCancellationReason>,
    ): Results<List<AgencyRefundPolicy>> =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyRefundPolicies(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }


    override suspend fun refundPolicyLatestLogs(
        agencyId: String,
        fromInstant: Instant?
    ): Results<List<AgencyRefundPolicy.Log>> {
        TODO("Not to be called from remote.")
    }

    override fun refundPoliciesLogFlow(agencyId: String) =
        TODO("Not to be called from remote.")


    override fun refundPoliciesFlow(agencyId: String) =
        dao.agencyRefundPolicyFlow(agencyId).map { Success(it) }

    override suspend fun createRefundPolicies(
        policies: List<AgencyRefundPolicy>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.createRefundPolicies(policies).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateRefundPolicies(
        agencyId: String,
        policies: List<AgencyRefundPolicy>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.updateRefundPolicies(policies).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteRefundPolicies(
        agencyId: String,
        reasons: List<TripCancellationReason>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.deleteRefundPolicies(agencyId, reasons))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countRefundPolicies(agencyId: String) =
        dao.agencyRefundPoliciesCount(agencyId).map { Success(it) }

    override suspend fun legalDocs(
        agencyId: String
    ): Results<AgencyLegalDocs> {
        TODO("Not yet implemented")
    }

    override suspend fun legalDocsLatestLog(
        agencyId: String,
        fromInstant: Instant?
    ): Results<AgencyLegalDocs.Log> {
        TODO("Not yet implemented")
    }

    override fun legalDocsLogFlow(agencyId: String): Flow<Results<AgencyLegalDocs.Log>> {
        TODO("Not yet implemented")
    }

    override fun legalDocsFlow(agencyId: String): Flow<Results<AgencyLegalDocs>> {
        TODO("Not yet implemented")
    }

    override suspend fun createLegalDocs(

        docs: AgencyLegalDocs
    ): Results<AgencyLegalDocs?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLegalDocs(
        agencyId: String,
        docs: List<AgencyLegalDocs>
    ): Results<AgencyLegalDocs?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLegalDocs(agencyId: String): Results<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun legalDocsCount(agencyId: String): Results<Long> {
        TODO("Not yet implemented")
    }
}
//
//    override fun observeStationJobs(station: String) =
//        dao.observeStationJobs(station).map { Success(it) }
//
//    override suspend fun stationScannerMaps(
//        station: String,
//        scanners: List<String>,
//    ) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stationScannerMaps(station))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//
//    override suspend fun stationJobsFromIds(station: String, ids: List<String>) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.stationJobsFromIds(station, ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//
//
//    override suspend fun stationJobs(station: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stationJobs(station))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun saveStationJobs(stationJobs: List<StationJob>) =
//        withContext(ioDispatcher) {
//            dao.saveStationJobs(stationJobs)
//        }
//
//
//    override suspend fun saveStations(stations: List<Station>) = withContext(ioDispatcher) {
//        dao.saveStations(stations)
//    }
//
//    override suspend fun scanners(agency: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.scannersFromAgency(agency))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun scannersFromIds(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.scannersFromIds(ids))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override fun observeStationScannerMaps(station: String) =
//        dao.observeStationScannerMaps(station).map { Success(it) }
//
////    override suspend fun stationScannerMapsFromStation(station: String) = withContext(ioDispatcher) {
////        return@withContext try {
////            Success(dao.stationScannerMaps(station))
////        } catch (e: Exception) {
////            Failure(e)
////        }
////    }
//
//    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) =
//        withContext(ioDispatcher) {
//            dao.saveStationScannerMaps(maps)
//        }
//
//    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) =
//        withContext(ioDispatcher) {
//            dao.deleteStationTownMaps(station, scanners)
//        }
//
//    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) =
//        withContext(ioDispatcher) {
//            dao.saveStationTownMaps(stationTownMaps)
//        }
//
//    override suspend fun stations(): Results<List<Station>> = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stations())
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun stationsFromIds(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stationsFromIds(ids))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override fun observeStationsFromIds(ids: List<String>) =
//        dao.observeStationsFromIds(ids).map { Success(it) }
//
//    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) =
//        withContext(ioDispatcher) {
//            dao.saveStationsLocation(id, lat, lon)
//        }
//
//    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) =
//        withContext(ioDispatcher) {
//            dao.deleteStationTownMaps(station, towns)
//        }
//
//    override fun observeStationTownMaps(station: String) =
//        dao.observeStationTownMaps(station).map { Success(it) }
//
//    override suspend fun stationTownMaps(station: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stationTownMaps(station))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//}