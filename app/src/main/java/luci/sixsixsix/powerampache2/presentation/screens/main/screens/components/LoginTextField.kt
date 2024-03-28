package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@Composable fun LoginTextField(
    @StringRes label: Int,
    value: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit)
{
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        label = {
            Text(text = stringResource(id = label))
        },
        maxLines = 1,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = colorResource(R.color.onBackgroundDark),
            unfocusedTextColor = colorResource(R.color.onSurfaceVariantDark),
            unfocusedLabelColor = colorResource(R.color.onSurfaceVariantDark),
            errorTextColor = colorResource(R.color.errorDark),
            focusedContainerColor = colorResource(R.color.surfaceContainerDark),
            unfocusedContainerColor = colorResource(R.color.surfaceContainerDark)
        )
    )
}
