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
package luci.sixsixsix.powerampache2.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import luci.sixsixsix.powerampache2.ui.R

val primaryDark @Composable get() = colorResource(R.color.primaryDark)
val onPrimaryDark @Composable get() = colorResource(R.color.onPrimaryDark)
val surfaceDark @Composable get() = colorResource(R.color.surfaceDark)
val onBackgroundDark @Composable get() = colorResource(R.color.onBackgroundDark)
val surfaceContainerDark @Composable get() = colorResource(R.color.surfaceContainerDark)
val onSurfaceVariantDark @Composable get() = colorResource(R.color.onSurfaceVariantDark)
val errorDark @Composable get() = colorResource(R.color.errorDark)

internal val surfaceContainerHighDark @Composable get() = colorResource(R.color.surfaceContainerHighDark)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)