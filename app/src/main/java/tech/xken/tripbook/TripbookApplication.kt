package tech.xken.tripbook

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Necessary to use Hilt DI
 */
@HiltAndroidApp
class TripbookApplication: Application()