package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import tech.xken.tripbook.R

@Entity(tableName = Booker.TABLE_NAME)
data class Booker(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = NAME) var name: String?,
    @ColumnInfo(name = GENDER) val genderID: String?,
    @ColumnInfo(name = PHOTO_URL) val photoUrl: String?,
    @ColumnInfo(name = BIRTHDAY) val birthday: Long?,
    @ColumnInfo(name = OCCUPATION) val occupation: String?,
    @ColumnInfo(name = PHONE) val phone: String?,
    @ColumnInfo(name = PHONE_CODE) val phoneCode: String?,
    @ColumnInfo(name = EMAIL) val email: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = NATIONALITY) val nationality: String?,// This should be a reference to the country
    @ColumnInfo(name = PASSWORD) val password: String?,
) {
    val gender get() = when(genderID){
        "male" -> Gender.MALE
        "female" -> Gender.FEMALE
        else -> null
    }
    companion object {
        const val TABLE_NAME = "Bookers"
        const val PASSWORD = "password"
        const val ID = "id"
        const val NAME = "name"
        const val GENDER = "gender_id"
        const val PHOTO_URL = "photo_url"
        const val BIRTHDAY = "birthday"
        const val OCCUPATION = "occupation"
        const val PHONE = "phone"
        const val PHONE_CODE = "phone_code"
        const val EMAIL = "email"
        const val TIMESTAMP = "timestamp"
        const val NATIONALITY = "nationality"

        /**
         * Creates a new empty [Booker] with id=[NEW_ID]
         */
        fun new(id: String = NEW_ID) = Booker(
            id = id,
            name = null,
            genderID = null,
            photoUrl = null,
            birthday = null,
            occupation = null,
            phone = null,
            phoneCode = null,
            email = null,
            timestamp = null,
            nationality = null,
            password = null
        )
    }
}

/**
 * A simulation for the currently signed in [Booker]
 */
@Entity(tableName = CurrentBooker.TABLE_NAME)
data class CurrentBooker(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = SIGN_IN_TIMESTAMP) val signInTimestamp: Long?,
) {
    companion object {
        const val TABLE_NAME = "CurrentBookers"
        const val ID = "id"
        const val SIGN_IN_TIMESTAMP = "sign_in_timestamp"
    }
}

@Entity(tableName = Scanner.TABLE_NAME, primaryKeys = [Scanner.BOOKER_ID, Scanner.AGENCY_ID])
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

    companion object {
        const val IS_SUSPENDED = "is_suspended"
        const val TABLE_NAME = "Scanners"
        const val BOOKER_ID = "booker_id"
        const val AGENCY_ID = "agency_id"
        const val TIMESTAMP = "timestamp"
    }
}


enum class Gender {
    MALE, FEMALE, UNSPECIFIED;

    val strGender
        get() = when (this) {
            MALE -> "male"
            FEMALE -> "female"
            UNSPECIFIED -> "unspecified"
        }

    val stringResId
        get() = when (this) {
            UNSPECIFIED -> gendersStrRes[0]
            MALE -> gendersStrRes[1]
            FEMALE -> gendersStrRes[2]
        }

    companion object {
        val gendersStrRes = arrayOf(
            R.string.lb_gender_unspecified,
            R.string.lb_gender_male,
            R.string.lb_gender_female,
        )

        fun resIdToGender(resId: Int) =
            when (resId) {
                R.string.lb_gender_male -> MALE
                R.string.lb_gender_female -> FEMALE
                else -> UNSPECIFIED
            }

        fun strGenderToGender(strGender: String?) =
            when (strGender) {
                "male" -> MALE
                "female" -> FEMALE
                else -> UNSPECIFIED
            }
    }
}


