package luci.sixsixsix.powerampache2.domain.plugin.models

import luci.sixsixsix.powerampache2.domain.models.AmpacheModel


data class PluginData<T: AmpacheModel>(
    val data: List<T>
)
