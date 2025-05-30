package luci.sixsixsix.powerampache2.common

typealias Stack<T> = MutableList<T>

fun <T> MutableList<T>.push(item: T) = add(this.count(), item)
fun <T> MutableList<T>.pop(): T? = if(this.isNotEmpty()) removeAt(this.count() - 1) else null
fun <T> MutableList<T>.peek(): T? = if(this.isNotEmpty()) this[this.count() - 1] else null
fun <T> MutableList<T>.hasMore() = this.isNotEmpty()
