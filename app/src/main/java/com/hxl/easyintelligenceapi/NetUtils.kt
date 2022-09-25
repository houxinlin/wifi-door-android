package com.hxl.easyintelligenceapi

import android.os.Handler
import android.os.Looper
import java.lang.Exception
import java.net.URL

object NetUtils {
    private val handler: Handler = Handler(Looper.myLooper()!!)
    fun sendGet(url: String, callback: (String?) -> Unit) {
        createThread {
            val result =send(url)
            handler.post { callback(result) }
        }
    }

    private fun createThread(runnable: Runnable) {
        Thread(runnable).start()
    }

    private fun send(url: String): String? {
        try {
            return URL(url).openConnection().getInputStream().bufferedReader().readText()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}