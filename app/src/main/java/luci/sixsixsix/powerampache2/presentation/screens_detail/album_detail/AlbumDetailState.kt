package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Song

@Parcelize
data class AlbumDetailState (
    val album: Album = Album(),
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isLikeLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
): Parcelable
