package tech.xken.tripbook.ui.screens.agency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tech.xken.tripbook.ui.navigation.AgencyNavGraph
import tech.xken.tripbook.ui.screens.booking.MainBookingVM
import tech.xken.tripbook.ui.theme.TripbookTheme

@AndroidEntryPoint
class MainAgencyActivity : ComponentActivity() {
    private lateinit var vm: MainAgencyVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            vm = hiltViewModel(this)
            TripbookTheme {
                AgencyNavGraph(authRepo = vm.authRepo,
                    modifier = Modifier.fillMaxSize())
            }
        }
    }
}
