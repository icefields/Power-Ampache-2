package luci.sixsixsix.powerampache2.domain.errors

class MusicException constructor(val musicError: MusicError): Exception(musicError.toString())