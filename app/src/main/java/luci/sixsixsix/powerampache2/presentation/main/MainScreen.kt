package luci.sixsixsix.powerampache2.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import luci.sixsixsix.powerampache2.presentation.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.home.HomeScreenViewModel
import luci.sixsixsix.powerampache2.presentation.main.screens.LoggedInScreen
import luci.sixsixsix.powerampache2.presentation.main.screens.LoginScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
) {
    if(authViewModel.state.isLoading) {
        LoadingScreen()
    } else {
        if (authViewModel.state.session != null) {
            LoggedInScreen(mainViewModel, authViewModel)
        } else {
            LoginScreen(viewModel = authViewModel)
        }
    }
}
