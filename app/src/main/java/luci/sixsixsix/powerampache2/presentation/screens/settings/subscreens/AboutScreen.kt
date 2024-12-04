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
package luci.sixsixsix.powerampache2.presentation.screens.settings.subscreens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.GITHUB_URL
import luci.sixsixsix.powerampache2.common.Constants.GPLV3_IMG_URL
import luci.sixsixsix.powerampache2.common.Constants.GPLV3_URL
import luci.sixsixsix.powerampache2.common.Constants.MASTODON_IMG_URL
import luci.sixsixsix.powerampache2.common.Constants.MASTODON_URL
import luci.sixsixsix.powerampache2.common.Constants.TELEGRAM_IMG_URL
import luci.sixsixsix.powerampache2.common.Constants.TELEGRAM_URL
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.common.openLinkInBrowser
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.common.DonateButton
import luci.sixsixsix.powerampache2.presentation.common.DonateButtonContent
import luci.sixsixsix.powerampache2.presentation.common.DonateConsider
import luci.sixsixsix.powerampache2.presentation.common.TextWithOverline
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsEvent
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel

@Composable
@Destination
fun AboutScreen(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel
) {
    val user by settingsViewModel.userStateFlow.collectAsState()
    val serverInfo by settingsViewModel.serverInfoStateFlow.collectAsState(ServerInfo())
    AboutScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
        userState = user,
        serverInfo = serverInfo,
        versionInfo = settingsViewModel.state.appVersionInfoStr,
        onDonateConsiderClick = {
            settingsViewModel.onEvent(SettingsEvent.GoToWebsite)
        }
    )
}

@Composable
@Destination
fun AboutScreenContent(
    userState: User?,
    serverInfo: ServerInfo?,
    versionInfo: String,
    onDonateConsiderClick: () -> Unit,
    modifier: Modifier = Modifier,
    donateButton: @Composable () -> Unit = { DonateButton(isExpanded = true, isTransparent = false) }
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.settings_padding_horizontal))
            .padding(top = dimensionResource(id = R.dimen.settings_padding_top))
    ) {
        items(25) { index ->
            when(index) {
                0 -> AboutHeader()
                //12 -> donateButton()
                1 -> serverInfo?.let { ServerInfoSection(it) }
                2 -> TextWithOverline(
                    title = R.string.about_appVersion_title,
                    subtitle = versionInfo,
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
                )
                3 -> DividerSeparator()
                5 -> TextWithOverline(title = R.string.about_license_title, subtitle = "")
                6 -> AboutImageWithLink(
                    modifier = Modifier.height(76.dp),
                    imgUrl = GPLV3_IMG_URL,
                    pageUrl = GPLV3_URL,
                    contentDescription = stringResource(id = R.string.about_license_title)
                )
                7 -> DividerSeparator(modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
                8 -> TextWithOverline(title = R.string.about_contact_title, subtitle = "")
                9 -> Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AboutImageWithLink(TELEGRAM_IMG_URL, TELEGRAM_URL, "telegram",
                        modifier = Modifier
                            .width(66.dp)
                            .height(66.dp))
                    AboutImageWithLink(MASTODON_IMG_URL, MASTODON_URL, "mastodon",
                        modifier = Modifier
                            .width(66.dp)
                            .height(66.dp))
                }
                10 -> DividerSeparator(modifier = Modifier.padding(vertical = 6.dp))
                11 -> DonateConsider(onClick = onDonateConsiderClick)
                12 -> TextWithOverline(title = R.string.about_sourceCode_title, subtitle = "")
                13 -> AboutImageWithLink(
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.height(66.dp),
                    imgId = R.drawable.ic_github,
                    pageUrl = GITHUB_URL,
                    contentDescription = stringResource(id = R.string.about_sourceCode_title)
                )
                20 -> TextWithOverline(
                    title = R.string.version_quote_title,
                    subtitle = BuildConfig.VERSION_QUOTE,
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
                )
                21 -> TextWithOverline(
                    title = R.string.sharing_provider_authority,
                    subtitle = BuildConfig.FLAVOR,
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
                )
                //10 -> userState?.let { user -> UserInfoSection(user = user) }
                else -> { }
            }
        }
    }
}

@Composable
fun DividerSeparator(
    modifier: Modifier = Modifier.padding(vertical = 6.dp),
    useDivider: Boolean = false
) = if (useDivider) Divider(modifier = modifier) else Spacer(modifier = modifier)


@Composable
fun AboutTitleText(
    @StringRes title: Int,
) = Text(
    modifier = Modifier.padding(
        horizontal = dimensionResource(id = R.dimen.listItem_padding_horizontal),
        vertical = dimensionResource(id = R.dimen.listItem_padding_vertical)
    ),
    text = stringResource(id = title),
    fontSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
)

@Composable
fun AboutImageWithLink(
    imgUrl: String,
    pageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val applicationContext = LocalContext.current.applicationContext
    AsyncImage(
    modifier = modifier
        .padding(horizontal = 11.dp)
        .clickable { applicationContext.openLinkInBrowser(pageUrl) },
    contentScale = ContentScale.Fit,
    model = imgUrl,
    contentDescription = contentDescription
)}

@Composable
fun AboutImageWithLink(
    @DrawableRes imgId: Int,
    pageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    val applicationContext = LocalContext.current.applicationContext
    if (tint == null) {
        Image(
            painter = painterResource(id = imgId),
            modifier = modifier
                .padding(horizontal = 11.dp)
                .clickable { applicationContext.openLinkInBrowser(pageUrl) },
            contentScale = ContentScale.Fit,
            contentDescription = contentDescription
        )
    } else {
        Icon(
            tint = tint,
            painter = painterResource(id = imgId),
            modifier = modifier
                .padding(horizontal = 11.dp)
                .clickable { applicationContext.openLinkInBrowser(pageUrl) },
            contentDescription = contentDescription
        )
    }
}

@Composable
fun AboutHeader() { }

@Composable
fun ServerInfoSection(
    serverInfo: ServerInfo,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(modifier = Modifier
            .wrapContentHeight()) {
            serverInfo.server?.let {
                TextWithOverline(
                    title = R.string.settings_serverInfo_title,
                    subtitle = it,
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
                )
            }
            serverInfo.version?.let {
                TextWithOverline(
                    title = R.string.settings_serverInfo_version,
                    subtitle = it,
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
                )
            }
            serverInfo.compatible?.let {
                TextWithOverline(
                    title = R.string.settings_serverInfo_compatible,
                    subtitle = it,
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize))
            }
        }
    }
}

@Preview
@Composable
fun PreviewAboutScreen() {
    AboutScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
            .background(MaterialTheme.colorScheme.background),
        userState = User.mockUser(),
        serverInfo = ServerInfo("some server", "6.78"),
        versionInfo = "0.11-beta (11)",
        onDonateConsiderClick = {},
        donateButton = { DonateButtonContent(isExpanded = true, isTransparent = false) },
    )
}
