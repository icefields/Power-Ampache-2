package luci.sixsixsix.powerampache2.presentation.main

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.ramcosta.composedestinations.DestinationsNavHost
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.subscreens.LoggedInScreen
import luci.sixsixsix.powerampache2.presentation.main.subscreens.LoginScreen
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    activity: ComponentActivity,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier) {

    if(authViewModel.state.isLoading) {
        LoadingScreen()
    } else {
        if (authViewModel.state.session != null) {
            LoggedInScreen()
        } else {
            LoginScreen()
        }
    }
}


