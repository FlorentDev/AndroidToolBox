package com.isen.salou.androidtoolbox


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_life_cycle.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class LifeCycleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_life_cycle, container, false)
    }

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
        Log.i("Cycle de vie fragment ", "destroy")
    }

    private fun showLog(message: String) {
        val msg = showcyclefrag.text.toString() + message + "\n"
        showcyclefrag.text = msg
        Log.i("Cycle de vie fragment ", message)
    }
}
