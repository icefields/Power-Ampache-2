package luci.sixsixsix.powerampache2.domain.errors

import kotlinx.coroutines.flow.FlowCollector
import luci.sixsixsix.powerampache2.common.Resource

interface ErrorHandler {
    suspend operator fun <T> invoke(
        label:String = "",
        e: Throwable,
        fc: FlowCollector<Resource<T>>,
        onError: (message: String, e: Throwable) -> Unit = { _, _ -> { } }
    )
}
