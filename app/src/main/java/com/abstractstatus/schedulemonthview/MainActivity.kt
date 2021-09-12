package com.abstractstatus.schedulemonthview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.abstractstatus.schedulemonthview.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    companion object{
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.monthItemView.onDaySelect = {date, posOfMonth ->
            Log.d(TAG, "onCreate: $date + $posOfMonth")
        }

        binding.monthItemView.onJustOpenStart = {
            Log.d(TAG, "onJustOpenStart ")
        }
        binding.monthItemView.onJustOpenEnd = {
            Log.d(TAG, "onJustOpenEnd ")
        }
        binding.monthItemView.onJustCloseStart = {
            Log.d(TAG, "onJustCloseStart ")
        }
        binding.monthItemView.onJustCloseEnd = {
            Log.d(TAG, "onJustCloseEnd ")
        }
        binding.monthItemView.onUnjustCloseStart = {
            Log.d(TAG, "onUnjustCloseStart ")
        }
        binding.monthItemView.onUnjustCloseEnd = {
            Log.d(TAG, "onUnjustCloseEnd ")
        }
        binding.monthItemView.onUnjustOpenStart = {
            Log.d(TAG, "onUnjustOpenStart ")
        }
        binding.monthItemView.onUnjustOpenEnd = {
            Log.d(TAG, "onUnjustOpenEnd ")
        }

        binding.asScheduleMonthView.onItemSelect = {date, posOfMonth ->
            Log.d(TAG, "onCreate: $date + $posOfMonth")
        }

        binding.asScheduleMonthView.onMonthChange = {year, month, pos ->
            Log.d(TAG, "onCreate: $year + $month + $pos")
        }

        binding.asScheduleMonthView.onNoneScheduleClick = {
            Log.d(TAG, "onCreate: onNoneScheduleClick")
        }

        binding.asScheduleMonthView.onScheduleItemClick = {scheduleId, scheduleType ->
            Log.d(TAG, "onCreate: $scheduleId + $scheduleType")
        }

    }
}