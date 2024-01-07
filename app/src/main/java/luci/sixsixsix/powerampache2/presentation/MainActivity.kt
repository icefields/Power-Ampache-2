package luci.sixsixsix.powerampache2.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.player.SimpleMediaService
import luci.sixsixsix.powerampache2.presentation.home.HomeScreenViewModel
import luci.sixsixsix.powerampache2.presentation.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.main.MainScreen
import luci.sixsixsix.powerampache2.presentation.main.MainViewModel
import luci.sixsixsix.powerampache2.ui.theme.PowerAmpache2Theme

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
    //private val authViewModel: AuthViewModel by viewModels()
    //private val mainViewModel: MainViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel
    //private lateinit var homeScreenViewModel: HomeScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerAmpache2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    authViewModel = hiltViewModel<AuthViewModel>(this)
                    mainViewModel = hiltViewModel<MainViewModel>(this)
                   // homeScreenViewModel = hiltViewModel<HomeScreenViewModel>(this)

                    MainScreen(
                        modifier = Modifier.fillMaxSize(),
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel,
                      //  homeScreenViewModel = homeScreenViewModel
                    )
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        L( "onRestart")
        // refresh token or autologin every time the app resumes
        authViewModel.verifyAndAutologin()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
