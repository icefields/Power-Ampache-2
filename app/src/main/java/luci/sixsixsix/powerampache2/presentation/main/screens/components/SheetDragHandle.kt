package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.presentation.song_detail.components.SongDetailTopBar
import luci.sixsixsix.powerampache2.presentation.song_detail.components.MiniPlayer

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SheetDragHandle(
    scaffoldState: BottomSheetScaffoldState,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val barHeight = dimensionResource(id = R.dimen.miniPlayer_height)

    Box(modifier = Modifier
        .height(dimensionResource(id = R.dimen.miniPlayer_height))
        .fillMaxWidth()
    ) {
        // show mini-player
        Box(modifier = Modifier
            .height(
                // if it's expanded do not show the player
                //if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                //    0.dp
                //} else {
                barHeight
                //}
            )
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                        scaffoldState.bottomSheetState.partialExpand() //only peek
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }
        ) {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                SongDetailTopBar(mainViewModel = mainViewModel)
            } else {
                MiniPlayer(mainViewModel = mainViewModel)
            }
        }
    }
}
