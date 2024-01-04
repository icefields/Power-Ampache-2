package luci.sixsixsix.powerampache2.presentation.album_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlbumDetailTopBar(
    navigator: DestinationsNavigator,
    album: Album,
    isLoading: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onRightIconClick: () -> Unit
) {
    LargeTopAppBar(
        modifier = Modifier.background(Color.Transparent),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        title = {
            Text(
                modifier = Modifier.basicMarquee().padding(15.dp),
                text = "${album.name} - ${album.artist.name}",
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                style = TextStyle(
                    fontSize = fontDimensionResource(id = R.dimen.albumDetail_title_fontSize),
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.background,
                        offset = Offset(0.0f, 0.0f),
                        blurRadius = 32f
                    )
                ),
                fontSize = fontDimensionResource(id = R.dimen.albumDetail_title_fontSize)

            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navigator.navigateUp()
            }) {
                Icon(
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_content_description)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxHeight().padding(12.dp)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            IconButton(onClick = {
                onRightIconClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search_content_description)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 300) //(widthDp = 50, heightDp = 50)
@Composable
fun AlbumDetailTopBarPreview() {
    AlbumDetailTopBar(
        navigator = EmptyDestinationsNavigator,
        album = Album(
            name = "Peace Sells, But Who's Buying",
            artist = MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
            time = 129,
            id = UUID.randomUUID().toString(),
            songCount = 11,
            genre = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Thrash Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Progressive Metal"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Jazz"),
            ),
            artists = listOf(
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Megadeth"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Marty Friedman"),
                MusicAttribute(id = UUID.randomUUID().toString(), name = "Other people"),
            ),
            year = 1986),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
        onRightIconClick = {},
        isLoading = true
    )
}
