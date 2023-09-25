package tech.xken.tripbook.data.models.booker

import android.telephony.PhoneNumberUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.domain.DATE_NOW

typealias CI = ColumnInfo
private typealias SN = SerialName

class Car(
    val name: String,
    var speed: Double,
    val model: String
) {
    fun incSpeed(new: Double) {
        speed += new
    }
}

fun main() {
    var myCar = Car(name = "Carina", speed = 4.0, "II")
}

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
    @ColumnInfo("modified_on") @SerialName("modified_on") val modifiedOn: Instant? = null
) {
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