package tech.xken.tripbook

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import java.io.File

/**
 * Necessary to use Hilt DI
 */
@HiltAndroidApp
class TripbookApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader() = ImageLoader(this)
        .newBuilder()
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(
                    File(this.getExternalFilesDir(null), "Tripbook")
                )
                .maxSizePercent(0.05)
                .build()
        }
        .build()
}