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
package luci.sixsixsix.powerampache2.ui

import android.os.Build
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.settings.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.settings.StreamingQuality
import luci.sixsixsix.powerampache2.presentation.screens.settings.components.PowerAmpacheDropdownItem

val streamQualityDropdownItems = listOf(
    StreamingQuality.VERY_HIGH.toPowerAmpacheDropdownItem(),
    StreamingQuality.HIGH.toPowerAmpacheDropdownItem(),
    StreamingQuality.MEDIUM.toPowerAmpacheDropdownItem(),
    StreamingQuality.MEDIUM_LOW.toPowerAmpacheDropdownItem(),
    StreamingQuality.LOW.toPowerAmpacheDropdownItem(),
)


val themesDropDownItems = listOf(
    PowerAmpTheme.MATERIAL_YOU_SYSTEM.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.MATERIAL_YOU_DARK.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.MATERIAL_YOU_LIGHT.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.SYSTEM.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.DARK.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.LIGHT.toPowerAmpacheDropdownItem(),
)

fun PowerAmpTheme.toPowerAmpacheDropdownItem() =
    PowerAmpacheDropdownItem(title = getTitleRes(), value = this, isEnabled = isThemeAvailable())

fun StreamingQuality.toPowerAmpacheDropdownItem() =
    PowerAmpacheDropdownItem(title = getTitleRes(), subtitle = getDescriptionRes(), value = this)

fun PowerAmpTheme.getTitleRes() = when(this) {
    PowerAmpTheme.DARK -> R.string.theme_dark_title
    PowerAmpTheme.LIGHT -> R.string.theme_light_title
    PowerAmpTheme.MATERIAL_YOU_DARK -> R.string.theme_dark_materialYou_title
    PowerAmpTheme.MATERIAL_YOU_LIGHT -> R.string.theme_light_materialYou_title
    PowerAmpTheme.MATERIAL_YOU_SYSTEM -> R.string.theme_system_materialYou_title
    PowerAmpTheme.SYSTEM -> R.string.theme_system_title
}

fun PowerAmpTheme.isThemeAvailable() = when(this) {
    PowerAmpTheme.DARK -> true
    PowerAmpTheme.LIGHT -> true
    PowerAmpTheme.MATERIAL_YOU_DARK -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    PowerAmpTheme.MATERIAL_YOU_LIGHT -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    PowerAmpTheme.MATERIAL_YOU_SYSTEM -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    PowerAmpTheme.SYSTEM -> true
}

fun StreamingQuality.getTitleRes() = when(this) {
    StreamingQuality.VERY_HIGH -> R.string.quality_veryHigh_title
    StreamingQuality.HIGH -> R.string.quality_high_title
    StreamingQuality.MEDIUM -> R.string.quality_medium_title
    StreamingQuality.MEDIUM_LOW ->  R.string.quality_mediumLow_title
    StreamingQuality.LOW -> R.string.quality_low_title
}

fun StreamingQuality.getDescriptionRes() = when(this) {
    StreamingQuality.VERY_HIGH -> R.string.quality_veryHigh_subtitle
    StreamingQuality.HIGH -> R.string.quality_high_subtitle
    StreamingQuality.MEDIUM -> R.string.quality_medium_subtitle
    StreamingQuality.MEDIUM_LOW ->  R.string.quality_mediumLow_subtitle
    StreamingQuality.LOW -> R.string.quality_low_subtitle
}
