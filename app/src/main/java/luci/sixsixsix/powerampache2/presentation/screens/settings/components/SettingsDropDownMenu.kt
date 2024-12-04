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
package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle

@Composable
fun <T> SettingsDropDownMenu(
    label: String,
    currentlySelected: PowerAmpacheDropdownItem<T>,
    items: List<PowerAmpacheDropdownItem<T>>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
){
    var isExpanded by remember { mutableStateOf(false) }
    var selectedTextId by remember { mutableIntStateOf(currentlySelected.title) }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (isExpanded) { Icons.Filled.KeyboardArrowUp } else { Icons.Filled.KeyboardArrowDown }

    Column(
        modifier.clickable { isExpanded = !isExpanded }
    ) {

        Box(
            contentAlignment = Alignment.CenterStart
        ) {

            OutlinedTextField(
                value = stringResource(id = selectedTextId),
                readOnly = true,
                singleLine = true,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    },
                label = {
                    Text(label,
                        modifier =
                        Modifier.clickable { isExpanded = !isExpanded }
                    )
                },
                trailingIcon = {
                    Icon(imageVector = icon,
                        contentDescription = "expand menu",
                        modifier = Modifier.clickable { isExpanded = !isExpanded } )
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent//MaterialTheme.colorScheme.onSurface
                ),
                shape = RectangleShape,
                onClick = { isExpanded = !isExpanded }) {
                Text(
                    text = stringResource(id = selectedTextId),
                    modifier = Modifier.fillMaxWidth().padding(top = 7.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    color = Color.Transparent
                )
            }
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .width(
                    with(LocalDensity.current) {
                        textFieldSize.width.toDp()
                    }
                )
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    enabled = item.isEnabled,
                    text = {
                        TextWithSubtitle(title = item.title, subtitle = item.subtitle,
                            onClick = {
                                onItemSelected(item.value)
                                selectedTextId = item.title
                                isExpanded = false
                            })
                    },
                    onClick = {
                        onItemSelected(item.value)
                        selectedTextId = item.title
                        isExpanded = false
                    }
                )
            }
        }
    }
}

data class PowerAmpacheDropdownItem<T>(
    @StringRes val title: Int,
    @StringRes val subtitle: Int? = null,
    val value: T,
    val isEnabled: Boolean = true
)
