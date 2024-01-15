package tech.xken.tripbook.data.sources.storage.local

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.sources.storage.StorageSource
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

//class LocalStorageSource @Inject constructor(
//    val ioDispatcher: CoroutineContext,
//    val context: Context
//) : StorageSource {
//    override suspend fun uploadProfilePhoto(bookerId: String, byteArray: ByteArray) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                val bookerFolder = File(context.filesDir, bookerId).also {
//                    if (!it.exists()) it.mkdir()
//                }
//                val destinationFile =
//                    File(bookerFolder.path, "profile").also { it.writeBytes(byteArray) }
//
//                Results.Success(destinationFile.path)
//            } catch (e: Exception) {
//                Results.Failure(e)
//            }
//        }
//
//    override suspend fun uploadProfilePhoto(bookerId: String, uri: Uri) = withContext(ioDispatcher) {
//        return@withContext try {
//            val bookerFolder = File(context.filesDir, bookerId).also {
//                if (!it.exists()) it.mkdir()
//            }
//            val destinationFile = File(bookerFolder.path, "profile")
//
//            context.contentResolver.openInputStream(uri)?.also {
//                val outputStream = FileOutputStream(destinationFile)
//                it.copyTo(outputStream)
//                it.close()
//            }
//            Results.Success(destinationFile.path)
//        } catch (e: Exception) {
//            Results.Failure(e)
//        }
//    }
//
////    override suspend fun profilePhoto(bookerId: String) = withContext(ioDispatcher) {
////        return@withContext try {
////            val bookerFolder = File(context.filesDir, bookerId)
////            val byteArray = File(bookerFolder.path, "profile").readBytes()
////            Results.Success(byteArray)
////        } catch (e: Exception) {
////            Results.Failure(e)
////        }
////    }
//
//    override suspend fun deleteProfilePhoto(bookerId: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            val bookerFolder = File(context.filesDir, bookerId)
//            val isDeleted = File(bookerFolder.path, "profile").delete()
//            Results.Success(isDeleted)
//        } catch (e: Exception) {
//            Results.Failure(e)
//        }
//    }
//
//    override suspend fun accountPhotoUrl(bookerId: String): Results<String> {
//        TODO("Not yet implemented")
//    }
//
//}
