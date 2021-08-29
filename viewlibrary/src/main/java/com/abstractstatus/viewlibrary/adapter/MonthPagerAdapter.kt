package com.abstractstatus.viewlibrary.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.abstractstatus.viewlibrary.view.ScheduleListView


/**
 ** Created by AbstractStatus at 2021/8/22 10:06.
 */
class MonthPagerAdapter(private val context: Context) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 3000
    }



}