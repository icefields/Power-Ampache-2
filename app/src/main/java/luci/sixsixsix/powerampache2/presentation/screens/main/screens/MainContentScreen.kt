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
package luci.sixsixsix.powerampache2.presentation.screens.main.screens

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Color
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
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.common.DownloadProgressView
import luci.sixsixsix.powerampache2.presentation.destinations.NotificationsScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.QueueScreenDestination

import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.albums.AlbumsScreen
import luci.sixsixsix.powerampache2.presentation.screens.artists.ArtistsScreen
import luci.sixsixsix.powerampache2.presentation.screens.home.HomeScreen
import luci.sixsixsix.powerampache2.presentation.screens.home.HomeScreenViewModel
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainContentMenuItem
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainContentTopAppBar
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainContentTopAppBarEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainDrawer
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainTabRow
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.MainTabRow.tabItems
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.TabItem
import luci.sixsixsix.powerampache2.presentation.screens.main.screens.components.drawerItems
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens.offline.OfflineSongsMainContent
import luci.sixsixsix.powerampache2.presentation.screens.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.screens.search.SearchResultsScreen
import luci.sixsixsix.powerampache2.presentation.screens.search.SearchViewEvent
import luci.sixsixsix.powerampache2.presentation.screens.search.SearchViewModel
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsEvent
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsScreen
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel
import luci.sixsixsix.powerampache2.presentation.screens.settings.subscreens.AboutScreen
import luci.sixsixsix.powerampache2.presentation.screens.songs.SongsListScreen
import luci.sixsixsix.powerampache2.ui.theme.additionalColours

@Composable
@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
@Destination(start = true)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun MainContentScreen(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    // IMPORTANT : set the main navigator right away here in MainScreen
    Ampache2NavGraphs.navigator = navigator
    val queueState by mainViewModel.currentQueue().collectAsState()
    val offlineModeState by settingsViewModel.offlineModeStateFlow.collectAsState()
    val localSettingsState by settingsViewModel.localSettingsStateFlow.collectAsState()
    val notificationQueueEmpty by mainViewModel.notificationQueueEmptyState.collectAsState(true)
    val user by authViewModel.userStateFlow.collectAsState()
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
            searchViewModel = searchViewModel,
            isActive = isSearchActive,
            navigator = navigator
        )
    }

    val floatingActionVisible = queueState.isEmpty() &&
            (MainContentMenuItem.toMainContentMenuItem(currentScreen) == MainContentMenuItem.Home)

    val offlineSwitchVisible =
        (MainContentMenuItem.toMainContentMenuItem(currentScreen) != MainContentMenuItem.Settings)

    val hideDonationButtons = BuildConfig.HIDE_DONATION || localSettingsState.hideDonationButton
    ModalNavigationDrawer(
        drawerState = drawerState,
        //scrimColor = MaterialTheme.colorScheme.scrim,
        drawerContent = {
            MainDrawer(
                items = drawerItems,
                currentItem = MainContentMenuItem.toMainContentMenuItem(currentScreen),
                user = user ?: User.emptyUser(),
                versionInfo = settingsViewModel.state.appVersionInfoStr,
                hideDonationButtons = hideDonationButtons,
                onItemClick = {
                    scope.launch { drawerState.close() }
                    if (it == MainContentMenuItem.Logout) {
                        mainViewModel.onEvent(MainEvent.OnLogout)
                    } else {
                        currentScreen = it.id
                        currentScreenClass = it.javaClass.canonicalName
                    }
                }
            )
        }
    ) {
        // we're in the genre screen and there are some results on screen
        val isGenreSubScreen = (currentScreen == MainContentMenuItem.Genres.id
                && !searchViewModel.state.isNoResults)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainContentTopAppBar(
                    isOfflineMode = offlineModeState,
                    showOfflineSwitch = offlineSwitchVisible,
                    searchVisibility = isSearchActive,
                    scrollBehavior = scrollBehavior,
                    isQueueEmpty = queueState.isEmpty(),
                    isNotificationQueueEmpty = notificationQueueEmpty,
                    floatingActionVisible = floatingActionVisible,
                    isFabLoading = mainViewModel.state.isFabLoading,
                    title = barTitle,
                    isGenreSubScreen = isGenreSubScreen,
                    onOfflineModeSwitch = {
                        settingsViewModel.onEvent(SettingsEvent.OnOfflineToggle)
                    },
                    onMagicPlayClick = {
                        mainViewModel.onEvent(MainEvent.OnFabPress)
                    },
                    onGenreScreenBackClick = {
                        searchViewModel.onEvent(SearchViewEvent.Clear)
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

                        MainContentTopAppBarEvent.OnNotificationsIconClick ->
                            navigator.navigate(NotificationsScreenDestination)
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
                           ).also { barTitle = stringResource(id = menuItem.title) }
                           is MainContentMenuItem.Offline -> OfflineSongsMainContent(
                               navigator = navigator,
                               mainViewModel = mainViewModel
                           ).also { barTitle = stringResource(id = menuItem.title) }
                           is MainContentMenuItem.Settings -> SettingsScreen(
                               navigator = navigator,
                               settingsViewModel = settingsViewModel
                           ).also { barTitle = stringResource(id = menuItem.title) }
                           MainContentMenuItem.Logout -> {
                               // already handled by the drawer header
                           }
                           MainContentMenuItem.About -> AboutScreen(
                               navigator = navigator,
                               settingsViewModel = settingsViewModel
                           ).also { barTitle = stringResource(id = menuItem.title) }
                           MainContentMenuItem.Genres -> SearchResultsScreen(
                               navigator = navigator,
                               mainViewModel = mainViewModel,
                               searchViewModel = searchViewModel).also {
                               barTitle = stringResource(id = menuItem.title)
                           }
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
    searchViewModel: SearchViewModel,
    mainViewModel: MainViewModel,
    isActive: MutableState<Boolean>,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    val state = mainViewModel.state
    val controller = LocalSoftwareKeyboardController.current

    SearchBar(
        leadingIcon = {
            Row(modifier = Modifier.wrapContentSize()) {
                CircleBackButton(background = Color.Transparent) {
                    if (searchViewModel.state.isNoSearch) {
                        isActive.value = false
                    }
                    searchViewModel.onEvent(SearchViewEvent.Clear)
                    mainViewModel.onEvent(MainEvent.OnSearchQueryChange(""))
                    controller?.hide()
                }
            }
        },
        colors = SearchBarDefaults.colors(
            dividerColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.additionalColours.surfaceContainerHigh,
            inputFieldColors = SearchBarDefaults.inputFieldColors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )
        ),
        //shape = RoundedCornerShape(10.dp),
        query = state.searchQuery,
        onQueryChange = {
            mainViewModel.onEvent(MainEvent.OnSearchQueryChange(it))
        },
        placeholder = {
            Text(text = stringResource(id = R.string.topBar_search_hint), maxLines = 1)
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
        SearchResultsScreen(
            modifier = modifier,
            navigator = navigator,
            mainViewModel = mainViewModel,
            searchViewModel = searchViewModel
        )
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
