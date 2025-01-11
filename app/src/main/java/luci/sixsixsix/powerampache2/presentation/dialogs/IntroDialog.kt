package luci.sixsixsix.powerampache2.presentation.dialogs

import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants

@Composable
fun IntroDialog(onDismissRequest: () -> Unit) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismissRequest
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Column {
                IntroWebView()
                OutlinedButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        }
    }
}

@Composable
fun IntroWebView() {
    val url = StringBuilder(stringResource(R.string.website))
        .append("/")
        .append(Constants.config.introMessage)
        .toString()

    AndroidView(factory = { context ->

        WebView(context).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
            settings.javaScriptEnabled = false
            webViewClient = object:WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    view?.loadUrl("file:///android_asset/introdialog_fallback.html")
                }
           }
            //settings.loadWithOverviewMode = true
            //settings.useWideViewPort = true
            //settings.setSupportZoom(true)
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}