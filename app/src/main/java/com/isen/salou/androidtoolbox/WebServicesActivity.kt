package com.isen.salou.androidtoolbox

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_web_services.*

class WebServicesActivity : AppCompatActivity() {
    val adapter = ArrayList<String>()
    var contacts: Contact? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_services)
        WebServiceTask(object : WebServiceInterface {
            override var data: String = ""
            override fun setSuccess() {
                val result = Gson()
                contacts = result.fromJson<Contact>(data, Contact::class.java)
                if (contacts != null) {
                    for (contact in contacts!!.results)
                        adapter.add(contact.name.title + " " + contact.name.first + " " + contact.name.last + "\n" + contact.cell + "\n" + contact.email)
                }
                setContact()
            }
        }).execute()
    }

    fun setContact() {
        if (contacts != null)
            contactView.layoutManager = LinearLayoutManager(this)
        contactView.adapter = RandomContactAdaptater(contacts!!)
//        contactView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, adapter)
    }
}
