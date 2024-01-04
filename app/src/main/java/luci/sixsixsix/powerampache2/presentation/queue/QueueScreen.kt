package luci.sixsixsix.powerampache2.presentation.queue

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.queue.components.QueueScreenContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@Destination(start = false)
fun QueueScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: QueueViewModel = hiltViewModel()
) {
    val state = mainViewModel.state
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var infoVisibility by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Transparent),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                title = {
                    Text(
                        text = "Queue",
                        maxLines = 1,
                        fontWeight = FontWeight.Normal,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_content_description)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = {
                            // show menu to add to an existing playlist or create a new one
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = stringResource(id = R.string.search_content_description)
                        )
                    }
                    IconButton(
                        onClick = {
                            mainViewModel.onEvent(MainEvent.Play(mainViewModel.state.queue[0]))
                            viewModel.onEvent(QueueEvent.OnPlayQueue)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(id = R.string.search_content_description)
                        )
                    }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .padding(it)
                .padding(top = dimensionResource(id = R.dimen.albumDetailScreen_top_padding)),
        ) {
            QueueScreenContent(navigator)
        }
    }
}