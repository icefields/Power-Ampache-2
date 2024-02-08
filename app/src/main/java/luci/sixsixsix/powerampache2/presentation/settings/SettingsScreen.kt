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
package luci.sixsixsix.powerampache2.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
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
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.common.DonateButton
import luci.sixsixsix.powerampache2.presentation.common.DonateButtonContent
import luci.sixsixsix.powerampache2.presentation.common.DonateButtonPreview
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpCheckBox
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpSwitch

@Composable
@Destination
fun SettingsScreen(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 10.dp)
) {
    SettingsScreenContent(
        userState = settingsViewModel.state.user,
        serverInfo = settingsViewModel.state.serverInfo,
        versionInfo = settingsViewModel.state.appVersionInfoStr,
        powerAmpTheme = settingsViewModel.state.localSettings.theme,
        remoteLoggingEnabled = settingsViewModel.state.localSettings.enableRemoteLogging,
        hideDonationButtons = settingsViewModel.state.localSettings.hideDonationButton,
        onThemeSelected = {
            settingsViewModel.onEvent(SettingsEvent.OnThemeChange(it))
        },
        onEnableLoggingChange = {
            settingsViewModel.onEvent(SettingsEvent.OnEnableRemoteLoggingSwitch(it))
        },
        onHideDonateButtonChange = {
            settingsViewModel.onEvent(SettingsEvent.OnHideDonationButtonSwitch(it))
        }
    )
}

@Composable
@Destination
fun SettingsScreenContent(
    userState: User?,
    serverInfo: ServerInfo?,
    versionInfo: String,
    remoteLoggingEnabled: Boolean = false,
    hideDonationButtons: Boolean = false,
    powerAmpTheme: PowerAmpTheme,
    onThemeSelected: (selected: PowerAmpTheme) -> Unit,
    onEnableLoggingChange: (newValue: Boolean) -> Unit,
    onHideDonateButtonChange: (newValue: Boolean) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 10.dp),
    donateButton: @Composable () -> Unit = { DonateButton(isExpanded = true, isTransparent = false) }
) {
    LazyColumn(modifier = modifier) {
        items(10) { index ->
            when(index) {
                0 -> SettingsHeader()
                1 -> userState?.let { user -> UserInfoSection(user = user) }
                2 -> SettingsThemeSelector(currentTheme = powerAmpTheme) {
                    onThemeSelected(it)
                }
                5 -> donateButton()
                6 -> serverInfo?.let {ServerInfoSection(it) }
                3 -> PowerAmpSwitch(
                    text = "Enable Anonymous Debug Logging",
                    checked = remoteLoggingEnabled,
                    onCheckedChange = onEnableLoggingChange,
                    modifier = modifier
                )
                4 -> PowerAmpCheckBox(
                    text = "Hide Donation Buttons on Menu",
                    checked = hideDonationButtons,
                    onCheckedChange = onHideDonateButtonChange,
                    modifier = modifier
                )
                7 -> Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    text = versionInfo,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 16.sp
                )
                9 -> WorkInProgressStrip()
                8 -> Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.version_quote),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 16.sp
                )
                else -> {}
            }
        }
    }
}

@Composable
fun SettingsHeader() { }

@Composable
fun UserInfoSection(user: User) {
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
            Column(modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 9.dp)) {

                // if using a demo server show static demo data
                // TODO use server to determine if we're in demo mode, right now I'm using email,
                //  which should be cryptic enough
                if (user.email != BuildConfig.DOGMAZIC_EMAIL) {
                    UserInfoText("Username", user.username)
                    //if (user.fullNamePublic == 1)
                    UserInfoText("Full Name", user.fullName)
                    UserInfoText("Email", user.email)
                    UserInfoText("Website", user.website)
                    UserInfoText("City", user.city)
                    UserInfoText("State", user.state)
                    UserInfoText("ID", user.id)
                    //UserInfoText("Access", user.access?.toString())
                    //UserInfoText("ID", LocalDateTime.parse(user.createDate).toString())
                } else {
                    UserInfoText("Username", "Power Ampache 2 Demo")
                    //if (user.fullNamePublic == 1)
                    UserInfoText("Full Name", "Power Ampache")
                    UserInfoText("Email", "powerampache.ducking336@silomails.com")
                    UserInfoText("Website", "github.com/icefields/power-ampache-2")
                    UserInfoText("City", "Pluto City")
                    UserInfoText("State", "Solar System")
                    UserInfoText("ID", "PowerAmp666")
                }
            }
        }
    }
}

@Composable
fun ServerInfoSection(serverInfo: ServerInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 9.dp)) {
            UserInfoText("Server", serverInfo.server)
            UserInfoText("Version", serverInfo.version)
            UserInfoText("Compatible", serverInfo.compatible)
        }
    }
}

@Composable
fun UserInfoText(title: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 26.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier
                .width(5.dp)
                .height(1.dp))
            Text(
                text = value,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
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
                text = "Theme Selector\n(${selected.title})",
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
                        enabled = item.isEnabled,
                        selected = selected == item,
                        onClick = {
                            setSelected(item)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )
                    Text(text = item.title, modifier = Modifier.padding(horizontal = 8.dp))
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
        versionInfo = "0.11-beta (11)",
        hideDonationButtons = false,
        onHideDonateButtonChange = {},
        onThemeSelected = {
        },
        onEnableLoggingChange = {},
        donateButton = { DonateButtonContent(isExpanded = true, isTransparent = false) }
    )
}
