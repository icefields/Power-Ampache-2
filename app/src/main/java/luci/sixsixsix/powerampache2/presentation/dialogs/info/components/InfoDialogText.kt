package luci.sixsixsix.powerampache2.presentation.dialogs.info.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoDialogText(key: String? = null, text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp, horizontal = 6.dp)) {
        key?.let {
            Text(
                color = MaterialTheme.colorScheme.secondary,
                text = it,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterStart),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterStart),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
    }
}

@Composable @Preview
fun InfoDialogTextPreview() {
    InfoDialogText("Author","Necrophagist")
}
