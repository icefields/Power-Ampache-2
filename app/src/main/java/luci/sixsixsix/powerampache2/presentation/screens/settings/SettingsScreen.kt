/**
 * Copyright (C) 2024  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.IS_AMPACHE_DATA
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.models.settings.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.settings.StreamingQuality
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.ui.getTitleRes
import luci.sixsixsix.powerampache2.ui.isThemeAvailable
import luci.sixsixsix.powerampache2.ui.streamQualityDropdownItems
import luci.sixsixsix.powerampache2.ui.themesDropDownItems
import luci.sixsixsix.powerampache2.ui.toPowerAmpacheDropdownItem
import luci.sixsixsix.powerampache2.presentation.common.DonateConsider
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpCheckBox
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpSwitch
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle
import luci.sixsixsix.powerampache2.presentation.common.donate_btn.DonateButton
import luci.sixsixsix.powerampache2.presentation.common.donate_btn.DonateButtonContent
import luci.sixsixsix.powerampache2.presentation.destinations.AmpacheUserPreferencesScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.DebugLogsScreenDestination
import luci.sixsixsix.powerampache2.presentation.dialogs.EraseConfirmDialog
import luci.sixsixsix.powerampache2.presentation.screens.settings.components.PlayerSettingsView
import luci.sixsixsix.powerampache2.presentation.screens.settings.components.SettingsDropDownMenu

private const val IS_MONO_SWITCH_ENABLED = false
private const val IS_NORMALIZE_SWITCH_ENABLED = false
private const val IS_OFFLINE_MODE_SWITCH_ENABLED = true
private const val IS_SMART_DOWNLOADS_SWITCH_ENABLED = false
private const val IS_AUTO_UPDATE_ENABLED = true
private const val IS_EQUALIZER_BTN_ENABLED = true
private const val MAX_VISIBLE_ITEMS = 15

@Composable
@Destination
fun SettingsScreen(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel
) {
    val localSettingsState by settingsViewModel.localSettingsStateFlow.collectAsState()
    val offlineModeState by settingsViewModel.offlineModeStateFlow.collectAsState()
    val user by settingsViewModel.userStateFlow.collectAsState()
    val serverInfo by settingsViewModel.serverInfoStateFlow.collectAsState(ServerInfo())
    val playerSettingsState by settingsViewModel.playerSettingsStateFlow.collectAsState()

    var showKillDialog by remember { mutableStateOf(false) }

    if (showKillDialog) {
        AlertDialog(
            onDismissRequest = { showKillDialog = false },
            title = { Text(stringResource(R.string.settings_player_killDialog_title)) },
            confirmButton = {
                Button(onClick = {
                    showKillDialog = false
                    settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnKillApp)
                }) { Text(stringResource(android.R.string.yes)) }
            },
            dismissButton = {
                Button(onClick = { showKillDialog = false }) {
                    Text(stringResource(android.R.string.no)) }
            }
        )
    }

    SettingsScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
        userState = user,
        serverInfo = serverInfo,
        versionInfo = settingsViewModel.state.appVersionInfoStr,
        powerAmpTheme = localSettingsState.theme,
        streamingQuality = localSettingsState.streamingQuality,
        isDownloadAfterPlayEnabled = localSettingsState.saveSongAfterPlayback,
        isDownloadFavouriteAfterPlayEnabled = localSettingsState.saveFavouriteSongAfterPlayback,
        remoteLoggingEnabled = localSettingsState.enableRemoteLogging,
        hideDonationButtons = localSettingsState.hideDonationButton,
        isNormalizeVolumeEnabled = localSettingsState.isNormalizeVolumeEnabled,
        isMonoAudioEnabled = localSettingsState.isMonoAudioEnabled,
        isSmartDownloadsEnabled = localSettingsState.isSmartDownloadsEnabled,
        isAutoCheckUpdatesEnabled = localSettingsState.enableAutoUpdates,
        isOfflineModeEnabled = offlineModeState,
        isDownloadSdCard = localSettingsState.isDownloadsSdCard,
        onThemeSelected = {
            settingsViewModel.onEvent(SettingsEvent.OnThemeChange(it))
        },
        onStreamingQualitySelected = {
            settingsViewModel.onEvent(SettingsEvent.OnStreamingQualityChange(it))
        },
        onEnableLoggingValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnEnableRemoteLoggingSwitch(it))
        },
        onHideDonateValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnHideDonationButtonSwitch(it))
        },
        onAutoCheckUpdatesValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnAutomaticUpdateValueChange(it))
        },
        onCheckUpdatesNowPress = {
            settingsViewModel.onEvent(SettingsEvent.UpdateNow)
        },
        onDebugLogsButtonPress = {
            navigator.navigate(DebugLogsScreenDestination())
        },
        onAmpachePreferencesButtonPress = {
            navigator.navigate(AmpacheUserPreferencesScreenDestination())
        },
        onDeleteDownloadsPress = {
            settingsViewModel.onEvent(SettingsEvent.DeleteDownloads)
        },
        onEqualizerPress = {
            // TODO navigate to equalizer screen
        },
        onDownloadAfterPlayChange = {
            settingsViewModel.onEvent(SettingsEvent.OnDownloadAfterPlayChange(it))
        },
        onDownloadFavouriteAfterPlayChange = {
            settingsViewModel.onEvent(SettingsEvent.OnDownloadFavouriteAfterPlayChange(it))
        },
        onMonoValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnMonoValueChange(it))
        },
        onNormalizeValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnNormalizeValueChange(it))
        },
        onSmartDownloadValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnSmartDownloadValueChange(it))
        },
        onOfflineModeValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnOfflineToggle)
        },
        onSdCardDownloadValueChange = {
            settingsViewModel.onEvent(SettingsEvent.OnDownloadsSdCardValueChange(it))
        },
        onBufferForPlaybackChange = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnBufferForPlaybackChange(it))
        },
        onMaxBufferChange = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnMaxBufferMsChange(it))
        },
        onMinBufferChange = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnMinBufferChange(it))
        },
        onBackBufferChange = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnBackBufferChange(it))
        },
        onBufferForPlaybackAfterRebufferChange = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnBufferForPlaybackAfterRebufferChange(it))
        },
        onCacheChange = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnPlayerCacheSizeChange(it))
        },
        donateButton = {
            SettingsDonationButtonView() {
                settingsViewModel.onEvent(SettingsEvent.GoToWebsite)
            }
        },
        onResetValuesClick = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnResetDefaults)
        },
        onKillAppClick = {
            showKillDialog = true
        },
        onUseOkHttpExoPlayer = {
            settingsViewModel.onPlayerEvent(PlayerSettingsEvent.OnUseOkHttpExoPlayer(newValue = it))
        },
        backBuffer = playerSettingsState.backBuffer,
        minBuffer = playerSettingsState.minBuffer,
        maxBuffer = playerSettingsState.maxBuffer,
        bufferForPlayback = playerSettingsState.bufferForPlayback,
        bufferForPlaybackAfterRebuffer = playerSettingsState.bufferForPlaybackAfterRebuffer,
        cache = playerSettingsState.cacheSizeMb,
        isUseOkHttpPlayer = playerSettingsState.useOkHttpExoplayer
    )
}

@Composable
fun SettingsDonationButtonView(onClick: () -> Unit) {
    if (BuildConfig.HIDE_DONATION) {
        val showDonationBtn = BuildConfig.HIDE_DONATION.not()
        DonateButton(
            isExpanded = true,
            isTransparent = false,
            showPaypal = showDonationBtn,
            showBTC = showDonationBtn,
            showBmac = true
        )
        DonateConsider(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            showLink = true
        )
    } else {
        DonateButton(isExpanded = true, isTransparent = false)
    }
}

@Composable
@Destination
fun SettingsScreenContent(
    userState: User?,
    serverInfo: ServerInfo?,
    versionInfo: String,
    remoteLoggingEnabled: Boolean,
    hideDonationButtons: Boolean,
    isNormalizeVolumeEnabled: Boolean,
    isMonoAudioEnabled: Boolean,
    isSmartDownloadsEnabled: Boolean,
    isAutoCheckUpdatesEnabled: Boolean,
    isOfflineModeEnabled: Boolean,
    isDownloadAfterPlayEnabled: Boolean,
    isDownloadFavouriteAfterPlayEnabled: Boolean,
    isDownloadSdCard: Boolean,
    powerAmpTheme: PowerAmpTheme,
    streamingQuality: StreamingQuality,
    backBuffer: Int,
    minBuffer: Int,
    maxBuffer: Int,
    cache: Int,
    bufferForPlayback: Int,
    bufferForPlaybackAfterRebuffer: Int,
    isUseOkHttpPlayer: Boolean,
    onThemeSelected: (selected: PowerAmpTheme) -> Unit,
    onStreamingQualitySelected: (selected: StreamingQuality) -> Unit,
    onEnableLoggingValueChange: (newValue: Boolean) -> Unit,
    onHideDonateValueChange: (newValue: Boolean) -> Unit,
    onNormalizeValueChange: (newValue: Boolean) -> Unit,
    onMonoValueChange: (newValue: Boolean) -> Unit,
    onDownloadAfterPlayChange: (newValue: Boolean) -> Unit,
    onDownloadFavouriteAfterPlayChange: (newValue: Boolean) -> Unit,
    onSmartDownloadValueChange: (newValue: Boolean) -> Unit,
    onAutoCheckUpdatesValueChange: (newValue: Boolean) -> Unit,
    onOfflineModeValueChange: (newValue: Boolean) -> Unit,
    onSdCardDownloadValueChange: (newValue: Boolean) -> Unit,
    onEqualizerPress: () -> Unit,
    onCheckUpdatesNowPress: () -> Unit,
    onDeleteDownloadsPress: () -> Unit,
    onDebugLogsButtonPress: () -> Unit,
    onAmpachePreferencesButtonPress: () -> Unit,
    onBackBufferChange: (newValue: Int) -> Unit,
    onMinBufferChange: (newValue: Int) -> Unit,
    onMaxBufferChange: (newValue: Int) -> Unit,
    onCacheChange: (newValue: Int) -> Unit,
    onBufferForPlaybackChange: (newValue: Int) -> Unit,
    onBufferForPlaybackAfterRebufferChange: (newValue: Int) -> Unit,
    onUseOkHttpExoPlayer: (newValue: Boolean) -> Unit,
    onResetValuesClick: () -> Unit,
    onKillAppClick: () -> Unit,
    modifier: Modifier = Modifier,
    donateButton: @Composable () -> Unit = { DonateButton(isExpanded = true, isTransparent = false) }
) {
    val paddingHorizontalItem = dimensionResource(id = R.dimen.settings_padding_horizontal_item)
    val paddingVerticalItem = dimensionResource(id = R.dimen.settings_padding_vertical_item)

    var showDeleteDownloadsDialog by remember { mutableStateOf(false) }
    if (showDeleteDownloadsDialog){
        EraseConfirmDialog(
            onDismissRequest = {
                showDeleteDownloadsDialog = false
            },
            onConfirmation = {
                showDeleteDownloadsDialog = false
                onDeleteDownloadsPress()
            },
            dialogTitle = R.string.settings_deleteDownloads_title,
            dialogText = R.string.settings_deleteDownloads_subtitle
        )
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.settings_padding_horizontal))
            .padding(top = dimensionResource(id = R.dimen.settings_padding_top))
    ) {
        items(25) { index ->
            when(index) {
                0 -> SettingsHeader()
                // THEME PICKER
                1 -> SettingsDropDownMenu(
                    modifier = Modifier.padding(vertical = paddingVerticalItem),
                    label = stringResource(id = R.string.settings_theme_title),
                    currentlySelected = powerAmpTheme.toPowerAmpacheDropdownItem(),
                    items = themesDropDownItems,
                    onItemSelected = onThemeSelected
                )
                // STREAM QUALITY PICKER
                2 -> SettingsDropDownMenu(
                    modifier = Modifier.padding(vertical = paddingVerticalItem),
                    label = stringResource(id = R.string.settings_quality_title), // "Streaming Quality"
                    currentlySelected = streamingQuality.toPowerAmpacheDropdownItem(),
                    items = streamQualityDropdownItems,
                    onItemSelected = onStreamingQualitySelected
                )
                // OFFLINE MODE SWITCH
                3 -> PowerAmpSwitch(
                    enabled = IS_OFFLINE_MODE_SWITCH_ENABLED,
                    title = R.string.settings_offlineMode_title,
                    subtitle = R.string.settings_offlineMode_subtitle,
                    checked = isOfflineModeEnabled,
                    onCheckedChange = onOfflineModeValueChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                // DOWNLOADS ON SD CARD VS INTERNAL STORAGE
                4 -> PowerAmpSwitch(
                    enabled = Constants.config.isDownloadsSdCardOptionEnabled,
                    title = R.string.settings_downloadsSdCard_title,
                    subtitle = R.string.settings_downloadsSdCard_subtitle,
                    checked = isDownloadSdCard,
                    onCheckedChange = onSdCardDownloadValueChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                5 -> PowerAmpSwitch(
                    enabled = true,
                    title = R.string.settings_downloadAfterPlay_title,
                    subtitle = R.string.settings_downloadAfterPlay_subtitle,
                    checked = isDownloadAfterPlayEnabled,
                    onCheckedChange = onDownloadAfterPlayChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                6 -> PowerAmpSwitch(
                    enabled = isDownloadAfterPlayEnabled,
                    title = R.string.settings_downloadAfterPlay_onlyFavourites_title,
                    checked = isDownloadFavouriteAfterPlayEnabled,
                    onCheckedChange = onDownloadFavouriteAfterPlayChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                // DELETE ALL DOWNLOADS
                7 -> TextWithSubtitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = paddingVerticalItem * 2,
                            horizontal = paddingHorizontalItem
                        ),
                    title = R.string.settings_deleteDownloads_title,
                    trailingIcon = Icons.Outlined.DeleteOutline,
                    onClick = {
                        showDeleteDownloadsDialog = true
                    }
                )
                8 -> PlayerSettingsView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = paddingVerticalItem,
                            horizontal = paddingHorizontalItem
                        ),
                    onBackBufferChange = onBackBufferChange,
                    onMinBufferChange = onMinBufferChange,
                    onMaxBufferChange = onMaxBufferChange,
                    onBufferForPlaybackChange = onBufferForPlaybackChange,
                    onBufferForPlaybackAfterRebufferChange = onBufferForPlaybackAfterRebufferChange,
                    backBuffer = backBuffer,
                    minBuffer = minBuffer,
                    maxBuffer = maxBuffer,
                    cache = cache,
                    bufferForPlayback = bufferForPlayback,
                    bufferForPlaybackAfterRebuffer = bufferForPlaybackAfterRebuffer,
                    onResetValuesClick = onResetValuesClick,
                    onKillAppClick = onKillAppClick,
                    onCacheChange = onCacheChange,
                    onUseOkHttpPlayer = onUseOkHttpExoPlayer,
                    isUseOkHttpPlayer = isUseOkHttpPlayer
                )
                // NORMALIZE VOLUME SWITCH
                9 -> PowerAmpSwitch(
                    enabled = IS_NORMALIZE_SWITCH_ENABLED,
                    title = R.string.settings_normalizeVolume_title,
                    subtitle = R.string.settings_normalizeVolume_subtitle,
                    checked = isNormalizeVolumeEnabled,
                    onCheckedChange = onNormalizeValueChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                // MONO SWITCH
                10 -> PowerAmpSwitch(
                    enabled = IS_MONO_SWITCH_ENABLED,
                    title = R.string.settings_monoAudio_title,
                    subtitle = R.string.settings_monoAudio_subtitle,
                    checked = isMonoAudioEnabled,
                    onCheckedChange = onMonoValueChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                11 -> if (IS_AMPACHE_DATA) {
                    AmpacheSettingsListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem),
                        onClick = onAmpachePreferencesButtonPress
                    )
                }
                12 -> PowerAmpSwitch(
                    title = R.string.settings_enableDebugLogging_title,
                    subtitle = R.string.settings_enableDebugLogging_subtitle,
                    checked = remoteLoggingEnabled,
                    onCheckedChange = onEnableLoggingValueChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                13 -> TextWithSubtitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem),
                    title = R.string.settings_debugLogs_title,
                    trailingIcon = Icons.Outlined.OpenInNew,
                    onClick = onDebugLogsButtonPress
                )

                14 -> donateButton()
                15 -> if (!BuildConfig.HIDE_DONATION) {
                    PowerAmpCheckBox(title = R.string.settings_hideDonationButtonsMenu_title,
                        checked = hideDonationButtons,
                        onCheckedChange = onHideDonateValueChange,
                        modifier = Modifier.padding(start = paddingHorizontalItem))
                }

                // Anything above MAX_VISIBLE_ITEMS is not visible
                27 -> Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    text = versionInfo,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 16.sp
                )
                28 -> Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    text = BuildConfig.VERSION_QUOTE,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 16.sp
                )
                // CHECK UPDATES NOW BUTTON
                30 -> TextWithSubtitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = paddingHorizontalItem)
                        .padding(top = paddingVerticalItem * 2, bottom = paddingVerticalItem),
                    title = R.string.settings_checkUpdatesNow_title,
                    subtitle = R.string.coming_soon,
                    onClick = onCheckUpdatesNowPress
                )
                // AUTO UPDATE CHECKBOX
                31 -> PowerAmpCheckBox(
                    enabled = IS_AUTO_UPDATE_ENABLED,
                    modifier = Modifier
                        .padding(vertical = paddingVerticalItem)
                        .padding(start = paddingHorizontalItem),
                    checked = isAutoCheckUpdatesEnabled,
                    onCheckedChange = onAutoCheckUpdatesValueChange,
                    title = R.string.settings_autoCheckUpdates_title,
                    subtitle = R.string.coming_soon,
                )
                // SMART DOWNLOADS SWITCH
                32 -> PowerAmpSwitch(
                    enabled = IS_SMART_DOWNLOADS_SWITCH_ENABLED,
                    title = R.string.settings_smartDownloads_title,
                    subtitle = R.string.settings_smartDownloads_subtitle,
                    checked = isSmartDownloadsEnabled,
                    onCheckedChange = onSmartDownloadValueChange,
                    modifier = Modifier.padding(vertical = paddingVerticalItem, horizontal = paddingHorizontalItem)
                )
                // EQUALIZER BUTTON
                33 -> TextWithSubtitle(
                    enabled = IS_EQUALIZER_BTN_ENABLED,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = paddingHorizontalItem)
                        .padding(top = paddingVerticalItem * 2, bottom = paddingVerticalItem),
                    title = R.string.settings_equalizer,
                    subtitle = R.string.coming_soon,
                    trailingIcon = Icons.Default.KeyboardArrowRight,
                    onClick = onEqualizerPress
                )
                else -> { }
            }
        }
    }
}

@Composable
fun SettingsHeader() { }

@Composable
fun AmpacheSettingsListItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    trailingIconContentDescription: String? = null,
    onClick: () -> Unit = { }
) {
    Row(
        modifier = modifier
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.settings_ampachePreferences_title),
                fontSize = 14.sp,
                lineHeight = 14.sp
            )
            Text(
                text = stringResource(R.string.settings_ampachePreferences_subtitle),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Light
            )
        }
        Image(
            painterResource(R.drawable.ampache_flat_mono),
            contentDescription = trailingIconContentDescription,
            modifier = Modifier.size(42.dp)
        )
    }
}

@Composable
fun ThemesRadioGroup(
    mItems: List<PowerAmpTheme>,
    selected: PowerAmpTheme,
    setSelected: (selected: PowerAmpTheme) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            mItems.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        enabled = item.isThemeAvailable(),
                        selected = selected == item,
                        onClick = {
                            setSelected(item)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )
                    Text(
                        text = stringResource(id = item.getTitleRes()),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WorkInProgressStrip() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.errorContainer)) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = "WORK IN PROGRESS. \nMore Settings and Themes coming soon",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 16.sp
        )
    }
}

@Composable
@Deprecated("use dropdown menu")
fun SettingsThemeSelector(
    currentTheme: PowerAmpTheme,
    onThemeSelected: (selected: PowerAmpTheme) -> Unit
) {
    // all the theme options in the defined order
    val themes = listOf(
        PowerAmpTheme.MATERIAL_YOU_SYSTEM,
        PowerAmpTheme.MATERIAL_YOU_DARK,
        PowerAmpTheme.MATERIAL_YOU_LIGHT,
        PowerAmpTheme.SYSTEM,
        PowerAmpTheme.DARK,
        PowerAmpTheme.LIGHT
    )

    val (selected, setSelected) = remember { mutableStateOf(currentTheme) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Theme Selector\n(${stringResource(id = selected.getTitleRes())})",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                minLines = 2,
                maxLines = 2,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 26.dp, vertical = 10.dp),
            )

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                ThemesRadioGroup(
                    mItems = themes,
                    selected = selected,
                    setSelected = {
                        setSelected(it)
                        onThemeSelected(it)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
            .background(MaterialTheme.colorScheme.background),
        userState = User.mockUser(),
        serverInfo = ServerInfo("some server", "6.78"),
        powerAmpTheme = PowerAmpTheme.DARK,
        streamingQuality = StreamingQuality.HIGH,
        versionInfo = "0.11-beta (11)",
        hideDonationButtons = false,
        remoteLoggingEnabled = false,
        isNormalizeVolumeEnabled = false,
        isMonoAudioEnabled = false,
        isSmartDownloadsEnabled = false,
        isAutoCheckUpdatesEnabled = false,
        isOfflineModeEnabled = true,
        isDownloadSdCard = true,
        onHideDonateValueChange = { },
        onThemeSelected = { },
        onStreamingQualitySelected = { },
        onEnableLoggingValueChange = { },
        donateButton = { DonateButtonContent(
            isExpanded = true,
            isTransparent = false,
            onDonateBmacButtonClick = {},
            onDonateBtcButtonClick =  {},
            onDonatePaypalButtonClick =  {}
        )},
        onAutoCheckUpdatesValueChange = { },
        onCheckUpdatesNowPress = { },
        onDebugLogsButtonPress = { },
        onDeleteDownloadsPress = { },
        onAmpachePreferencesButtonPress = { },
        onEqualizerPress = {},
        onMonoValueChange = {},
        onNormalizeValueChange = {},
        onSmartDownloadValueChange = {},
        onOfflineModeValueChange = {},
        onSdCardDownloadValueChange = {},
        onBufferForPlaybackChange = {},
        onMinBufferChange = {},
        onBackBufferChange = {},
        onBufferForPlaybackAfterRebufferChange = {},
        backBuffer = 34,
        minBuffer = 22,
        maxBuffer = 55,
        isDownloadAfterPlayEnabled = true,
        onDownloadAfterPlayChange = {},
        bufferForPlayback = 129,
        bufferForPlaybackAfterRebuffer = 400,
        onMaxBufferChange = {},
        onResetValuesClick = {},
        onCacheChange = {},
        cache = 100,
        isDownloadFavouriteAfterPlayEnabled = true,
        onDownloadFavouriteAfterPlayChange = {},
        onKillAppClick = {}, isUseOkHttpPlayer = true, onUseOkHttpExoPlayer = {}
    )
}
