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

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF70CCCC),
    onPrimary = Color(0xFF122E2E),
    primaryContainer = Color(0xFF1F4C4C),
    onPrimaryContainer = Color(0xFF9DE0E0),
    inversePrimary = Color(0xFF1B6B6B),
    secondary = Color(0xFFB8C0CC),
    onSecondary = Color(0xFF2D2F33),
    secondaryContainer = Color(0xFF41444C),
    onSecondaryContainer = Color(0xFFCFD8E5),
    tertiary = Color(0xFFFABA82),
    onTertiary = Color(0xFF421900),
    tertiaryContainer = Color(0xFF6E2A00),
    onTertiaryContainer = Color(0xFFF7C9A1),
    background = Color(0xFF282E2C),
    onBackground = Color(0xFFDFE5E3),
    surface = Color(0xFF1B1F1D),
    onSurface = Color(0xFFDFE5E3),
    surfaceVariant = Color(0xFF4E5359),
    onSurfaceVariant = Color(0xFFCBCED1),
    surfaceTint = Color(0xFFF2A591),
    inverseSurface = Color(0xFFDFE5E3),
    inverseOnSurface = Color(0xFF333B38),
    error = Color(0xFFF593AB),
    onError = Color(0xFF520417),
    errorContainer = Color(0xFF800322),
    onErrorContainer = Color(0xFFFAB4C5),
    outline = Color(0xFFA5A9AD),
    outlineVariant = Color(0xFF4E5359),
    scrim = Color(0xFF0B0D0C)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1B6B6B),
    onPrimary = Color(0xFFF2FFFF),
    primaryContainer = Color(0xFF9DE0E0),
    onPrimaryContainer = Color(0xFF0D2121),
    inversePrimary = Color(0xFF70CCCC),
    secondary = Color(0xFF4F5661),
    onSecondary = Color(0xFFFFF8F7),
    secondaryContainer = Color(0xFFCFD8E5),
    onSecondaryContainer = Color(0xFF161719),
    tertiary = Color(0xFFA14300),
    onTertiary = Color(0xFFFAF3ED),
    tertiaryContainer = Color(0xFFF7C9A1),
    onTertiaryContainer = Color(0xFF260D00),
    background = Color(0xFFF2FAF7),
    onBackground = Color(0xFF1F2124),
    surface = Color(0xFFF2FAF7),
    onSurface = Color(0xFF1F2124),
    surfaceVariant = Color(0xFFDFE5E3),
    onSurfaceVariant = Color(0xFF4E5359),
    surfaceTint = Color(0xFF993412),
    inverseSurface = Color(0xFF333B38),
    inverseOnSurface = Color(0xFFEBF2F0),
    error = Color(0xFF990127),
    onError = Color(0xFFFFF5F9),
    errorContainer = Color(0xFFFAB4C5),
    onErrorContainer = Color(0xFF26020B),
    outline = Color(0xFF879099),
    outlineVariant = Color(0xFFCBCED1),
    scrim = Color(0xFF0B0D0C)
)

@Composable
fun PowerAmpache2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}