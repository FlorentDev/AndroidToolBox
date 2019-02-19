package com.isen.salou.androidtoolbox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Frida detection", "Start frida detection")
        try {
            val process = Runtime.getRuntime().exec("ps")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val buffer = CharArray(4096)
            val output = StringBuffer()
            var read = reader.read()
            while (read > 0) {
                output.append(buffer, 0, read)
                read = reader.read(buffer)
            }
            reader.close()

            process.waitFor()

            if (output.toString().contains("frida")) {
                Log.i("Frida detection", "Frida Server found!")
                finish()
                System.exit(0)
            }
        }
        catch(e: IOException){
            Log.e("Frida", "Erreur IO")
        }
        catch(e: InterruptedException){
            Log.e("Frida", "Erreur d'excecution")
        }
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        cyclevie.setOnClickListener { startActivity(Intent(this, LifeCycle::class.java)) }
        saved.setOnClickListener { startActivity(Intent(this, SavingActivity::class.java)) }
        permissions.setOnClickListener { startActivity(Intent(this, PhoneInfoActivity::class.java)) }
        webservices.setOnClickListener { startActivity(Intent(this, WebServicesActivity::class.java)) }
        disconnect.setOnClickListener { disconnect() }
    }

    fun disconnect() {
        this.getSharedPreferences("authent", Context.MODE_PRIVATE).edit().clear().apply()
        finish()
    }
}
