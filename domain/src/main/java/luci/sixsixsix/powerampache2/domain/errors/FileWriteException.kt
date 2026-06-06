package luci.sixsixsix.powerampache2.domain.errors

import java.io.IOException

class FileWriteException(infoData: String): IOException("FILE-WRITE-EXCEPTION: $infoData")