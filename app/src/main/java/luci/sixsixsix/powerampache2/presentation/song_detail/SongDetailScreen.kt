package luci.sixsixsix.powerampache2.presentation.song_detail


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.getPeakHeight
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueDragHandle
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueTopBar
import luci.sixsixsix.powerampache2.presentation.queue.QueueEvent
import luci.sixsixsix.powerampache2.presentation.queue.QueueViewModel
import luci.sixsixsix.powerampache2.presentation.queue.components.QueueScreenContent
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailContent
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueScreenContent
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItem
import luci.sixsixsix.powerampache2.presentation.songs.components.SongItemEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    //navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val scaffoldState = rememberBottomSheetScaffoldState()
    val barHeight = dimensionResource(id = R.dimen.queue_dragHandle_height)
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SongDetailQueueScreenContent()
        },
        sheetDragHandle = {
            SongDetailQueueDragHandle(scaffoldState = scaffoldState)
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = barHeight
    ) {
        SongDetailContent(
            //navigator = navigator,
            Modifier.padding(paddingValues = it))
    }
}
