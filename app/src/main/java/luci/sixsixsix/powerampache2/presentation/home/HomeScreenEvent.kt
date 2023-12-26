package luci.sixsixsix.powerampache2.presentation.home

sealed class HomeScreenEvent {
    data object Refresh: HomeScreenEvent()
    data class OnSearchQueryChange(val query: String): HomeScreenEvent()
}
