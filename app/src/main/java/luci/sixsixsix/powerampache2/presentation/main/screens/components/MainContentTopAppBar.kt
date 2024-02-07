package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.presentation.destinations.QueueScreenDestination
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContentTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchVisibility: MutableState<Boolean>,
    title: String,
    modifier: Modifier = Modifier,
    isQueueEmpty: Boolean,
    isFabLoading: Boolean,
    floatingActionVisible: Boolean,
    onMagicPlayClick: () -> Unit,
    onNavigationIconClick: (MainContentTopAppBarEvent) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val transitionState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    TopAppBar(
        modifier = modifier,
        title = {
            AnimatedVisibility(visibleState = transitionState) {
                Text(
                    modifier = Modifier
                        .basicMarquee(),
                    text = title,
                    maxLines = 1
                )
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
            IconButton(
                onClick = {
                    searchVisibility.value = !searchVisibility.value
                }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    //tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(!isQueueEmpty) {
                IconButton(
                    onClick = {
                        onNavigationIconClick(MainContentTopAppBarEvent.OnPlaylistIconClick)
                    }) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        //tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (!isQueueEmpty) {

            }

            if (!floatingActionVisible) {

            }
            AnimatedVisibility(!floatingActionVisible) {
                if (isFabLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(46.dp)
                            .padding(6.dp))
                } else {
                    Icon(modifier = Modifier.size(46.dp).padding(horizontal = 6.dp)
                        .clickable {
                            onMagicPlayClick()
                        },
                        painter = painterResource(id = R.drawable.ic_play_speaker),
                        contentDescription = "Quick Play",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
//                IconButton(onClick = onMagicPlayClick) {
//                    Icon(
//                        painterResource(id = R.drawable.ic_play_speaker),
//                        contentDescription = "Magic Play"
//                    )
//                }
            }
        }
    )
}

@Composable
fun TopBarOLD(
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


