package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.presentation.albums.AlbumsEvent

sealed class MainEvent {
    data class OnSearchQueryChange(val query: String): MainEvent()

}
