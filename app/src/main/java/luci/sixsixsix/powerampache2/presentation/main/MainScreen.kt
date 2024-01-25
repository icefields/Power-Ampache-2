package luci.sixsixsix.powerampache2.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.main.screens.LoggedInScreen
import luci.sixsixsix.powerampache2.presentation.main.screens.LoginScreen
import luci.sixsixsix.powerampache2.presentation.settings.SettingsViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel
) {
    if(authViewModel.state.isLoading) {
        LoadingScreen()
    } else {
        if (authViewModel.state.session != null) {
            LoggedInScreen(mainViewModel, authViewModel, settingsViewModel)
        } else {
            LoginScreen(viewModel = authViewModel)
        }
    }
}
