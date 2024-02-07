package luci.sixsixsix.powerampache2.presentation.common

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.presentation.dialogs.textPaddingVertical

@Composable
fun RoundedCornerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderEnabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.onSurface
) {
    TextButton(
        modifier = modifier
            .wrapContentSize(Alignment.Center),
        shape = RoundedCornerShape(25.dp),
        border = if (borderEnabled) BorderStroke(
            width = 0.5.dp,
            color = borderColor
        ) else null,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

    }
}

@Composable
fun RoundedCornerButton(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderEnabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    RoundedCornerButton(
        text = stringResource(id = text),
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        borderEnabled = borderEnabled,
        borderColor = borderColor
    )
}
