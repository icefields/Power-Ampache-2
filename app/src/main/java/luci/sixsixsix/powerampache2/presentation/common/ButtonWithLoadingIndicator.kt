package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonWithLoadingIndicator(
    imageVector: ImageVector,
    imageContentDescription: String,
    isLoading: Boolean,
    showBoth: Boolean = false,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    borderStroke: Dp = 0.dp,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = { onClick() }
    ) {
        Card(
            shape = RoundedCornerShape(27.dp), // half or icon size
            //elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.albumDetail_chip_elevation)),
            border = BorderStroke(
                width = borderStroke,
                color = if(borderStroke != 0.dp) iconTint else Color.Transparent
            ),
            colors = CardDefaults.cardColors(containerColor = background),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                }
                if (!isLoading || showBoth) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = imageContentDescription,
                        tint = iconTint
                    )
                }
            }
        }
    }
}

@Composable @Preview
fun ButtonWithLoadingIndicatorPreview() {
    ButtonWithLoadingIndicator(imageVector = Icons.Outlined.AddBox,
        imageContentDescription = "Add to playlist",
        background = Color.Transparent,
        borderStroke = 0.dp,
        showBoth = true,
        isLoading = true
    ) {
    }
}
