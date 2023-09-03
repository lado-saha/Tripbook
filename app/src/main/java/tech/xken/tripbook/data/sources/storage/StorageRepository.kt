package tech.xken.tripbook.data.sources.storage

import android.graphics.Bitmap
import android.net.Uri
import tech.xken.tripbook.data.models.Results


interface StorageRepository {
    suspend fun profilePhoto(bookerId: String, offline: Boolean? = null): Results<ByteArray>
    suspend fun saveProfilePhotoLocal(bookerId: String, uri: Uri): Results<String>
    suspend fun deleteProfilePhoto(bookerId: String): Results<Boolean>
    suspend fun saveProfilePhoto(bookerId: String, uri: Uri): Results<String>
//    suspend fun remoteToLocalAccountPhoto(bookerId: String): Results<String>
}