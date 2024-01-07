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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.AndroidEntryPoint
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.player.SimpleMediaService
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
    private var isServiceRunning = false

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

                    MainScreen(
                        modifier = Modifier.fillMaxSize(),
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }

        startService()
    }

    override fun onRestart() {
        super.onRestart()
        L( "onRestart")
        // refresh token or autologin every time the app resumes
        authViewModel.verifyAndAutologin()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, SimpleMediaService::class.java))
        isServiceRunning = false
    }

    private fun startService() {
        if (!isServiceRunning) {
            Intent(this, SimpleMediaService::class.java).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(this)
                    isServiceRunning = true
                }
            }
        }
    }
}
