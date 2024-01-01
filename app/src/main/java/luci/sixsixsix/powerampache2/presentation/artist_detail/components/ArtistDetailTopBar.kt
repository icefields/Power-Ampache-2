package luci.sixsixsix.powerampache2.presentation.artist_detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Artist


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArtistDetailTopBar(
    navigator: DestinationsNavigator,
    artist: Artist,
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
                text = artist.name,
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
fun ArtistDetailTopBarTopBarPreview() {
    ArtistDetailTopBar(
        navigator = EmptyDestinationsNavigator,
        artist = Artist.mockArtist(),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
        onRightIconClick = {},
        isLoading = true
    )
}
