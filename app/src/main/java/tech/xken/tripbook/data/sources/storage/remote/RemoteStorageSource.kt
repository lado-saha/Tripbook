package tech.xken.tripbook.data.sources.storage.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.sources.storage.StorageSource
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RemoteStorageSource @Inject constructor(
    var ioDispatcher: CoroutineContext,
    var client: SupabaseClient
) : StorageSource {

    @OptIn(SupabaseExperimental::class)
    override suspend fun saveProfilePhoto(bookerId: String, uri: Uri): Results<String> =
        withContext(ioDispatcher) {
            return@withContext try {
                Results.Success(
                    client.storage["bookers"].upload("account_photos/$bookerId", uri, true)
                )
            } catch (e: Exception) {
                Results.Failure(e)
            }
        }

    override suspend fun saveProfilePhoto(bookerId: String, byteArray: ByteArray) =
        withContext(ioDispatcher) {
            return@withContext try {
                Results.Success(
                    client.storage["bookers"].upload("\"account_photos/$bookerId\"", byteArray, true)
                )
            } catch (e: Exception) {
                Results.Failure(e)
            }
        }


    override suspend fun profilePhoto(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            /*BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)*/
            Results.Success(client.storage["bookers"].downloadPublic("account_photos/$bookerId"))
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    override suspend fun deleteProfilePhoto(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            /*BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)*/
            client.storage["bookers"]. delete("account_photos/$bookerId")
//            We modify the authClient
            Results.Success(true)
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    /*override suspend fun profilePhoto(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            val byteArray = client.storage["booker"] .downloadPublic("$bookerId/profile") {
                size(100, 100)
                quality = 100
                fill()
            }
            Results.Success(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }*/

}