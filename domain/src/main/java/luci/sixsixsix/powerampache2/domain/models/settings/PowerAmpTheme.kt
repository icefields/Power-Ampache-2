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
package luci.sixsixsix.powerampache2.domain.models.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

private const val ID_SYSTEM = "SYSTEM"
const val ID_DARK = "DARK"
private const val ID_LIGHT = "LIGHT"
private const val ID_MATERIAL_YOU_SYSTEM = "MATERIAL_YOU_SYSTEM"
private const val ID_MATERIAL_YOU_DARK = "MATERIAL_YOU_DARK"
private const val ID_MATERIAL_YOU_LIGHT = "MATERIAL_YOU_LIGHT"
private const val SETTINGS_DEFAULTS_THEME = ID_DARK

val defaultTheme = PowerAmpTheme.getThemeFromId(SETTINGS_DEFAULTS_THEME)

/**
 * available themes
 */
@Parcelize
sealed class PowerAmpTheme(val themeId: String): Parcelable {
    @Parcelize
    data object SYSTEM:
        PowerAmpTheme(ID_SYSTEM)
    @Parcelize
    data object DARK:
        PowerAmpTheme(ID_DARK)
    @Parcelize
    data object LIGHT:
        PowerAmpTheme(ID_LIGHT)
    @Parcelize
    data object MATERIAL_YOU_SYSTEM:
        PowerAmpTheme(ID_MATERIAL_YOU_SYSTEM)
    @Parcelize
    data object MATERIAL_YOU_DARK:
        PowerAmpTheme(ID_MATERIAL_YOU_DARK)
    @Parcelize
    data object MATERIAL_YOU_LIGHT:
        PowerAmpTheme(ID_MATERIAL_YOU_LIGHT)

    override fun equals(other: Any?) = try { (other as PowerAmpTheme).themeId == themeId } catch (e: Exception) { false }
    override fun hashCode() = themeId.hashCode()
    override fun toString() = themeId

    companion object {
        fun getThemeFromId(themeId: String): PowerAmpTheme =
            when(themeId) {
                ID_SYSTEM -> SYSTEM
                ID_DARK -> DARK
                ID_LIGHT -> LIGHT
                ID_MATERIAL_YOU_SYSTEM -> MATERIAL_YOU_SYSTEM
                ID_MATERIAL_YOU_DARK -> MATERIAL_YOU_DARK
                ID_MATERIAL_YOU_LIGHT -> MATERIAL_YOU_LIGHT
                else -> defaultTheme
            }
    }
}
