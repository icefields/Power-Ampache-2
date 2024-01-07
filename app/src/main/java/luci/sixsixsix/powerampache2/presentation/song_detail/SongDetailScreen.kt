package luci.sixsixsix.powerampache2.presentation.song_detail


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailContent
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueDragHandle
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailQueueScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    // navigator: DestinationsNavigator,
    mainScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val barHeight = dimensionResource(id = R.dimen.queue_dragHandle_height)
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SongDetailQueueScreenContent(
                mainScaffoldState = mainScaffoldState,
                mainViewModel = viewModel
            )
        },
        sheetDragHandle = {
            SongDetailQueueDragHandle(scaffoldState = scaffoldState)
        },
        sheetShape = RectangleShape,
        sheetSwipeEnabled = true,
        sheetPeekHeight = barHeight
    ) {
        SongDetailContent(
            mainScaffoldState = mainScaffoldState,
            modifier = Modifier.padding(paddingValues = it),
            mainViewModel = viewModel)
    }
}
