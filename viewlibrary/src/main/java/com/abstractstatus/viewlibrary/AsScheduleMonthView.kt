package com.abstractstatus.viewlibrary

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.abstractstatus.viewlibrary.view.MonthItemView




/**
 ** Created by AbstractStatus at 2021/8/21 21:38.
 */
class AsScheduleMonthView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "AsScheduleMonthView"
    }

    private lateinit var monthItemView: MonthItemView
    private var preMonthItemViewPos = -1
    private var curMonthItemViewPos = -1

    private var selectItemPos = -1 //  0 ~ 41


}