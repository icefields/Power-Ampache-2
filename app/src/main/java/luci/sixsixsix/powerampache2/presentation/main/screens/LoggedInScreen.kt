package luci.sixsixsix.powerampache2.presentation.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.NavGraphs
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.components.SheetDragHandle
import luci.sixsixsix.powerampache2.presentation.song_detail.SongDetailScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoggedInScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    val state = mainViewModel.state
    val scaffoldState = rememberBottomSheetScaffoldState()

    // TODO DEBUG snackbar errors
    if (state.errorMessage != "") {
        LaunchedEffect(scaffoldState.snackbarHostState, state.errorMessage) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = state.errorMessage,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            ).apply {
                when (this) {
                    SnackbarResult.Dismissed -> mainViewModel.onEvent(MainEvent.OnDismissErrorMessage)
                    SnackbarResult.ActionPerformed -> mainViewModel.onEvent(MainEvent.OnDismissErrorMessage)
                }
            }
        }
    }

    // This scaffold is used just for the bottom sheet
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SongDetailScreen(mainScaffoldState = scaffoldState)
        },
        sheetDragHandle = {
            SheetDragHandle(scaffoldState = scaffoldState, mainViewModel = mainViewModel)
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = getPeakHeight(mainViewModel.state.song) // peek only when a song is pulled up
    ) {
        Column {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                dependenciesContainerBuilder = {
                    // those are declared in the activity
                    dependency(mainViewModel)
                    dependency(authViewModel)

                    // To tie SettingsViewModel to "settings" nested navigation graph,
                    // making it available to all screens that belong to it
//                    dependency(NavGraphs.root) {
//                        val parentEntry = remember(navBackStackEntry) {
//                            navController.getBackStackEntry(NavGraphs.root.route)
//                        }
//                        hiltViewModel<MainViewModel>(parentEntry)
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    )
            )
        }
    }
}

@Composable
fun getPeakHeight(song: Song?): Dp =
    if (song == null) { 0.dp } else { dimensionResource(id = R.dimen.miniPlayer_height) }
