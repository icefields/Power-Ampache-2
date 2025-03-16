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
package luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.AMPACHE_PREFERENCE_UNDO_VISIBLE
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreference
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreferenceType
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.common.DividerSeparator
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpSwitch
import luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.components.CategoryText
import luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.components.PowerAmpEdit
import luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.components.PreferencesTabRow
import luci.sixsixsix.powerampache2.presentation.screens.settings.ampache_preferences.components.TabbedPreferencesView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = false)
fun AmpacheUserPreferencesScreen(
    navigator: DestinationsNavigator,
    viewModel: AmpacheUserPreferencesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val pagerState = rememberPagerState(initialPage = 0) {
        if (state.systemPreferences.isNotEmpty()) { 2 } else { 1 }
    }
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Transparent),
                title = {
                    Text(
                        text = stringResource(R.string.ampachePreferences_screen_title),
                        maxLines = 1,
                        fontWeight = FontWeight.Normal,
                    )
                },
                navigationIcon = { CircleBackButton { navigator.navigateUp() } },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (AMPACHE_PREFERENCE_UNDO_VISIBLE) {
                        TextButton(onClick = {
                            viewModel.onEvent(AmpacheUserPreferencesEvent.Undo)
                        }) {
                            Text(stringResource(R.string.ampachePreferences_screen_undo_btn_title))
                        }
                    }
                }
            )
        }
    ) {
        Surface(modifier = Modifier.padding(it).padding(top = dimensionResource(id = R.dimen.albumDetailScreen_top_padding))) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(44.dp).align(Alignment.Center)
                    )
                }
            } else {
                Column {
                    if (state.systemPreferences.isNotEmpty()) {
                        PreferencesTabRow(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            showSystem = state.systemPreferences.isNotEmpty(),
                            pagerState = pagerState,
                            selectedTabIndex = selectedTabIndex
                        )
                    }
                    TabbedPreferencesView(
                        modifier = Modifier.fillMaxWidth().weight(1.0f),
                        userPreferences = state.userPreferences,
                        systemPreferences = state.systemPreferences,
                        pagerState = pagerState,
                        onUpdateUserPreference = { userPreference, newValue ->
                            viewModel.onEvent(
                                AmpacheUserPreferencesEvent.UpdatePreference(userPreference, newValue)
                            )
                        },
                        onUpdateSystemPreference = { systemPreference, newValue ->
                            viewModel.onEvent(
                                AmpacheUserPreferencesEvent.UpdateSystemPreference(systemPreference, newValue)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AmpachePreferencesScreenContent(
    preferenceMap: Map<String, AmpachePreference>,
    onUpdatePreference: (AmpachePreference, String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(preferenceMap.keys.size) { i ->
            preferenceMap[preferenceMap.keys.toList()[i]]?.let { userPreference ->
                if (i == 0) {
                    CategoryText(userPreference.category)
                } else if (i > 0) {
                    preferenceMap[preferenceMap.keys.toList()[i - 1]]?.category?.let { previousElementCategory ->
                            if (previousElementCategory != userPreference.category) {
                                CategoryText(userPreference.category)
                            }
                        }
                }

                when (userPreference.type) {
                    AmpachePreferenceType.BOOLEAN -> {
                        PowerAmpSwitch(
                            enabled = true,
                            title = userPreference.description,
                            subtitle = null,
                            clickActionOnText = false,
                            checked = userPreference.value != "0",
                            onCheckedChange = { newValue ->
                                onUpdatePreference(userPreference, if (newValue) "1" else "0")
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    AmpachePreferenceType.STRING -> {
                        PowerAmpEdit(
                            enabled = true,
                            title = userPreference.description,
                            value = userPreference.value,
                            onValueChange = { newValue ->
                                onUpdatePreference(userPreference, newValue)
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    AmpachePreferenceType.INTEGER -> PowerAmpEdit(
                        enabled = true,
                        title = userPreference.description,
                        value = userPreference.value,
                        onValueChange = { newValue ->
                            onUpdatePreference(userPreference, newValue)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    AmpachePreferenceType.SPECIAL -> PowerAmpEdit(
                        enabled = true,
                        title = userPreference.description,
                        value = userPreference.value,
                        onValueChange = { newValue ->
                            onUpdatePreference(userPreference, newValue)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    AmpachePreferenceType.NONE -> {}
                }
                DividerSeparator(
                    modifier = Modifier.fillMaxWidth()
                        .height(1.dp), useDivider = true
                )
            }
        }
    }
}
