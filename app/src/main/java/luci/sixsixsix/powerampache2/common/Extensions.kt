package luci.sixsixsix.powerampache2.common

import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import java.lang.StringBuilder
import java.security.MessageDigest

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

/**
 * TODO DEBUG
 * this function uses reflection to quickly turn any of the music object into a String to quickly
 * print and visualize data
 */
fun Any.toDebugString(): String {
    val album = this
    val sb = StringBuilder()
    for (field in album.javaClass.declaredFields) {
        field.isAccessible = true

        field.get(album)?.let {
            if(
                !field.name.lowercase().contains("url") &&
                !field.name.lowercase().contains("artist") &&
                !field.name.lowercase().contains("CREATOR") &&
                !field.name.lowercase().contains("\$stable") &&
                "$it".isNotBlank() &&
                "$it" != "0" &&
                !"$it".contains("CREATOR") &&
                !"$it".contains("\$stable") &&
                "$it" != "[]"
            ) {
                if (it is List<*>) {
                    if (field.name != "genre") {
                        sb.append(field.name)
                            .append(": ")
                    } else {
                        sb.append(" | ")
                    }

                    it.forEach { listElem ->
                        listElem?.let {
                            if (listElem is MusicAttribute) {
                                sb.append(listElem.name)
                                sb.append(" | ")
                            }
                        }
                    }
                    sb.append("\n")


                }
                else if (it is MusicAttribute) {
                    sb.append(field.name)
                        .append(": ")
                        .append("${it.name}")
                        .append("\n")
                } else {
                    sb.append(field.name)
                        .append(": ")
                        .append("${field.get(album)}")
                        .append("\n")
                }
            }
        }
    }
    // remove variables that are auto generate by the parcelable
    return sb.toString().split("CREATOR")[0]
}

@Composable
@ReadOnlyComposable
fun fontDimensionResource(@DimenRes id: Int) = dimensionResource(id = id).value.sp

fun processFlag(flag: Any?): Int {
    return flag?.let {
        if (it is Boolean) {
            if (it == true) {
                1
            } else {
                0
            }
        } else if (it is Int) {
            it
        } else 0
    } ?: 0
}
