package tech.xken.tripbook.data.sources.agency

import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.DbAction
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

class AgencyRepositoryImpl(
    private val localDS: AgencyDataSource,
    private val remoteDS: AgencyDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AgencyRepository {
    override val channel: RealtimeChannel = remoteDS.channel!!

    override suspend fun agencyAccountFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyAccountLastModifiedOn(agencyId).data?.let { instant ->
            remoteDS.agencyAccountLatestLog(
                agencyId,
                instant
            ).data.run {
                when (dbAction) {
                    DbAction.INSERT -> localDS.createAgencyAccount(data!!)
                    DbAction.UPDATE -> localDS.updateAgencyAccount(agencyId, data!!)
                    DbAction.DELETE -> Success(Unit)
                }
            }
        }
        Success(Unit)
    }

    override suspend fun emailSupportsFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyEmailSupportsLastModifiedOn(agencyId).data?.let { instant ->
            remoteDS.emailSupportLatestLogs(
                agencyId,
                instant
            )
                .data
                .groupBy { it.dbAction }
                .map { group ->
                    when (group.key) {
                        DbAction.INSERT -> localDS.createEmailSupports(group.value.map { it.data!! })
                        DbAction.UPDATE -> localDS.updateEmailSupports(
                            agencyId,
                            group.value.map { it.data!! })

                        DbAction.DELETE -> localDS.deleteEmailSupports(
                            agencyId,
                            group.value.map { it.email })
                    }
                }
        }
        Success(Unit)
    }

    override suspend fun phoneSupportsFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyPhoneSupportsLastModifiedOn(agencyId).data?.let { instant ->
            remoteDS.phoneSupportsLatestLogs(
                agencyId,
                instant
            )
                .data
                .groupBy { it.dbAction }
                .map { group ->
                    when (group.key) {
                        DbAction.INSERT -> localDS.createPhoneSupports(group.value.map { it.data!! })
                        DbAction.UPDATE -> localDS.updatePhoneSupports(
                            agencyId,
                            group.value.map { it.data!! })

                        DbAction.DELETE -> localDS.deletePhoneSupports(
                            agencyId,
                            group.value.map { it.phoneCode },
                            group.value.map { it.phoneNumber }
                        )
                    }
                }.run { Success(Unit) }
        }
        Success(Unit)
    }

    override suspend fun socialSupportFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencySocialSupportsLastModifiedOn(agencyId).data?.let { instant ->
            remoteDS.socialSupportLatestLogs(
                agencyId,
                instant
            ).data.run {
                when (dbAction) {
                    DbAction.INSERT -> localDS.createSocialSupport(data!!)
                    DbAction.UPDATE -> localDS.updateSocialSupports(agencyId, data!!)
                    DbAction.DELETE -> localDS.deleteSocialSupport(agencyId)
                }
            }
        }
        Success(Unit)
    }

    override suspend fun refundPoliciesFullSync(agencyId: String) = withContext(ioDispatcher) {
        localDS.agencyRefundPoliciesLastModifiedOn(agencyId).data?.let { instant ->
            remoteDS.refundPolicyLatestLogs(
                agencyId,
                instant
            )
                .data
                .groupBy { it.dbAction }
                .map { group ->
                    when (group.key) {
                        DbAction.INSERT -> localDS.createRefundPolicies(group.value.map { it.data!! })
                        DbAction.UPDATE -> localDS.updateRefundPolicies(
                            agencyId,
                            group.value.map { it.data!! })

                        DbAction.DELETE -> localDS.deleteRefundPolicies(
                            agencyId,
                            group.value.map { it.reason }
                        )
                    }
                }
        }
        Success(Unit)
    }

    override suspend fun agencyAccount(agencyId: String) = localDS.agencyAccount(agencyId)


    override fun agencyAccountLogFlow(agencyId: String) =
        remoteDS.agencyAccountLogFlow(agencyId)
            .onEach { flow ->
                if (flow is Success) {
                    when (flow.data.dbAction) {
                        DbAction.INSERT -> {
                            remoteDS.agencyAccount(agencyId).data.let {
                                localDS.createAgencyAccount(it!!)
                            }
                        }

                        DbAction.UPDATE -> {
                            remoteDS.agencyAccount(agencyId).data.let {
                                localDS.updateAgencyAccount(it!!.agencyId, it)
                            }
                        }

                        DbAction.DELETE -> {
                            TODO("Never happen for now")
                        }
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

    override suspend fun countAgencyAccount(agencyId: String) = localDS.countAgencyAccount(agencyId)

    override fun emailSupportsLogFlow(agencyId: String) =
        remoteDS.emailSupportsLogFlow(agencyId)
            .onEach { flow ->
                if (flow is Success) {
                    val result: AgencyEmailSupport.Log = flow.data
                    when (result.dbAction) {
                        DbAction.INSERT -> localDS.createEmailSupports(listOf(result.data!!))
                        DbAction.UPDATE -> localDS.updateEmailSupports(
                            agencyId,
                            listOf(result.data!!)
                        )

                        DbAction.DELETE -> localDS.deleteEmailSupports(
                            agencyId,
                            listOf(result.email)
                        )
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
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

    override suspend fun countEmailSupports(agencyId: String) = localDS.countEmailSupports(agencyId)

    override suspend fun phoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ) = localDS.phoneSupports(agencyId, phoneCodes, phoneNumbers)

    override fun phoneSupportsLogFlow(agencyId: String) = remoteDS.phoneSupportsLogFlow(agencyId)
        .onEach { flow ->
            if (flow is Success) {
                val log = flow.data
                when (flow.data.dbAction) {
                    DbAction.INSERT -> {
                        remoteDS.phoneSupports(
                            agencyId,
                            listOf(log.phoneCode),
                            listOf(log.phoneNumber)
                        ).data.let {
                            localDS.createPhoneSupports(it)
                        }
                    }

                    DbAction.UPDATE -> {
                        remoteDS.phoneSupports(
                            agencyId,
                            listOf(log.phoneCode),
                            listOf(log.phoneNumber)
                        ).data.let {
                            localDS.updatePhoneSupports(agencyId, it)
                        }
                    }

                    DbAction.DELETE -> localDS.deletePhoneSupports(
                        agencyId,
                        listOf(log.phoneCode),
                        listOf(log.phoneNumber)
                    )
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

    override suspend fun countPhoneSupports(agencyId: String) = localDS.countPhoneSupports(agencyId)

    override suspend fun socialSupport(agencyId: String) = localDS.socialSupport(agencyId)
    override fun socialSupportLogFlow(agencyId: String) = remoteDS.socialSupportLogFlow(agencyId)
        .onEach { flow ->
            if (flow is Success) {
                val log = flow.data
                when (flow.data.dbAction) {
                    DbAction.INSERT -> {
                        remoteDS.socialSupport(agencyId).data!!.let {
                            localDS.createSocialSupport(it)
                        }
                    }

                    DbAction.UPDATE -> {
                        remoteDS.socialSupport(agencyId).data!!.let {
                            localDS.updateSocialSupports(agencyId, it)
                        }
                    }

                    DbAction.DELETE -> localDS.deleteSocialSupport(agencyId)
                }
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
    ) = remoteDS.updateSocialSupports(agencyId, support)

    override suspend fun deleteSocialSupport(agencyId: String) =
        remoteDS.deleteSocialSupport(agencyId)

    override suspend fun countSocialAccount(agencyId: String) = localDS.countSocialAccount(agencyId)

    override suspend fun refundPolicies(
        agencyId: String,
        reasons: List<TripCancellationReason>
    ): Results<List<AgencyRefundPolicy>> = localDS.refundPolicies(agencyId, reasons)

    override fun refundPoliciesLogFlow(agencyId: String) = remoteDS.refundPoliciesLogFlow(agencyId)
        .onEach { flow ->
            if (flow is Success) {
                val log = flow.data
                when (flow.data.dbAction) {
                    DbAction.INSERT -> remoteDS.refundPolicies(agencyId, listOf(log.reason)).data.let {
                        localDS.createRefundPolicies(it)
                    }

                    DbAction.UPDATE -> remoteDS.refundPolicies(agencyId, listOf(log.reason)).data.let {
                        localDS.updateRefundPolicies(agencyId, it)
                    }

                    DbAction.DELETE -> localDS.deleteRefundPolicies(agencyId, listOf(log.reason))
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

    override suspend fun countRefundPolicies(agencyId: String) =
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