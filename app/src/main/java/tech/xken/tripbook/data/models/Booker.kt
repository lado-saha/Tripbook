package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Booker.TABLE_NAME)
data class Booker(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = NAME) val name: String?,
    @ColumnInfo(name = GENDER) val gender: Boolean?,
    @ColumnInfo(name = PHOTO_URL) val photoUrl: String?,
    @ColumnInfo(name = BIRTHPLACE) val birthplace: Long?,
    @ColumnInfo(name = OCCUPATION) val occupation: String?,
    @ColumnInfo(name = PHONE) val phone: String?,
    @ColumnInfo(name = EMAIL) val email: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = NATIONALITY) val nationality: String?,
    @ColumnInfo(name = PASSWORD) val password: String
) {
    companion object {
        const val TABLE_NAME = "Bookers"
        const val PASSWORD = "password"
        const val ID = "id"
        const val NAME = "name"
        const val GENDER = "gender"
        const val PHOTO_URL = "photo_url"
        const val BIRTHPLACE = "birthplace"
        const val OCCUPATION = "occupation"
        const val PHONE = "phone"
        const val EMAIL = "email"
        const val TIMESTAMP = "timestamp"
        const val NATIONALITY = "nationality"
    }
}

@Entity(tableName = Scanner.TABLE_NAME, primaryKeys = [Scanner.BOOKER, Scanner.AGENCY])
data class Scanner(
    @ColumnInfo(name = BOOKER) val booker: String,
    @ColumnInfo(name = AGENCY) val agency: String,
    @ColumnInfo(name = PERMISSIONS) val permissions: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = ROLE) val role: String?,
) {
    companion object {
        const val TABLE_NAME = "Scanners"
        const val BOOKER = "booker"
        const val AGENCY = "agency"
        const val PERMISSIONS = "permissions"
        const val TIMESTAMP = "timestamp"
        const val ROLE = "role"
    }
}