package com.isen.salou.androidtoolbox

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onResume() {
        super.onResume()
        val authent = this.getSharedPreferences("authent", Context.MODE_PRIVATE)
        if (authent.contains("Username") && authent.contains("Pass")) {
            checkAlready(authent)
        }
        connect.setOnClickListener { this.checkUser() }
    }

    private fun showUser() {
        Toast.makeText(this, username.text, Toast.LENGTH_LONG).show()
    }

    private fun checkUser() {
        var user = username.text.toString()
        var pass = password.text.toString()
        if (user == "admin" && pass == "123") {
            Toast.makeText(this, "Authent success", Toast.LENGTH_LONG).show()
            val setting = this.getSharedPreferences("authent", Context.MODE_PRIVATE).edit()
            setting.putString("Username", user)
            setting.putString("Pass", pass)
            setting.apply()
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            Toast.makeText(this, "Wrong user or pass", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkAlready(authent: SharedPreferences) {
        if (authent.getString("Username", "") == "admin" && authent.getString("Pass", "") == "123") {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}
