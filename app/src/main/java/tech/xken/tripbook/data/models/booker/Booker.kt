package tech.xken.tripbook.data.models.booker

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.NOW

typealias CI = ColumnInfo
typealias SN = SerialName

@Serializable
@Entity(tableName = "booker", primaryKeys = ["booker_id"])
data class Booker(
    @CI(name = "booker_id") @SN("booker_id") val bookerId: String = "",
    @CI(name = "name") @SN("name") val name: String = "",
    @CI(name = "booker_sex") @SN("booker_sex") val bookerSex: Sex = Sex.Unspecified,
    @CI(name = "added_on") @SN("added_on") val addedOn: Instant = Clock.System.now(),
    @CI(name = "id_card_number") @SN("id_card_number") val idCardNumber: String = "",
    @CI(name = "birthday") @SN("birthday") val birthday: LocalDate = NOW,
    @CI(name = "occupation") @SN("occupation") val occupation: String? = null,
    @CI(name = "nationality") @SN("nationality") val nationality: String? = null,
)

@Serializable
data class BookerWithPhone(
    val phone: String = "",
    val booker: Booker = Booker()
)

data class BookerCredentials(
    val phoneNumber: String = "",
    val phoneCode: String = "237",
    val token: String? = null
) {
    val formattedPhone get() = "+$phoneCode$phoneNumber"
}

/*@Entity(tableName = Scanner.TABLE_NAME, primaryKeys = [Scanner.BOOKER_ID, Scanner.AGENCY_ID])
data class Scanner(
    @ColumnInfo(name = BOOKER_ID) val bookerID: String,
    @ColumnInfo(name = AGENCY_ID) val agencyID: String,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = IS_SUSPENDED) val isSuspended: Boolean?,
) {
    @Ignore
    var jobIds: MutableList<String>? = null

    @Ignore
    var booker: Booker = Booker.new(bookerID)

}*/

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