package luci.sixsixsix.powerampache2.presentation.songs.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset

@Composable
fun SongDropDownMenu(
    modifier: Modifier = Modifier,
    isContextMenuVisible: Boolean,
    pressOffset: DpOffset,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    onDismissRequest:() -> Unit
) {
    DropdownMenu(
        expanded = isContextMenuVisible,
        offset = pressOffset.copy(
            y = pressOffset.y,
            x = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.toDp() }
        ),
        onDismissRequest = onDismissRequest
    ) {

        DropdownMenuItem(text = { Text(text = "Play next") }, onClick = {
            songItemEventListener(SongItemEvent.PLAY_NEXT)
        })
        DropdownMenuItem(text = { Text(text = "Add to queue") }, onClick = {
            songItemEventListener(SongItemEvent.ADD_SONG_TO_QUEUE)
        })
        DropdownMenuItem(text = { Text(text = "Add to playlist") }, onClick = {
            songItemEventListener(SongItemEvent.ADD_SONG_TO_PLAYLIST)
        })
        DropdownMenuItem(text = { Text(text = "Go to Album") }, onClick = {
            songItemEventListener(SongItemEvent.GO_TO_ALBUM)
        })
        DropdownMenuItem(text = { Text(text = "Go to Artist") }, onClick = {
            songItemEventListener(SongItemEvent.GO_TO_ARTIST)
        })
        DropdownMenuItem(text = { Text(text = "Share") }, onClick = {
            songItemEventListener(SongItemEvent.SHARE_SONG)
        })
        DropdownMenuItem(text = { Text(text = "Download") }, onClick = {
            songItemEventListener(SongItemEvent.DOWNLOAD_SONG)
        })
    }
}
