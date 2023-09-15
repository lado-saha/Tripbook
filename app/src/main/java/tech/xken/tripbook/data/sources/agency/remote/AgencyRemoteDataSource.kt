package tech.xken.tripbook.data.sources.agency.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import tech.xken.tripbook.data.models.Channel
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.Schema
import tech.xken.tripbook.data.models.agency.AgencyAccount
import tech.xken.tripbook.data.models.agency.AgencyEmailSupport
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs
import tech.xken.tripbook.data.models.agency.AgencyPhoneSupport
import tech.xken.tripbook.data.models.agency.AgencyRefundPolicy
import tech.xken.tripbook.data.models.agency.AgencySocialSupport
import tech.xken.tripbook.data.models.agency.TripCancellationReason
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.RPC
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AgencyRemoteDataSource @Inject constructor(
    val ioDispatcher: CoroutineContext,
    val client: SupabaseClient,
) : AgencyDataSource {
    private val parser = Json {
        coerceInputValues = true
        encodeDefaults = true
    }

    override val channel = client.realtime.createChannel(Channel.AGENCY)
    override suspend fun agencyAccountLastModifiedOn(agencyId: String)=TODO("Not to be called from remote")
    override suspend fun agencyEmailSupportsLastModifiedOn(agencyId: String)=TODO("Not to be called from remote")
    override suspend fun agencyPhoneSupportsLastModifiedOn(agencyId: String)=TODO("Not to be called from remote")
    override suspend fun agencySocialSupportsLastModifiedOn(agencyId: String)=TODO("Not to be called from remote")
    override suspend fun agencyRefundPoliciesLastModifiedOn(agencyId: String)=TODO("Not to be called from remote")

    override suspend fun agencyAccount(
        agencyId: String,
    ): Results<AgencyAccount?> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyAccount.NAME].select {
                AgencyAccount::agencyId eq agencyId
            }.runCatching { parser.decodeFromJsonElement<List<AgencyAccount>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun agencyAccountFlow(agencyId: String) = TODO("Not to be called from remote.")

    override suspend fun agencyAccountLatestLog(
        agencyId: String,
        fromInstant: Instant?
    ): Results<AgencyAccount.Log> =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.AGENCY_LATEST_ACCOUNT_LOG,
                    parameters = mapOf(
                        "agency_id" to agencyId, "from_instant" to fromInstant
                    )
                ).run { Success(parser.decodeFromJsonElement<List<AgencyAccount.Log>>(body!!)[0]) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun agencyAccountLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(AgencyAccount.Log.NAME) {
            table = AgencyAccount.Log.NAME
            filter = "${AgencyAccount.Log::agencyId}.eq.$agencyId"
        }
            .map { r ->
                parser.decodeFromJsonElement<List<AgencyAccount.Log>>(r.record)[0]
                    .runCatching {
                        Success(
                            copy(
                                data = when (dbAction) {
                                    DbAction.DELETE -> null
                                    else -> agencyAccount(agencyId).data
                                }
                            )
                        )
                    }.getOrElse { Failure(it as Exception) }
            }
            .catch { emit(Failure(it as Exception)) }

    override suspend fun createAgencyAccount(
        account: AgencyAccount
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyAccount.NAME].insert(account)
                .runCatching { parser.decodeFromJsonElement<List<AgencyAccount>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateAgencyAccount(
        agencyId: String, account: AgencyAccount
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyAccount.NAME].update(account) {
                AgencyAccount::agencyId eq agencyId
            }.runCatching { parser.decodeFromJsonElement<List<AgencyAccount>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countAgencyAccount(agencyId: String) =
        TODO("Not to be called from remote.")

    override suspend fun emailSupports(
        agencyId: String,
        emails: List<String>,
    ): Results<List<AgencyEmailSupport>> =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyEmailSupport.NAME].select {
                    AgencyEmailSupport::agencyId eq agencyId
                }.run { Success(parser.decodeFromJsonElement<List<AgencyEmailSupport>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun emailSupportsFlow(agencyId: String) = TODO("Not to be called from remote.")

    override suspend fun emailSupportLatestLogs(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.AGENCY_LATEST_EMAIL_SUPPORT_LOG,
                    parameters = mapOf(
                        "agency_id" to agencyId, "from_instant" to fromInstant
                    )
                )
                    .run { Success(parser.decodeFromJsonElement<List<AgencyEmailSupport.Log>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun emailSupportsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(AgencyEmailSupport.Log.NAME) {
            table = AgencyEmailSupport.Log.NAME
            filter = "${AgencyEmailSupport.Log::agencyId}.eq.$agencyId"
        }.map { r ->
            parser.decodeFromJsonElement<List<AgencyEmailSupport.Log>>(r.record)[0]
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> emailSupports(agencyId, listOf(email)).data[0]
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }


    override suspend fun createEmailSupports(
        supports: List<AgencyEmailSupport>
    ): Results<List<AgencyEmailSupport?>> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyEmailSupport.NAME].insert(supports)
                .run { Success(parser.decodeFromJsonElement<List<AgencyEmailSupport>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateEmailSupports(
        agencyId: String, supports: List<AgencyEmailSupport>
    ): Results<List<AgencyEmailSupport?>> = try {
        client.postgrest[Schema.AGENCY, AgencyEmailSupport.NAME].update(supports) {
            AgencyEmailSupport::agencyId eq agencyId
        }.run { Success(parser.decodeFromJsonElement<List<AgencyEmailSupport>>(body!!)) }
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun deleteEmailSupports(
        agencyId: String, emails: List<String>
    ) = try {
        client.postgrest[Schema.AGENCY, AgencyEmailSupport.NAME].delete {
            AgencyEmailSupport::agencyId eq agencyId
        }.run { Success(Unit) }
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun countEmailSupports(agencyId: String) =
        TODO("Not to be called from remote.")

    override suspend fun phoneSupports(
        agencyId: String,
        phoneCodes: List<String>,
        phoneNumbers: List<String>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyPhoneSupport.NAME].select {
                AgencyPhoneSupport::agencyId eq agencyId
            }.run { Success(parser.decodeFromJsonElement<List<AgencyPhoneSupport>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun phoneSupportsFlow(agencyId: String): Flow<Results<List<AgencyPhoneSupport>>> =
        TODO("Not to be called from remote.")

    override suspend fun phoneSupportsLatestLogs(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY_LOG, AgencyPhoneSupport.Log.NAME].select {
                    AgencyPhoneSupport.Log::agencyId eq agencyId
                    fromInstant?.let {
                        AgencyPhoneSupport.Log::timestamp gt it
                    }
                }
                    .run { Success(parser.decodeFromJsonElement<List<AgencyPhoneSupport.Log>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun phoneSupportsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(AgencyPhoneSupport.Log.NAME) {
            table = AgencyPhoneSupport.Log.NAME
            filter = "${AgencyPhoneSupport.Log::agencyId}.eq.$agencyId"
        }.map { r ->
            parser.decodeFromJsonElement<List<AgencyPhoneSupport.Log>>(r.record)[0]
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> phoneSupports(
                                    agencyId,
                                    listOf(phoneCode),
                                    listOf(phoneNumber)
                                ).data[0]
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }


    override suspend fun createPhoneSupports(
        supports: List<AgencyPhoneSupport>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyPhoneSupport.NAME].insert(supports)
                .run { Success(parser.decodeFromJsonElement<List<AgencyPhoneSupport>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updatePhoneSupports(
        agencyId: String, supports: List<AgencyPhoneSupport>
    ) = try {
        client.postgrest[Schema.AGENCY, AgencyPhoneSupport.NAME].update(supports) {
            AgencyPhoneSupport::agencyId eq agencyId
        }.run { Success(parser.decodeFromJsonElement<List<AgencyPhoneSupport>>(body!!)) }
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun deletePhoneSupports(
        agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>
    ) = try {
        client.postgrest[Schema.AGENCY, AgencyPhoneSupport.NAME].delete {
            AgencyPhoneSupport::agencyId eq agencyId
        }.run { Success(Unit) }
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun countPhoneSupports(agencyId: String) =
        TODO("Not to be called from remote.")

    override suspend fun socialSupport(
        agencyId: String,
    ): Results<AgencySocialSupport?> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencySocialSupport.NAME].select {
                AgencySocialSupport::agencyId eq agencyId
            }.runCatching { parser.decodeFromJsonElement<List<AgencySocialSupport>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun socialSupportFlow(agencyId: String) = TODO("Not to be called from remote.")

    override suspend fun socialSupportLatestLogs(
        agencyId: String,
        fromInstant: Instant?
    ) = withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY_LOG, AgencySocialSupport.Log.NAME].select {
                    AgencySocialSupport.Log::agencyId eq agencyId
                    fromInstant?.let {
                        AgencySocialSupport.Log::timestamp gt it
                    }
                }
                    .run { Success(parser.decodeFromJsonElement<List<AgencySocialSupport.Log>>(body!!)[0]) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun socialSupportLogFlow(agencyId: String)=
        channel.postgresChangeFlow<PostgresAction.Insert>(AgencySocialSupport.Log.NAME) {
            table = AgencySocialSupport.Log.NAME
            filter = "${AgencySocialSupport.Log::agencyId}.eq.$agencyId"
        }.map { r ->
            parser.decodeFromJsonElement<List<AgencySocialSupport.Log>>(r.record)[0]
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> socialSupport(agencyId).data
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }


    override suspend fun createSocialSupport(

        support: AgencySocialSupport
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencySocialSupport.NAME].insert(support)
                .runCatching { parser.decodeFromJsonElement<List<AgencySocialSupport>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateSocialSupports(
        agencyId: String, supports: AgencySocialSupport
    ): Results<AgencySocialSupport?> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencySocialSupport.NAME].update(supports) {
                AgencySocialSupport::agencyId eq agencyId
            }.runCatching { parser.decodeFromJsonElement<List<AgencySocialSupport>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteSocialSupport(agencyId: String) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencySocialSupport.NAME].delete {
                AgencySocialSupport::agencyId eq agencyId
            }.run { Success(Unit) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun countSocialAccount(agencyId: String) =
        TODO("Not to be called from remote.")

    override suspend fun refundPolicies(
        agencyId: String,
        reasons: List<TripCancellationReason>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyRefundPolicy.NAME].select {
                AgencyRefundPolicy::agencyId eq agencyId
            }.run { Success(parser.decodeFromJsonElement<List<AgencyRefundPolicy>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun refundPoliciesFlow(agencyId: String) = TODO("Not to be called from remote.")

    override suspend fun refundPolicyLatestLogs(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY_LOG, AgencyRefundPolicy.Log.NAME].select {
                    AgencyRefundPolicy.Log::agencyId eq agencyId
                    fromInstant?.let {
                        AgencyRefundPolicy.Log::timestamp gt it
                    }
                }
                    .run { Success(parser.decodeFromJsonElement<List<AgencyRefundPolicy.Log>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun refundPoliciesLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(AgencyRefundPolicy.Log.NAME) {
            table = AgencyRefundPolicy.Log.NAME
            filter = "${AgencyRefundPolicy.Log::agencyId}.eq.$agencyId"
        }.map { r ->
            parser.decodeFromJsonElement<List<AgencyRefundPolicy.Log>>(r.record)[0]
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> refundPolicies(agencyId, listOf(reason)).data[0]
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }


    override suspend fun createRefundPolicies(

        policies: List<AgencyRefundPolicy>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyRefundPolicy.NAME].insert(policies)
                .run { Success(parser.decodeFromJsonElement<List<AgencyRefundPolicy>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateRefundPolicies(
        agencyId: String, policies: List<AgencyRefundPolicy>
    ) = try {
        client.postgrest[Schema.AGENCY, AgencyRefundPolicy.NAME].update(policies) {
            AgencyRefundPolicy::agencyId eq agencyId
        }.run { Success(parser.decodeFromJsonElement<List<AgencyRefundPolicy>>(body!!)) }
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun deleteRefundPolicies(
        agencyId: String, reasons: List<TripCancellationReason>
    ) = try {
        client.postgrest[Schema.AGENCY, AgencyRefundPolicy.NAME].delete {
            AgencyRefundPolicy::agencyId eq agencyId
        }.run { Success(Unit) }
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun countRefundPolicies(agencyId: String) =
        TODO("Not to be called from remote.")

    override suspend fun legalDocs(
        agencyId: String
    ): Results<AgencyLegalDocs> {
        TODO("Not yet implemented")
    }

    override fun legalDocsFlow(agencyId: String) = TODO("Not to be called from remote.")

    override suspend fun legalDocsLatestLog(
        agencyId: String,
        fromInstant: Instant?
    ): Results<AgencyLegalDocs.Log> =
        TODO("Not yet implemented")

    override fun legalDocsLogFlow(agencyId: String): Flow<Results<AgencyLegalDocs.Log>> {
        TODO("Not yet implemented")
    }

    override suspend fun createLegalDocs(

        docs: AgencyLegalDocs
    ): Results<AgencyLegalDocs?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLegalDocs(
        agencyId: String, docs: List<AgencyLegalDocs>
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