package tech.xken.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import tech.xken.tripbook.ui.navigation.AppNavGraph
import tech.xken.tripbook.ui.theme.TripbookTheme

@AndroidEntryPoint
class AgencyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripbookTheme {
                AppNavGraph()
            }
        }
    }
}
