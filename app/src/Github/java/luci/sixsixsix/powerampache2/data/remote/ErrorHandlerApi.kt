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
package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.time.LocalDateTime

interface ErrorHandlerApi {
    @FormUrlEncoded
    @POST("api_post.php")
    suspend fun sendErrorReport(
        @Field("api_user_key") apiUserKey: String = "56cc36787e45df9549c77636fb889fd4",
        @Field("api_paste_private") apiPastePrivate: String = "1",
        @Field("api_paste_name") apiPasteName: String = LocalDateTime.now().toString(),
        @Field("api_paste_expire_date") apiPasteExpireDate: String = "1W",
        @Field("api_paste_format") apiPasteFormat: String = "php",
        @Field("api_dev_key") apiDevKey: String = BuildConfig.PASTEBIN_API_KEY,
        @Field("api_paste_code") apiPasteCode: String,
        @Field("api_option") apiOption: String = "paste"
        //@Url url: String = BuildConfig.URL_ERROR_LOG,
        //@Body body: String
    ): ResponseBody
}
