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
package luci.sixsixsix.powerampache2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.data.remote.ErrorHandlerApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GithubModule {
    @Provides
    @Singleton
    fun provideErrorHandlerApi(retrofit: Retrofit): ErrorHandlerApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.URL_ERROR_LOG)
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(Constants.TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
                    .readTimeout(Constants.TIMEOUT_READ_S, TimeUnit.SECONDS)
                    .writeTimeout(Constants.TIMEOUT_WRITE_S, TimeUnit.SECONDS)
                    .build()
            )
            //.addConverterFactory(GsonConverterFactory.create())
            .build().create(ErrorHandlerApi::class.java)
}