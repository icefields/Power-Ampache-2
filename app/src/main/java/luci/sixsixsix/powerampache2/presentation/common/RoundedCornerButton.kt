package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.presentation.dialogs.textPaddingVertical

@Composable
fun RoundedCornerButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .padding(vertical = textPaddingVertical),
        shape = RoundedCornerShape(1.dp),
        onClick = onClick
    ) {
        Card(
            shape = RoundedCornerShape(5.dp), // half or icon size
            //elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.albumDetail_chip_elevation)),
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(
                            horizontal = 16.dp,
                            vertical = textPaddingVertical
                        ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
