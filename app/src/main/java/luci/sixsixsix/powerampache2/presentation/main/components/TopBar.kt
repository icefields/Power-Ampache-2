package luci.sixsixsix.powerampache2.presentation.main.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.MainEvent
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    currentPage: Int,
    interactionSource: MutableInteractionSource
) {
    val state = viewModel.state
    Box(modifier = modifier) {
        if (currentPage != 0 && currentPage != 4) {
            OutlinedTextField(
                interactionSource = interactionSource,
                value = state.searchQuery,
                onValueChange = {
                    viewModel.onEvent(MainEvent.OnSearchQueryChange(it))
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.topBar_search_hint))
                },
                maxLines = 1,
                singleLine = true
            )
        }
    }
}
