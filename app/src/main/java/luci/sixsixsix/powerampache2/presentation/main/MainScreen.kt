package luci.sixsixsix.powerampache2.presentation.main

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Piano

import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Piano
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.common.Logger
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.MainActivity
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumsScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.LoggedInScreenDestination
import luci.sixsixsix.powerampache2.presentation.home.HomeScreen
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.song_detail.SongDetailScreen
import luci.sixsixsix.powerampache2.presentation.songs.SongsEvent
import luci.sixsixsix.powerampache2.presentation.songs.SongsListScreen

private val tabItems = listOf<TabItem>(
    TabItem("Home", unselectedIcon = Icons.Outlined.Home, selectedIcon = Icons.Filled.Home),
    TabItem("Songs", unselectedIcon = Icons.Outlined.LibraryMusic, selectedIcon = Icons.Filled.LibraryMusic),
    TabItem("Albums", unselectedIcon = Icons.Outlined.Album, selectedIcon = Icons.Filled.Album),
    TabItem("Artists", unselectedIcon = Icons.Outlined.Piano, selectedIcon = Icons.Filled.Piano),
    TabItem("Playlists", unselectedIcon = Icons.Outlined.FeaturedPlayList, selectedIcon = Icons.Filled.FeaturedPlayList),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    activity: ComponentActivity,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier) {

    if(authViewModel.state.isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    } else {
        if (authViewModel.state.session != null) {
            DestinationsNavHost(navGraph = Ampache2NavGraphs.root)
        } else {
            LoginScreen()
        }
    }
}

val miniPlayerHeight = 70.0.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
@Destination
@Composable
fun LoggedInScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabItems.size }
    val state = viewModel.state
//    (LocalContext.current as MainActivity).mainViewModel = viewModel

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if(!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
        //val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
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
                        SnackbarResult.Dismissed -> viewModel.onEvent(MainEvent.OnDismissErrorMessage)
                        SnackbarResult.ActionPerformed -> viewModel.onEvent(MainEvent.OnDismissErrorMessage)
                    }
                }
            }
        }

        L("MAIN ScREEN Current song ${state.song}")

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                SongDetailScreen(navigator = navigator)
            },
            topBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = {
                            viewModel.onEvent(MainEvent.OnSearchQueryChange(it))
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(text = "Search ...")
                        },
                        maxLines = 1,
                        singleLine = true
                    )
                }
            },
            sheetDragHandle = {
                Box(modifier = Modifier
                    .height(miniPlayerHeight)
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                ) {
                    Text(text = state.song?.title ?: "ERROR")
                    // show mini-player
                    Box(modifier = Modifier
                        .height(
                            // if it's expanded do not show the player
                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                0.dp
                            } else {
                                miniPlayerHeight
                            }
                        )
                        .fillMaxWidth()
                        .background(Color.Blue)
                        .clickable {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.partialExpand() //only peek
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        }
                    ) {}
                }

            },
            sheetShape = RectangleShape,
            sheetSwipeEnabled = true,
            sheetPeekHeight = getPeakHeight(viewModel.state.song),

        ) {
            Column {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = index
                            },
                            text = {
                                Text(text = item.title)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedTabIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    //.background(Color.LightGray),
                ) { index ->
                    when(index) {
                        0 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.home)
                        1 -> SongsListScreen(navigator, modifier = Modifier.fillMaxSize())
                        2 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.albums)    //AlbumsScreen(navigator, modifier = Modifier.fillMaxSize())
                        3 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.artists)   //ArtistsScreen(navigator, modifier = Modifier.fillMaxSize())
                        4 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.playlists) //PlaylistsScreen(navigator, modifier = Modifier.fillMaxSize())
                    }
                }

                // add spacing at the bottom when the mini player is visible
                Box(modifier = Modifier.height(getPeakHeight(viewModel.state.song)))
            }

        }
    }
}

fun getPeakHeight(song: Song?): Dp = if (song == null) { 0.dp } else { miniPlayerHeight }

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
