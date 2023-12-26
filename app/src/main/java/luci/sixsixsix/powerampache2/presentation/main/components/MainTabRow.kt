package luci.sixsixsix.powerampache2.presentation.main.components

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
import androidx.compose.ui.unit.sp

object MainTabRow {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainTabRow(pagerState: PagerState) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if(!pagerState.isScrollInProgress) {
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
                        Text(text = item.title, fontSize = 12.sp, maxLines = 1)
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedTabIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }
    }

    data class TabItem(
        val title: String,
        val unselectedIcon: ImageVector,
        val selectedIcon: ImageVector
    )

    val tabItems = listOf<TabItem>(
        TabItem("Home", unselectedIcon = Icons.Outlined.Home, selectedIcon = Icons.Filled.Home),
        TabItem("Songs", unselectedIcon = Icons.Outlined.LibraryMusic, selectedIcon = Icons.Filled.LibraryMusic),
        TabItem("Albums", unselectedIcon = Icons.Outlined.Album, selectedIcon = Icons.Filled.Album),
        TabItem("Artists", unselectedIcon = Icons.Outlined.Piano, selectedIcon = Icons.Filled.Piano),
        TabItem("Playlists", unselectedIcon = Icons.Outlined.FeaturedPlayList, selectedIcon = Icons.Filled.FeaturedPlayList),
    )
}
