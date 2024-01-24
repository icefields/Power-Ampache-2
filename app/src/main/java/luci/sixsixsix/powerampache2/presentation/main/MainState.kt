package luci.sixsixsix.powerampache2.presentation.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import luci.sixsixsix.powerampache2.domain.models.Song

@Parcelize
data class MainState (
    val searchQuery: String = "",
    val errorMessage: String = "",
    val song: Song? = null,
    val queue: List<Song> = listOf(),
    val isLikeLoading:Boolean = false,
    val isDownloading:Boolean = false
): Parcelable
