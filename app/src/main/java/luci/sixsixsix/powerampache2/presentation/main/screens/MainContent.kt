package luci.sixsixsix.powerampache2.presentation.main.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.presentation.albums.AlbumsScreen
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsScreen
import luci.sixsixsix.powerampache2.presentation.destinations.QueueScreenDestination
import luci.sixsixsix.powerampache2.presentation.home.HomeScreen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.components.DrawerBody
import luci.sixsixsix.powerampache2.presentation.main.screens.components.DrawerHeader
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainContentMenuItem
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainContentTopAppBar
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainContentTopAppBarEvent
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainTabRow
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainTabRow.tabItems
import luci.sixsixsix.powerampache2.presentation.main.screens.components.TabItem
import luci.sixsixsix.powerampache2.presentation.main.screens.components.drawerItems
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.songs.SongsListScreen

@Composable
@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
@Destination(start = true)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun MainContent(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    val tabsCount = MainTabRow.tabItems.size
    val pagerState = rememberPagerState { tabsCount }
    L("MainContent Current song ${viewModel.state.song}")
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentScreen: String by rememberSaveable {
        mutableStateOf(MainContentMenuItem.Home.id)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                Divider()
                DrawerBody(items = drawerItems,
                    onItemClick = {
                        currentScreen = it.id
                        scope.launch {
                            drawerState.close()
                        }
                })
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainContentTopAppBar(
                    pagerState = pagerState,
                    scrollBehavior = scrollBehavior
                ) { event ->
                    when(event) {
                        MainContentTopAppBarEvent.OnLeftDrawerIconClick -> scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                        MainContentTopAppBarEvent.OnPlaylistIconClick ->
                            navigator.navigate(QueueScreenDestination)
                    }
                }
            }
        ) {
            Surface(modifier = Modifier.padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding()
            )) {
                L("MainContent currentScreen ${currentScreen}")

                when (MainContentMenuItem.toMainContentMenuItem(currentScreen)) {
                    is MainContentMenuItem.Home -> HomeScreen(navigator = navigator)
                    is MainContentMenuItem.Library -> TabbedLibraryView(
                        navigator = navigator,
                        pagerState = pagerState
                    )
                    is MainContentMenuItem.Settings -> HomeScreen(navigator = navigator)
                    MainContentMenuItem.Logout -> viewModel.onEvent(MainEvent.OnLogout)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabbedLibraryView(
    navigator: DestinationsNavigator,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
        Column {
            MainTabRow.MainTabRow(pagerState)
            HorizontalPager(
                state = pagerState,
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            ) { index ->
                // The order of the items on screen is the same of the tabItems list order
                // to change the order, change the order in tabItems
                when(tabItems[index]) {
                    TabItem.Albums -> AlbumsScreen(navigator = navigator)
                    TabItem.Artists -> ArtistsScreen(navigator = navigator)
                    TabItem.Playlists -> PlaylistsScreen(navigator = navigator)
                    TabItem.Songs -> SongsListScreen(navigator)
                }
            }
        }
}



