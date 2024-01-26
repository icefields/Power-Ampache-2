package luci.sixsixsix.powerampache2.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.User
import luci.sixsixsix.powerampache2.presentation.common.SubtitleString
import java.time.LocalDateTime

// TODO WIP rename functions, finish this screen. add donation links

@Composable
@Destination
fun SettingsScreen(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel
) {
    SettingsScreenContent(
        userState = settingsViewModel.userState,
        powerAmpTheme = settingsViewModel.state.theme,
        onThemeSelected = {
            settingsViewModel.setTheme(it)
        }
    )
}

@Composable
@Destination
fun SettingsScreenContent(
    userState: User?,
    powerAmpTheme: PowerAmpTheme,
    onThemeSelected: (selected: PowerAmpTheme) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 10.dp)) {
        items(5) { index ->
            when(index) {
                0 -> Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    text = "WORK IN PROGRESS. \nMore Settings and Themes coming soon",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 16.sp
                )

                1 -> UserInfoSection(user = userState!!)

                2 ->  SettingsThemeSelector(currentTheme = powerAmpTheme) {
                    onThemeSelected(it)
                }
                //ServerInfoSection(serverInfo)
                3 -> DonateBtcButton()
                4 -> DonatePaypalButton()
                else -> {}
            }
        }
    }
}

@Composable
fun DonateBtcButton() {
    TextButton(
        modifier = Modifier
            .padding(horizontal = 26.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = {  }
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
fun DonatePaypalButton() {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = {  }
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

@Composable
fun UserInfoSection(user: User) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 9.dp)) {
                UserInfoText("Username", user.username)
                //if (user.fullNamePublic == 1)
                    UserInfoText("Full Name", user.fullName)
                UserInfoText("Email", user.email)
                UserInfoText("Website", user.website)
                UserInfoText("City", user.city)
                UserInfoText("State", user.state)
                UserInfoText("ID", user.id)
                UserInfoText("Access", user.access?.toString())
                //UserInfoText("ID", LocalDateTime.parse(user.createDate).toString())
            }
        }
    }
}

@Composable
fun ServerInfoSection(serverInfo: ServerInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 9.dp)) {
                UserInfoText("Server", serverInfo.server)
                UserInfoText("Version", serverInfo.version)
                UserInfoText("Compatible", serverInfo.compatible)
            }
        }
    }
}

@Composable
fun UserInfoText(title: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 26.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier
                .width(5.dp)
                .height(1.dp))
            Text(
                text = value,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun SettingsThemeSelector(
    currentTheme: PowerAmpTheme,
    onThemeSelected: (selected: PowerAmpTheme) -> Unit
) {
    // all the theme options in the defined order
    val themes = listOf(
        PowerAmpTheme.MATERIAL_YOU_SYSTEM,
        PowerAmpTheme.MATERIAL_YOU_DARK,
        PowerAmpTheme.MATERIAL_YOU_LIGHT,
        PowerAmpTheme.SYSTEM,
        PowerAmpTheme.DARK,
        PowerAmpTheme.LIGHT
    )

    val (selected, setSelected) = remember { mutableStateOf(currentTheme) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            border = BorderStroke(
                width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {

            Text(
                text = "Theme Selector (${selected.title})",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 26.dp, vertical = 10.dp),
            )

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                ThemesRadioGroup(
                    mItems = themes,
                    selected = selected,
                    setSelected = {
                        setSelected(it)
                        onThemeSelected(it)
                    }
                )
            }
        }
    }
}

@Composable
fun ThemesRadioGroup(
    mItems: List<PowerAmpTheme>,
    selected: PowerAmpTheme,
    setSelected: (selected: PowerAmpTheme) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            mItems.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        enabled = item.isEnabled,
                        selected = selected == item,
                        onClick = {
                            setSelected(item)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )
                    Text(text = item.title, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreenContent(
        userState = User.mockUser(),
        powerAmpTheme = PowerAmpTheme.DARK,
        onThemeSelected = {
        }
    )
}