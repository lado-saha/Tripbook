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
import tech.xken.tripbook.data.models.DbAction.*
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
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.agency.remote.AgencyRemoteDataSource
import tech.xken.tripbook.data.sources.storage.StorageRepository

class AgencyRepositoryImpl(
    private val localDS: AgencyDataSource,
    private val remoteDS: AgencyDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val storageRepo: StorageRepository
) : AgencyRepository {

    init {
        CoroutineScope(ioDispatcher).launch {
            try {
                (remoteDS as AgencyRemoteDataSource).client.realtime.connect()
                remoteDS.channel.join()
            } catch (_: IllegalStateException) {

            }
        }
    }

    override suspend fun cancelJobs() {
        CoroutineScope(ioDispatcher).launch {
            remoteDS.channel!!.leave()
            println("Left it")
        }
    }

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

    override suspend fun moMoAccountsFullSync(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            localDS.agencyMoMoAccountsLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.moMoAccountsLatestLogs(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                ).also { res ->
                    when (res) {
                        is Failure -> Log.e("A_repo_FS_moMo", "${res.exception}")
                        is Success -> {
                            res.data
                                .groupBy { it.dbAction }
                                .map { group ->
                                    Log.d("A_repo_FS_moMo", "$group")
                                    when (group.key) {
                                        INSERT -> {
                                            try {
                                                localDS.createMoMoAccounts(group.value.map { it.data!! })
                                            } catch (e: Exception) {
                                                Log.e("A_repo_FS_moMo", "$group")
                                            }
                                        }

                                        UPDATE -> localDS.updateMoMoAccounts(
                                            agencyId,
                                            group.value.map { it.data!! })

                                        DELETE -> localDS.deleteMoMoAccounts(
                                            agencyId,
                                            group.value.map { it.phoneNumber })
                                    }
                                }
                        }
                    }
                }
            }.run {
                Success(Unit)
            }
        }

    override suspend fun oMAccountsFullSync(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            localDS.agencyOMAccountsLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.oMAccountsLatestLogs(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                ).also { res ->
                    when (res) {
                        is Failure -> Log.e("A_repo_FS_oM", "${res.exception}")
                        is Success -> {
                            res.data
                                .groupBy { it.dbAction }
                                .map { group ->
                                    Log.d("A_repo_FS_oM", "$group")
                                    when (group.key) {
                                        INSERT -> {
                                            try {
                                                localDS.createOMAccounts(group.value.map { it.data!! })
                                            } catch (e: Exception) {
                                                Log.e("A_repo_FS_oM", "$group")
                                            }
                                        }

                                        UPDATE -> localDS.updateOMAccounts(
                                            agencyId,
                                            group.value.map { it.data!! })

                                        DELETE -> localDS.deleteOMAccounts(
                                            agencyId,
                                            group.value.map { it.phoneNumber })
                                    }
                                }
                        }
                    }
                }
            }.run {
                Success(Unit)
            }
        }

    override suspend fun payPalAccountsFullSync(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            localDS.agencyPayPalAccountsLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.payPalAccountsLatestLogs(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                ).also { res ->
                    when (res) {
                        is Failure -> Log.e("A_repo_FS_payPal", "${res.exception}")
                        is Success -> {
                            res.data
                                .groupBy { it.dbAction }
                                .map { group ->
                                    Log.d("A_repo_FS_payPal", "$group")
                                    when (group.key) {
                                        INSERT -> {
                                            try {
                                                localDS.createPayPalAccounts(group.value.map { it.data!! })
                                            } catch (e: Exception) {
                                                Log.e("A_repo_FS_payPal", "$group")
                                            }
                                        }

                                        UPDATE -> localDS.updatePayPalAccounts(
                                            agencyId,
                                            group.value.map { it.data!! })

                                        DELETE -> localDS.deletePayPalAccounts(
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

    override suspend fun agencyGraphicsFullSync(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            localDS.agencyGraphicsLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.agencyGraphicsLatestLog(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                ).also {
                    when (it) {
                        is Failure -> Log.e("A_repo_FS_graph", "${it.exception}")
                        is Success -> {
                            when (it.data.dbAction) {
                                INSERT -> localDS.createAgencyGraphics(it.data.data!!)
                                UPDATE -> localDS.updateAgencyGraphics(agencyId, it.data.data!!)
                                DELETE -> localDS.deleteAgencyGraphics(agencyId)
                            }
                        }
                    }
                }
            }.run {
                Success(Unit)
            }
        }

    override suspend fun legalDocsFullSync(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            localDS.agencyLegalDocsLastModifiedOn(agencyId).data.let { instant ->
                remoteDS.legalDocsLatestLog(
                    agencyId,
                    instant ?: Instant.fromEpochSeconds(0L)
                ).also {
                    when (it) {
                        is Failure -> Log.e("A_repo_FS_docs", "${it.exception}")
                        is Success -> {
                            when (it.data.dbAction) {
                                INSERT -> localDS.createLegalDocs(it.data.data!!)
                                UPDATE -> localDS.updateLegalDocs(agencyId, it.data.data!!)
                                DELETE -> localDS.deleteLegalDocs(agencyId)
                            }
                        }
                    }
                }
            }.run {
                Success(Unit)
            }
        }

    // Agency account
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

    //   Agency Graphics
    override suspend fun agencyGraphics(agencyId: String) = localDS.agencyGraphics(agencyId)
    override fun agencyGraphicsLogFlow(agencyId: String) =
        remoteDS.agencyGraphicsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT ->
                                remoteDS.agencyGraphics(agencyId).also {
                                    localDS.createAgencyGraphics(it.data!!)
                                }

                            UPDATE ->
                                remoteDS.agencyGraphics(agencyId).also {
                                    localDS.updateAgencyGraphics(log.agencyId, it.data!!)
                                }

                            DELETE -> localDS.deleteAgencyGraphics(log.agencyId)
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Graph", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun agencyGraphicsFlow(agencyId: String) = localDS.agencyGraphicsFlow(agencyId)
    override suspend fun createAgencyGraphics(graphics: AgencyGraphics) =
        remoteDS.createAgencyGraphics(graphics)

    override suspend fun updateAgencyGraphics(agencyId: String, graphics: AgencyGraphics) =
        remoteDS.updateAgencyGraphics(agencyId, graphics)
            .run {
                when (this) {
                    is Failure -> this
                    is Success -> graphics.updateOrDeleteImage(storageRepo)
                }
            }

    override suspend fun deleteAgencyGraphics(agencyId: String) =
        remoteDS.deleteAgencyGraphics(agencyId)
            .run {
                when (this) {
                    is Failure -> this
                    is Success -> AgencyGraphics(agencyId).deleteAllImages(storageRepo)
                }
            }

    override fun countAgencyGraphics(agencyId: String) = localDS.countAgencyGraphics(agencyId)

    // Agency Legal Documents
    override suspend fun legalDocs(agencyId: String) = localDS.legalDocs(agencyId)
    override fun legalDocsLogFlow(agencyId: String) =
        remoteDS.legalDocsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT ->
                                remoteDS.legalDocs(agencyId).also {
                                    localDS.createLegalDocs(it.data!!)
                                }

                            UPDATE ->
                                remoteDS.legalDocs(agencyId).also {
                                    localDS.updateLegalDocs(log.agencyId, it.data!!)
                                }


                            DELETE -> localDS.deleteLegalDocs(log.agencyId)
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Docs", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun legalDocsFlow(agencyId: String) = localDS.legalDocsFlow(agencyId)
    override suspend fun createLegalDocs(docs: AgencyLegalDocs) = remoteDS.createLegalDocs(docs)
    override suspend fun updateLegalDocs(agencyId: String, docs: AgencyLegalDocs) =
        remoteDS.updateLegalDocs(agencyId, docs)
            .run {
                when (this) {
                    is Failure -> this
                    is Success -> docs.updateDocs(storageRepo)
                }
            }

    override suspend fun deleteLegalDocs(agencyId: String) = remoteDS.deleteLegalDocs(agencyId)
        .run {
            when (this) {
                is Failure -> this
                is Success -> {
                    AgencyLegalDocs(agencyId).deleteAllDocs(storageRepo)
                }
            }
        }

    override fun countLegalDocs(agencyId: String) = localDS.countLegalDocs(agencyId)

    // Email
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

    // Phone support
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

    // Social Support
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

    // Refund policy
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

    // MoMo Accounts
    override suspend fun moMoAccounts(agencyId: String, phoneNumbers: List<String>) =
        localDS.moMoAccounts(agencyId, phoneNumbers)

    override fun moMoAccountsLogFlow(agencyId: String) =
        remoteDS.moMoAccountsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT -> {
                                // We first get the
                                remoteDS.moMoAccounts(
                                    agencyId,
                                    listOf(log.phoneNumber)
                                ).data.let {
                                    localDS.createMoMoAccounts(it)
                                }
                            }

                            UPDATE -> {
                                remoteDS.moMoAccounts(
                                    agencyId,
                                    listOf(log.phoneNumber)
                                ).data.let {
                                    localDS.updateMoMoAccounts(agencyId, it)
                                }
                            }

                            DELETE -> localDS.deleteMoMoAccounts(
                                agencyId,
                                listOf(log.phoneNumber)
                            )
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Momo", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun moMoAccountsFlow(agencyId: String) = localDS.moMoAccountsFlow(agencyId)
    override suspend fun createMoMoAccounts(accounts: List<AgencyMoMoAccount>) =
        remoteDS.createMoMoAccounts(accounts)

    override suspend fun updateMoMoAccounts(agencyId: String, accounts: List<AgencyMoMoAccount>) =
        remoteDS.updateMoMoAccounts(agencyId, accounts)

    override suspend fun deleteMoMoAccounts(agencyId: String, phoneNumbers: List<String>) =
        remoteDS.deleteMoMoAccounts(agencyId, phoneNumbers)

    override suspend fun countMoMoAccounts(agencyId: String) = localDS.countMoMoAccounts(agencyId)

    // OM Accounts
    override suspend fun oMAccounts(agencyId: String, phoneNumbers: List<String>) =
        localDS.oMAccounts(agencyId, phoneNumbers)

    override fun oMAccountsLogFlow(agencyId: String) =
        remoteDS.oMAccountsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT -> {
                                // We first get the
                                remoteDS.oMAccounts(
                                    agencyId,
                                    listOf(log.phoneNumber)
                                ).data.let {
                                    localDS.createOMAccounts(it)
                                }
                            }

                            UPDATE -> {
                                remoteDS.oMAccounts(
                                    agencyId,
                                    listOf(log.phoneNumber)
                                ).data.let {
                                    localDS.updateOMAccounts(agencyId, it)
                                }
                            }

                            DELETE -> localDS.deleteOMAccounts(
                                agencyId,
                                listOf(log.phoneNumber)
                            )
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_OM", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun oMAccountsFlow(agencyId: String) = localDS.oMAccountsFlow(agencyId)
    override suspend fun createOMAccounts(accounts: List<AgencyOMAccount>) =
        remoteDS.createOMAccounts(accounts)

    override suspend fun updateOMAccounts(agencyId: String, accounts: List<AgencyOMAccount>) =
        remoteDS.updateOMAccounts(agencyId, accounts)

    override suspend fun deleteOMAccounts(agencyId: String, phoneNumbers: List<String>) =
        remoteDS.deleteOMAccounts(agencyId, phoneNumbers)

    override suspend fun countOMAccounts(agencyId: String) = localDS.countOMAccounts(agencyId)

    // PayPal Accounts
    override suspend fun payPalAccounts(agencyId: String, email: List<String>) =
        localDS.payPalAccounts(agencyId, email)

    override fun payPalAccountsLogFlow(agencyId: String) =
        remoteDS.payPalAccountsLogFlow(agencyId)
            .onEach { flow ->
                when (flow) {
                    is Success -> {
                        val log = flow.data
                        when (flow.data.dbAction) {
                            INSERT -> {
                                // We first get the
                                remoteDS.payPalAccounts(
                                    agencyId,
                                    listOf(log.email)
                                ).data.let {
                                    localDS.createPayPalAccounts(it)
                                }
                            }

                            UPDATE -> {
                                remoteDS.payPalAccounts(
                                    agencyId,
                                    listOf(log.email)
                                ).data.let {
                                    localDS.updatePayPalAccounts(agencyId, it)
                                }
                            }

                            DELETE -> localDS.deletePayPalAccounts(
                                agencyId,
                                listOf(log.email)
                            )
                        }
                    }

                    is Failure -> {
                        Log.e("A_repo_RS_Paypal", flow.exception.toString())
                    }
                }
            }
            .catch {
                emit(Failure(it as Exception))
            }

    override fun payPalAccountsFlow(agencyId: String) = localDS.payPalAccountsFlow(agencyId)
    override suspend fun createPayPalAccounts(accounts: List<AgencyPayPalAccount>) =
        remoteDS.createPayPalAccounts(accounts)

    override suspend fun updatePayPalAccounts(
        agencyId: String,
        accounts: List<AgencyPayPalAccount>
    ) = remoteDS.updatePayPalAccounts(agencyId, accounts)

    override suspend fun deletePayPalAccounts(agencyId: String, emails: List<String>) =
        remoteDS.deletePayPalAccounts(agencyId, emails)

    override suspend fun countPayPalAccounts(agencyId: String) =
        localDS.countPayPalAccounts(agencyId)
}

/*

    override suspend fun saveStationJobs(stationJobs: List<StationJob>) =
        localAgencySource.saveStationJobs(stationJobs)

    override fun observeStationJobs(station: String): Flow<Results<List<StationJob>>> =
        localAgencySource.observeStationJobs(station)

    override suspend fun stationJobsFromIds(
        station: String,
        ids: List<String>,
    ): Results<List<StationJob>> = localAgencySource.stationJobsFromIds(station, ids)

    override suspend fun stationJobs(station: String) = localAgencySource.stationJobs(station)

    override suspend fun scannersFromIds(
        ids: List<String>,
        station: String,
        getBookers: Boolean,
        getJobs: Boolean,
    ): Results<Map<String, Scanner>> {
        val scanners = mutableMapOf<String, Scanner>()
        var exception: Exception? = null
        //Get scanner
        when (val results = localAgencySource.scannersFromIds(ids)) {
            is Success -> scanners += results.data.associateBy { it.bookerID }
            is Failure -> exception = results.exception
        }
        //Get booker associated with scanner
        if (getBookers && exception == null)
            when (val results = bookingRepo.bookerFromId(scanners.keys.toList())) {
                is Success -> results.data.forEach {
                    scanners[it.bookerId]!!.apply { booker = it }
                }
                is Failure -> exception = results.exception
            }
        //Get jobs ids associated with each scanner
        if (getJobs && exception == null)
            when (
                val results = localAgencySource.stationScannerMaps(
                    station = station,
                    scanners = scanners.keys.toList()
                )
            ) {
                is Failure -> exception = results.exception
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.jobIds?.plusAssign(it.job)
                }
            }
        //Get Permissions ids
        */
/*if (getPermissions && exception == null)
            when (val results =
                localAgencySource.stationJobPermissionsMaps(jobs = scanners.values.map { it. })) {
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.apply {
                        if (permissionIds == null) permissionIds = mutableListOf(it.permission)
                        else permissionIds!!.add(it.permission)
                    }
                }
                is Failure -> exception = results.exception
            }*//*

        return if (exception == null) Success(scanners) else Failure(exception)
    }

    override suspend fun scanners(
        agency: String,
        station: String,
        getBookers: Boolean,
        getJobs: Boolean,
    ): Results<Map<String, Scanner>> {
        val scanners = mutableMapOf<String, Scanner>()
        var exception: Exception? = null
        when (val results = localAgencySource.scanners(agency)) {
            is Success -> scanners += results.data.associateBy { it.bookerID }
            is Failure -> exception = results.exception
        }
        if (getBookers && exception == null)
            when (val results = bookingRepo.bookerFromId(scanners.keys.toList())) {
                is Success -> results.data.forEach {
                    scanners[it.bookerId]!!.apply { booker = it }
                }
                is Failure -> exception = results.exception
            }
        */
/*if (getPermissions && exception == null)
            when (val results =
                localAgencySource.stationJobPermissionsMaps(scanners = scanners.keys.toList())) {
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.apply {
                        if (permissionIds == null) permissionIds = mutableListOf(it.permission)
                        else permissionIds!!.add(it.permission)
                    }
                }
                is Failure -> exception = results.exception
            }*//*

        if (getJobs && exception == null)
            when (
                val results = localAgencySource.stationScannerMaps(
                    station = station,
                    scanners = scanners.keys.toList()
                )
            ) {
                is Failure -> exception = results.exception
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.jobIds?.plusAssign(it.job)
                }
            }

        return if (exception == null) Success(scanners) else Failure(exception)
    }


    override fun observeStationScannerMaps(station: String) =
        localAgencySource.observeStationScannerMaps(station)

    override suspend fun stationScannerMaps(station: String, scanners: List<String>) =
        localAgencySource.stationScannerMaps(station, scanners)

    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) =
        localAgencySource.saveStationScannerMaps(maps)

    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) =
        localAgencySource.deleteStationScannerMap(station, scanners)

    override suspend fun saveStations(stations: List<Station>) =
        localAgencySource.saveStations(stations)

    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) =
        localAgencySource.saveStationTownMaps(stationTownMaps)

    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) =
        localAgencySource.deleteStationTownMaps(station, towns)

    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) =
        localAgencySource.saveStationLocation(id, lat, lon)

    override fun observeStationTownMaps(station: String) =
        localAgencySource.observeStationTownMaps(station)

    override fun observeStationsFromIds(ids: List<String>) =
        localAgencySource.observeStationsFromIds(ids)

    override suspend fun stationTownMaps(station: String) =
        localAgencySource.stationTownMaps(station)

    override suspend fun stations() = localAgencySource.stations()

    override suspend fun stationsFromIds(ids: List<String>) = localAgencySource.stationsFromIds(ids)

    */
/**
 * Returns the [Station] from the ids gotten from [StationTownMap]
 *//*

//    override suspend fun parksFromTown(town: String) =
//        when(val townParkMaps = localAgencySource.townParkMapFromTown(town)){
//            is Results.Success -> {
//                val parkIds = townParkMaps.data.map { it.park }
//                localAgencySource.parksFromIds(parkIds)
//            }
//            is Results.Failure -> {
//                throw townParkMaps.exception
//            }
//        }

    */
/**
 * Returns the [Station] from the ids gotten from [StationTownMap]
 *//*

//    override suspend fun town(station: String) = when(val townParkMaps = localAgencySource.townParkMapFromTown(station)){
//        is Results.Success -> {
//            val townIds = townParkMaps.data.map { it.town }
//            localUnivSource.townsFromIds(townIds)
//        }
//        is Results.Failure -> {
//            throw townParkMaps.exception
//        }
//    }
}*/
