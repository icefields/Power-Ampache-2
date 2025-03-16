/**
 * Copyright (C) 2025  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R

@Composable
fun PreferencesTabRow(
    modifier: Modifier = Modifier,
    showSystem: Boolean,
    pagerState: PagerState,
    selectedTabIndex: MutableIntState
) {
    LaunchedEffect(selectedTabIndex.intValue) {
        pagerState.animateScrollToPage(selectedTabIndex.intValue)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex.intValue = pagerState.currentPage
        }
    }

    val scope = rememberCoroutineScope()
    val textColour = MaterialTheme.colorScheme.onSurface
    TabRow(
        // indicator = TabRowDefaults.PrimaryIndicator(),
        modifier = modifier,
        selectedTabIndex = selectedTabIndex.intValue,
        contentColor = textColour,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Tab(
            unselectedContentColor = textColour.copy(alpha = 0.66f),
            selected = selectedTabIndex.intValue == 0,
            onClick = {
                scope.launch {
                    selectedTabIndex.intValue = 0
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.ampachePreferences_userPreferences_title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
        )
        if (showSystem) {
            Tab(
                unselectedContentColor = textColour.copy(alpha = 0.66f),
                selected = selectedTabIndex.intValue == 1,
                onClick = {
                    scope.launch {
                        selectedTabIndex.intValue = 1
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.ampachePreferences_systemPreferences_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}