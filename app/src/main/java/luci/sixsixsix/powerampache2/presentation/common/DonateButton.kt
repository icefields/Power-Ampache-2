package luci.sixsixsix.powerampache2.presentation.common

import android.app.Application
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.presentation.settings.DonateBtcButton
import luci.sixsixsix.powerampache2.presentation.settings.DonatePaypalButton
import javax.inject.Inject

@Composable
fun DonateButton(
    modifier: Modifier = Modifier,
    isExpanded:Boolean = false,
    donateViewModel: DonateViewModel = hiltViewModel(),
    onDonateBtcButtonClick: () -> Unit = {},
    onDonatePaypalButtonClick: () -> Unit = {}
) {
    DonateButtonContent(
        modifier = modifier,
        isExpanded = isExpanded,
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
    onDonateBtcButtonClick: () -> Unit = {},
    onDonatePaypalButtonClick: () -> Unit = {}
) {
    val isShowDonateButtons = remember { mutableStateOf(isExpanded) }
    Card(
        border = BorderStroke(
            width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
            color = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
            .wrapContentSize()
            .padding(horizontal = 26.dp, vertical = 10.dp)
        //.padding(all = 10.dp)
        ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        AnimatedVisibility (isShowDonateButtons.value) {
            DonateButtons(
                onDonateBtcButtonClick = {
                    onDonateBtcButtonClick()
                }, onDonatePaypalButtonClick = {
                    onDonatePaypalButtonClick()
                }
            )
        }
        AnimatedVisibility (!isShowDonateButtons.value) {
            DonateButtonSingle(isShowDonateButtons)
        }
    }
}

@Composable
fun DonateButtons(
    onDonateBtcButtonClick: () -> Unit,
    onDonatePaypalButtonClick: () -> Unit
) {
    Column {
        DonateBtcButton(onDonateBtcButtonClick)
        DonatePaypalButton(onDonatePaypalButtonClick)
    }
}

@Composable
fun DonateButtonSingle(
    isShowDonateButtons: MutableState<Boolean>
) {
    TextButton(
        modifier = Modifier

            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
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

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {
    fun donateBtc() {
        application.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(Constants.DONATION_BITCOIN_URI)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
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
        isExpanded = true,
        onDonateBtcButtonClick = { },
        onDonatePaypalButtonClick = { }
    )
}
