package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.presentation.common.RoundedCornerButton

@Composable
fun EraseConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String = "",
    icon: ImageVector = Icons.Default.Warning,
    iconContentDescription: String = "Warning"
) {
    AlertDialog(
        icon = {
            Icon(
                modifier = Modifier.fillMaxSize(0.3f),
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        title = {
            Text(
                text = dialogTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 6.dp,
                        vertical = 6.dp
                    ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
        },
        text = {
            Text(
                text = dialogText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 6.dp,
                        vertical = 6.dp
                    ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            RoundedCornerButton(text = "CONFIRM") {
                onConfirmation()
            }
        },
        dismissButton = {
            RoundedCornerButton(text = "DISMISS") {
                onDismissRequest()
            }
        }
    )
}

@Composable @Preview
fun EraseConfirmDialogPreview() {
    EraseConfirmDialog(
        onDismissRequest = {},
        onConfirmation = {},
        dialogTitle = "title dialog erase",
        dialogText = "dialogText dialog erase"
    )
}
