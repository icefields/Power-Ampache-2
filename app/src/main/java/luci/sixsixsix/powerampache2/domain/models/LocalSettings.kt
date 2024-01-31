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
package luci.sixsixsix.powerampache2.domain.models

import android.os.Build
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * interfaces local settings and remote settings from server
 */
@Parcelize
data class LocalSettings(
    val username: String,
    val theme: PowerAmpTheme
): Parcelable {
    companion object {
        fun defaultSettings(username: String? = null) =
            LocalSettings(username ?: "luci.sixsixsix.powerampache2.user.db.pa_default_user",
                PowerAmpTheme.MATERIAL_YOU_DARK
            )
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is LocalSettings) return false
        return "${username}${theme}" == "${other.username}${other.theme}"
    }
}

private val defaultThemeChosen = PowerAmpTheme.DARK
/**
 * available themes
 */
@Parcelize
sealed class PowerAmpTheme(val themeId: String, val title: String, val isEnabled: Boolean):Parcelable {
    companion object {
        fun getThemeFromId(themeId: String): PowerAmpTheme =
            when(themeId) {
                "SYSTEM" -> SYSTEM
                "DARK" -> DARK
                "LIGHT" -> LIGHT
                "MATERIAL_YOU_SYSTEM" -> MATERIAL_YOU_SYSTEM
                "MATERIAL_YOU_DARK" -> MATERIAL_YOU_DARK
                "MATERIAL_YOU_LIGHT" -> MATERIAL_YOU_LIGHT
                else -> defaultThemeChosen
            }
    }

    @Parcelize data object SYSTEM:
        PowerAmpTheme("SYSTEM", "System Theme", true)
    @Parcelize data object DARK:
        PowerAmpTheme("DARK", "Dark", true)
    @Parcelize data object LIGHT:
        PowerAmpTheme("LIGHT", "Light", true)
    @Parcelize data object MATERIAL_YOU_SYSTEM:
        PowerAmpTheme("MATERIAL_YOU_SYSTEM", "MaterialYou System", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    @Parcelize data object MATERIAL_YOU_DARK:
        PowerAmpTheme("MATERIAL_YOU_DARK", "MaterialYou Dark", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    @Parcelize data object MATERIAL_YOU_LIGHT:
        PowerAmpTheme("MATERIAL_YOU_LIGHT", "MaterialYou Light", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}
