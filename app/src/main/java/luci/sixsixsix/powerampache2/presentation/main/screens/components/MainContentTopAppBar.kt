package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.destinations.QueueScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContentTopAppBar(
    pagerState: PagerState,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onNavigationIconClick: (MainContentTopAppBarEvent) -> Unit
) {
    var searchVisibility by remember { mutableFloatStateOf(0.0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val transitionState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    TopAppBar(
        modifier = modifier,
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
            IconButton(onClick = {
                onNavigationIconClick(MainContentTopAppBarEvent.OnLeftDrawerIconClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
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
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
            if (viewModel.state.queue.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onNavigationIconClick(MainContentTopAppBarEvent.OnPlaylistIconClick)
                    }) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue"
                    )
                }
            }
        }
    )
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    currentPage: Int,
    interactionSource: MutableInteractionSource
) {
    val state = viewModel.state
    Box(modifier = modifier) {
        if (currentPage != 0 && currentPage != 4) {
            OutlinedTextField(
                interactionSource = interactionSource,
                value = state.searchQuery,
                onValueChange = {
                    viewModel.onEvent(MainEvent.OnSearchQueryChange(it))
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.topBar_search_hint))
                },
                maxLines = 1,
                singleLine = true
            )
        }
    }
}

sealed class MainContentTopAppBarEvent {
    data object OnLeftDrawerIconClick: MainContentTopAppBarEvent()
    data object OnPlaylistIconClick: MainContentTopAppBarEvent()
}

@Composable
private fun generateBarTitle(song: Song?): String =
    stringResource(id = R.string.app_name) + (song?.title?.let {
        "(${song.artist.name} - ${song.title})"
    } ?: "" )
