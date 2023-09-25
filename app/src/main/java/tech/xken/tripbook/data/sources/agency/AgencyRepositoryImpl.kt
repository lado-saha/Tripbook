package tech.xken.tripbook.data.sources.agency

import android.util.Log
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.DbAction.*
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.agency.AgencyAccount
import tech.xken.tripbook.data.models.agency.AgencyEmailSupport
import tech.xken.tripbook.data.models.agency.AgencyPhoneSupport
import tech.xken.tripbook.data.models.agency.AgencyRefundPolicy
import tech.xken.tripbook.data.models.agency.AgencySocialSupport
import tech.xken.tripbook.data.models.agency.TripCancellationReason
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.agency.remote.AgencyRemoteDataSource

class AgencyRepositoryImpl(
    private val localDS: AgencyDataSource,
    private val remoteDS: AgencyDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AgencyRepository {
    init {
        CoroutineScope(ioDispatcher).launch {
            (remoteDS as AgencyRemoteDataSource).client.realtime.connect()
            remoteDS.channel.join()
        }
    }
//    override val channel: RealtimeChannel = remoteDS.channel!!

    override suspend fun agencyAccountFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyAccountLastModifiedOn(agencyId).data.let { instant ->
            remoteDS.agencyAccountLatestLog(
                agencyId,
                instant ?: Instant.fromEpochSeconds(0L)
            ).also {
                when (it) {
                    is Failure -> Log.e("A_repo_FS_account", "${it.exception}")
                    is Success -> {
                        when (it.data.dbAction) {
                            INSERT -> localDS.createAgencyAccount(it.data.data!!)
                            UPDATE -> localDS.updateAgencyAccount(agencyId, it.data.data!!)
                            DELETE -> Success(Unit)
                        }
                    }
                }
            }
        }
        Success(Unit)
    }

    override suspend fun emailSupportsFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyEmailSupportsLastModifiedOn(agencyId).data.let { instant ->
            remoteDS.emailSupportLatestLogs(
                agencyId,
                instant ?: Instant.fromEpochSeconds(0L)
            ).also { res ->
                when (res) {
                    is Failure -> Log.e("A_repo_FS_Email", "${res.exception}")
                    is Success -> {
                        res.data
                            .groupBy { it.dbAction }
                            .map { group ->
                                Log.d("A_repo_FS_Email", "$group")
                                when (group.key) {
                                    INSERT -> {
                                        try {
                                            localDS.createEmailSupports(group.value.map { it.data!! })
                                        } catch (e: Exception) {
                                            Log.e("A_repo_FS_Email1", "$group")
                                        }
                                    }

                                    UPDATE -> localDS.updateEmailSupports(
                                        agencyId,
                                        group.value.map { it.data!! })

                                    DELETE -> localDS.deleteEmailSupports(
                                        agencyId,
                                        group.value.map { it.email })
                                }
                            }
                    }
                }
            }
        }.run {
            Success(Unit)
        }
    }

    override suspend fun phoneSupportsFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyPhoneSupportsLastModifiedOn(agencyId).data.let { instant ->
            remoteDS.phoneSupportsLatestLogs(
                agencyId,
                instant ?: Instant.fromEpochSeconds(0L)
            ).also { res ->
                when (res) {
                    is Failure -> Log.e("A_repo_FS_Phone", "${res.exception}")

                    is Success -> {
                        res.data
                            .groupBy { it.dbAction }
                            .map { group ->
                                when (group.key) {
                                    INSERT -> localDS.createPhoneSupports(group.value.map { it.data!! })
                                    UPDATE -> localDS.updatePhoneSupports(
                                        agencyId,
                                        group.value.map { it.data!! })

                                    DELETE -> localDS.deletePhoneSupports(
                                        agencyId,
                                        group.value.map { it.phoneCode },
                                        group.value.map { it.phoneNumber }
                                    )
                                }
                            }.run { Success(Unit) }
                    }
                }
            }.run {
                Success(Unit)
            }
        }
    }

    override suspend fun socialSupportFullSync(agencyId: String) =
        withContext(ioDispatcher) {
            localDS.agencySocialSupportsLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.socialSupportLatestLogs(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                ).also {
                    when (it) {
                        is Failure -> {
                            Log.e("A_repo_FS_social", it.exception.toString())
                        }

                        is Success -> {
                            when (it.data.dbAction) {
                                INSERT -> localDS.createSocialSupport(it.data.data!!)

                                UPDATE -> localDS.updateSocialSupport(
                                    agencyId,
                                    it.data.data!!
                                )

                                DELETE -> localDS.deleteSocialSupport(agencyId)
                            }
                        }
                    }
                }
            }.run {
                Success(Unit)
            }
        }

    override suspend fun refundPoliciesFullSync(agencyId: String) =
        withContext(ioDispatcher) {
            localDS.agencyRefundPoliciesLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.refundPolicyLatestLogs(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                )
                    .also { res ->
                        when (res) {
                            is Failure -> {
                                Log.e("A_repo_FS_Policy", "${res.exception}")
                            }

                            is Success -> {
                                res.data
                                    .groupBy { it.dbAction }
                                    .map { group ->
                                        when (group.key) {
                                            INSERT -> localDS.createRefundPolicies(group.value.map { it.data!! })
                                            UPDATE -> localDS.updateRefundPolicies(
                                                agencyId,
                                                group.value.map { it.data!! })

                                            DELETE -> localDS.deleteRefundPolicies(
                                                agencyId,
                                                group.value.map { it.reason }
                                            )
                                        }
                                    }
                            }
                        }
                    }.run {
                        Success(Unit)
                    }
            }
        }

    override suspend fun agencyAccount(agencyId: String) = localDS.agencyAccount(agencyId)

    override fun agencyAccountLogFlow(agencyId: String) =
        remoteDS.agencyAccountLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT ->
                                remoteDS.agencyAccount(agencyId).also {
                                    localDS.createAgencyAccount(it.data!!)
                                }

                            UPDATE ->
                                remoteDS.agencyAccount(agencyId).also {
                                    localDS.updateAgencyAccount(log.agencyId, it.data!!)
                                }


                            DELETE -> TODO("Never happen for now")
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Account", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun agencyAccountFlow(agencyId: String) = localDS.agencyAccountFlow(agencyId)

    override suspend fun createAgencyAccount(
        offline: Boolean?,
        account: AgencyAccount
    ) = remoteDS.createAgencyAccount(account)

    override suspend fun updateAgencyAccount(
        agencyId: String,
        account: AgencyAccount
    ) = remoteDS.updateAgencyAccount(agencyId, account)

    override fun countAgencyAccount(agencyId: String) = localDS.countAgencyAccount(agencyId)

    override fun emailSupportsLogFlow(agencyId: String) =
        remoteDS.emailSupportsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log: AgencyEmailSupport.Log = flow.data
                        Log.d("A_repo_RS_Email1", "$log")
                        when (log.dbAction) {
                            INSERT -> {
                                remoteDS.emailSupports(agencyId, listOf(log.email)).also {
                                    localDS.createEmailSupports(it.data)
                                }

                            }

                            UPDATE -> remoteDS.emailSupports(agencyId, listOf(log.email))
                                .also {
                                    localDS.updateEmailSupports(
                                        agencyId,
                                        it.data
                                    )
                                }

                            DELETE -> localDS.deleteEmailSupports(
                                agencyId,
                                listOf(log.email)
                            )
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_email", flow.exception.toString())
                    }
                }
            }

    override suspend fun emailSupports(
        agencyId: String,
        emails: List<String>
    ) = localDS.emailSupports(agencyId, emails)

    override fun emailSupportsFlow(agencyId: String) = localDS.emailSupportsFlow(agencyId)

    override suspend fun createEmailSupports(
        offline: Boolean?,
        supports: List<AgencyEmailSupport>
    ) = remoteDS.createEmailSupports(supports)

    override suspend fun updateEmailSupports(
        agencyId: String,
        supports: List<AgencyEmailSupport>
    ) = remoteDS.updateEmailSupports(agencyId, supports)

    override suspend fun deleteEmailSupports(
        agencyId: String,
        emails: List<String>
    ) = remoteDS.deleteEmailSupports(agencyId, emails)

    override fun countEmailSupports(agencyId: String) = localDS.countEmailSupports(agencyId)

    override suspend fun phoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ) = localDS.phoneSupports(agencyId, phoneCodes, phoneNumbers)

    override fun phoneSupportsLogFlow(agencyId: String) =
        remoteDS.phoneSupportsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT -> {
                                // We first get the
                                remoteDS.phoneSupports(
                                    agencyId,
                                    listOf(log.phoneCode),
                                    listOf(log.phoneNumber)
                                ).data.let {
                                    localDS.createPhoneSupports(it)
                                }
                            }

                            UPDATE -> {
                                remoteDS.phoneSupports(
                                    agencyId,
                                    listOf(log.phoneCode),
                                    listOf(log.phoneNumber)
                                ).data.let {
                                    localDS.updatePhoneSupports(agencyId, it)
                                }
                            }

                            DELETE -> localDS.deletePhoneSupports(
                                agencyId,
                                listOf(log.phoneCode),
                                listOf(log.phoneNumber)
                            )
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Phone", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun phoneSupportsFlow(agencyId: String) = localDS.phoneSupportsFlow(agencyId)

    override suspend fun createPhoneSupports(
        offline: Boolean?,
        supports: List<AgencyPhoneSupport>
    ) = remoteDS.createPhoneSupports(supports)

    override suspend fun updatePhoneSupports(
        agencyId: String,
        supports: List<AgencyPhoneSupport>
    ) = remoteDS.updatePhoneSupports(agencyId, supports)

    override suspend fun deletePhoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ) = remoteDS.deletePhoneSupports(agencyId, phoneCodes, phoneNumbers)

    override fun countPhoneSupports(agencyId: String) = localDS.countPhoneSupports(agencyId)

    override suspend fun socialSupport(agencyId: String) = localDS.socialSupport(agencyId)

    override fun socialSupportLogFlow(agencyId: String) =
        remoteDS.socialSupportLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (log.dbAction) {
                            INSERT -> remoteDS.socialSupport(agencyId).also {
                                localDS.createSocialSupport(it.data!!)
                            }

                            UPDATE -> remoteDS.socialSupport(agencyId).also {
                                localDS.updateSocialSupport(agencyId, it.data!!)
                            }

                            DELETE -> localDS.deleteSocialSupport(agencyId)
                        }
                    }

                    is Failure -> Log.e("A_repo_RS_Social", flow.exception.toString())
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun socialSupportFlow(agencyId: String) = localDS.socialSupportFlow(agencyId)

    override suspend fun createSocialSupport(
        offline: Boolean?,
        support: AgencySocialSupport
    ) = remoteDS.createSocialSupport(support)

    override suspend fun updateSocialSupports(
        agencyId: String,
        support: AgencySocialSupport
    ) = remoteDS.updateSocialSupport(agencyId, support)

    override suspend fun deleteSocialSupport(agencyId: String) =
        remoteDS.deleteSocialSupport(agencyId)

    override fun countSocialAccount(agencyId: String) = localDS.countSocialAccount(agencyId)

    override suspend fun refundPolicies(
        agencyId: String,
        reasons: List<TripCancellationReason>
    ): Results<List<AgencyRefundPolicy>> = localDS.refundPolicies(agencyId, reasons)

    override fun refundPoliciesLogFlow(agencyId: String) =
        remoteDS.refundPoliciesLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT -> remoteDS.refundPolicies(
                                agencyId,
                                listOf(log.reason)
                            ).data.let {
                                localDS.createRefundPolicies(it)
                            }

                            UPDATE -> remoteDS.refundPolicies(
                                agencyId,
                                listOf(log.reason)
                            ).data.let {
                                localDS.updateRefundPolicies(agencyId, it)
                            }

                            DELETE -> localDS.deleteRefundPolicies(
                                agencyId,
                                listOf(log.reason)
                            )
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Policy", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun refundPoliciesFlow(agencyId: String) = localDS.refundPoliciesFlow(agencyId)
    override suspend fun createRefundPolicies(
        offline: Boolean?,
        policies: List<AgencyRefundPolicy>
    ) = remoteDS.createRefundPolicies(policies)

    override suspend fun updateRefundPolicies(
        agencyId: String,
        policies: List<AgencyRefundPolicy>
    ) = remoteDS.updateRefundPolicies(agencyId, policies)

    override suspend fun deleteRefundPolicies(
        agencyId: String,
        reasons: List<TripCancellationReason>
    ) = remoteDS.deleteRefundPolicies(agencyId, reasons)

    override fun countRefundPolicies(agencyId: String) =
        localDS.countRefundPolicies(agencyId)
}

//
//    override suspend fun saveStationJobs(stationJobs: List<StationJob>) =
//        localAgencySource.saveStationJobs(stationJobs)
//
//    override fun observeStationJobs(station: String): Flow<Results<List<StationJob>>> =
//        localAgencySource.observeStationJobs(station)
//
//    override suspend fun stationJobsFromIds(
//        station: String,
//        ids: List<String>,
//    ): Results<List<StationJob>> = localAgencySource.stationJobsFromIds(station, ids)
//
//    override suspend fun stationJobs(station: String) = localAgencySource.stationJobs(station)
//
//    override suspend fun scannersFromIds(
//        ids: List<String>,
//        station: String,
//        getBookers: Boolean,
//        getJobs: Boolean,
//    ): Results<Map<String, Scanner>> {
//        val scanners = mutableMapOf<String, Scanner>()
//        var exception: Exception? = null
//        //Get scanner
//        when (val results = localAgencySource.scannersFromIds(ids)) {
//            is Success -> scanners += results.data.associateBy { it.bookerID }
//            is Failure -> exception = results.exception
//        }
//        //Get booker associated with scanner
//        if (getBookers && exception == null)
//            when (val results = bookingRepo.bookerFromId(scanners.keys.toList())) {
//                is Success -> results.data.forEach {
//                    scanners[it.bookerId]!!.apply { booker = it }
//                }
//                is Failure -> exception = results.exception
//            }
//        //Get jobs ids associated with each scanner
//        if (getJobs && exception == null)
//            when (
//                val results = localAgencySource.stationScannerMaps(
//                    station = station,
//                    scanners = scanners.keys.toList()
//                )
//            ) {
//                is Failure -> exception = results.exception
//                is Success -> results.data.forEach {
//                    scanners[it.scanner]!!.jobIds?.plusAssign(it.job)
//                }
//            }
//        //Get Permissions ids
//        /*if (getPermissions && exception == null)
//            when (val results =
//                localAgencySource.stationJobPermissionsMaps(jobs = scanners.values.map { it. })) {
//                is Success -> results.data.forEach {
//                    scanners[it.scanner]!!.apply {
//                        if (permissionIds == null) permissionIds = mutableListOf(it.permission)
//                        else permissionIds!!.add(it.permission)
//                    }
//                }
//                is Failure -> exception = results.exception
//            }*/
//        return if (exception == null) Success(scanners) else Failure(exception)
//    }
//
//    override suspend fun scanners(
//        agency: String,
//        station: String,
//        getBookers: Boolean,
//        getJobs: Boolean,
//    ): Results<Map<String, Scanner>> {
//        val scanners = mutableMapOf<String, Scanner>()
//        var exception: Exception? = null
//        when (val results = localAgencySource.scanners(agency)) {
//            is Success -> scanners += results.data.associateBy { it.bookerID }
//            is Failure -> exception = results.exception
//        }
//        if (getBookers && exception == null)
//            when (val results = bookingRepo.bookerFromId(scanners.keys.toList())) {
//                is Success -> results.data.forEach {
//                    scanners[it.bookerId]!!.apply { booker = it }
//                }
//                is Failure -> exception = results.exception
//            }
//        /*if (getPermissions && exception == null)
//            when (val results =
//                localAgencySource.stationJobPermissionsMaps(scanners = scanners.keys.toList())) {
//                is Success -> results.data.forEach {
//                    scanners[it.scanner]!!.apply {
//                        if (permissionIds == null) permissionIds = mutableListOf(it.permission)
//                        else permissionIds!!.add(it.permission)
//                    }
//                }
//                is Failure -> exception = results.exception
//            }*/
//        if (getJobs && exception == null)
//            when (
//                val results = localAgencySource.stationScannerMaps(
//                    station = station,
//                    scanners = scanners.keys.toList()
//                )
//            ) {
//                is Failure -> exception = results.exception
//                is Success -> results.data.forEach {
//                    scanners[it.scanner]!!.jobIds?.plusAssign(it.job)
//                }
//            }
//
//        return if (exception == null) Success(scanners) else Failure(exception)
//    }
//
//
//    override fun observeStationScannerMaps(station: String) =
//        localAgencySource.observeStationScannerMaps(station)
//
//    override suspend fun stationScannerMaps(station: String, scanners: List<String>) =
//        localAgencySource.stationScannerMaps(station, scanners)
//
//    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) =
//        localAgencySource.saveStationScannerMaps(maps)
//
//    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) =
//        localAgencySource.deleteStationScannerMap(station, scanners)
//
//    override suspend fun saveStations(stations: List<Station>) =
//        localAgencySource.saveStations(stations)
//
//    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) =
//        localAgencySource.saveStationTownMaps(stationTownMaps)
//
//    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) =
//        localAgencySource.deleteStationTownMaps(station, towns)
//
//    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) =
//        localAgencySource.saveStationLocation(id, lat, lon)
//
//    override fun observeStationTownMaps(station: String) =
//        localAgencySource.observeStationTownMaps(station)
//
//    override fun observeStationsFromIds(ids: List<String>) =
//        localAgencySource.observeStationsFromIds(ids)
//
//    override suspend fun stationTownMaps(station: String) =
//        localAgencySource.stationTownMaps(station)
//
//    override suspend fun stations() = localAgencySource.stations()
//
//    override suspend fun stationsFromIds(ids: List<String>) = localAgencySource.stationsFromIds(ids)
//
//    /**
//     * Returns the [Station] from the ids gotten from [StationTownMap]
//     */
////    override suspend fun parksFromTown(town: String) =
////        when(val townParkMaps = localAgencySource.townParkMapFromTown(town)){
////            is Results.Success -> {
////                val parkIds = townParkMaps.data.map { it.park }
////                localAgencySource.parksFromIds(parkIds)
////            }
////            is Results.Failure -> {
////                throw townParkMaps.exception
////            }
////        }
//
//    /**
//     * Returns the [Station] from the ids gotten from [StationTownMap]
//     */
////    override suspend fun town(station: String) = when(val townParkMaps = localAgencySource.townParkMapFromTown(station)){
////        is Results.Success -> {
////            val townIds = townParkMaps.data.map { it.town }
////            localUnivSource.townsFromIds(townIds)
////        }
////        is Results.Failure -> {
////            throw townParkMaps.exception
////        }
////    }
//}