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
package luci.sixsixsix.powerampache2.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import luci.sixsixsix.powerampache2.ui.R

object AppFont {
    val LatoFont = FontFamily(
        Font(R.font.nunito_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(R.font.nunito_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(R.font.nunito_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
        Font(R.font.nunito_light, FontWeight.Light),
        Font(R.font.nunito_black, FontWeight.Black),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_condensed_italic, FontWeight.Normal, style = FontStyle.Italic)
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.LatoFont),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.LatoFont),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.LatoFont),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.LatoFont),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.LatoFont),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.LatoFont),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.LatoFont),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.LatoFont),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.LatoFont),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.LatoFont),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.LatoFont),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.LatoFont),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.LatoFont),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.LatoFont),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.LatoFont)
)

// Set of Material typography styles to start with
/* val Typography = Typography(
bodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)
Other default text styles to override
titleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
),
labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)
*/