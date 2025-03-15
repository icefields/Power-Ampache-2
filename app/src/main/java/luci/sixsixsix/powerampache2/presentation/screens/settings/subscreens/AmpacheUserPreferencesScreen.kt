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
package luci.sixsixsix.powerampache2.presentation.screens.settings.subscreens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AmpachePreferencesRepository
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreference
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreferenceType
import luci.sixsixsix.powerampache2.presentation.common.CircleBackButton
import luci.sixsixsix.powerampache2.presentation.common.DividerSeparator
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpSwitch
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle
import javax.inject.Inject

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
                scrollBehavior = scrollBehavior
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
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 16.dp)) {
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

@Composable
fun PowerAmpEdit(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit),
) {
    var text by remember { mutableStateOf(value) }

    Column(modifier = modifier.alpha(if (!enabled) { 0.5f } else { 1f })) {
        TextWithSubtitle(
            modifier = Modifier.fillMaxWidth(),
            title = title,
            subtitle = null
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                text,
                singleLine = true,
                modifier = Modifier.weight(3f),
                onValueChange = { text = it },
                enabled = true
            )
            Button(
                modifier = Modifier.wrapContentWidth().padding(start = 10.dp),
                onClick = { onValueChange(text) },
                enabled = true,
            ) {
                Text(stringResource(android.R.string.ok))
            }
        }

    }

}

@Composable
private fun CategoryText(
    category: String,
    textSize: TextUnit = 12.sp,
    colour: Color = MaterialTheme.colorScheme.tertiary
) {
    Text(category.uppercase(),
        modifier = Modifier.padding(horizontal = 12.dp),
        fontWeight = FontWeight.Bold,
        fontSize = textSize,
        color = colour
    )
}

sealed class AmpacheUserPreferencesEvent {
    data object GetUserPreferences: AmpacheUserPreferencesEvent()
    data object GetSystemPreferences: AmpacheUserPreferencesEvent()
    data class UpdatePreference(val ampachePreference: AmpachePreference, val newValue: String): AmpacheUserPreferencesEvent()
    data class UpdateSystemPreference(val ampachePreference: AmpachePreference, val newValue: String): AmpacheUserPreferencesEvent()
}

data class AmpachePreferencesState (
    val userPreferences: Map<String, AmpachePreference> = mapOf(),
    val systemPreferences: Map<String, AmpachePreference> = mapOf(),
    val isLoading: Boolean = false,
    val isLoadingEdit: Boolean = false
)

@HiltViewModel
class AmpacheUserPreferencesViewModel @Inject constructor(
    private val repository: AmpachePreferencesRepository,
    private val musicRepository: MusicRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    var state by mutableStateOf(AmpachePreferencesState())

    init {
        viewModelScope.launch {
            musicRepository.userLiveData.filterNotNull().first().let { user ->
                getUserPreferences()
                getSystemPreferences()
            }
        }
    }

    fun onEvent(event: AmpacheUserPreferencesEvent) {
        when(event) {
            is AmpacheUserPreferencesEvent.GetUserPreferences -> getUserPreferences()
            is AmpacheUserPreferencesEvent.GetSystemPreferences -> getSystemPreferences()
            is AmpacheUserPreferencesEvent.UpdatePreference ->
                updatePreference(event.ampachePreference, event.newValue, false)
            is AmpacheUserPreferencesEvent.UpdateSystemPreference ->
                updatePreference(event.ampachePreference, event.newValue, true)
        }
    }

    private fun updatePreference(ampachePreference: AmpachePreference, newValue: String, isSystem: Boolean) = viewModelScope.launch {
        repository.updateAmpachePreference(
            filter = ampachePreference.name,
            value = newValue,
            applyToAll = isSystem
        ).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        var updated = false
                        if (isSystem) {
                            if (state.systemPreferences.containsKey(data.name)) {
                                updated = true
                                state = state.copy(
                                    systemPreferences = state.systemPreferences.toMutableMap()
                                        .apply {
                                            this[data.name] = data
                                        }
                                )
                            }
                        }
                        // user preference needs to be updated both when isSystem is true or false
                        if (state.userPreferences.containsKey(data.name)) {
                            updated = true
                            state = state.copy(
                                userPreferences = state.userPreferences.toMutableMap().apply {
                                    this[data.name] = data
                                }
                            )
                        }
                        if (updated)
                            Toast.makeText(context, R.string.ampachePreferences_updated_toast, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> state = state.copy(isLoadingEdit = false)
                is Resource.Loading -> state = state.copy(isLoadingEdit = result.isLoading)
            }
        }
    }

    private fun getSystemPreferences() = viewModelScope.launch {
        repository.getAmpacheSystemPreferences().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        state = state.copy(systemPreferences = data.associateBy { it.name })
                    }
                }
                is Resource.Error -> state = state.copy(isLoading = false)
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
            }
        }
    }

    private fun getUserPreferences() = viewModelScope.launch {
        repository.getAmpacheUserPreferences().collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        state = state.copy(userPreferences = data.associateBy { it.name })
                    }
                }
                is Resource.Error -> state = state.copy(isLoading = false)
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
            }
        }
    }
}
