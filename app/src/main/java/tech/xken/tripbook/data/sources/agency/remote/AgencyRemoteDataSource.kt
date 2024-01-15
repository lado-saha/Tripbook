package tech.xken.tripbook.data.sources.agency.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.Schema
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
import tech.xken.tripbook.data.models.agency.Param
import tech.xken.tripbook.data.models.agency.TripCancellationReason
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.RPC
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AgencyRemoteDataSource @Inject constructor(
    val ioDispatcher: CoroutineContext,
    val client: SupabaseClient,
    override val channel: RealtimeChannel
) : AgencyDataSource {

    private val parser = Json {
        coerceInputValues = true
        encodeDefaults = true
    }

    override suspend fun agencyAccountLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyEmailSupportsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyPhoneSupportsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencySocialSupportsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyRefundPoliciesLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyMoMoAccountsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyOMAccountsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyPayPalAccountsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyLegalDocsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")

    override suspend fun agencyGraphicsLastModifiedOn(agencyId: String) =
        TODO("Not to be called from remote")


    // Agency account
    override suspend fun agencyAccount(
        agencyId: String,
    ): Results<AgencyAccount?> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyAccount.NAME].select {
                AgencyAccount::agencyId eq agencyId
            }.runCatching {
                parser.decodeFromJsonElement<List<AgencyAccount>>(body!!)[0]
            }
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
                    function = RPC.LATEST_AGENCY_ACCOUNT_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                ).run {
                    Success(parser.decodeFromJsonElement<List<AgencyAccount.Log>>(body!!)[0])
                }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun agencyAccountLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyAccount.Log.NAME
//            filter =
        }
            .map { r ->
                parser.decodeFromJsonElement<AgencyAccount.Log>(r.record)
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

    override fun countAgencyAccount(agencyId: String) =
        TODO("Not to be called from remote.")

    // Email support
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
                    function = RPC.LATEST_AGENCY_EMAIL_SUPPORT_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                )
                    .run {
                        Success(
                            parser.decodeFromJsonElement<List<AgencyEmailSupport.Log>>(body!!)
                        )
                    }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun emailSupportsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyEmailSupport.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyEmailSupport.Log>(r.record)
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
    ): Results<List<AgencyEmailSupport?>> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyEmailSupport.NAME].update(supports) {
                AgencyEmailSupport::agencyId eq agencyId
            }.run { Success(parser.decodeFromJsonElement<List<AgencyEmailSupport>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteEmailSupports(
        agencyId: String, emails: List<String>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyEmailSupport.NAME].delete {
                AgencyEmailSupport::agencyId eq agencyId
            }.run { Success(Unit) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countEmailSupports(agencyId: String) =
        TODO("Not to be called from remote.")

    // Phone support
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
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_PHONE_SUPPORT_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                )
                    .run { Success(parser.decodeFromJsonElement<List<AgencyPhoneSupport.Log>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun phoneSupportsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyPhoneSupport.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyPhoneSupport.Log>(r.record)
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
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyPhoneSupport.NAME].update(supports) {
                AgencyPhoneSupport::agencyId eq agencyId
            }.run { Success(parser.decodeFromJsonElement<List<AgencyPhoneSupport>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deletePhoneSupports(
        agencyId: String, phoneCodes: List<String>, phoneNumbers: List<String>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyPhoneSupport.NAME].delete {
                AgencyPhoneSupport::agencyId eq agencyId
            }.run { Success(Unit) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countPhoneSupports(agencyId: String) =
        TODO("Not to be called from remote.")

    //    Agency Graphics
    override suspend fun agencyGraphics(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyGraphics.NAME]
                    .select { AgencyGraphics::agencyId eq agencyId }
                    .runCatching {
                        parser.decodeFromJsonElement<List<AgencyGraphics>>(body!!)[0]
                    }
                    .getOrNull().let {
                        Success(it)
                    }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyGraphicsLatestLog(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_GRAPHICS_LOG,
                    parameters = Param(agencyId, fromInstant!!)
                )
                    .run { Success(parser.decodeFromJsonElement<List<AgencyGraphics.Log>>(body!!)[0]) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun agencyGraphicsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyGraphics.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyGraphics.Log>(r.record)
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> agencyGraphics(agencyId).data
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }

    override fun agencyGraphicsFlow(agencyId: String) = TODO("Not to be called from remote.")
    override suspend fun createAgencyGraphics(graphics: AgencyGraphics) =
        withContext(ioDispatcher) {
            val newGraphic = AgencyGraphics(
                agencyId = graphics.agencyId
            )
            try {
                client.postgrest[Schema.AGENCY, AgencyGraphics.NAME].insert(newGraphic)
                    .runCatching { parser.decodeFromJsonElement<List<AgencyGraphics>>(body!!)[0] }
                    .getOrNull().let { Success(it) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateAgencyGraphics(agencyId: String, graphics: AgencyGraphics) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyGraphics.NAME].update(graphics) {
                    AgencyGraphics::agencyId eq agencyId
                }.runCatching { parser.decodeFromJsonElement<List<AgencyGraphics>>(body!!)[0] }
                    .getOrNull().let { Success(it) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun countAgencyGraphics(agencyId: String) = TODO("Not to be called from remote.")
    override suspend fun deleteAgencyGraphics(agencyId: String) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyGraphics.NAME].delete {
                AgencyGraphics::agencyId eq agencyId
            }.run { Success(Unit) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    // Social Support
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
            client.postgrest.rpc(
                function = RPC.LATEST_AGENCY_SOCIAL_SUPPORT_LOG,
                parameters = Param(agencyId, fromInstant!!)
            )
                .run { Success(parser.decodeFromJsonElement<List<AgencySocialSupport.Log>>(body!!)[0]) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun socialSupportLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencySocialSupport.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencySocialSupport.Log>(r.record)
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

    override suspend fun updateSocialSupport(
        agencyId: String, supports: AgencySocialSupport
    ) = withContext(ioDispatcher) {
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

    override fun countSocialAccount(agencyId: String) =
        TODO("Not to be called from remote.")


    // Refund policies
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
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_REFUND_POLICY_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                )
                    .run { Success(parser.decodeFromJsonElement<List<AgencyRefundPolicy.Log>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun refundPoliciesLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyRefundPolicy.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyRefundPolicy.Log>(r.record)
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
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyRefundPolicy.NAME].update(policies) {
                AgencyRefundPolicy::agencyId eq agencyId
            }.run { Success(parser.decodeFromJsonElement<List<AgencyRefundPolicy>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteRefundPolicies(
        agencyId: String, reasons: List<TripCancellationReason>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyRefundPolicy.NAME].delete {
                AgencyRefundPolicy::agencyId eq agencyId
            }.run { Success(Unit) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countRefundPolicies(agencyId: String) =
        TODO("Not to be called from remote.")

    // MoMo Accounts
    override suspend fun moMoAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyMoMoAccount.NAME].select {
                    AgencyMoMoAccount::agencyId eq agencyId
                }.run { Success(parser.decodeFromJsonElement<List<AgencyMoMoAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun moMoAccountsLatestLogs(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_MOMO_ACCOUNT_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                )
                    .run {
                        Success(
                            parser.decodeFromJsonElement<List<AgencyMoMoAccount.Log>>(body!!)
                        )
                    }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun moMoAccountsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyMoMoAccount.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyMoMoAccount.Log>(r.record)
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> moMoAccounts(
                                    agencyId,
                                    listOf(phoneNumber)
                                ).data[0]
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }

    override fun moMoAccountsFlow(agencyId: String) = TODO("Not to be called from remote")

    override suspend fun createMoMoAccounts(accounts: List<AgencyMoMoAccount>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyMoMoAccount.NAME].insert(accounts)
                    .run { Success(parser.decodeFromJsonElement<List<AgencyMoMoAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateMoMoAccounts(agencyId: String, accounts: List<AgencyMoMoAccount>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyMoMoAccount.NAME].update(accounts) {
                    AgencyMoMoAccount::agencyId eq agencyId
                }.run { Success(parser.decodeFromJsonElement<List<AgencyMoMoAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteMoMoAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyMoMoAccount.NAME].delete {
                    AgencyMoMoAccount::agencyId eq agencyId
                }.run { Success(Unit) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countMoMoAccounts(agencyId: String) = TODO("Not to be called from remote")

    // OM Accounts
    override suspend fun oMAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyOMAccount.NAME].select {
                    AgencyOMAccount::agencyId eq agencyId
                }.run { Success(parser.decodeFromJsonElement<List<AgencyOMAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun oMAccountsLatestLogs(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_OM_ACCOUNT_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                )
                    .run {
                        Success(
                            parser.decodeFromJsonElement<List<AgencyOMAccount.Log>>(body!!)
                        )
                    }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun oMAccountsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyOMAccount.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyOMAccount.Log>(r.record)
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> oMAccounts(
                                    agencyId,
                                    listOf(phoneNumber)
                                ).data[0]
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }

    override fun oMAccountsFlow(agencyId: String) = TODO("Not to be called from remote")

    override suspend fun createOMAccounts(accounts: List<AgencyOMAccount>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyOMAccount.NAME].insert(accounts)
                    .run { Success(parser.decodeFromJsonElement<List<AgencyOMAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updateOMAccounts(agencyId: String, accounts: List<AgencyOMAccount>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyOMAccount.NAME].update(accounts) {
                    AgencyOMAccount::agencyId eq agencyId
                }.run { Success(parser.decodeFromJsonElement<List<AgencyOMAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteOMAccounts(agencyId: String, phoneNumbers: List<String>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyOMAccount.NAME].delete {
                    AgencyOMAccount::agencyId eq agencyId
                }.run { Success(Unit) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countOMAccounts(agencyId: String) = TODO("Not to be called from remote")

    // PayPal Accounts
    override suspend fun payPalAccounts(agencyId: String, email: List<String>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyPayPalAccount.NAME].select {
                    AgencyPayPalAccount::agencyId eq agencyId
                }.run { Success(parser.decodeFromJsonElement<List<AgencyPayPalAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun payPalAccountsLatestLogs(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_PAYPAL_ACCOUNT_LOG,
                    parameters = Param(agencyId = agencyId, fromInstant = fromInstant!!)
                )
                    .run {
                        Success(
                            parser.decodeFromJsonElement<List<AgencyPayPalAccount.Log>>(body!!)
                        )
                    }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun payPalAccountsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyPayPalAccount.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyPayPalAccount.Log>(r.record)
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> payPalAccounts(
                                    agencyId,
                                    listOf(email)
                                ).data[0]
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }

    override fun payPalAccountsFlow(agencyId: String) = TODO("Not to be called from remote")
    override suspend fun createPayPalAccounts(accounts: List<AgencyPayPalAccount>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyPayPalAccount.NAME].insert(accounts)
                    .run { Success(parser.decodeFromJsonElement<List<AgencyPayPalAccount>>(body!!)) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun updatePayPalAccounts(
        agencyId: String,
        accounts: List<AgencyPayPalAccount>
    ) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyPayPalAccount.NAME].update(accounts) {
                AgencyPayPalAccount::agencyId eq agencyId
            }.run { Success(parser.decodeFromJsonElement<List<AgencyPayPalAccount>>(body!!)) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deletePayPalAccounts(agencyId: String, emails: List<String>) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyPayPalAccount.NAME].delete {
                    AgencyPayPalAccount::agencyId eq agencyId
                }.run { Success(Unit) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun countPayPalAccounts(agencyId: String) =
        TODO("Not to be called from remote")

    //Legal docs
    override suspend fun legalDocs(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                client.postgrest[Schema.AGENCY, AgencyLegalDocs.NAME]
                    .select { AgencyLegalDocs::agencyId eq agencyId }
                    .runCatching {
                        parser.decodeFromJsonElement<List<AgencyLegalDocs>>(body!!)[0]
                    }
                    .getOrNull().let {
                        Success(it)
                    }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun legalDocsLatestLog(agencyId: String, fromInstant: Instant?) =
        withContext(ioDispatcher) {
            try {
                client.postgrest.rpc(
                    function = RPC.LATEST_AGENCY_GRAPHICS_LOG,
                    parameters = Param(agencyId, fromInstant!!)
                )
                    .run { Success(parser.decodeFromJsonElement<List<AgencyLegalDocs.Log>>(body!!)[0]) }
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override fun legalDocsLogFlow(agencyId: String) =
        channel.postgresChangeFlow<PostgresAction.Insert>(Schema.AGENCY) {
            table = AgencyLegalDocs.Log.NAME
//            filter =
        }.map { r ->
            parser.decodeFromJsonElement<AgencyLegalDocs.Log>(r.record)
                .runCatching {
                    Success(
                        copy(
                            data = when (dbAction) {
                                DbAction.DELETE -> null
                                else -> legalDocs(agencyId).data
                            }
                        )
                    )
                }.getOrElse { Failure(it as Exception) }
        }.catch { emit(Failure(it as Exception)) }

    override fun legalDocsFlow(agencyId: String) = TODO("Not to be called from remote.")
    override suspend fun createLegalDocs(docs: AgencyLegalDocs) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyLegalDocs.NAME].insert(docs)
                .runCatching { parser.decodeFromJsonElement<List<AgencyLegalDocs>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun updateLegalDocs(
        agencyId: String,
        docs: AgencyLegalDocs
    ): Results<AgencyLegalDocs?> = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyLegalDocs.NAME].update(docs) {
                AgencyLegalDocs::agencyId eq agencyId
            }.runCatching { parser.decodeFromJsonElement<List<AgencyLegalDocs>>(body!!)[0] }
                .getOrNull().let { Success(it) }
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun countLegalDocs(agencyId: String) = TODO("Not to be called from remote.")
    override suspend fun deleteLegalDocs(agencyId: String) = withContext(ioDispatcher) {
        try {
            client.postgrest[Schema.AGENCY, AgencyLegalDocs.NAME].delete {
                AgencyLegalDocs::agencyId eq agencyId
            }.run { Success(Unit) }
        } catch (e: Exception) {
            Failure(e)
        }
    }
}