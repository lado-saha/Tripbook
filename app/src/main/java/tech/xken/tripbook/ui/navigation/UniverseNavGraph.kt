package tech.xken.tripbook.ui.navigation

//import tech.xken.tripbook.ui.screens.universe_editor.UniverseEditorScreen
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tech.xken.tripbook.ui.navigation.UniverseDestinationArgs.UNIV_SEARCH_RESULT
import tech.xken.tripbook.ui.screens.universe_editor.UniverseEditorScreen
import tech.xken.tripbook.ui.screens.universe_editor.UniverseSearchScreen
import tech.xken.tripbook.ui.screens.universe_editor.UniverseVisualizer
import tech.xken.tripbook.ui.screens.universe_editor.UniverseVisualizerVM

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UniverseNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
//    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = UnivScreens.UNIVERSE_VISUALIZER,
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(UnivScreens.UNIVERSE_VISUALIZER) {
            UniverseVisualizer(viewModel = hiltViewModel<UniverseVisualizerVM>().apply {
                savedStateHandle = it.savedStateHandle
            }) {
                navController.navigate(route = UnivScreens.UNIVERSE_SEARCH)
            }
        }
        composable(UnivScreens.UNIVERSE_EDITOR) {
            UniverseEditorScreen()
        }
        composable(UnivScreens.UNIVERSE_SEARCH) {
            UniverseSearchScreen { results ->
//                it.enableSavedStateHandles()
                navController.getBackStackEntry(UnivScreens.UNIVERSE_VISUALIZER).savedStateHandle[UNIV_SEARCH_RESULT] =
                    results
                Log.d("UNIV SEARCH CLEAR",
                    navController.getBackStackEntry(UnivScreens.UNIVERSE_VISUALIZER)
                        .savedStateHandle.contains(UNIV_SEARCH_RESULT).toString())
                navController.popBackStack()
            }
        }
    }
}
