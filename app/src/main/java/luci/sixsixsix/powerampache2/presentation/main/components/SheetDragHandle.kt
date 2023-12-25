package luci.sixsixsix.powerampache2.presentation.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.presentation.main.MainState
import luci.sixsixsix.powerampache2.presentation.main.subscreens.miniPlayerHeight

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun SheetDragHandle(state: MainState, scaffoldState: BottomSheetScaffoldState) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .height(miniPlayerHeight)
        .fillMaxWidth()
        .background(Color.DarkGray)
    ) {
        Text(text = state.song?.title ?: "ERROR")
        // show mini-player
        Box(modifier = Modifier
            .height(
                // if it's expanded do not show the player
                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                    0.dp
                } else {
                    miniPlayerHeight
                }
            )
            .fillMaxWidth()
            .background(Color.Blue)
            .clickable {
                scope.launch {
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                        scaffoldState.bottomSheetState.partialExpand() //only peek
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }
        ) {}
    }
}
