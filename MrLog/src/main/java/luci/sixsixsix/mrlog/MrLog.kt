/**
 * Created by Antonio Tari
 *
 * Mr.Log, an advanced logger for Android
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.mrlog

import android.util.Log
import com.google.gson.Gson
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

typealias L = MrLog

enum class LogType { DEBUG, WARNING, ERROR }

object MrLog: MrLogI {
    private val stringStackBuilder = StringStackBuilder()
    private val callingMethod: String
        get() = stringStackBuilder.getCallingMethod(TRACE_START)

    override operator fun invoke(vararg messages: Any?) =
        tag(tag = TAG, logType = LogType.DEBUG, messages = messages.toList())

    override fun d(vararg strings: Any?) = tag(TAG, LogType.DEBUG, strings.toList())
    override fun w(vararg strings: Any?) = tag(TAG, LogType.WARNING, strings.toList())
    override fun e(vararg strings: Any?) = tag(TAG, LogType.ERROR, strings.toList())
    override fun json(obj: Any?) = tag(TAG, LogType.DEBUG, listOf(Gson().toJson(obj)))

    private fun tag(tag: String, logType: LogType, messages: List<Any?>) {
        if (!LOGS_ON) return

        val message = try {
            stringStackBuilder.buildString(messages
                .ifEmpty { listOf(callingMethod) } // if no message just print the calling method
            )
        } catch (e: Throwable) {
            // in case of errors FALLBACK to printing with string builder
            val sb = StringBuilder()
            messages.forEach {
                sb.append("$it")
                sb.append(SEPARATOR_DEFAULT)
            }
            "Mr.Log ERROR logging. Best effort Log:\n${sb.toString()}"
        }

        when (logType) {
            LogType.DEBUG -> Log.d(tag, message)
            LogType.ERROR -> Log.e(tag, message)
            LogType.WARNING -> Log.w(tag, message)
        }
    }
}

/**
 *
 */
private class StringStackBuilder {
    // classes to ignore when calculating the stack trace
    private val ignoredClasses = listOf(
        Log::class.java.name,
        StringStackBuilder::class.java.name,
        MrLog::class.java.name,
        L::class.java.name
    )
    // methods to ignore when calculating the stack trace
    private val ignoredMethods = listOf("invoke", "emit", "invokeSuspend")

    fun buildString(messages: List<Any?>, separator: String = SEPARATOR_DEFAULT): String =
        StringBuilder(calcStackLine(ignoreClasses = ignoredClasses))
            .append("\t")
            .apply {
                messages.forEachIndexed { i, elem ->
                    append(checkString(elem))
                    if (i < messages.size - 1) {
                        append(separator)
                    }
                }
            }
            .toString()

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

        if (!ignoredMethods.contains(line.methodName)) {
            stringBuilder.append(".").append(line.methodName)
        }

        val lineNumber = line.lineNumber
        if (lineNumber >= 0) {
            // Note: getLineNumber returns a negative number if the line number is unknown.
            stringBuilder.append("($lineNumber)")
        }
        return stringBuilder.toString()
    }

    fun stackTraceToString(e: Throwable?): String? = e?.let { throwable ->
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        try {
            throwable.printStackTrace(pw)
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
     * @param message
     * @return
     */
    private fun checkString(message: Any?): String = try {
        when(message) {
            null -> NULL_MESSAGE

            is Iterable<*> -> {
                val sb = StringBuilder("[")
                val iterator: Iterator<Any?> = message.iterator()
                if (iterator.hasNext()) {
                    sb.append(checkString(iterator.next()))
                }
                while (iterator.hasNext()) {
                    sb.append(", ")
                    sb.append(checkString(iterator.next()))
                }
                sb.append("]")
                checkString(sb.toString())
            }

            is Exception -> {
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
            }

            is Throwable -> {
                checkString(stackTraceToString(message as Throwable?))
            }

            else -> {
                if (message is String && (message.isBlank() || message.toString().isBlank())) {
                    EMPTY_MESSAGE
                } else {
                    message.toString()
                }
            }
        }
    } catch (d: Exception) {
        "Mr.Log - Cannot log, Exception: " + checkString(d.localizedMessage)
    }

    fun getCallingMethod(depth: Int): String {
        val stackTrace = Thread.currentThread().stackTrace.filter { !ignoredClasses.contains(it.className) }
        val actualDepth = if (depth < stackTrace.size) { depth } else { stackTrace.size - 1 }
        val stringBuilder = StringBuilder()
        val element = stackTrace[actualDepth]
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
     * logs the chain of methods before the current
     * @param depth     how deep to go in the stack trace
     */
    fun trace(depth: Int = 11) {
        if (!LOGS_ON) return
        var maxCalls = TRACE_START + depth
        val elements = Thread.currentThread().stackTrace
        if (elements.size < maxCalls) {
            maxCalls = elements.size
        }
        val stringBuilder = StringBuilder()
        for (i in TRACE_START until maxCalls) {
            stringBuilder.append(getCallingMethod(i))
            stringBuilder.append(" \n")
        }
        Log.w(TAG, stringBuilder.toString())
    }
}
