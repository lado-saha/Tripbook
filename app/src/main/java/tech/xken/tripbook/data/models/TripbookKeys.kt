package tech.xken.tripbook.data.models

import androidx.datastore.preferences.core.booleanPreferencesKey

object BookingKeys {
    const val name = "booking_sync"
    val SYNC_DONE = booleanPreferencesKey("sync_done")
    val HAS_SYNC_ACCOUNT = booleanPreferencesKey("has_sync_account")
    val HAS_ACCOUNT_PHOTO = booleanPreferencesKey("has_sync_account_photo")
    val HAS_SYNC_MOMO_ACCOUNTS = booleanPreferencesKey("has_sync_momo_accounts")
    val HAS_SYNC_OM_ACCOUNTS = booleanPreferencesKey("has_sync_om_accounts")
}