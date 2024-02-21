/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.main.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.common.DownloadProgressView
import luci.sixsixsix.powerampache2.presentation.destinations.QueueScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainContentMenuItem
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainContentTopAppBar
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainContentTopAppBarEvent
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainDrawer
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainTabRow
import luci.sixsixsix.powerampache2.presentation.main.screens.components.MainTabRow.tabItems
import luci.sixsixsix.powerampache2.presentation.main.screens.components.TabItem
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.albums.AlbumsScreen
import luci.sixsixsix.powerampache2.presentation.screens.artists.ArtistsScreen
import luci.sixsixsix.powerampache2.presentation.screens.home.HomeScreen
import luci.sixsixsix.powerampache2.presentation.screens.home.HomeScreenViewModel
import luci.sixsixsix.powerampache2.presentation.screens.offline.OfflineSongsMainContent
import luci.sixsixsix.powerampache2.presentation.screens.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsScreen
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel
import luci.sixsixsix.powerampache2.presentation.screens.settings.subscreens.AboutScreen
import luci.sixsixsix.powerampache2.presentation.screens.songs.SongsListScreen
import luci.sixsixsix.powerampache2.presentation.search.SearchResultsScreen

@Composable
@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
@Destination(start = true)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun MainContentScreen(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    // IMPORTANT : set the main navigator right away here in MainScreen
    Ampache2NavGraphs.navigator = navigator

    val tabsCount = tabItems.size
    val pagerState = rememberPagerState { tabsCount }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen: String by rememberSaveable { mutableStateOf(MainContentMenuItem.Home.id) }
    var currentScreenClass by rememberSaveable { mutableStateOf(MainContentMenuItem.Home::class.java.canonicalName) }
    val isSearchActive = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val appName = stringResource(id = R.string.app_name)
    var barTitle by remember { mutableStateOf(appName) }

    if (isSearchActive.value) {
        MainSearchBar(
            modifier = Modifier.focusRequester(focusRequester),
            mainViewModel = mainViewModel,
            isActive = isSearchActive,
            navigator = navigator
        )
    }

    val floatingActionVisible = mainViewModel.state.queue.isEmpty() &&
            (MainContentMenuItem.toMainContentMenuItem(currentScreen) == MainContentMenuItem.Home)

    ModalNavigationDrawer(
        drawerState = drawerState,
        //scrimColor = MaterialTheme.colorScheme.scrim,
        drawerContent = {
            MainDrawer(
                user = authViewModel.state.user ?: User.emptyUser(),
                versionInfo = settingsViewModel.state.appVersionInfoStr,
                hideDonationButtons = settingsViewModel.state.localSettings.hideDonationButton,
                onItemClick = {
                    currentScreen = it.id
                    currentScreenClass = it.javaClass.canonicalName
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainContentTopAppBar(
                    searchVisibility = isSearchActive,
                    scrollBehavior = scrollBehavior,
                    isQueueEmpty = mainViewModel.state.queue.isEmpty(),
                    floatingActionVisible = floatingActionVisible,
                    isFabLoading = mainViewModel.state.isFabLoading,
                    title = barTitle,
                    onMagicPlayClick = {
                        mainViewModel.onEvent(MainEvent.OnFabPress)
                    }
                ) { event ->
                    when(event) {
                        // OPEN-CLOSE drawer
                        MainContentTopAppBarEvent.OnLeftDrawerIconClick -> scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                        MainContentTopAppBarEvent.OnPlaylistIconClick ->
                            navigator.navigate(QueueScreenDestination)
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                AnimatedVisibility (
                    visible = floatingActionVisible,
                    exit = fadeOut(spring(stiffness = Spring.StiffnessVeryLow)),
                    enter = fadeIn(spring(stiffness = Spring.StiffnessMedium))
                ) {
                    MainFloatingButton(dimensionResource(id = R.dimen.main_floating_button_size), mainViewModel.state.isFabLoading) {
                        mainViewModel.onEvent(MainEvent.OnFabPress)
                    }
                }
            }
        ) {
               Surface(
                   modifier = Modifier.padding(
                       top = it.calculateTopPadding(),
                       bottom = it.calculateBottomPadding()
                   )
               ) {
                   Column {
                       AnimatedVisibility(mainViewModel.state.isDownloading,
                           enter = slideInVertically(initialOffsetY = { dire -> dire / 2 }) + fadeIn(),
                           exit = slideOutVertically(spring(stiffness = Spring.StiffnessVeryLow))
                       ) {
                           DownloadProgressView {
                               mainViewModel.onEvent(MainEvent.OnStopDownloadSongs)
                           }
                       }

                       when (val menuItem = MainContentMenuItem.toMainContentMenuItem(currentScreen)) {
                           is MainContentMenuItem.Home -> HomeScreen(
                               navigator = navigator,
                               viewModel = homeScreenViewModel
                           ).also { barTitle = appName }
                           is MainContentMenuItem.Library -> TabbedLibraryView(
                               navigator = navigator,
                               pagerState = pagerState,
                               mainViewModel = mainViewModel
                           ).also { barTitle = menuItem.title }
                           is MainContentMenuItem.Offline -> OfflineSongsMainContent(
                               navigator = navigator,
                               mainViewModel = mainViewModel
                           ).also { barTitle = menuItem.title }
                           is MainContentMenuItem.Settings -> SettingsScreen(
                               navigator = navigator,
                               settingsViewModel = settingsViewModel
                           ).also { barTitle = menuItem.title }
                           MainContentMenuItem.Logout ->
                               mainViewModel.onEvent(MainEvent.OnLogout)
                           MainContentMenuItem.About -> AboutScreen(
                               navigator = navigator,
                               settingsViewModel = settingsViewModel
                           ).also { barTitle = menuItem.title }
                       }
                   }

               }
           }
    }
}

@Composable
fun MainFloatingButton(
    floatingButtonSize: Dp = dimensionResource(id = R.dimen.main_floating_button_size),
    isFabLoading: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.size(floatingButtonSize),
        shape = RoundedCornerShape(floatingButtonSize/2 + 5.dp),
        containerColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = MaterialTheme.colorScheme.primary,
        onClick = { }
    ) {
        if (isFabLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp))
        } else {
            Icon(modifier = Modifier
                .size(floatingButtonSize)
                .padding(4.dp)
                .clickable {
                    onClick()
                },
                painter = painterResource(id = R.drawable.ic_tune_spinner),
                //imageVector = Icons.Default.PlayArrow,
                contentDescription = "Quick Play",
                //tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MainSearchBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    isActive: MutableState<Boolean>,
    navigator: DestinationsNavigator
) {
    val state = mainViewModel.state
    val controller = LocalSoftwareKeyboardController.current

    SearchBar(
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "search")
        },
        query = state.searchQuery,
        onQueryChange = {
            mainViewModel.onEvent(MainEvent.OnSearchQueryChange(it))
        },
        placeholder = {
            Text(text = stringResource(id = R.string.topBar_search_hint))
        },
        enabled = true,
        onSearch = {
            isActive.value = true
            controller?.hide()
        }, //the callback to be invoked when the input service triggers the ImeAction.Search action
        active = isActive.value, //whether the user is searching or not
        onActiveChange = {
            isActive.value = it
            if (!it) { mainViewModel.onEvent(MainEvent.OnSearchQueryChange("")) }
        }, //the callback to be invoked when this search bar's active state is changed
    ) {
        SearchResultsScreen(navigator = navigator, mainViewModel = mainViewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabbedLibraryView(
    navigator: DestinationsNavigator,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
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
                TabItem.Songs -> SongsListScreen(navigator = navigator, mainViewModel = mainViewModel)
            }
        }
    }
}
