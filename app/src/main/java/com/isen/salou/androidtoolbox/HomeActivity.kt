package com.isen.salou.androidtoolbox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
