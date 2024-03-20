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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LikeButton(
    isLikeLoading: Boolean,
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    background: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    borderStroke: Dp = 1.dp,
    onClick: () -> Unit
) = ButtonWithLoadingIndicator(
    imageVector = if (isFavourite)
        Icons.Default.Favorite else
        Icons.Default.FavoriteBorder,
    imageContentDescription = "favourite",
    isLoading = isLikeLoading,
    modifier = modifier,
    background = background,
    iconTint = iconTint,
    borderStroke = borderStroke,
    onClick = onClick
)

@Composable
fun LikeButton(
    isLikeLoading: Boolean,
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    onClick: () -> Unit
) = LikeButton(
    modifier = modifier,
    isLikeLoading = isLikeLoading,
    isFavourite = isFavourite,
    iconTint = MaterialTheme.colorScheme.primary,
    background = Color.Transparent,
    borderStroke = 0.dp,
    onClick = onClick
)


@Composable @Preview
fun PreviewLikeButton() {
    LikeButton(isLikeLoading = false, isFavourite = false, background = Color.Transparent) { }
}
