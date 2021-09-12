package com.abstractstatus.viewlibrary

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.abstractstatus.viewlibrary.adapter.MonthPagerAdapter
import com.abstractstatus.viewlibrary.adapter.SchedulePagerAdapter
import com.abstractstatus.viewlibrary.util.CalendarUtil
import com.abstractstatus.viewlibrary.util.MonthViewDelegate
import com.abstractstatus.viewlibrary.view.MonthItemView
import com.abstractstatus.viewlibrary.view.ScheduleView

/**
 ** Created by AbstractStatus at 2021/8/21 21:38.
 */
class AsScheduleMonthView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "AsScheduleMonthView"
    }

    private var monthItemView: MonthItemView? = null
    private var scheduleView: ScheduleView? = null

    private val vpMonth by lazy { findViewById<ViewPager>(R.id.vp_month) }
    private val vpSchedule by lazy { findViewById<ViewPager>(R.id.vp_schedule)}

    init{
        LayoutInflater.from(context)
                .inflate(
                        R.layout.month_all,
                        this
                )

        vpMonth.adapter = MonthPagerAdapter(context)
        vpMonth.currentItem = MonthViewDelegate.startMonthPos
        vpMonth.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                monthItemView = vpMonth.findViewWithTag(position)
                monthItemView?.onDaySelect = onItemSelect
                onMonthChange?.let {
                    it(
                            CalendarUtil.getAddYearAndMonth(
                                    CalendarUtil.getCurTime()[0],
                                    CalendarUtil.getCurTime()[1],
                                    position - MonthViewDelegate.startMonthPos)[0],
                            CalendarUtil.getAddYearAndMonth(
                                    CalendarUtil.getCurTime()[0],
                                    CalendarUtil.getCurTime()[1],
                                    position - MonthViewDelegate.startMonthPos)[1],
                            position
                    )
                }
            }
        })

        vpSchedule.adapter = SchedulePagerAdapter(context)
        vpSchedule.currentItem = MonthViewDelegate.startDayOfWeek
        vpSchedule.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }
        })

    }



    //对外公开接口
    var onItemSelect: ((date: IntArray, posOfMonth: Int) -> Unit)? = null
    var onMonthChange : ((year: Int, month: Int, pos: Int) -> Unit)? = null
    var onNoneScheduleClick: (() -> Unit)? = null
    var onScheduleItemClick: ((scheduleId: String, scheduleType: Int) -> Unit)? = null

    fun <T> setAdapterEntityData(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            entityClass: Class<T>,
            data: T){

    }
}