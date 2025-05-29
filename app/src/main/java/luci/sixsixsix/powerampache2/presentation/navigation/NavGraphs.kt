package luci.sixsixsix.powerampache2.presentation.navigation

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination

//import luci.sixsixsix.powerampache2.presentation.NavGraph
//import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.AlbumsScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.ArtistDetailScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.ArtistsScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.HomeScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.LoginScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.MainContentDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistsScreenDestination
//import luci.sixsixsix.powerampache2.presentation.destinations.SongsListScreenDestination

object Ampache2NavGraphs {

    // TODO this is not taking into consideration lifecycle, it's set the first time the NavGraph
    //  is declared inside LoggedInScreen and MainContent
    var navigator: DestinationsNavigator? = null

    fun navigateToAlbum(albumId: String) = try {
        navigator?.navigate(AlbumDetailScreenDestination(albumId = albumId))
        true
    } catch (e: Exception) {
        L.e(e)
        false
    }


    fun navigateToArtist(artistId: String, artist: Artist? = null) = try {
        navigator?.navigate(ArtistDetailScreenDestination(artistId = artistId, artist = artist))
        true
    } catch (e: Exception) {
        L.e(e)
        false
    }

    fun navigateToArtist(
        nav: DestinationsNavigator?,
        artistId: String,
        artist: Artist? = null
    ) = nav?.let {
        it.navigate(ArtistDetailScreenDestination(artistId = artistId, artist = artist))
        if (navigator == null) navigator = it
    } ?: navigateToArtist(artistId, artist)


//
//    val albums: NavGraph = NavGraph(
//        route = "albums",
//        startRoute = AlbumsScreenDestination,
//        destinations = listOf(
//            AlbumDetailScreenDestination,
//            AlbumsScreenDestination,
//            AlbumDetailScreenDestination,
//        )
//    )
//
//    val artists: NavGraph = NavGraph(
//        route = "artists",
//        startRoute = ArtistsScreenDestination,
//        destinations = listOf(
//            ArtistDetailScreenDestination,
//            ArtistsScreenDestination,
//            AlbumDetailScreenDestination,)
//    )
//
//    val playlists: NavGraph = NavGraph(
//        route = "playlists",
//        startRoute = PlaylistsScreenDestination,
//        destinations = listOf(
//            ArtistDetailScreenDestination,
//            PlaylistsScreenDestination,
//            PlaylistDetailScreenDestination,
//            AlbumDetailScreenDestination,)
//    )
//
//    val home: NavGraph = NavGraph(
//        route = "home",
//        startRoute = HomeScreenDestination,
//        destinations = listOf(
//            ArtistDetailScreenDestination,
//            HomeScreenDestination,
//            PlaylistDetailScreenDestination,
//            AlbumDetailScreenDestination,)
//    )
//
//    val root: NavGraph = NavGraph(
//        route = "root",
//        startRoute = MainContentDestination,
//        destinations = listOf(
//            LoginScreenDestination,
//            AlbumDetailScreenDestination,
//            MainContentDestination,
//            AlbumDetailScreenDestination,
//            PlaylistDetailScreenDestination,
//            PlaylistsScreenDestination,
//            SongsListScreenDestination
//        ),
//        nestedNavGraphs = listOf(
//            albums,
//            artists
//        )
//    )
}
