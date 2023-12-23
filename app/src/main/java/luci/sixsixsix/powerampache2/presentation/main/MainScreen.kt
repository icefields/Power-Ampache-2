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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Piano

import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Piano
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumsScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.LoggedInScreenDestination
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.song_detail.SongDetailScreen
import luci.sixsixsix.powerampache2.presentation.songs.SongsListScreen

private val tabItems = listOf<TabItem>(
    TabItem("Songs", unselectedIcon = Icons.Outlined.LibraryMusic, selectedIcon = Icons.Filled.LibraryMusic),
    TabItem("Albums", unselectedIcon = Icons.Outlined.Album, selectedIcon = Icons.Filled.Album),
    TabItem("Artists", unselectedIcon = Icons.Outlined.Piano, selectedIcon = Icons.Filled.Piano),
    TabItem("Playlists", unselectedIcon = Icons.Outlined.FeaturedPlayList, selectedIcon = Icons.Filled.FeaturedPlayList),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    activity: ComponentActivity,
    viewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier) {

    if(viewModel.state.isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    } else {
        if (viewModel.state.session != null) {
            DestinationsNavHost(navGraph = Ampache2NavGraphs.root)
        } else {
            LoginScreen()
        }
    }
}

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

//        scope.launch {
//            sheetState.partialExpand()
//        }

        state.currentSong?.let{ song ->
            Log.d("aaaa", "MAIN ${song}")
//            scope.launch {
//                scaffoldState.bottomSheetState.hide()
//            }
        }
        
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                SongDetailScreen(navigator = navigator)
            },
            topBar = {
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
            },
            sheetDragHandle = {
                Box(modifier = Modifier
                    .height(70.0.dp)
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                ) {
                    Text(text = state.currentSong?.title ?: "ERROR")
                    // show miniplayer
                    Box(modifier = Modifier
                        .height(
                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                0.dp
                            } else {
                                70.0.dp
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
            sheetPeekHeight = if (
//                scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded ||
                viewModel.playlistManager.getCurrentSong() == null) {
                0.dp
            } else { 70.0.dp },

        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    //.background(Color.LightGray),
            ) { index ->
                when(index) {
                    0 -> SongsListScreen(navigator, modifier = Modifier.fillMaxSize())
                    1 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.albums,) //AlbumsScreen(navigator, modifier = Modifier.fillMaxSize())
                    2 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.artists)//ArtistsScreen(navigator, modifier = Modifier.fillMaxSize())
                    3 -> DestinationsNavHost(navGraph = Ampache2NavGraphs.playlists)//PlaylistsScreen(navigator, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
