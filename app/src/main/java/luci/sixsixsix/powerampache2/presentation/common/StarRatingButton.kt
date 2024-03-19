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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@Composable
fun StarRatingButton(
    modifier: Modifier = Modifier,
    currentRating: Int = 0,
    onRate: (Int) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    val iconTint = MaterialTheme.colorScheme.surface
    //Box {
        AnimatedVisibility(enter = fadeIn(), // slideInHorizontally(spring(stiffness = Spring.StiffnessHigh)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessHigh)) ,//fadeOut(spring(stiffness = Spring.StiffnessHigh)),
            visible = isExpanded) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
            ) {
                Row(
                    modifier = modifier.wrapContentWidth().padding(3.dp).padding(bottom = 1.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                            onRate(1)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            tint = iconTint,
                            imageVector = if (currentRating >= 1) Icons.Outlined.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = "1 star rating"
                        )
                    }
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                            onRate(2)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            //tint = iconTint,
                            imageVector = if (currentRating >= 2) Icons.Outlined.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = "2 star rating"
                        )
                    }
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                            onRate(3)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            tint = iconTint,
                            imageVector = if (currentRating >= 3) Icons.Outlined.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = "3 star rating"
                        )
                    }
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                            onRate(4)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            tint = iconTint,
                            imageVector = if (currentRating >= 4) Icons.Outlined.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = "4 star rating"
                        )
                    }
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                            onRate(5)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            tint = iconTint,
                            imageVector = if (currentRating == 5) Icons.Outlined.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = "5 star rating"
                        )
                    }
                }
            }
        }

        // no animation
        if (!isExpanded) {
            IconButton(onClick = { isExpanded = !isExpanded }) {
                StarRatingIcon(rating = currentRating, padding = 2.dp)
            }
        }

    // with animation
//        AnimatedVisibility(visible = !isExpanded,
//            exit = slideOutHorizontally(spring(stiffness = Spring.StiffnessHigh), targetOffsetX = { it / 2 }),
//            enter = slideInHorizontally(spring(stiffness = Spring.StiffnessHigh), initialOffsetX = { it / 2 })) {
//            IconButton(onClick = { isExpanded = !isExpanded }) {
//                StarRatingIcon(rating = currentRating, padding = 2.dp)
//            }
//        }
    //}

}

@Composable @Preview
fun PreviewStarRatingButton() {
    StarRatingButton(currentRating = 0,onRate = {})
}