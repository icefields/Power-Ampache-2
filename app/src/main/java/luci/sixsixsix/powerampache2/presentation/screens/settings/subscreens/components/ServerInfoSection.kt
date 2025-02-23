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
package luci.sixsixsix.powerampache2.presentation.screens.settings.subscreens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.presentation.common.TextWithOverline

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
                    subtitleTextSize = fontDimensionResource(id = R.dimen.about_item_fontSize)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewServerInfo() {
    ServerInfo("some server", "6.78", compatible = "350000")
}
