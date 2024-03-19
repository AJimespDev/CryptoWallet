package com.antonioje.cryptowallet.utils

import android.os.AsyncTask
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Arrays
import java.util.concurrent.ExecutionException
import okhttp3.Response;


class HttpUtil private constructor() {
    fun <T> getResponseData(httpRequest: String, type: Class<Array<T>>): List<T> {
        val httpTask = HttpTask(httpRequest)
        var responseData = "[]"
        try {
            responseData = httpTask.execute().get().toString()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val gson = Gson()
        val data: Array<T> = gson.fromJson(responseData, type)
        return Arrays.asList(*data)
    }

    companion object {
        var instance: HttpUtil? = null
            get() {
                if (field == null) field = HttpUtil()
                return field
            }
            private set
    }

    inner class HttpTask(private val httpRequest: String) :
        AsyncTask<Void?, Void?, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(httpRequest)
                .get()
                .addHeader("x-cg-demo-api-key", "CG-R5hGCa3GHuTpbSF1ibpto3qc")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                return response.body?.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}

