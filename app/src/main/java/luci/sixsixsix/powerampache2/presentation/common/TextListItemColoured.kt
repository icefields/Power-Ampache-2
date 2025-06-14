package luci.sixsixsix.powerampache2.presentation.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour

@Composable fun TextListItemColoured(
    @StringRes text: Int,
    onClick: () -> Unit,
    colour: Color = RandomThemeBackgroundColour()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().background(colour)
            .clickable { onClick() }
            .padding(vertical = 11.dp)
    ) {
        Text(
            text = stringResource(text),
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                ),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp
        )
    }
}
