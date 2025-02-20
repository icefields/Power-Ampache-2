/**
 * Copyright (C) 2025  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.presentation.dialogs

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.config
import luci.sixsixsix.powerampache2.common.openLinkInBrowser
import luci.sixsixsix.powerampache2.ui.theme.additionalColours

const val LOCAL_INTRO_DIALOG_URI = "file:///android_asset/introdialog_fallback.html"

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
                containerColor = MaterialTheme.colorScheme.additionalColours.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Column(modifier = Modifier
                .padding(8.dp)) {
                IntroWebView()
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest,

                ) {
                    Text(stringResource(android.R.string.ok), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun IntroWebView() {
    val url = if (config.isIntroMessageLocal)
        LOCAL_INTRO_DIALOG_URI
    else
        StringBuilder(stringResource(R.string.website))
            .append("/")
            .append(config.introMessage)
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
                    view?.loadUrl(LOCAL_INTRO_DIALOG_URI)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val urlRedirect = request?.url.toString()
                    if (urlRedirect.startsWith("http://") || urlRedirect.startsWith("https://")) {
                        context.openLinkInBrowser( urlRedirect)
                        return true
                    }
                    // do not redirect if url is not http or https to lower the rick of malicious
                    // injection of local or other app deep-links.
                    return true
                }
           }
            //settings.loadWithOverviewMode = true
            //settings.useWideViewPort = true
            settings.defaultFontSize = 18
            settings.minimumLogicalFontSize = 18
            settings.serifFontFamily = "Roboto"
            settings.standardFontFamily = "Roboto"
            //settings.setSupportZoom(true)
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}
