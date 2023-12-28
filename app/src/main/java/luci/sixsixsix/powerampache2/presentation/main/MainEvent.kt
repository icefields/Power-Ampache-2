package luci.sixsixsix.powerampache2.presentation.main

import luci.sixsixsix.powerampache2.domain.models.Song

sealed class MainEvent {
    data class OnSearchQueryChange(val query: String): MainEvent()
    data object OnDismissErrorMessage: MainEvent()
    data object OnLogout: MainEvent() // TODO move this to AuthViewModel
    data class Play(val song: Song): MainEvent()
}
