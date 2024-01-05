package luci.sixsixsix.powerampache2.common

import android.util.Log
import com.google.gson.Gson
import luci.sixsixsix.powerampache2.BuildConfig
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Created by antonio tari
 *
 * Mr.Log, an advanced logger for Android
 * Copyright (C) 2016  Antonio Tari
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
typealias L = MrLog

object MrLog {
    private const val NULL_MESSAGE = "__NULL__"
    private const val EMPTY_MESSAGE = "__EMPTY__"
    private const val SEPARATOR_DEFAULT = " \n "
    private const val TAG = Constants.TAG_LOG
    private const val TRACE_START = 5
    private const val showLogs: Boolean = BuildConfig.MRLOG_ON
    private val stringStackBuilder = StringStackBuilder()

    private val callingMethod: String
        get() = getCallingMethod(TRACE_START)
    enum class LogType { DEBUG, WARNING, ERROR }

    operator fun invoke(vararg messages: Any?) {
        try {
            d(messages.toList())
        } catch (e: Throwable) {
            // in case of errors fallback to printing with string builder
            val sb = StringBuilder()
            messages.forEach {
                sb.append("$it")
                sb.append(" **** ")
            }
            Log.d(TAG, "Mr.Log ERROR. Best effort Log:\n${sb.toString()}")
        }
    }

    private fun tag(tag: String, logType: LogType, strings: List<Any?>) {
        if (!showLogs) return

        val message = stringStackBuilder.buildString(strings.ifEmpty { listOf(NULL_MESSAGE) })
        when (logType) {
            LogType.DEBUG -> Log.d(tag, message)
            LogType.ERROR -> Log.e(tag, message)
            LogType.WARNING -> Log.w(tag, message)
        }
    }

//    fun d(vararg strings: Any?) {
//        if (showLogs) {
//            tag(TAG, LogType.DEBUG, strings.toList())
//        }
//    }

    fun d(messages: List<Any?> = emptyList()) {
        if (showLogs) {
            if(messages.isNotEmpty()) {
                tag(TAG, LogType.DEBUG, messages)
            } else {
                Log.d(TAG, callingMethod)
            }
        }
    }

//    fun w(vararg strings: Any?) {
//        if (showLogs) {
//            //tag(TAG, LogType.WARNING, *strings)
//        }
//    }
//
//    fun e(vararg strings: Any?) {
//        if (showLogs) {
//            //tag(TAG, LogType.ERROR, *strings)
//        }
//    }
//

    fun json(obj: Any?) {
        if (showLogs) {
            d(listOf(Gson().toJson(obj)))
        }
    }

    /**
     * @param e
     * @return
     */
    fun stackTraceToString(e: Throwable?): String? {
        if (e == null) return null

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        return try {
            e.printStackTrace(pw)
            sw.toString() // stack trace as a string
        } catch (ioe: IOException) {
            null
        }.also {
            pw.flush()
            pw.close()
            sw.flush()
            sw.close()
        }
    }

    /**
     * logs the chain of methods before the current
     * @param depth     how deep to go in the stack trace
     */
    @JvmOverloads
    fun trace(depth: Int = 11) {
        if (!showLogs) return
        var MAX_CALLS = TRACE_START + depth
        val elements = Thread.currentThread().stackTrace
        if (elements.size < MAX_CALLS) {
            MAX_CALLS = elements.size
        }
        val stringBuilder = StringBuilder()
        for (i in TRACE_START until MAX_CALLS) {
            stringBuilder.append(getCallingMethod(i))
            stringBuilder.append(" \n")
        }
        Log.w(TAG, stringBuilder.toString())
    }

    private fun getCallingMethod(depth: Int): String {
        val actualDepth = if (depth < Thread.currentThread().stackTrace.size) {
            depth
        } else {
            Thread.currentThread().stackTrace.size - 1
        }
        val element = Thread.currentThread().stackTrace[actualDepth]
        val stringBuilder = StringBuilder()
        stringBuilder.append("(")
        stringBuilder.append(element.lineNumber)
        stringBuilder.append(") ")
        val classNames = element.className
            .split("\\.".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        stringBuilder.append(classNames[classNames.size - 1])
        stringBuilder.append(".")
        stringBuilder.append(element.methodName)
        return stringBuilder.toString()
    }

    /**
     *
     */
    private class StringStackBuilder {
        /**
         * @param sa
         * @param separator
         * @return
         */
        /**
         * @param sa
         * @return
         */
        @JvmOverloads
        fun buildString(messages: List<Any?>, separator: String = SEPARATOR_DEFAULT): String {
            val sb = StringBuilder()
            messages.forEachIndexed { i, elem ->
                sb.append(checkString(elem))
                if (i < messages.size - 1) {
                    sb.append(separator)
                }
            }

            val stackTrace = calcStackLine(
                ignoreClasses = listOf(
                    Log::class.java.name,
                    StringStackBuilder::class.java.name,
                    MrLog::class.java.name,
                    L::class.java.name
                )
            )
            return stackTrace + "\t" + sb.toString()
        }

        /**
         * @param ignoreClasses List of class names to ignore.
         */
        private fun calcStackLine(ignoreClasses: List<String>): String {
            val line = Throwable().stackTrace
                .filter { !ignoreClasses.contains(it.className) }[0]

            // add class name
            val stringBuilder = StringBuilder()
            val periodIndex = line.className.lastIndexOf(".")
            if (periodIndex != -1) {
                stringBuilder.append(
                    line.className
                        .substring(periodIndex + 1)
                        .replace("$1", "")
                        .replace("$", ".")
                )
            }

            if (!listOf("invoke", "emit", "invokeSuspend").contains(line.methodName)) {
                stringBuilder.append(".").append(line.methodName)
            }

            val lineNumber = line.lineNumber
            if (lineNumber >= 0) {
                // Note: getLineNumber returns a negative number if the line number is unknown.
                stringBuilder.append("($lineNumber)")
            }
            return stringBuilder.toString()
        }

        /**
         * @param message
         * @return
         */
        private fun checkString(message: Any?): String = try {
            if (message == null) {
                NULL_MESSAGE
            } else if (message is Iterable<*>) {
                val sb = StringBuilder("[")
                val flavoursIter: Iterator<Any?> = message.iterator()
                if (flavoursIter.hasNext()) {
                    sb.append(checkString(flavoursIter.next()))
                }
                while (flavoursIter.hasNext()) {
                    sb.append(", ")
                    sb.append(checkString(flavoursIter.next()))
                }
                sb.append("]")
                checkString(sb.toString())
                //return checkString(ToStringBuilder.reflectionToString(str, ToStringStyle.MULTI_LINE_STYLE));
            } else if (message is Exception) {
                val sb = StringBuilder("EXCEPTION ON CLASS: ")
                sb.append(message.javaClass.simpleName)
                sb.append(", Exception ")
                when (message) {
                    is JSONException -> sb.append("JSONException ")
                    is FileNotFoundException -> sb.append("FileNotFoundException ")
                    is IOException -> sb.append("IOException ")
                    is NullPointerException -> sb.append("NullPointerException ")
                }
                sb.append(" : ")
                sb.append(checkString(message.localizedMessage))
                sb.append(SEPARATOR_DEFAULT)
                sb.append(stackTraceToString(message as Exception?))
                checkString(sb.toString())
                //				return checkString("EXCEPTION ON CLASS: "+str.getClass().getSimpleName()+", exception:")+
                //						checkString(((Exception)str).getLocalizedMessage());
            } else if (message is Throwable) {
                //String thStr = ((Throwable)str).getLocalizedMessage();
                checkString(stackTraceToString(message as Throwable?))
            } else if (message is String && (message.isBlank() || message.toString().isBlank())) {
                EMPTY_MESSAGE
            } else {
                message.toString()
            }
        } catch (d: Exception) {
            "Cannot log, Exception: " + checkString(d.localizedMessage)
        }
    }
}
