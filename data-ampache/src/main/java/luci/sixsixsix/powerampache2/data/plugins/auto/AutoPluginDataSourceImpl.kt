package luci.sixsixsix.powerampache2.data.plugins.auto

import android.os.TransactionTooLargeException
import luci.sixsixsix.powerampache2.domain.errors.Pa2CastQueueException
import luci.sixsixsix.powerampache2.domain.errors.Pa2DataPluginException
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.auto.AutoPluginDataSource
import javax.inject.Inject

/**
 *
 */
class AutoPluginDataSourceImpl @Inject constructor(
    private val autoPluginClient: AutoPluginClient
): AutoPluginDataSource {
    override fun isAutoPluginInstalled(): Boolean = autoPluginClient.isAutoPluginInstalled()

    /**
     * This requires an action from the user to send the queue. This will not happen automatically.
     */
    @Throws(Pa2DataPluginException::class)
    override suspend fun sendQueueToAuto(queue: List<Song>) {
        try {
            autoPluginClient.sendQueueToPlugin(queue)
        } catch (e: TransactionTooLargeException) {
            throw Pa2CastQueueException(e)
        }
    }


    override suspend fun sendAlbumsToAuto(albums: List<Album>) {
        TODO("Not yet implemented")
    }

    override suspend fun sendArtistsToAuto(queue: List<Artist>) {
        TODO("Not yet implemented")
    }

    @Throws(Pa2DataPluginException::class)
    override suspend fun sendFavouriteAlbumsToAuto(albums: List<Album>) {
        autoPluginClient.sendFavouriteAlbumsToPlugin(
            albums
            // albumsRepository.getFlaggedAlbums().first().data ?: emptyList()
        )
    }

    @Throws(Pa2DataPluginException::class)
    override suspend fun sendLatestAlbumsToAuto(albums: List<Album>) {
        autoPluginClient.sendLatestAlbumsToPlugin(
            albums
            // albumsRepository.getNewestAlbums().first().data ?: emptyList()
        )
    }

    @Throws(Pa2DataPluginException::class)
    override suspend fun sendRecentAlbumsToAuto(albums: List<Album>) {
        autoPluginClient.sendRecentAlbumsToPlugin(
            albums
            // albumsRepository.getRecentAlbums().first().data ?: emptyList()
        )
    }

    @Throws(Pa2DataPluginException::class)
    override suspend fun sendHighestAlbumsToAuto(albums: List<Album>) {
        autoPluginClient.sendHighestAlbumsToPlugin(
            albums
            //albumsRepository.getHighestAlbums().first().data ?: emptyList()
        )
    }
}
