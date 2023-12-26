package luci.sixsixsix.powerampache2.presentation.album_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttributeText(
    modifier: Modifier = Modifier,
    title: String,
    name: String,
    fontSizeTitle: TextUnit = 14.sp,
    fontSizeName: TextUnit = 17.sp,
    fontWeightTitle: FontWeight = FontWeight.Normal,
    fontWeightName: FontWeight = FontWeight.Bold,
) {
    Row(modifier = modifier) {
        Text( // title
            modifier = Modifier.align(Alignment.CenterVertically),
            text = title,
            fontWeight = fontWeightTitle,
            fontSize = fontSizeTitle
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text( // name
            modifier = Modifier.align(Alignment.CenterVertically),
            text = name,
            fontWeight = fontWeightName,
            fontSize = fontSizeName
        )
    }
}
