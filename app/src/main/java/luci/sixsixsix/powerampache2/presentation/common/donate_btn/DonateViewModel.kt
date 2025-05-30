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

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Constants.DONATION_BITCOIN_ADDRESS
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val application: Application,
    private val playlistManager: MusicPlaylistManager
) : AndroidViewModel(application) {

    fun donateBtc() {
        try {
            application.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(Constants.DONATION_BITCOIN_URI)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            })
        } catch (e: Exception) {
            (application.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(
                    ClipData.newPlainText(
                    "BITCOIN donation address for ${application.getString(R.string.app_name)}",
                    DONATION_BITCOIN_ADDRESS
                ))
            }
            playlistManager.updateUserMessage("No Bitcoin Wallet found on this device, BTC address copied to clipboard")
        }
    }

    private fun donateWebLink(link: String) {
        application.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }

    fun donatePaypal() = donateWebLink(Constants.DONATION_PAYPAL_URI)

//    {
//        application.startActivity(Intent(Intent.ACTION_VIEW).apply {
//            data = Uri.parse(Constants.DONATION_PAYPAL_URI)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//        })
//    }

    fun donateBmac() =  donateWebLink(Constants.BUYMEACOFFEE_URL)
}
