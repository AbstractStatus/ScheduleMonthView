package com.abstractstatus.viewlibrary.view

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractstatus.viewlibrary.R
import com.abstractstatus.viewlibrary.adapter.ScheduleListAdapter
import com.abstractstatus.viewlibrary.adapter.ScheduleListAdapter.OnScheduleItemClickListener
import com.abstractstatus.viewlibrary.entity.ScheduleEntity


/**
 ** Created by AbstractStatus at 2021/8/22 10:08.
 */
class ScheduleListView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var schedule_none: ScrollView? = null
    private var iv_schedule_none: ImageView? = null
    private var tv_schedule_none: TextView? = null
    private var schedule_list: LinearLayout? = null
    private var rv_schedule: RecyclerView? = null
    private var scheduleListAdapter: ScheduleListAdapter? = null
    private var scheduleEntityList: List<ScheduleEntity>? = null

    init {
        LayoutInflater.from(context).inflate(
                R.layout.month_schedule_list,
                this
        )
        schedule_none = findViewById(R.id.schedule_none)
        iv_schedule_none = findViewById(R.id.iv_schedule_none)
        tv_schedule_none = findViewById(R.id.tv_schedule_none)
        schedule_list = findViewById(R.id.schedule_list)
        rv_schedule = findViewById(R.id.rv_schedule)
        val spannableString = SpannableString("当前没有日程，创建一个吧~")
        spannableString.setSpan(ForegroundColorSpan(-0x66ff01), 7, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        tv_schedule_none!!.text = spannableString
        setClickListener()
        setAdapter(context)
    }


    private fun setClickListener() {
        tv_schedule_none?.setOnClickListener {


        }
    }


    private fun setAdapter(context: Context) {

        rv_schedule?.adapter = scheduleListAdapter
        rv_schedule?.layoutManager = LinearLayoutManager(context)
    }


    fun setScheduleEntityList(scheduleEntityList: List<ScheduleEntity>?) {
        this.scheduleEntityList = scheduleEntityList
        if (scheduleEntityList == null || scheduleEntityList.isEmpty()) {
            setNoneView()
            return
        }
        setListView()

        scheduleListAdapter?.notifyDataSetChanged()
    }


    fun setNoneView() {
        schedule_none?.visibility = View.VISIBLE
        schedule_list?.visibility = View.GONE
    }


    fun setListView() {
        schedule_none?.visibility = View.GONE
        schedule_list?.visibility = View.VISIBLE
    }
}