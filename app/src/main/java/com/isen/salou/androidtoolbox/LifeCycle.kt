package com.isen.salou.androidtoolbox

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_life_cycle.*

class LifeCycle : AppCompatActivity() {
    val frag1 = LifeCycleFragment()
    val frag2 = LifeCycleFragment2()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_cycle)
        supportFragmentManager.beginTransaction().add(R.id.fragment, frag1).commit()
        changefrag.setOnClickListener { changeFragment() }
        showLog("Create")
    }

    var frag = true

    fun changeFragment() {
        if (frag) {
            supportFragmentManager.beginTransaction().remove(frag1).commit()
            supportFragmentManager.beginTransaction().add(R.id.fragment, frag2).commit()
        } else {
            supportFragmentManager.beginTransaction().remove(frag2).commit()
            supportFragmentManager.beginTransaction().add(R.id.fragment, frag1).commit()
        }
        frag = !frag
    }
    // Récupérer une instance de la class du fragment
    // Commencer une transaction en ajoutant le 1e fragment
    // (instance + lien vers l'id du fragment au sein de l'agtivité)
    // Lancer la transaction

    // à l'aide d'un bouton ramplacer le fragement 1 par le fragment 2

    override fun onStart() {
        super.onStart()
        showLog("Start")
    }

    override fun onResume() {
        super.onResume()
        showLog("Resume")
    }

    override fun onPause() {
        super.onPause()
        showLog("Pause")
    }

    override fun onStop() {
        super.onStop()
        showLog("Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Cycle de vie ", "destroy")
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()
        showLog("Restart")
    }

    private fun showLog(message: String) {
        val msg = showcycle.text.toString() + message + "\n"
        showcycle.text = msg
        Log.i("Cycle de vie ", message)
    }
}
