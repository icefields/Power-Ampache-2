/**
 * Copyright (C) 2024  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.common.donate_btn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import luci.sixsixsix.powerampache2.R

@Composable
fun DonateButton(
    modifier: Modifier = Modifier,
    isExpanded:Boolean = false,
    isTransparent: Boolean = false,
    donateViewModel: DonateViewModel = hiltViewModel()
) {
    DonateButtonContent(
        modifier = modifier,
        isExpanded = isExpanded,
        isTransparent = isTransparent,
        onDonateBtcButtonClick = donateViewModel::donateBtc,
        onDonatePaypalButtonClick = donateViewModel::donatePaypal,
        onDonateBmacButtonClick = donateViewModel::donateBmac
    )
}

@Composable
fun DonateButtonContent(
    modifier: Modifier = Modifier,
    isExpanded:Boolean = false,
    isTransparent: Boolean = false,
    onDonateBtcButtonClick: () -> Unit,
    onDonatePaypalButtonClick: () -> Unit,
    onDonateBmacButtonClick: () -> Unit
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
            .padding(vertical = 10.dp),
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
                }, onDonateBmacButtonClick = onDonateBmacButtonClick
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
    onDonatePaypalButtonClick: () -> Unit,
    onDonateBmacButtonClick: () -> Unit
) {
    val buttonsVertSpacing = 10.dp
    Column {
        DonateBtcButton(isTransparent, onDonateBtcButtonClick)
        Spacer(Modifier.height(buttonsVertSpacing))
        DonatePaypalButton(isTransparent, onDonatePaypalButtonClick)
        Spacer(Modifier.height(buttonsVertSpacing))
        DonateBmacButton(isTransparent, onDonateBmacButtonClick)
    }
}

@Composable
fun DonateButtonSingle(
    isShowDonateButtons: MutableState<Boolean>,
    isTransparent: Boolean = false
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth(),
        colors = getButtonBgColour(isTransparent = isTransparent),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            isShowDonateButtons.value = true
        }
    ) {
        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = "Donate")
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 6.dp),
            text = "Donate ",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = getButtonBgColour(isTransparent = isTransparent),
        onClick = {
            onDonateBtcButtonClick()
        }
    ) {
        Icon(
            imageVector = Icons.Default.CurrencyBitcoin,
            contentDescription = stringResource(R.string.bitcoin_btn_contentDescription)
        )
        Text(
            modifier = Modifier.padding(vertical = 9.dp),
            text = stringResource(R.string.donate_btn_text),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(Modifier.width(4.dp))
        Text(text = stringResource(R.string.bitcoin_btn_text))
    }
}

@Composable
fun DonatePaypalButton(
    isTransparent: Boolean,
    onDonatePaypalButtonClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = getButtonBgColour(isTransparent = isTransparent),
        onClick = { onDonatePaypalButtonClick() }
    ) {
        Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = stringResource(R.string.paypal_btn_contentDescription)
        )
        Text(
            modifier = Modifier
                .padding(vertical = 9.dp),
            text = stringResource(R.string.donate_btn_text),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(Modifier.width(4.dp))
        Text(text = stringResource(R.string.paypal_btn_text))
    }
}

@Composable
fun DonateBmacButton(
    isTransparent: Boolean,
    onDonateBmacButtonClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = getButtonBgColour(isTransparent = isTransparent),
        onClick = { onDonateBmacButtonClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.bmc_brand_logo),
                contentDescription = stringResource(R.string.bmac_btn_text)
            )
        }

    }
}

@Composable
@Preview
fun DonateButtonPreview() {
    DonateButtonContent(
        isExpanded = false,
        isTransparent = false,
        onDonateBtcButtonClick = { },
        onDonatePaypalButtonClick = { }
    ) {}
}

@Composable
@Preview
fun DonateButtonPreviewExpanded() {
    DonateButtonContent(
        isExpanded = true,
        isTransparent = false,
        onDonateBtcButtonClick = { },
        onDonatePaypalButtonClick = { }
    ) {}
}