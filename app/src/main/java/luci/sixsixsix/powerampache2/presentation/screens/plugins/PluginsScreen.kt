package luci.sixsixsix.powerampache2.presentation.screens.plugins

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_INFO_ACTIVITY_ID
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_INFO_ID
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_LYRICS_ACTIVITY_ID
import luci.sixsixsix.powerampache2.domain.common.Constants.PLUGIN_LYRICS_ID
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel

@Composable
@Destination
fun PluginsScreen(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel,
    pluginsViewModel: PluginsViewModel = hiltViewModel()
) {
    val state by pluginsViewModel.state
    PluginsScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
        isLoading = state.isLoading,
        isLyricsPluginInstalled = state.isLyricsPluginInstalled,
        isAndroidAutoPluginInstalled = state.isAndroidAutoPluginInstalled,
        isMetadataPluginInstalled = state.isMetadataPluginInstalled,
        isExternalDataSourcePluginInstalled = state.isExternalDataSourcePluginInstalled
    )
}

@Composable
fun PluginsScreenContent(
    isLoading: Boolean = false,
    isLyricsPluginInstalled: Boolean = false,
    isChromecastPluginInstalled: Boolean = false,
    isAndroidAutoPluginInstalled: Boolean = false,
    isMetadataPluginInstalled: Boolean = false,
    isExternalDataSourcePluginInstalled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val spacerH = 4.dp
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(top = dimensionResource(id = R.dimen.settings_padding_top))
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        Text("Enhance Power Ampache 2 with custom plugins.\nAvailable for download via your favorite app store, GitHub Releases, and Telegram.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        PluginListItem (
            isInstalled = isLyricsPluginInstalled,
            title = "Lyrics",
            description = "Fetch Lyrics for songs when missing."
        ) { context.startActivity(Intent().setClassName(PLUGIN_LYRICS_ID, PLUGIN_LYRICS_ACTIVITY_ID)) }

        Spacer(modifier = Modifier.fillMaxWidth().height(spacerH))
        PluginListItem (isInstalled = isExternalDataSourcePluginInstalled,
            title = "External Music Source",
            description = "Feed Power Ampache 2 with a custom media source—such as local music files, scraped content from video platforms, or your own personal media sources."
        ) { }

        Spacer(modifier = Modifier.fillMaxWidth().height(spacerH))
        PluginListItem (isInstalled = isMetadataPluginInstalled,
            title = "Metadata",
            description = "Add artist, album, and song metadata on top of what’s provided by the backend."
        ) { context.startActivity(Intent().setClassName(PLUGIN_INFO_ID, PLUGIN_INFO_ACTIVITY_ID)) }

        Spacer(modifier = Modifier.fillMaxWidth().height(spacerH))
        PluginListItem (
            isInstalled = false,
            title = "Live Shows",
            description = "Shows touring/shows info and dates in the Artist screen"
        ) {  }

        Spacer(modifier = Modifier.fillMaxWidth().height(spacerH))
        PluginListItem (isInstalled = isChromecastPluginInstalled,
            title = "Chromecast",
            description = "Plugin to connect to Chromecast"
        ) { }

        Spacer(modifier = Modifier.fillMaxWidth().height(spacerH))
        PluginListItem (isInstalled = isAndroidAutoPluginInstalled,
            title = "Android Auto",
            description = "Plugin to connect to Android Auto"
        ) { }
    }
}

@Composable
fun PluginListItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isInstalled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        onClick = onClick,
        enabled = isInstalled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 1.dp)
            ) {
                Text(title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(description)
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.5f).padding(top = 4.dp, bottom = 1.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Text("Installed: ${if (isInstalled) "yes" else "no" }",
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

    }
}


@Preview
@Composable
fun PluginsScreenContentPreview() {
    PluginsScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
            .background(MaterialTheme.colorScheme.background),
        isLoading = false,
        isLyricsPluginInstalled = true,
        isChromecastPluginInstalled = false,
        isAndroidAutoPluginInstalled = false,
        isMetadataPluginInstalled = true
    )
}
