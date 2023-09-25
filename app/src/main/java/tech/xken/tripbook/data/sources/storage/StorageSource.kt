package tech.xken.tripbook.data.sources.storage

import android.graphics.Bitmap
import android.net.Uri
import tech.xken.tripbook.data.models.Results

interface StorageSource {
    suspend fun saveProfilePhoto(bookerId: String, uri: Uri): Results<String>
    suspend fun saveProfilePhoto(bookerId: String, byteArray: ByteArray): Results<String>
    suspend fun profilePhoto(bookerId: String): Results<ByteArray>
    suspend fun deleteProfilePhoto(bookerId: String): Results<Boolean>
}