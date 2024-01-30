package luci.sixsixsix.powerampache2.presentation.common

import android.R.attr.label
import android.R.attr.text
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.DONATION_BITCOIN_ADDRESS
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject


@Composable
fun DonateButton(
    modifier: Modifier = Modifier,
    isExpanded:Boolean = false,
    isTransparent: Boolean = false,
    donateViewModel: DonateViewModel = hiltViewModel(),
    onDonateBtcButtonClick: () -> Unit = {},
    onDonatePaypalButtonClick: () -> Unit = {}
) {
    DonateButtonContent(
        modifier = modifier,
        isExpanded = isExpanded,
        isTransparent = isTransparent,
        onDonateBtcButtonClick = {
            onDonateBtcButtonClick()
            donateViewModel.donateBtc()
        }, onDonatePaypalButtonClick = {
            onDonatePaypalButtonClick()
            donateViewModel.donatePaypal()
        }
    )
}

@Composable
fun DonateButtonContent(
    modifier: Modifier = Modifier,
    isExpanded:Boolean = false,
    isTransparent: Boolean = false,
    onDonateBtcButtonClick: () -> Unit = {},
    onDonatePaypalButtonClick: () -> Unit = {}
) {
    val isShowDonateButtons = remember { mutableStateOf(isExpanded) }
    Card(
        border = BorderStroke(
            width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
            color = if (isTransparent) {
                MaterialTheme.colorScheme.onSurface
            } else MaterialTheme.colorScheme.background
        ),
        modifier = modifier
            .wrapContentSize()
            .padding(horizontal = 26.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isTransparent) {
                MaterialTheme.colorScheme.background
            } else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        AnimatedVisibility (isShowDonateButtons.value) {
            DonateButtons(
                isTransparent = isTransparent,
                onDonateBtcButtonClick = {
                    onDonateBtcButtonClick()
                }, onDonatePaypalButtonClick = {
                    onDonatePaypalButtonClick()
                }
            )
        }
        AnimatedVisibility (!isShowDonateButtons.value) {
            DonateButtonSingle(isShowDonateButtons, isTransparent)
        }
    }
}

@Composable
fun DonateButtons(
    isTransparent: Boolean,
    onDonateBtcButtonClick: () -> Unit,
    onDonatePaypalButtonClick: () -> Unit
) {
    Column {
        DonateBtcButton(isTransparent, onDonateBtcButtonClick)
        DonatePaypalButton(isTransparent, onDonatePaypalButtonClick)
    }
}

@Composable
fun DonateButtonSingle(
    isShowDonateButtons: MutableState<Boolean>,
    isTransparent: Boolean = false
) {
    DefaultFullWidthButton(
        colours = getButtonBgColour(isTransparent = isTransparent),
        onClick = {
            isShowDonateButtons.value = true
        }
    ) {
        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Donate")
        Icon(imageVector = Icons.Default.CurrencyBitcoin, contentDescription = "Donate")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = "Donate ",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun getButtonBgColour(isTransparent: Boolean) = if (!isTransparent) {
    ButtonDefaults.textButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
} else {
    ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun DonateBtcButton(
    isTransparent: Boolean,
    onDonateBtcButtonClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = getButtonBgColour(isTransparent = isTransparent),
        onClick = {
            onDonateBtcButtonClick()
        }
    ) {
        Icon(imageVector = Icons.Default.CurrencyBitcoin, contentDescription = "Donate Bitcoin")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = "Donate ",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Text(text = "Bitcoin")
    }
}

@Composable
fun DonatePaypalButton(
    isTransparent: Boolean,
    onDonatePaypalButtonClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = getButtonBgColour(isTransparent = isTransparent),
        onClick = { onDonatePaypalButtonClick() }
    ) {
        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Donate Paypal")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = "Donate ",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Text(text = "Paypal")
    }
}

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val application: Application,
    private val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application) {

    fun donateBtc() {
        try {
            application.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(Constants.DONATION_BITCOIN_URI)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            })
        } catch (e: Exception) {
            (application.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(ClipData.newPlainText(
                    "BITCOIN donation address for ${application.getString(R.string.app_name)}",
                    DONATION_BITCOIN_ADDRESS
                ))
            }
            playlistManager.updateErrorMessage("No Bitcoin Wallet found on this device, BTC address copied to clipboard")
        }
    }

    fun donatePaypal() {
        application.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(Constants.DONATION_PAYPAL_URI)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }
}

@Composable
@Preview
fun DonateButtonPreview() {
    DonateButtonContent(
        isExpanded = false,
        isTransparent = true,
        onDonateBtcButtonClick = { },
        onDonatePaypalButtonClick = { }
    )
}
