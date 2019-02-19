package com.isen.salou.androidtoolbox


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.isen.salou.androidtoolbox.checkEmulator.DetectEmulator
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var REQUEST_CODE_PERMISSIONS = 100

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /**
         * Code Copié de Jules --------------------------
         */
        val retour: String = FirebaseInstanceId.getInstance().id
        Toast.makeText(this, "id firebase "+retour, Toast.LENGTH_LONG).show()
        /**
         * ---------------------------------------------------------
         */




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
                REQUEST_CODE_PERMISSIONS
            )
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS),
            REQUEST_CODE_PERMISSIONS
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            REQUEST_CODE_PERMISSIONS
        )


        isQEmuEnvDetected()
        Log.i("DetectEmul" ,DetectEmul().toString() )
/*
        if (DetectEmul()) {
            moveTaskToBack(true);
            onDestroy()
        }
        */
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

    fun isQEmuEnvDetected() {

        Log.i("Checking" ,"Checking for QEmu env..." )
        Log.i ("testPierre" , DetectEmulator.isEmulatorCheck1.toString())
        Log.i("hasKnownDeviceId : " ,""+ DetectEmulator.hasKnownDeviceId(applicationContext))
        Log.i("hasKnownPhoneNumber : " ,""+DetectEmulator.hasKnownPhoneNumber(applicationContext))
        Log.i("isOperatorNameAndroid :" ,""+ DetectEmulator.isOperatorNameAndroid(applicationContext))
        Log.i("hasKnownImsi : " , ""+DetectEmulator.hasKnownImsi(applicationContext))
        Log.i("hasEmulatorBuild : ", ""+ DetectEmulator.hasEmulatorBuild(applicationContext))
        Log.i("hasPipes : " ,""+ DetectEmulator.hasPipes())
        Log.i("hasQEmuDriver : ", "" + DetectEmulator.hasQEmuDrivers())
        Log.i("hasQEmuFiles : " ,"" + DetectEmulator.hasQEmuFiles())
        Log.i("hasGenyFiles : ", ""+ DetectEmulator.hasGenyFiles())
        Log.i("hasGenyProps : ", ""+ DetectEmulator.hasQEmuProps(applicationContext))

    }

    fun DetectEmul(): Boolean {

        return (   DetectEmulator.isEmulatorCheck1
                || DetectEmulator.hasKnownDeviceId(applicationContext)
                || DetectEmulator.hasKnownPhoneNumber(applicationContext)
                || DetectEmulator.hasKnownPhoneNumber(applicationContext)
                || DetectEmulator.isOperatorNameAndroid(applicationContext)
                || DetectEmulator.hasKnownImsi(applicationContext)
                || DetectEmulator.hasEmulatorBuild(applicationContext)
                || DetectEmulator.hasPipes()
                || DetectEmulator.hasQEmuDrivers()
                || DetectEmulator.hasQEmuFiles()
                || DetectEmulator.hasGenyFiles()
                || DetectEmulator.hasQEmuProps(applicationContext)
                )

    }


    override fun onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid())
        super.onDestroy()
    }
}

