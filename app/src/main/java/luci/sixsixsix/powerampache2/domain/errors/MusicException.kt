package luci.sixsixsix.powerampache2.domain.errors

open class MusicException constructor(val musicError: MusicError): Exception(musicError.toString())
