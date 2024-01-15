package tech.xken.tripbook.data.models.agency

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.storage.StorageRepository

typealias CI = ColumnInfo

/**
 * This enum class represents the reasons for a trip cancellation.
 */
enum class TripCancellationReason {
    /**
     * The booking was cancelled due to a medical reason on the part of the booker.
     */
    BOOKER_MEDICAL_REASON,

    /**
     * The booking was cancelled due to a change of plan on the part of the booker.
     */
    BOOKER_CHANGE_OF_PLAN,

    /**
     * The booking was cancelled due to weather conditions.
     */
    AGENCY_WEATHER,

    /**
     * The booking was cancelled due to technical difficulties on the part of the agency.
     */
    AGENCY_TECHNICAL_DIFFICULTIES,

    /**
     * Other
     */
    OTHER
}

/**
 * This data class stores the legal settings for a travel agency.
 *
 * @property agencyId The ID of the travel agency.
 * @property businessEntity The business entity of the travel agency.
 * @property licence The licence of the travel agency.
 * @property privacyPolicy The privacy policy of the travel agency.
 */
@Serializable
@Entity(AgencyLegalDocs.NAME, primaryKeys = ["agency_id"])
data class AgencyLegalDocs(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("business_entity_url") @SN("business_entity_url") val businessEntityUrl: String? = null,
    @CI("licence_url") @SN("licence_url") val licenceUrl: String? = null,
    @CI("privacy_policy_url") @SN("privacy_policy_url") val privacyPolicyUrl: String? = null,
    @CI("scope_of_authority_url") @SN("scope_of_authority_url") val scopeOfAuthorityUrl: String? = null,
    @CI("duties_of_agency_url") @SN("duties_of_agency_url") val dutiesOfAgencyUrl: String? = null,
    @CI("rights_of_client_url") @SN("rights_of_client_url") val rightsOfClientUrl: String? = null,
    @CI("liability_of_agency_url") @SN("liability_of_agency_url") val liabilityOfAgencyUrl: String? = null,
    @CI("dispute_resolution_process_url") @SN("dispute_resolution_process_url") val disputeResolutionProcessUrl: String? = null,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {

    suspend fun getUrls(repo: StorageRepository) = copy(
        businessEntityUrl = businessEntityUrl?.let { repo.agencyBusinessEntityDocUrl(agencyId).data },
        licenceUrl = licenceUrl?.let { repo.agencyLicenceDocUrl(agencyId).data },
        privacyPolicyUrl = privacyPolicyUrl?.let { repo.agencyPrivacyPolicyDocUrl(agencyId).data },
        scopeOfAuthorityUrl = scopeOfAuthorityUrl?.let { repo.agencyScopeOfAuthorityDocUrl(agencyId).data },
        dutiesOfAgencyUrl = dutiesOfAgencyUrl?.let { repo.agencyDutiesOfAgencyDocUrl(agencyId).data },
        rightsOfClientUrl = rightsOfClientUrl?.let { repo.agencyRightsOfClientDocUrl(agencyId).data },
        liabilityOfAgencyUrl = liabilityOfAgencyUrl?.let {
            repo.agencyLiabilityOfAgencyDocUrl(
                agencyId
            ).data
        },
    )

    @Ignore
    @Contextual
    var businessEntityUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var licenceUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var privacyPolicyUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var scopeOfAuthorityUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var dutiesOfAgencyUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var rightsOfClientUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var liabilityOfAgencyUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var disputeResolutionProcessUri: Uri? = Uri.EMPTY

    @Ignore
    private var urlInitComplete = false


    /**
     * The concept is as follows. If this document exist in the supabase storage, it will be declared
     * in the table agency_legal_docs as not null (We will put a dummy value like "", "true" etc) else if it does not exist,
     * we keep as null.
     * Now, when we retrieve the table agency_legal_docs from the online db, this field is either null(No document in the storage)
     * or not null(Image exist in the storage). We then define an equivalent Uri for the document which is initialised to
     * EMPTY(Meaning untouched). This uri is the one to be modified if the document is changed from the ui. Finally after the UI is finished
     * It is our job to know if we should update, create or delete the particular document from/to storage.
     * Case Url = null => Image Not found in the storage
     * Case Url != null => Image found in the storage
     * Case Uri = EMPTY => The UI did not MODIFY the document
     * Case Uri = null => The UI DELETED the document
     * Case Uri != null => The UI UPDATED(CHANGE) the document
     *
     * Now, we can use the combination of the 2 states Uri and url to know which CRUD op to to
     * 1.1 Uri = EMPTY AND url = EMPTY: do nothing
     * 1.2 Uri = EMPTY AND Url = null: do nothing
     * --------------------------------------------------------------------------
     * 1.3 Uri = null AND Url != null: Ui finally decided to settle on NO photo, WE DELETE FROM STORAGE
     * 2.1 Uri = null AND Url = EMPTY: Ui settled on no photo but did not have in the first place. DO NOTHING
     * --------------------------------------------------------------------------
     * 2.1 Uri != null AND Url = null: Ui added a new document thus we need to CREATE IT IN STORAGE
     * 2.2 Uri != null AND Url != null: Ui changed the document to another, WE EDIT THE IMAGE IN STORAGE
     */
    suspend fun updateDocs(repo: StorageRepository) = try {
        when (businessEntityUri) {
            Uri.EMPTY -> {}
            null -> businessEntityUrl?.let { repo.deleteAgencyBusinessEntityDoc(agencyId) }
            else -> repo.uploadAgencyBusinessEntityDoc(agencyId, businessEntityUri!!)
        }
        when (licenceUri) {
            Uri.EMPTY -> {}
            null -> licenceUrl?.let { repo.deleteAgencyLicenceDoc(agencyId) }
            else -> repo.uploadAgencyLicenceDoc(agencyId, licenceUri!!)
        }
        when (privacyPolicyUri) {
            Uri.EMPTY -> {}
            null -> privacyPolicyUrl?.let { repo.deleteAgencyPrivacyPolicyDoc(agencyId) }
            else -> repo.uploadAgencyPrivacyPolicyDoc(agencyId, privacyPolicyUri!!)
        }
        when (scopeOfAuthorityUri) {
            Uri.EMPTY -> {}
            null -> scopeOfAuthorityUrl?.let { repo.deleteAgencyScopeOfAuthorityDoc(agencyId) }
            else -> repo.uploadAgencyScopeOfAuthorityDoc(agencyId, scopeOfAuthorityUri!!)
        }
        when (dutiesOfAgencyUri) {
            Uri.EMPTY -> {}
            null -> dutiesOfAgencyUrl?.let { repo.deleteAgencyDutiesOfAgencyDoc(agencyId) }
            else -> repo.uploadAgencyDutiesOfAgencyDoc(agencyId, dutiesOfAgencyUri!!)
        }
        when (rightsOfClientUri) {
            Uri.EMPTY -> {}
            null -> rightsOfClientUrl?.let { repo.deleteAgencyRightsOfClientDoc(agencyId) }
            else -> repo.uploadAgencyRightsOfClientDoc(agencyId, rightsOfClientUri!!)
        }
        when (liabilityOfAgencyUri) {
            Uri.EMPTY -> {}
            null -> liabilityOfAgencyUrl?.let { repo.deleteAgencyLiabilityOfAgencyDoc(agencyId) }
            else -> repo.uploadAgencyLiabilityOfAgencyDoc(agencyId, liabilityOfAgencyUri!!)
        }
        when (disputeResolutionProcessUri) {
            Uri.EMPTY -> {}
            null -> disputeResolutionProcessUrl?.let {
                repo.deleteAgencyDisputeResolutionProcessDoc(
                    agencyId
                )
            }

            else -> repo.uploadAgencyDisputeResolutionProcessDoc(
                agencyId,
                disputeResolutionProcessUri!!
            )
        }
        Results.Success(this)
    } catch (e: Exception) {
        Results.Failure(e)
    }

    /**
     * To be called when the current AgencyLegalDoc row is delete from db
     */
    suspend fun deleteAllDocs(repo: StorageRepository) = try {
        repo.deleteAgencyBusinessEntityDoc(agencyId)
        repo.deleteAgencyLicenceDoc(agencyId)
        repo.deleteAgencyPrivacyPolicyDoc(agencyId)
        repo.deleteAgencyScopeOfAuthorityDoc(agencyId)
        repo.deleteAgencyDutiesOfAgencyDoc(agencyId)
        repo.deleteAgencyRightsOfClientDoc(agencyId)
        repo.deleteAgencyLiabilityOfAgencyDoc(agencyId)
        repo.deleteAgencyDisputeResolutionProcessDoc(agencyId)
        Results.Success(Unit)
    } catch (e: Exception) {
        Results.Failure(e)
    }

    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyLegalDocs? = null,
        @SN("agency_id") val agencyId: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null
    ) {
        companion object {
            const val NAME = "agency_legal_docs_log"
        }
    }

    companion object {
        const val NAME = "agency_legal_docs"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docBusinessEntityUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_business_entity${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docLicenceUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_licence${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docPrivacyPolicyUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_privacy_policy${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docScopeOfAuthorityUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_scope_of_authority${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docDutiesOfAgencyUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_duties_of_agency${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docRightsOfClientsUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_rights_of_clients${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docLiabilityOfAgencyUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_liability_of_agency${if (isPath) "/${agencyId}.pdf" else ""}"

        /**
         * Returns a path or url to the file
         * @param isPath if true, we return the path else the url. False by default
         */
        fun docDisputeResolutionUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "legal_docs/doc_dispute_resolution${if (isPath) "/${agencyId}.pdf" else ""}"
    }
}

/**
 * This class stores the refund policy for a travel agency.
 *
 * @property agencyId The ID of the travel agency.
 * @property maxCancellationDaysBeforeDeparture The maximum number of days before departure that a booking can be cancelled.
 * @property reason The reason for the cancellation.
 * @property refundPercentage The percentage of the booking price that will be refunded if the booking is cancelled.
 * @property description A description of the refund policy.
 */
@Serializable
@Entity(AgencyRefundPolicy.NAME, primaryKeys = ["agency_id", "reason"])
class AgencyRefundPolicy(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("reason") @SN("reason") val reason: TripCancellationReason = TripCancellationReason.OTHER,
    @CI("max_cancellation_days_before_departure") @SN("max_cancellation_days_before_departure") val maxCancellationDaysBeforeDeparture: Int = 0,
    @CI("refund_percentage") @SN("refund_percentage") val refundPercentage: Double = 0.0,
    @CI("description") @SN("description") val description: String = "",
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyRefundPolicy? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("reason") val reason: TripCancellationReason,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null
    ) {
        companion object {
            const val NAME = "agency_refund_policy_log"
        }

    }

    companion object {
        const val NAME = "agency_refund_policy"
    }
}



