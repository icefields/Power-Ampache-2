package luci.sixsixsix.powerampache2.domain.errors

class ScrobbleException(musicError: MusicError): MusicException(musicError)
