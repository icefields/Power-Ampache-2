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
package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R

@Composable
fun DownloadProgressView(onStopDownload: () -> Unit) {
    Card(
        modifier = Modifier
        .fillMaxWidth(),
        shape = RoundedCornerShape(1.dp),
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.albumDetail_chip_elevation))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .padding(top = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Download in progress",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily.SansSerif)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))

                LinearProgressIndicator(
                    modifier = Modifier.padding(horizontal = 10.dp),
                )
            }

            Button(
                shape = RoundedCornerShape(1.dp),
                onClick = {
                    onStopDownload()
                }
            ) {
                Icon(Icons.Outlined.FileDownloadOff, contentDescription = "stop download progress")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = "Cancel")
            }
        }


    }

}

@Preview
@Composable
fun PreviewDownloadProgressView() {
    DownloadProgressView(){}
}
