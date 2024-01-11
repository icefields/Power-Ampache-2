package luci.sixsixsix.powerampache2.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.unit.dp

@Composable
fun LikeButton(
    isLikeLoading: Boolean,
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    background: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier.size(24.dp),
        onClick = { onClick() }
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = background),
            modifier = Modifier.fillMaxSize()

        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLikeLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = if (isFavourite)
                            Icons.Default.Favorite else
                            Icons.Default.FavoriteBorder,
                        contentDescription = "favourite album",
                        tint = iconTint
                    )
                }
            }
        }
    }
}
