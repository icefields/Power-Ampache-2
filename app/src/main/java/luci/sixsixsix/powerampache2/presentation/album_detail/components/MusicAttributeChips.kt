package luci.sixsixsix.powerampache2.presentation.album_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

@Composable
fun MusicAttributeChips(
    modifier: Modifier = Modifier,
    attributes: List<MusicAttribute>,
    containerColor: Color = Color(red = 0, blue = 0, green = 0, alpha = 180)
) {
    LazyRow(modifier = modifier
        .padding(
            horizontal = dimensionResource(R.dimen.albumDetailScreen_infoSection_chipsRow_padding)
        )) {
        items(attributes) {
            if (it.name.isNotBlank()) {
                Row {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor
                        ),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.albumDetail_chip_radius)),
                        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.albumDetail_chip_elevation))
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            text = it.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}
