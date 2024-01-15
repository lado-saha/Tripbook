package tech.xken.tripbook.data.models.agency

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.booker.CI
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.storage.StorageRepository
import tech.xken.tripbook.domain.DATE_NOW

typealias SN = SerialName

@Serializable
data class Param(
    @SN("p_agency_id") val agencyId: String,
    @SN("p_from_instant") val fromInstant: Instant
)

@Serializable
@Entity(AgencyAccount.NAME, primaryKeys = ["agency_id"])
data class AgencyAccount(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("name") @SN("name") val name: String = "",
    @CI("physical_created_on") @SN("physical_created_on") val physicalCreatedOn: LocalDate = DATE_NOW,
    @CI("motto") @SN("motto") val motto: String = "",
    @CI("about_us") @SN("about_us") val aboutUs: String? = null,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {

    @Serializable
    data class Log(
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null,
        @SN("data_json") val data: AgencyAccount? = null,
    ) {
        companion object {
            const val NAME = "agency_account_log"
        }

    }

    companion object {
        const val NAME = "agency_account"
    }
}

/**
 * Contains things like the agency logos, photos etc
 */
@Serializable
@Entity(tableName = AgencyGraphics.NAME, primaryKeys = ["agency_id"])
data class AgencyGraphics(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("large_logo_url") @SN("large_logo_url") val largeLogoUrl: String? = null,
    @CI("small_logo_url") @SN("small_logo_url") val smallLogoUrl: String? = null,
    @CI("head_quarter_photo_url") @SN("hq_photo_url") val headQuarterPhotoUrl: String? = null,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {

    @Ignore
    @Contextual
    var largeLogoUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var smallLogoUri: Uri? = Uri.EMPTY

    @Ignore
    @Contextual
    var headQuarterPhotoUri: Uri? = Uri.EMPTY

    /**
     * This method is used to create the local version of the graphics class. It gets the
     */
    suspend fun getUrls(repo: StorageRepository) = copy(
        largeLogoUrl = largeLogoUrl?.let { repo.agencyLargeLogoUrl(agencyId).data },
        smallLogoUrl = smallLogoUrl?.let { repo.agencySmallLogoUrl(agencyId).data },
        headQuarterPhotoUrl = headQuarterPhotoUrl?.let { repo.headQuarterPhotoUrl(agencyId).data },
    )

    /**
     * The concept is as follows. If this image exist in the supabase storage, it will be declared
     * in the table agency_graphics as not null (We will put a dummy value like "", "true" etc) else if it does not exist,
     * we keep as null.
     * Now, when we retrieve the table agency_graphics from the online db, this field is either null(No image in the storage)
     * or not null(Image exist in the storage). We then define an equivalent Uri for the image which is initialised to
     * EMPTY(Meaning untouched). This uri is the one to be modified if the image is changed from the ui. Finally after the UI is finished
     * It is our job to know if we should update, create or delete the particular image from/to storage.
     * Case Url = null => Image Not found in the storage
     * Case Url != null => Image found in the storage
     * Case Uri = EMPTY => The UI did not MODIFY the image
     * Case Uri = null => The UI DELETED the image
     * Case Uri != null => The UI UPDATED(CHANGE) the image
     *
     * Now, we can use the combination of the 2 states Uri and url to know which CRUD op to to
     * 1.1 Uri = EMPTY AND url = EMPTY: do nothing
     * 1.2 Uri = EMPTY AND Url = null: do nothing
     * --------------------------------------------------------------------------
     * 1.3 Uri = null AND Url != null: Ui finally decided to settle on NO photo, WE DELETE FROM STORAGE
     * 2.1 Uri = null AND Url = EMPTY: Ui settled on no photo but didnot have in the first place. DO NOTHING
     * --------------------------------------------------------------------------
     * 2.1 Uri != null AND Url = null: Ui added a new image thus we need to CREATE IT IN STORAGE
     * 2.2 Uri != null AND Url != null: Ui changed the image to another, WE EDIT THE IMAGE IN STORAGE
     */
    suspend fun updateOrDeleteImage(repo: StorageRepository) = try {
        when (largeLogoUri) {
            Uri.EMPTY -> {}
            null -> largeLogoUrl?.let { repo.deleteAgencyLargeLogoUrl(agencyId) }
            else -> repo.uploadAgencyLargeLogoUrl(agencyId, largeLogoUri!!)// Create / Update
        }
        when (smallLogoUri) {
            Uri.EMPTY -> {}
            null -> smallLogoUrl?.let { repo.deleteAgencyLargeLogoUrl(agencyId) }
            else -> repo.uploadAgencyLargeLogoUrl(agencyId, smallLogoUri!!)// Create / Update
        }
        when (headQuarterPhotoUri) {
            Uri.EMPTY -> {}
            null -> headQuarterPhotoUrl?.let { repo.deleteAgencyLargeLogoUrl(agencyId) }
            else -> repo.uploadAgencyLargeLogoUrl(agencyId, headQuarterPhotoUri!!)// Create / Update
        }
        Results.Success(this)
    } catch (e: Exception) {
        Results.Failure(e)
    }

    /**
     * This delete all agency graphic images from storage
     */
    suspend fun deleteAllImages(repo: StorageRepository) = try {
        repo.deleteAgencyLargeLogoUrl(agencyId)
        repo.deleteAgencyLargeLogoUrl(agencyId)
        repo.deleteAgencyLargeLogoUrl(agencyId)
        Results.Success(Unit)
    } catch (e: Exception) {
        Results.Failure(e)
    }

    @Serializable
    data class Log(
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null,
        @SN("data_json") val data: AgencyGraphics? = null,
    ) {
        companion object {
            const val NAME = "agency_graphics_log"
        }
    }

    companion object {
        const val NAME = "agency_graphics"
        fun imgLargeLogoUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "agency_graphics/large_logo${if (isPath) "/${agencyId}.jpeg" else ""}"

        fun imgSmallLogoUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "agency_graphics/small_logo${if (isPath) "/${agencyId}.jpeg" else ""}"

        fun imgHeadQuarterPictureUrlOrPath(agencyId: String, isPath: Boolean = false) =
            "agency_graphics/head_quarter_photo${if (isPath) "/${agencyId}.jpeg" else ""}"
    }
}

@Serializable
@Entity(AgencyMetadata.NAME, primaryKeys = ["agency_id"])
data class AgencyMetadata(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("is_suspended") @SN("is_suspended") val isSuspended: Boolean = false,
    @CI("reputation") @SN("reputation") val reputation: Float = 5.0f,
    @CI("accident_count") @SN("accident_count") val accidentCount: Int = 0,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    companion object {
        const val NAME = "agency_metadata"
    }
}

@Serializable
@Entity(AgencySettings.NAME, primaryKeys = ["agency_id"])
data class AgencySettings(
    @CI("agency_id") @SN("agency_id") val agencyId: String? = "",
    @CI("cost_per_km") @SN("cost_per_km") val costPerKm: Double?,
    @CI("vip_cost_per_km") @SN("vip_cost_per_km") val vipCostPerKm: Double?,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    companion object {
        const val NAME = "agency_settings"
    }
}

@Serializable
@Entity(AgencyEvent.NAME, primaryKeys = ["agency_id", "event_id"])
data class AgencyEvent(
    @CI("event_id") @SN("event_id") val eventId: String = "",
    @CI("start_time") @SN("start_time") val startTime: Instant = Clock.System.now(),
    @CI("end_time") @SN("end_time") val endTime: Instant = Clock.System.now(),
    @CI("description") @SN("description") val description: String = "",
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    @SerialName(Log.NAME)
    data class Log(
        @SN("data_json") val data: AgencyEvent? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("event_id") val eventId: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null,
    ) {
        companion object {
            const val NAME = "agency_event_log"
        }

    }

    companion object {
        const val NAME = "agency_event"
    }
}

/**
 * Profile
 * Vehicle
 * Support Contacts
 * Stations
 * Towns
 * Jobs & Salaries
 * Locations
 * Trips
 * Personnel
 * Other
 * */