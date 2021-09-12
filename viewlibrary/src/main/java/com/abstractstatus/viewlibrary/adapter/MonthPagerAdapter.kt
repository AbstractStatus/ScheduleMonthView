package com.abstractstatus.viewlibrary.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.abstractstatus.viewlibrary.util.CalendarUtil.getAddYearAndMonth
import com.abstractstatus.viewlibrary.util.CalendarUtil.getNowYearAndMonth
import com.abstractstatus.viewlibrary.util.MonthViewDelegate
import com.abstractstatus.viewlibrary.view.MonthItemView


/**
 ** Created by AbstractStatus at 2021/8/22 10:06.
 */
class MonthPagerAdapter(private val context: Context) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return MonthViewDelegate.maxYearCount
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = MonthItemView(context)

        view.monthPosition = position
        view.tag = position

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}