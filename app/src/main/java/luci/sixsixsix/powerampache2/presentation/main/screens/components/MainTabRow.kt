package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Piano
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource

object MainTabRow {
    val tabItems: List<TabItem> = listOf(
        TabItem.Albums,
        TabItem.Playlists,
        TabItem.Songs,
        TabItem.Artists
    )

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainTabRow(pagerState: PagerState) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex = pagerState.currentPage
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = stringResource(id = item.titleRes),
                            fontSize = fontDimensionResource(id = R.dimen.main_tab_textSize),
                            maxLines = 1
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedTabIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = stringResource(id = item.titleRes)
                        )
                    }
                )
            }
        }
    }

}

sealed class TabItem(
    @StringRes val titleRes: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Songs: TabItem(
        titleRes = R.string.main_tab_title_songs,
        unselectedIcon = Icons.Outlined.LibraryMusic,
        selectedIcon = Icons.Filled.LibraryMusic
    )

    data object Albums: TabItem(
        R.string.main_tab_title_albums,
        unselectedIcon = Icons.Outlined.Album,
        selectedIcon = Icons.Filled.Album
    )

    data object Playlists: TabItem(
         R.string.main_tab_title_playlists,
        unselectedIcon = Icons.Outlined.FeaturedPlayList,
        selectedIcon = Icons.Filled.FeaturedPlayList
    )

    data object Artists: TabItem(
        R.string.main_tab_title_artists,
        unselectedIcon = Icons.Outlined.Piano,
        selectedIcon = Icons.Filled.Piano
    )
}
