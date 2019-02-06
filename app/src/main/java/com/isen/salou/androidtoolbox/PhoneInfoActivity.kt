package com.isen.salou.androidtoolbox

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_phone_info.*

class PhoneInfoActivity : AppCompatActivity() {

    private val REQUEST_CODE = 11
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 22
    private val PERMISSION_REQUEST_LOCATION = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_info)
        chooseSnap.setOnClickListener { choosePic() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
        setContact()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_LOCATION
        )
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fun locListener() = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location?) {
                val pos =
                    "Lattitude : " + location?.latitude.toString() + "\nLongitude : " + location?.longitude.toString()
                gps.text = pos
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locListener())
    }

    fun setContact() {
        var contactReceive = ArrayList<String>()
        val contactList = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (contactList != null && contactList.moveToNext()) {
            val name =
                contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val number =
                contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            contactReceive.add(name + "\n" + number)
        }
        if (contactList != null)
            contactList.close()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactReceive)
        contact.adapter = adapter
    }

    fun choosePic() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null)
            chooseSnap.setImageBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, data.data))
    }
}
