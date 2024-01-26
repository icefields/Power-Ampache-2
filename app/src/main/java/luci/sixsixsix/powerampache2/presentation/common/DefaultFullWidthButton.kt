package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultFullWidthButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colours: ButtonColors = ButtonDefaults.textButtonColors(),
    content: @Composable () -> Unit
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth(),
        colors = colours,
        shape = RoundedCornerShape(10.dp),
        onClick = {onClick()}
    ) {
        content()
    }
}
