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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreference
import luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.AmpachePreferencesScreenContent

@Composable
fun TabbedPreferencesView(
    userPreferences: Map<String, AmpachePreference>,
    systemPreferences: Map<String, AmpachePreference>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onUpdateUserPreference: (AmpachePreference, String) -> Unit,
    onUpdateSystemPreference: (AmpachePreference, String) -> Unit
) {
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
        ) { index ->
            when(index) {
                0 -> {
                    AmpachePreferencesScreenContent(
                        preferenceMap = userPreferences,
                        onUpdatePreference = onUpdateUserPreference
                    )
                }
                else -> {
                    AmpachePreferencesScreenContent(
                        preferenceMap = systemPreferences,
                        onUpdatePreference = onUpdateSystemPreference
                    )
                }
            }
        }
    }
}
