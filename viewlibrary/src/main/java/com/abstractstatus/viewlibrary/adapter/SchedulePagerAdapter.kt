package com.abstractstatus.viewlibrary.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.abstractstatus.viewlibrary.view.ScheduleListView

/**
 ** Created by AbstractStatus at 2021/8/22 10:05.
 */
class SchedulePagerAdapter(private val context: Context) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 7
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = ScheduleListView(context)
        view.tag = position
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }
}