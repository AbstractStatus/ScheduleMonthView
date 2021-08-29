package com.abstractstatus.schedulemonthview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.abstractstatus.viewlibrary.util.LunarUtil

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LunarUtil.init(this)
//        Log.d(TAG, "onCreate: ${LunarUtil.getLunarText(2021,9,4)}")
    }
}