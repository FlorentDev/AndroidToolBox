package com.isen.salou.androidtoolbox

import android.os.AsyncTask
import android.util.Log
import java.net.URL

class WebServiceTask(data: WebServiceInterface) : AsyncTask<Int, String, String?>() {
    val transmit: WebServiceInterface = data
    var content: String = ""
    override fun doInBackground(vararg params: Int?): String? {
        val connection = URL("https://randomuser.me/api/?results=40&nat=fr").openConnection()
        try {
            content = connection.getInputStream().bufferedReader().readLine()
        } catch (e: Exception) {
            Log.e("Connection", "Connection error")
        }
        return content
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        transmit.data = content
        transmit.setSuccess()
    }
}