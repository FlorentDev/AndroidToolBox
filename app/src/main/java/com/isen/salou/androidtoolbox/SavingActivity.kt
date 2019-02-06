package com.isen.salou.androidtoolbox

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_saving.*
import org.json.JSONObject
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class SavingActivity : AppCompatActivity() {

    val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)
        save.setOnClickListener { saved() }
        read.setOnClickListener { reading() }

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        date.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@SavingActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
    }

    override fun onStart() {
        super.onResume()
        try {
            val file = openFileInput("data.json")
            var data = JSONObject(file.readBytes().toString(Charset.defaultCharset()))
            firstname.setText(data.get("firstname").toString())
            name.setText(data.get("name").toString())
            date.setText(data.get("birthday").toString())
        } catch (e: FileNotFoundException) {
            Log.v("Sauvegarde", "Pas de fichier")
        }
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        date.text = sdf.format(cal.getTime())
    }

    fun saved() {
        val data = JSONObject()
        data.put("firstname", firstname.text)
        data.put("name", name.text)
        data.put("birthday", date.text)
        val file = openFileOutput("data.json", Context.MODE_PRIVATE)
        file.write(data.toString().toByteArray())
    }

    fun reading() {
        var firstname = ""
        var name = ""
        var birthday = ""
        var age = 0
        try {
            val file = openFileInput("data.json")
            val data = JSONObject(file.readBytes().toString(Charset.defaultCharset()))
            firstname = data.get("firstname").toString()
            name = data.get("name").toString()
            birthday = data.get("birthday").toString()

            val birth = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).parse(date.text.toString())
            age = ((cal.time.time - birth.time) / 1000).toInt()
            age = age / 31557600
        } catch (e: FileNotFoundException) {
            Log.v("Sauvegarde", "Pas de fichier")
        }

        val text = "Prenom : " + firstname + "\n" +
                "Nom : " + name + "\n" +
                "Date de naissance : " + birthday + "\n" +
                "Age : " + age
        AlertDialog.Builder(this).setMessage(text).setTitle("Données du fichier").show()
    }
}
