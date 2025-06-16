package luci.sixsixsix.powerampache2.domain.errors

class NullDataException(infoData: String): NullPointerException("NULL-DATA-EXCEPTION: $infoData")
