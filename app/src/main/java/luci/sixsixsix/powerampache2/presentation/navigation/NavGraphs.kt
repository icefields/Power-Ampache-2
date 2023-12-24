package luci.sixsixsix.powerampache2.presentation.navigation

import luci.sixsixsix.powerampache2.presentation.NavGraph
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumsScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistsScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.HomeScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.LoggedInScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.LoginScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistsScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.SongsListScreenDestination

object Ampache2NavGraphs {

    val albums: NavGraph = NavGraph(
        route = "albums",
        startRoute = AlbumsScreenDestination,
        destinations = listOf(
            AlbumDetailScreenDestination,
            AlbumsScreenDestination,
            AlbumDetailScreenDestination,
        )
    )

    val artists: NavGraph = NavGraph(
        route = "artists",
        startRoute = ArtistsScreenDestination,
        destinations = listOf(
            ArtistDetailScreenDestination,
            ArtistsScreenDestination,
            AlbumDetailScreenDestination,)
    )

    val playlists: NavGraph = NavGraph(
        route = "playlists",
        startRoute = PlaylistsScreenDestination,
        destinations = listOf(
            ArtistDetailScreenDestination,
            PlaylistsScreenDestination,
            PlaylistDetailScreenDestination,
            AlbumDetailScreenDestination,)
    )

    val home: NavGraph = NavGraph(
        route = "home",
        startRoute = HomeScreenDestination,
        destinations = listOf(
            ArtistDetailScreenDestination,
            HomeScreenDestination,
            PlaylistDetailScreenDestination,
            AlbumDetailScreenDestination,)
    )

    val root: NavGraph = NavGraph(
        route = "root",
        startRoute = LoggedInScreenDestination,
        destinations = listOf(
            LoginScreenDestination,
            LoggedInScreenDestination,
            PlaylistDetailScreenDestination,
            PlaylistsScreenDestination,
            SongsListScreenDestination
        ),
        nestedNavGraphs = listOf(
            albums,
            artists
        )
    )
}
