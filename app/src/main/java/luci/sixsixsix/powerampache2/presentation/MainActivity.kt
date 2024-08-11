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
package luci.sixsixsix.powerampache2.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.AndroidEntryPoint
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.utils.ShareManager.Companion.parseDeepLinkIntent
import luci.sixsixsix.powerampache2.presentation.screens.main.AuthViewModel
import luci.sixsixsix.powerampache2.presentation.screens.main.MainScreen
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens.settings.SettingsViewModel
import luci.sixsixsix.powerampache2.ui.theme.PowerAmpache2Theme

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    //private lateinit var homeScreenViewModel: HomeScreenViewModel
    //private val authViewModel: AuthViewModel by viewModels()
    //private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            authViewModel = hiltViewModel<AuthViewModel>(this)
            mainViewModel = hiltViewModel<MainViewModel>(this)
            settingsViewModel = hiltViewModel<SettingsViewModel>(this)
            // homeScreenViewModel = hiltViewModel<HomeScreenViewModel>(this)

            val localSettingsState by settingsViewModel.localSettingsStateFlow.collectAsState()
            var dynamicColour = true
            var isDarkTheme = true
            when (localSettingsState.theme) {
                PowerAmpTheme.SYSTEM -> {
                    dynamicColour = false
                    isDarkTheme = isSystemInDarkTheme()
                }
                PowerAmpTheme.DARK -> {
                    dynamicColour = false
                    isDarkTheme = true
                }
                PowerAmpTheme.LIGHT -> {
                    dynamicColour = false
                    isDarkTheme = false
                }
                PowerAmpTheme.MATERIAL_YOU_SYSTEM -> {
                    dynamicColour = true
                    isDarkTheme = isSystemInDarkTheme()
                }
                PowerAmpTheme.MATERIAL_YOU_DARK -> {
                    dynamicColour = true
                    isDarkTheme = true
                }
                PowerAmpTheme.MATERIAL_YOU_LIGHT -> {
                    dynamicColour = true
                    isDarkTheme = false
                }
            }
            RandomThemeBackgroundColour.reset()
            RandomThemeBackgroundColour.isDarkTheme = isDarkTheme

            PowerAmpache2Theme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColour
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        modifier = Modifier.fillMaxSize(),
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel
                      //  homeScreenViewModel = homeScreenViewModel
                    )
                }
            }

            // parse intent for deeplink after initializing mainViewModel
            parseIntent(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        // refresh token or autologin every time the app resumes
        authViewModel.pingServer()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseIntent(intent)
    }

    private fun parseIntent(intent: Intent) {
        parseDeepLinkIntent(intent) { type, id, title, artist ->
            try {
                mainViewModel.onDeepLink(type, id, title, artist)
            } catch (e: Exception) {
                L.e(e)
            }
        }

        setIntent(Intent())
    }
}
