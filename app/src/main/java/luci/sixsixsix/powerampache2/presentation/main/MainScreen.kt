package luci.sixsixsix.powerampache2.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.subscreens.LoggedInScreen
import luci.sixsixsix.powerampache2.presentation.main.subscreens.LoginScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {

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
