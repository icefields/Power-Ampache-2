package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.getCustomDirPermission
import luci.sixsixsix.powerampache2.common.hasPersistedWritePermission
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsEvent
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel

@Composable fun CheckCustomStoragePermissionDialog(settingsViewModel: SettingsViewModel) {
    var enableExternalDirDownloads by remember { mutableStateOf<Boolean>(false) }

    LaunchedEffect(Unit) {
        enableExternalDirDownloads =
            Constants.getConfig().enableExternalDirDownloads
    }

    when (enableExternalDirDownloads) {
        true -> {
            val rootUri = settingsViewModel.playerSettingsStateFlow
                .collectAsStateWithLifecycle().value.customDownloadLocation

            CheckCustomStoragePermission(rootUri) {
                settingsViewModel.onEvent(SettingsEvent.OnChooseCustomDirDownloads(it))
            }
        }
        false -> {
            // do nothing
        }
    }
}

@Composable
private fun CheckCustomStoragePermission(rootUri: Uri?, onSelectCustomDir: (Uri) -> Unit) {
    rootUri?.let { uri ->
        val context = LocalContext.current
        if(!context.hasPersistedWritePermission(uri)) {
            val launcher = context.getCustomDirPermission(onFolderSelected = onSelectCustomDir)
            var showExplanationDialog by rememberSaveable { mutableStateOf(true) }

            if (showExplanationDialog) {
                DirectoryPermissionDialog(
                    onConfirm = {
                        showExplanationDialog = false
                        launcher.launch(null)
                    },
                    onDismiss = { showExplanationDialog = false }
                )
            }

        }
    }
}

@Composable
private fun DirectoryPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val titleText = stringResource(R.string.settings_storagePermission_dialog_title)
    val contentText = stringResource(R.string.settings_storagePermission_dialog_text)
    val confirmText = stringResource(R.string.settings_storagePermission_dialog_confirm)
    val denyText = stringResource(android.R.string.cancel)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(titleText)
        },
        text = {
            Text(contentText)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(denyText)
            }
        }
    )
}
