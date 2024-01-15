package tech.xken.tripbook.data.sources.agency.local

import android.util.Log
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
import tech.xken.tripbook.data.models.agency.AgencyGraphics
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs
import tech.xken.tripbook.data.models.agency.AgencyMoMoAccount
import tech.xken.tripbook.data.models.agency.AgencyOMAccount
import tech.xken.tripbook.data.models.agency.AgencyPayPalAccount
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

    // General last modified on
    override suspend fun agencyAccountLastModifiedOn(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                dao.agencyAccountLastModifiedOn(agencyId).also { Log.d("A_repo", it.toString()) }
            )
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

    override suspend fun agencyOMAccountsLastModifiedOn(agencyId: String): Results<Instant?> =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyOMAccountsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyMoMoAccountsLastModifiedOn(agencyId: String): Results<Instant?> =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyMoMoAccountsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyPayPalAccountsLastModifiedOn(agencyId: String): Results<Instant?> =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyPayPalAccountsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyLegalDocsLastModifiedOn(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyLegalDocsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyGraphicsLastModifiedOn(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyGraphicsLastModifiedOn(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }


    // Agency Account
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
        TODO("Not to be called from local.")
    }

    override fun agencyAccountLogFlow(agencyId: String) =
        TODO("Not to be called from local.")

    override fun agencyAccountFlow(agencyId: String) =
        dao.agencyAccountFlow(agencyId).map { Success(it) }

    override suspend fun createAgencyAccount(
        account: AgencyAccount
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertAgencyAccount(account).run { null })
        } catch (e: Exception) {
            Log.d("A_repo", "$e")
            Failure(e)
        }
    }

    override suspend fun updateAgencyAccount(
        agencyId: String,
        account: AgencyAccount
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertAgencyAccount(account).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countAgencyAccount(agencyId: String) =
        dao.agencyAccountCount(agencyId).map { Success(it) }

    // Email Support
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
        TODO("Not to be called from local.")
    }

    override fun emailSupportsLogFlow(agencyId: String) = TODO("Not to be called from local.")

    override fun emailSupportsFlow(agencyId: String) =
        dao.agencyEmailSupportsFlow(agencyId).map { Success(it) }

    override suspend fun createEmailSupports(
        supports: List<AgencyEmailSupport>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertEmailSupports(supports).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateEmailSupports(
        agencyId: String,
        supports: List<AgencyEmailSupport>
    ): Results<List<AgencyEmailSupport?>> = withContext(ioDispatcher) {
        try {
            Success(dao.upsertEmailSupports(supports).run { listOf(null) })
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

    override fun countEmailSupports(agencyId: String) =
        dao.agencyEmailSupportsCount(agencyId).map { Success(it) }

    //Phone support
    override suspend fun phoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ): Results<List<AgencyPhoneSupport>> = withContext(ioDispatcher) {
        try {
            Success(dao.agencyPhoneSupports(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun phoneSupportsLatestLogs(
        agencyId: String,
        fromInstant: Instant?
    ): Results<List<AgencyPhoneSupport.Log>> {
        TODO("Not to be called from local.")
    }

    override fun phoneSupportsLogFlow(agencyId: String) =
        TODO("Not to be called from local.")

    override fun phoneSupportsFlow(agencyId: String): Flow<Results<List<AgencyPhoneSupport>>> =
        dao.agencyPhoneSupportsFlow(agencyId).map { Success(it) }

    override suspend fun createPhoneSupports(
        supports: List<AgencyPhoneSupport>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertPhoneSupports(supports).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updatePhoneSupports(
        agencyId: String,
        supports: List<AgencyPhoneSupport>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertPhoneSupports(supports).run { listOf(null) })
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

    override fun countPhoneSupports(agencyId: String) =
        dao.agencyPhoneSupportsCount(agencyId).map { Success(it) }

    // Social Support
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
        TODO("Not to be called from local.")
    }

    override fun socialSupportLogFlow(agencyId: String) = TODO("Not to be called from local.")

    override fun socialSupportFlow(agencyId: String) =
        dao.agencySocialSupportFlow(agencyId).map { Success(it) }

    override suspend fun createSocialSupport(
        support: AgencySocialSupport
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertSocialSupport(support).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateSocialSupport(
        agencyId: String,
        supports: AgencySocialSupport
    ): Results<AgencySocialSupport?> = withContext(ioDispatcher) {
        try {
            Success(dao.upsertSocialSupport(supports).run { null })
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

    override fun countSocialAccount(agencyId: String): Flow<Results<Long>> =
        dao.agencySocialSupportsCount(agencyId).map { Success(it) }

    //Refund policies
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
        TODO("Not to be called from local.")
    }

    override fun refundPoliciesLogFlow(agencyId: String) =
        TODO("Not to be called from local.")

    override fun refundPoliciesFlow(agencyId: String) =
        dao.agencyRefundPolicyFlow(agencyId).map { Success(it) }

    override suspend fun createRefundPolicies(
        policies: List<AgencyRefundPolicy>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertRefundPolicies(policies).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateRefundPolicies(
        agencyId: String,
        policies: List<AgencyRefundPolicy>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertRefundPolicies(policies).run { listOf(null) })
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

    override fun countRefundPolicies(agencyId: String) =
        dao.agencyRefundPoliciesCount(agencyId).map { Success(it) }

    override suspend fun agencyGraphics(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(dao.agencyGraphics(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun agencyGraphicsLatestLog(
        agencyId: String,
        fromInstant: Instant?
    ) = TODO("Not to be called from local.")
    // Agency Graphics

    override fun agencyGraphicsLogFlow(agencyId: String) = TODO("Not to be called from local.")
    override fun agencyGraphicsFlow(agencyId: String) =
        dao.agencyGraphicsFlow(agencyId).map { Success(it) }

    override suspend fun createAgencyGraphics(graphics: AgencyGraphics) =
        withContext(ioDispatcher) {
            try {
                Success(dao.upsertAgencyGraphics(graphics).run { null })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateAgencyGraphics(
        agencyId: String,
        graphics: AgencyGraphics
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertAgencyGraphics(graphics).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteAgencyGraphics(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(dao.deleteAgencyGraphics(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countAgencyGraphics(agencyId: String) =
        dao.agencyGraphicsCount(agencyId).map { Success(it) }

    //Legal documents
    override suspend fun legalDocs(
        agencyId: String
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.agencyLegalDocs(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun legalDocsLatestLog(
        agencyId: String,
        fromInstant: Instant?
    ): Results<AgencyLegalDocs.Log> {
        TODO("Not yet implemented")
    }

    override fun legalDocsLogFlow(agencyId: String) = TODO("Not to be called from local.")

    override fun legalDocsFlow(agencyId: String) =
        dao.agencyLegalDocsFlow(agencyId).map { Success(it) }

    override suspend fun createLegalDocs(
        docs: AgencyLegalDocs
    ): Results<AgencyLegalDocs?> = withContext(ioDispatcher) {
        try {
            Success(dao.upsertAgencyLegalDocs(docs).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateLegalDocs(
        agencyId: String,
        docs: AgencyLegalDocs
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertAgencyLegalDocs(docs).run { null })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteLegalDocs(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(dao.deleteAgencyLegalDocs(agencyId))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countLegalDocs(agencyId: String) =
        dao.agencyLegalDocsCount(agencyId).map { Success(it) }

    // MoMo Accounts
    override suspend fun moMoAccountsLatestLogs(agencyId: String, fromInstant: Instant?) =
        TODO("Not to be called from local.")

    override suspend fun moMoAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyMoMoAccounts(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun moMoAccountsLogFlow(agencyId: String): Flow<Results<AgencyMoMoAccount.Log>> =
        TODO("Not to be called from local.")

    override fun moMoAccountsFlow(agencyId: String): Flow<Results<List<AgencyMoMoAccount>>> =
        dao.agencyMoMoAccountsFlow(agencyId).map { Success(it) }

    override suspend fun createMoMoAccounts(accounts: List<AgencyMoMoAccount>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.upsertMoMoAccounts(accounts).run { listOf(null) })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateMoMoAccounts(agencyId: String, accounts: List<AgencyMoMoAccount>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.upsertMoMoAccounts(accounts).run { listOf(null) })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteMoMoAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.deleteMoMoAccounts(agencyId, phoneNumbers))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countMoMoAccounts(agencyId: String) =
        dao.agencyMoMoAccountsCount(agencyId).map { Success(it) }

    // OM Accounts
    override suspend fun oMAccountsLatestLogs(agencyId: String, fromInstant: Instant?) =
        TODO("Not to be called from local.")

    override suspend fun oMAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyOMAccounts(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun oMAccountsLogFlow(agencyId: String): Flow<Results<AgencyOMAccount.Log>> =
        TODO("Not to be called from local.")

    override fun oMAccountsFlow(agencyId: String): Flow<Results<List<AgencyOMAccount>>> =
        dao.agencyOMAccountsFlow(agencyId).map { Success(it) }

    override suspend fun createOMAccounts(accounts: List<AgencyOMAccount>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.upsertOMAccounts(accounts).run { listOf(null) })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateOMAccounts(agencyId: String, accounts: List<AgencyOMAccount>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.upsertOMAccounts(accounts).run { listOf(null) })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteOMAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.deleteOMAccounts(agencyId, phoneNumbers))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countOMAccounts(agencyId: String) =
        dao.agencyOMAccountsCount(agencyId).map { Success(it) }

    // PayPal Accounts
    override suspend fun payPalAccountsLatestLogs(agencyId: String, fromInstant: Instant?) =
        TODO("Not to be called from local.")

    override suspend fun payPalAccounts(agencyId: String, email: List<String>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.agencyPayPalAccounts(agencyId))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun payPalAccountsLogFlow(agencyId: String): Flow<Results<AgencyPayPalAccount.Log>> =
        TODO("Not to be called from local.")

    override fun payPalAccountsFlow(agencyId: String): Flow<Results<List<AgencyPayPalAccount>>> =
        dao.agencyPayPalAccountsFlow(agencyId).map { Success(it) }

    override suspend fun createPayPalAccounts(accounts: List<AgencyPayPalAccount>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.upsertPayPalAccounts(accounts).run { listOf(null) })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updatePayPalAccounts(
        agencyId: String,
        accounts: List<AgencyPayPalAccount>
    ) = withContext(ioDispatcher) {
        try {
            Success(dao.upsertPayPalAccounts(accounts).run { listOf(null) })
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deletePayPalAccounts(agencyId: String, emails: List<String>) =
        withContext(ioDispatcher) {
            try {
                Success(dao.deletePayPalAccounts(agencyId, emails))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countPayPalAccounts(agencyId: String) =
        dao.agencyPayPalAccountsCount(agencyId).map { Success(it) }


}
/*
            Failure(e)
        }
    }

    override fun observeStationScannerMaps(station: String) =
        dao.observeStationScannerMaps(station).map { Success(it) }

//    override suspend fun stationScannerMapsFromStation(station: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stationScannerMaps(station))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }

    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) =
        withContext(ioDispatcher) {
            dao.saveStationScannerMaps(maps)
        }

    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) =
        withContext(ioDispatcher) {
            dao.deleteStationTownMaps(station, scanners)
        }

    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) =
        withContext(ioDispatcher) {
            dao.saveStationTownMaps(stationTownMaps)
        }

    override suspend fun stations(): Results<List<Station>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stations())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun stationsFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationsFromIds(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun observeStationsFromIds(ids: List<String>) =
        dao.observeStationsFromIds(ids).map { Success(it) }

    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) =
        withContext(ioDispatcher) {
            dao.saveStationsLocation(id, lat, lon)
        }

    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) =
        withContext(ioDispatcher) {
            dao.deleteStationTownMaps(station, towns)
        }

    override fun observeStationTownMaps(station: String) =
        dao.observeStationTownMaps(station).map { Success(it) }

    override suspend fun stationTownMaps(station: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationTownMaps(station))
        } catch (e: Exception) {
            Failure(e)
        }
    }


    override fun observeStationJobs(station: String) =
        dao.observeStationJobs(station).map { Success(it) }

    override suspend fun stationScannerMaps(
        station: String,
        scanners: List<String>,
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationScannerMaps(station))
        } catch (e: Exception) {
            Failure(e)
        }
    }


    override suspend fun stationJobsFromIds(station: String, ids: List<String>) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.stationJobsFromIds(station, ids))
            } catch (e: Exception) {
                Failure(e)
            }
        }


    override suspend fun stationJobs(station: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationJobs(station))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun saveStationJobs(stationJobs: List<StationJob>) =
        withContext(ioDispatcher) {
            dao.saveStationJobs(stationJobs)
        }


    override suspend fun saveStations(stations: List<Station>) = withContext(ioDispatcher) {
        dao.saveStations(stations)
    }

    override suspend fun scanners(agency: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.scannersFromAgency(agency))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun scannersFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.scannersFromIds(ids))
        } catch (e: Exception) {}
*/
