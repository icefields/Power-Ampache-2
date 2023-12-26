package luci.sixsixsix.powerampache2.presentation.main.subscreens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.L
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.NavGraphs
import luci.sixsixsix.powerampache2.presentation.albums.AlbumsScreen
import luci.sixsixsix.powerampache2.presentation.artists.ArtistsScreen
import luci.sixsixsix.powerampache2.presentation.home.HomeScreen
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.main.components.MainTabRow
import luci.sixsixsix.powerampache2.presentation.main.components.SheetDragHandle
import luci.sixsixsix.powerampache2.presentation.main.components.TopBar
import luci.sixsixsix.powerampache2.presentation.playlists.PlaylistsScreen
import luci.sixsixsix.powerampache2.presentation.song_detail.SongDetailScreen
import luci.sixsixsix.powerampache2.presentation.songs.SongsListScreen
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoggedInScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state
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

    // This scaffold is used just for the bottom sheet
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SongDetailScreen()
        },
        sheetDragHandle = {
            SheetDragHandle(state = state, scaffoldState = scaffoldState)
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = getPeakHeight(viewModel.state.song) // peek only when a song is pulled up
    ) {
        Column {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
            )
        }
    }
}

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
    val scroll = rememberScrollState(0)
    var searchVisibility by remember { mutableFloatStateOf(0.0f) }

    val transitionState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    //Toast.makeText(LocalContext.current, "focused : $isFocused", Toast.LENGTH_LONG).show()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    if (searchVisibility == 1.0f) {
                        TopBar(
                            modifier = Modifier.alpha(searchVisibility),
                            viewModel = viewModel,
                            currentPage = pagerState.currentPage,
                            interactionSource = interactionSource
                        )
                    } else {
                        AnimatedVisibility(visibleState = transitionState) {
                            Text(
                                modifier = Modifier
                                    .basicMarquee()
                                    .alpha(
                                        abs(searchVisibility - 1.0f)
                                    ),
                                text = generateBarTitle(viewModel.state.song),
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (searchVisibility == 0.0f) {
                        IconButton(
                            modifier = Modifier.alpha(
                                abs(searchVisibility - 1.0f)
                            ),
                            onClick = {
                                searchVisibility = abs(searchVisibility - 1.0f)
                            }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        }
    ) {
        Surface(modifier = Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())) {
            Column {
                MainTabRow.MainTabRow(pagerState)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                ) { index ->
                    when (index) {
                        0 -> HomeScreen(navigator = navigator)
                        1 -> SongsListScreen(navigator)
                        2 -> AlbumsScreen(navigator = navigator)
                        3 -> ArtistsScreen(navigator = navigator)
                        4 -> PlaylistsScreen(navigator = navigator)
                    }
                }
            }
        }
    }
}

@Composable
private fun generateBarTitle(song: Song?): String =
    stringResource(id = R.string.app_name) + (song?.title?.let {
        "(${song.artist.name} - ${song.title})"
    } ?: "" )



@Composable
fun getPeakHeight(song: Song?): Dp =
    if (song == null) { 0.dp } else { dimensionResource(id = R.dimen.miniPlayer_height) }
