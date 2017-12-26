package nl.mpcjanssen.simpletask

import android.os.Handler
import android.os.Looper
import android.util.Log
import nl.mpcjanssen.simpletask.dao.gen.LogItem
import nl.mpcjanssen.simpletask.dao.gen.LogItemDao
import java.util.*

/*
 * Small logging wrapper to be able to swap loggers without
 * changing any code.
 */

object Logger : Thread() {
    private var dao: LogItemDao? = null
    private var mHandler: Handler? = null
    init {
        start()
    }
    override fun run(): Unit {
        Looper.prepare()
        mHandler = Handler() // the Handler hooks up to the current Thread
        Looper.loop()
    }

    fun setDao (dao: LogItemDao) {
        this.dao = dao
    }

    fun logInDB(severity: String, tag: String, s: String, throwable: Throwable? = null) {
        mHandler?.post {
            var throwableMessage: String = ""
            throwable?.let {
                throwableMessage = Log.getStackTraceString(throwable)
            }
            val item = LogItem(null, Date(), severity, tag, s, throwableMessage)
            dao?.insert(item)
        }
    }

    fun error(tag: String, s: String) {
        Log.e(tag, s)
        logInDB("e", tag, s)
    }
    fun warn(tag: String, s: String) {
        Log.w(tag, s)
        logInDB("w", tag, s)
    }

    fun info(tag: String, s: String) {
        Log.i(tag, s)
        logInDB("i", tag, s)
    }

    fun debug(tag: String, s: String) {
        Log.d(tag, s)
        logInDB("d", tag, s)
    }

    fun error(tag: String, s: String, throwable: Throwable) {
        Log.e(tag, s, throwable)
        logInDB("e", tag, s, throwable)
    }

    fun warn(tag: String, s: String, throwable: Throwable) {
        Log.w(tag, s, throwable)
        logInDB("w", tag, s, throwable)
    }

    fun info(tag: String, s: String, ex: Throwable) {
        Log.i(tag, s, ex)
        logInDB("i", tag, s, ex)
    }

    fun debug(tag: String, s: String, ex: Throwable) {
        Log.d(tag, s, ex)
    }
}
