package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@Composable
fun LikeButton(
    isLikeLoading: Boolean,
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    background: Color = MaterialTheme.colorScheme.background,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) = ButtonWithLoadingIndicator(
    imageVector = if (isFavourite)
        Icons.Default.Favorite else
        Icons.Default.FavoriteBorder,
    imageContentDescription = "favourite",
    isLoading = isLikeLoading,
    modifier = modifier.size(29.dp),
    background = background.copy(alpha = 0.3f),
    iconTint = iconTint,
    borderStroke = 1.dp,
    onClick = onClick
)


@Composable @Preview
fun PreviewLikeButton() {
    LikeButton(isLikeLoading = false, isFavourite = false) { }
}
