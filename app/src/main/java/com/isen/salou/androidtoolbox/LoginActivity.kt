package com.isen.salou.androidtoolbox


import android.Manifest
import android.annotation.SuppressLint

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Base64

import android.support.v4.app.ActivityCompat


import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.isen.salou.androidtoolbox.checkEmulator.DetectEmulator
import kotlinx.android.synthetic.main.activity_login.*
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginActivity : AppCompatActivity() {

    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private val KEY_NAME = "example_key"
    private var cipher: Cipher? = null


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
        checkAlreadyConnect()
        connect.setOnClickListener { this.checkUser() }
        reset_password.setOnClickListener { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.resetPassword()
        }
        }
    }

    private fun showUser() {
        Toast.makeText(this, username.text, Toast.LENGTH_LONG).show()
    }

    private fun checkUser() {
        val authent = this.getSharedPreferences("authent", Context.MODE_PRIVATE)
        Log.i("test",authent.toString())
        if (authent.contains("username_env") && authent.contains("username_iv") && authent.contains("password_env") && authent.contains("password_iv")) {
            if(authent.contains("is_connect") && authent.getBoolean("is_connect",false)){
                Log.i("account", "is connected")
                startActivity(Intent(this, HomeActivity::class.java))
            }else{
                Log.i("account","is not connected")
                Log.i("formulaire", username.text.toString())
                Log.i("formulaire", password.text.toString())

                Log.i("account", authent.getString("username_env",""))
                Log.i("accoount", authent.getString("username_iv",""))

                Log.i("account", authent.getString("password_env",""))
                Log.i("account", authent.getString("password_iv",""))

                val username_decryptor = DeCryptor()
                val password_decryptor = DeCryptor()
                val username_uncrypt = username_decryptor.decryptData("username",Base64.decode(authent.getString("username_env",""),Base64.DEFAULT),Base64.decode(authent.getString("username_iv",""), Base64.DEFAULT))
                val password_uncrypt = password_decryptor.decryptData("password", Base64.decode(authent.getString("password_env",""),Base64.DEFAULT),Base64.decode(authent.getString("password_iv",""),Base64.DEFAULT))
                if(username_uncrypt == username.text.toString() && password_uncrypt == password.text.toString()){
                    val setting = authent.edit()
                    setting.putBoolean("is_connect", true)
                    setting.apply()
                    startActivity(Intent(this, HomeActivity::class.java))
                    Log.i("account", "Valid")
                }else{
                    this.getSharedPreferences("authent", Context.MODE_PRIVATE).edit().remove("is_connect").apply()
                    Log.i("account", "No valid")
                }
            }
        } else {
            Log.i("Acount", "Create account")
            val username_encryptor = EnCryptor()
            val password_encryptor = EnCryptor()

            username_encryptor.encryptText("username",username.text.toString())
            Log.i("KEY",username_encryptor.encryption.toString())
            Log.i("KEY", username_encryptor.iv.toString())
            password_encryptor.encryptText("password",password.text.toString())
            Log.i("KEY",password_encryptor.encryption.toString())
            Log.i("KEY", password_encryptor.iv.toString())

            Toast.makeText(this, "Set account", Toast.LENGTH_LONG).show()
            val setting = authent.edit()

            setting.putString("username_env", Base64.encodeToString(username_encryptor.encryption, Base64.DEFAULT))
            setting.putString("username_iv", Base64.encodeToString(username_encryptor.iv, Base64.DEFAULT))
            setting.putString("password_env", Base64.encodeToString(password_encryptor.encryption, Base64.DEFAULT))
            setting.putString("password_iv", Base64.encodeToString(password_encryptor.iv, Base64.DEFAULT))
            setting.putBoolean("is_connect", true)
            setting.apply()
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun resetPassword(){
        Log.i("account","reset account")
        this.getSharedPreferences("authent", Context.MODE_PRIVATE).edit().clear().apply()
        Toast.makeText(this, "Reset account", Toast.LENGTH_LONG).show()
    }

    private fun checkAlreadyConnect() {
        val authent = this.getSharedPreferences("authent", Context.MODE_PRIVATE)
        Log.i("test",authent.toString())
        if (authent.contains("username_env") && authent.contains("username_iv") && authent.contains("password_env") && authent.contains("password_iv")) {
            if(authent.contains("is_connect") && authent.getBoolean("is_connect",false)){
                Log.i("account", "is connected")
                startActivity(Intent(this, HomeActivity::class.java))
            }else{
                Log.i("account","is not connected")
                Log.i("formulaire", username.text.toString())
                Log.i("formulaire", password.text.toString())

                Log.i("account", authent.getString("username_env",""))
                Log.i("accoount", authent.getString("username_iv",""))

                Log.i("account", authent.getString("password_env",""))
                Log.i("account", authent.getString("password_iv",""))

                val username_decryptor = DeCryptor()
                val password_decryptor = DeCryptor()
                val username_uncrypt = username_decryptor.decryptData("username",Base64.decode(authent.getString("username_env",""),Base64.DEFAULT),Base64.decode(authent.getString("username_iv",""), Base64.DEFAULT))
                val password_uncrypt = password_decryptor.decryptData("password", Base64.decode(authent.getString("password_env",""),Base64.DEFAULT),Base64.decode(authent.getString("password_iv",""),Base64.DEFAULT))
                if(username_uncrypt == username.text.toString() && password_uncrypt == password.text.toString()){
                    val setting = authent.edit()
                    setting.putBoolean("is_connect", true)
                    setting.apply()
                    startActivity(Intent(this, HomeActivity::class.java))
                    Log.i("account", "Valid")
                }else{
                    this.getSharedPreferences("authent", Context.MODE_PRIVATE).edit().remove("is_connect").apply()
                    Log.i("account", "No valid")
                }
            }
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

