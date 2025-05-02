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
package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreference
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreferenceType

data class AmpachePreferenceDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String? = "",

    @SerializedName("level")
    val level: Int?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("value")
    val value: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("subcategory")
    val subcategory: String?,
)

data class AmpachePreferenceResponse(
    @SerializedName("preference") val preferences: List<AmpachePreferenceDto>?,
) : AmpacheBaseResponse()

fun AmpachePreferenceDto.toAmpachePreference() = AmpachePreference(
    id = id,
    name = name ?: Constants.ERROR_STRING,
    level = level ?: Constants.ERROR_INT,
    description = description ?: "",
    value = value ?: Constants.ERROR_INT.toString(),
    type = typeToAmpachePreferenceType(type),
    category = category ?: "",
    subcategory = subcategory
)

private fun typeToAmpachePreferenceType(type: String?) = type?.let {
    try {
        if (type == "bool") AmpachePreferenceType.BOOLEAN else AmpachePreferenceType.valueOf(type.uppercase())
    } catch (e: Exception) {
        AmpachePreferenceType.NONE
    }
} ?: AmpachePreferenceType.NONE
