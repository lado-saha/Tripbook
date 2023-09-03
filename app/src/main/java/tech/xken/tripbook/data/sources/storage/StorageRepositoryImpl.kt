package tech.xken.tripbook.data.sources.storage

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.domain.NetworkState

class StorageRepositoryImpl(
    private val lStorage: StorageSource,
    private val rStorage: StorageSource,
    private val authRepo: AuthRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkState: NetworkState
) : StorageRepository {
    override suspend fun profilePhoto(bookerId: String, offline: Boolean?): Results<ByteArray> {
        return when (offline) {
            true -> lStorage.profilePhoto(bookerId)
            false -> rStorage.profilePhoto(bookerId)
            null -> {
                val localPhoto = lStorage.profilePhoto(bookerId)
                if (localPhoto is Results.Success) localPhoto
                else {
                    Log.d("TGAAAA", "Am online")
                    val remotePhoto = rStorage.profilePhoto(bookerId)
                    when (remotePhoto) {
                        is Results.Success -> {
                            Log.d("TGAAAA", "Am online, Good")
                            if (remotePhoto.data.isNotEmpty())
                                lStorage.saveProfilePhoto(bookerId, remotePhoto.data)
                        }

                        else -> {}
                    }
                    Log.d("TGAAAA", "Why")
                    remotePhoto
                }
            }
        }
    }

//    override suspend fun remoteToLocalAccountPhoto(bookerId: String): Results<String> {
//        val remote = rStorage.profilePhoto(bookerId)
//        return lStorage.saveProfilePhoto(bookerId, remote.data)
//    }

    override suspend fun saveProfilePhoto(bookerId: String, uri: Uri): Results<String> {
        val remote = rStorage.saveProfilePhoto(bookerId, uri)
        if (remote is Results.Success && remote.data.isNotBlank()) {
            lStorage.deleteProfilePhoto(bookerId)
        }
        return remote
    }

    override suspend fun saveProfilePhotoLocal(bookerId: String, uri: Uri): Results<String> {
        val remote = rStorage.saveProfilePhoto(bookerId, uri)
        if (remote is Results.Success && remote.data.isNotBlank()) {
            lStorage.deleteProfilePhoto(bookerId)
        }
        return remote
    }

    override suspend fun deleteProfilePhoto(bookerId: String): Results<Boolean> {
        val remote = rStorage.deleteProfilePhoto(bookerId)
        if (remote is Results.Success) {
            lStorage.deleteProfilePhoto(bookerId)
        }
        return remote
    }


}