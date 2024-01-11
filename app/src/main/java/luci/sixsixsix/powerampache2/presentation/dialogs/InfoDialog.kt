package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun InfoDialog(
    info: String,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = info,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(vertical = textPaddingVertical)
                    .verticalScroll(rememberScrollState()),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}
