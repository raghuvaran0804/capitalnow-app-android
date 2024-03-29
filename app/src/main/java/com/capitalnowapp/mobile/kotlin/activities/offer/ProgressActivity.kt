package com.capitalnowapp.mobile.kotlin.activities.offer

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.databinding.ActivityProgressBinding
import com.capitalnowapp.mobile.util.NetworkConnectionDetector

class ProgressActivity : BaseActivity() {
    private var binding: ActivityProgressBinding? = null
    //private var ha: Handler? = null
    private var mRunnable: Runnable? = null
    private var mHandler = Handler()
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProgressBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        activity = this
        initView()

        /*var my_runnable = Runnable {
            // your code here
            getApplyLoanDataBase()

        }

        var handler =
            Handler() // use 'new Handler(Looper.getMainLooper());' if you want this handler to control something in the UI

        // to start the handler
        fun start() {
            handler.postDelayed(my_runnable, 30000)
        }

        // to stop the handler
        fun stop() {
            handler.removeCallbacks(my_runnable)
        }

        // to reset the handler
        fun restart() {
            handler.removeCallbacks(my_runnable)
            handler.postDelayed(my_runnable, 10000)
        }*/
    }

    private fun initView() {
         /*ha = Handler()
        ha!!.postDelayed(object : Runnable {
            override fun run() {
                getApplyLoanDataBase()
                    ha!!.postDelayed(this, 30000)
            }
        }, 30000)*/

        mRunnable = Runnable {
            mHandler.removeCallbacks(mRunnable!!)
            getApplyLoanDataBase(true)
        }
        if (NetworkConnectionDetector(activityContext).isNetworkConnected) {
            mHandler.postDelayed(mRunnable!!, 30000)
        }
    }

}