package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.dpTextUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StarRatingIcon(
    rating: Int,
    padding: Dp = 0.dp
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.starRating_button_cornerRadius))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 1.dp)
                .padding(start = if (rating > 0) 4.dp else 0.dp, end = 0.dp)
                .padding(padding)
        ) {
            if (rating > 0) {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = "$rating",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.dpTextUnit,
                    maxLines = 1,
                    textAlign = TextAlign.Start
                )
            }
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.playlistItem_icon_size)),
                tint = MaterialTheme.colorScheme.surface,
                imageVector = Icons.Rounded.StarRate,
                contentDescription = "favorite playlist")
        }
    }
}

@Composable @Preview
fun PreviewStarRatingIcon() {
    StarRatingIcon(rating = 0)
}