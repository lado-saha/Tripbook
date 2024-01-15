package tech.xken.tripbook.data.models.booker

import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.storage.StorageRepository
import tech.xken.tripbook.domain.DATE_NOW

typealias CI = ColumnInfo
private typealias SN = SerialName


@Serializable
@Entity(tableName = Booker.NAME, primaryKeys = ["booker_id"])
data class Booker(
    @CI(name = "booker_id") @SN("booker_id") val bookerId: String = "",

    @CI(name = "name") @SN("name") val name: String = "",
    @CI(name = "booker_sex") @SN("booker_sex") val bookerSex: Sex = Sex.Unspecified,
    @CI(name = "added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI(name = "id_card_number") @SN("id_card_number") val idCardNumber: String = "",
    @CI(name = "birthday") @SN("birthday") val birthday: LocalDate = DATE_NOW,
    @CI(name = "occupation") @SN("occupation") val occupation: String? = null,
    @CI(name = "nationality") @SN("nationality") val nationality: String? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null
) {
    @Transient
    @CI(name = "account_photo_url")
    @SN("account_photo_url")
    var accountPhotoUrl: String? = null

    @Ignore
    @CI(name = "has_account_photo")
    @SN("has_account_photo")
    var hasAccountPhoto: Boolean? = null

    @Ignore
    @Transient
    var accountPhotoUri: Uri? = null
        private set

    fun setPhotoUri(new: Uri?): Booker {
        accountPhotoUri = new
        return this
    }

    suspend fun initUrls(repo: StorageRepository) =
        copy().apply {
            accountPhotoUrl = accountPhotoUrl?.run { repo.accountPhotoUrl(bookerId).data }
        }


    /**
     * To update to the booker profile photo
     */
    suspend fun updateOrDeletePhoto(repo: StorageRepository) = try {
        when (accountPhotoUri) {
            null -> Log.d("Nothing to do", "True")
            Uri.EMPTY -> accountPhotoUrl?.let { repo.deleteAccountPhoto(bookerId) }
            else -> repo.uploadAccountPhoto(bookerId, accountPhotoUri!!)
        }.run {
            Results.Success(this)
        }
    } catch (e: Exception) {
        Results.Failure(e)
    }


    companion object {
        const val NAME = "booker"
    }
}

@Serializable
data class BookerWithPhone(
    val phone: String = "",
    val booker: Booker = Booker()
)

data class BookerCredentials(
    val phoneNumber: String = "",
    val phoneCode: String = "237",
    val password: String = "",
    val token: String? = null
) {
    val fullPhoneNumber get() = "+$phoneCode$phoneNumber"
    val formatedPhoneNumber
        get() = PhoneNumberUtils.formatNumber(
            phoneNumber,
            codeCountryMap[phoneCode]
        )
}

@Serializable
enum class Sex {
    Male, Female, Unspecified;

    val strGender
        get() = when (this) {
            Male -> "male"
            Female -> "female"
            Unspecified -> "unspecified"
        }

    val resId
        get() = when (this) {
            Unspecified -> gendersStrRes[0]
            Male -> gendersStrRes[1]
            Female -> gendersStrRes[2]
        }

    companion object {
        val gendersStrRes = arrayOf(
            R.string.lb_sex_unspecified,
            R.string.lb_sex_male,
            R.string.lb_sex_female,
        )

        fun resIdToSex(resId: Int) = when (resId) {
            R.string.lb_sex_male -> Male
            R.string.lb_sex_female -> Female
            else -> Unspecified
        }

    }
}

@Serializable
data class BookerAgencySettings(
    @SerialName("booker_id") val bookerId: String,
    @SerialName("is_seeking_job") val isSeekingJob: Boolean,
    @SerialName("is_employed") val isEmployed: Boolean
)