package luci.sixsixsix.powerampache2.presentation.song_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel


@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun SongDetailQueueDragHandle(
    scaffoldState: BottomSheetScaffoldState,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val barHeight = dimensionResource(id = R.dimen.queue_dragHandle_height)

    Box(modifier = Modifier
        .height(dimensionResource(id = R.dimen.queue_dragHandle_height))
        .fillMaxWidth()
    ) {
        // show mini-player
        Box(modifier = Modifier
            .height(barHeight)
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
                SongDetailQueueTopBar()
            } else {
                SongDetailQueueTopBar(showCloseIcon = false)
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDetailQueueTopBar(
    modifier: Modifier = Modifier,
    showCloseIcon: Boolean = true
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(
                vertical = 5.dp,
                horizontal = 8.dp
            )
    ) {
        if (showCloseIcon) {
//            Image(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .padding(2.dp),
//                contentScale = ContentScale.FillHeight,
//                imageVector = Icons.Default.KeyboardArrowDown,
//                contentDescription = "close song detail screen"
//            )

            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(dimensionResource(id = R.dimen.close_handle_icon_padding)),
                //contentScale = ContentScale.FillHeight,
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "close song detail screen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.basicMarquee(),
                text = "UP NEXT",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
