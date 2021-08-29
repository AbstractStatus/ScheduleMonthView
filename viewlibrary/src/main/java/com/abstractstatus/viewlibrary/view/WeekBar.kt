package com.abstractstatus.viewlibrary.view

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView


/**
 ** Created by AbstractStatus at 2021/8/21 21:42.
 */
class WeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object{
        private const val TAG = "WeekBar"
        private val weekDayText = arrayOf("日", "一", "二", "三", "四", "五", "六")

        /**
         * dp转px
         * @param context context
         * @param dpValue dp
         * @return px
         */
        fun dipToPx(context: Context, dpValue: Float): Int{
            val scale: Float = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    init {
        setBackgroundResource(com.abstractstatus.viewlibrary.R.drawable.bg_calendar_week_bar)
        this.orientation = HORIZONTAL
        for (strDay in weekDayText) {
            val textView = TextView(context)
            val tvLayoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            tvLayoutParams.weight = 1f
            //            tvLayoutParams.gravity = ViewGroup.TEXT_ALIGNMENT_CENTER;
            tvLayoutParams.setMargins(
                0,
                dipToPx(context, 12f),
                0,
                dipToPx(context, 12f)
            )
            //            tvLayoutParams.setMargins(0, 36, 0, 36);
            textView.gravity = Gravity.CENTER
            textView.text = strDay
            textView.setTextColor(-0x5a5753)
            textView.textSize = 12f
            textView.layoutParams = tvLayoutParams
            //            textView.setPadding(16, 20, 16, 20);
            addView(textView)
        }
    }

}