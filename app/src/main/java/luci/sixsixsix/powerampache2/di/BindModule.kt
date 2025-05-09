/**
 * Copyright (C) 2025  Antonio Tari
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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import luci.sixsixsix.powerampache2.common.ShareManagerImpl
import luci.sixsixsix.powerampache2.common.WorkerHelperImpl
import luci.sixsixsix.powerampache2.domain.utils.ShareManager
import luci.sixsixsix.powerampache2.domain.utils.WorkerHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    @Singleton
    abstract fun bindShareManager(
        shareManagerImpl: ShareManagerImpl
    ): ShareManager

    @Binds
    @Singleton
    abstract fun bindWorkerHelper(
        workerHelperImpl: WorkerHelperImpl
    ): WorkerHelper
}
