package com.abstractstatus.viewlibrary.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.abstractstatus.viewlibrary.util.CalendarUtil
import com.abstractstatus.viewlibrary.util.MonthViewDelegate

import com.abstractstatus.viewlibrary.util.lunar.LunarCalendar

/**
 ** Created by AbstractStatus at 2021/8/21 21:39.
 */
class MonthItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object{
        private const val TAG = "MonthItemView"
        private const val oneThird: Float = 1f / 3f
        //打开动画的标识
        private const val FLAG_ANIMATOR_JUST_OPEN = 1
        //关闭动画的标识
        private const val FLAG_ANIMATOR_JUST_CLOSE = 2
        //关闭-打开的关闭动画标识
        private const val FLAG_ANIMATOR_UNJUST_CLOSE = 3
        //关闭-打开的打开动画标识
        private const val FLAG_ANIMATOR_UNJUST_OPEN = 4

        //字符串压缩成5个字符
        private fun getShortName(ss: String): String {
            var s = ss
            if (s.length >= 5) {
                s = s.substring(0, 4)
                s += ".."
            }
            return s
        }
    }

    //视图的宽高
    private var mWidth: Float = 0f
    private var mHeight: Float = 0f

    //每一格的宽高
    private var averageWidth: Float = 0f
    private var averageHeight: Float = 0f

    //动画锁
    private var lockAnimator: Boolean = false

    //打开的状态
    private var isOpen: Boolean = false

    //触摸格子的x, y索引
    private var touchIndexX: Int = -1
    private var touchIndexY: Int = -1

    //选中格子的x, y索引
    private var selectIndexX: Int = -1
    private var selectIndexY: Int = -1

    //今日格子的x, y索引
    private var todayIndexX: Int = -1
    private var todayIndexY: Int = -1

    //动画需要的格子x, y索引
    private var openUpIndexX: Int = -1
    private var openUpIndexY: Int = -1

    //该月视图的月份和年份
    private var thisMonth: Int = 0
    private var thisYear: Int = 0

    //今日的坐标，0 ~ 41
    private var todayPosition:Int = -1

    //该月视图的日期数组
    private var dates = CalendarUtil.get42DaysDataByCurMonth()

    //月份坐标
    var monthPosition: Int = MonthViewDelegate.startMonthPos
        set(value){
            field = value
            dates = CalendarUtil.get42DaysDataByMonthPosition(value)
            val nowYearAndMonth = CalendarUtil.getNowYearAndMonth()
            val yearAndMonth = CalendarUtil.getAddYearAndMonth(nowYearAndMonth[0], nowYearAndMonth[1], monthPosition - MonthViewDelegate.startMonthPos)
            thisYear = yearAndMonth[0]
            thisMonth = yearAndMonth[1]
            todayPosition = CalendarUtil.getTodayPosition(dates)
            todayIndexX = todayPosition % MonthViewDelegate.daysAWeek
            todayIndexY = todayPosition / MonthViewDelegate.daysAWeek
            invalidate()
        }

    private var nameLists = ArrayList<ArrayList<String>>().apply {
        repeat(42){
            add(ArrayList())
        }
    }

    private var statusLists = ArrayList<ArrayList<Boolean>>().apply {
        repeat(42){
            add(ArrayList())
        }
    }

    private var openPercent: Float = 0f
    set(value) {
        field = value
        invalidate()
    }

    private var upIncrement: Float = 0f
    private var downIncrement: Float = 0f

    private var justOpenAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 400
        addUpdateListener {
            openPercent = it.animatedValue as Float
        }
        addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                lockAnimator = false
                isOpen = true
                onJustOpenEnd?.let { it() }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                onJustOpenStart?.let { it() }
            }
        })
    }

    private var justCloseAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
        duration = 400
        addUpdateListener {
            openPercent = it.animatedValue as Float
        }
        addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                lockAnimator = false
                isOpen = false
                onJustCloseEnd?.let { it() }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                onJustCloseStart?.let { it() }
            }
        })
    }

    private var unjustCloseAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
        duration = 400
        addUpdateListener {
            openPercent = it.animatedValue as Float
        }
        addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                openUpIndexX = selectIndexX
                openUpIndexY = selectIndexY
                onUnjustCloseEnd?.let { it() }
                startAnimator(FLAG_ANIMATOR_UNJUST_OPEN)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                onUnjustCloseStart?.let { it() }
            }
        })
    }

    private var unjustOpenAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 400
        addUpdateListener {
            openPercent = it.animatedValue as Float
        }
        addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                lockAnimator = false
                onUnjustOpenEnd?.let { it() }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                onUnjustOpenStart?.let { it() }
            }
        })
    }

    //每日格子的背景
    private val monthCellBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0xFFFFFF
        alpha = 0xFF
    }

    //本月的日期文本
    private val thisMonthCellDayTextPaint = Paint().apply {
        color = 0x181910
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //本月的每日日程文本背景
    private val thisMonthCellNameBgPaint = Paint().apply {
        color = 0xEAF9F1
        alpha = 0xFF
    }

    //本月每日日程文本
    private val thisMonthCellNameTextPaint = Paint().apply {
        color = 0x1C8E4F
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //其他月的日期文本
    private val otherMonthCellDayTextPaint = Paint().apply {
        color = 0xC6C9CC
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //月份水印
    private val monthWaterMarkPaint = Paint().apply {
        color = 0xAAAAAA
        alpha = 0x22
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //今日的日期文本背景
    private val todayCellTextBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0x196EFF
        alpha = 0x2F
    }

    //今日的日期文本
    private val todayCellTextPaint = Paint().apply {
        color = 0x196EFF
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //选中的日期文本背景
    private val selectCellTextBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0x196EFF
        alpha = 0xFF
    }

    //选中的日期文本
    private val selectCellTextPaint = Paint().apply {
        color = 0xFFFFFF
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //选中的格子背景
    private val selectCellBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0xF7F8FA
        alpha = 0xFF
    }

    //本月日期的农历文字
    private val thisMonthLunarTextPaint = Paint().apply {
        color = 0xA5A8AD
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //非本月日期的农历文字
    private val otherMonthLunarTextPaint = Paint().apply {
        color = 0xC6C9CC
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //选中的农历文字
    private val selectLunarTextPaint = Paint().apply {
        color = 0xFFFFFF
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //今日的农历文字
    private val todayLunarTextPaint = Paint().apply {
        color = 0x196EFF
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //箭头
    private val arrowPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 9f
        strokeCap = Paint.Cap.ROUND
        color = 0x000000
        alpha = 0xFF
        isAntiAlias = true

    }

    //取消日程的线
    private val nameCancelLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = 0x000000
        alpha = 0xFF
    }

    //过去的每日日程文本背景
    private val beforeDayCellNameBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0xEAF9F1
        alpha = 0x7F
    }

    //过去的每日日程文本
    private val beforeDayCellNameTextPaint = Paint().apply {
        color = 0x1C8E4F
        alpha = 0x7F
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //还有n项的文本颜色
    private val remainingNNameTextPaint = Paint().apply {
        color = 0xA5A8AD
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //textSize和distance（字体底部基线到字体正中心的垂直距离）的关系为 100 : 39.257813
    //现在假设textSize为100时，字的大小为80 = 40 * 2，反求出的textSize偏小
    private val radioTextSize = 1.25f

    //日期字大小  用于setTextSize()
    private var sizeDailyNumText = 0f
    //农历字大小
    private var sizeLunarText = 0f
    //日程字大小
    private var sizeScheduleText = 0f
    //水印字大小
    private var sizeWaterMarkText = 0f

    //日期字绝对大小
    private var rawSizeDailyNumText = 0f
    //农历字绝对大小
    private var rawSizeLunarText = 0f
    //日程字绝对大小
    private var rawSizeScheduleText = 0f
    //水印字绝对大小
    private var rawSizeWaterMarkText = 0f

    //日程背景矩形的高
    private var heightScheduleItemRect = 0f
    //日程矩形间的间距
    private var heightSpaceScheduleItemRect = 0f
    //两倍间距，用于减少计算量
    private var heightDoubleSpaceScheduleItemRect = 0f
    //四倍间距
    private var heightFourTimesSpaceScheduleItemRect = 0f

    //日期数字圆形的半径
    private var radiusDailyText = 0f

    //日期字体的矩阵数据和中心到基线的距离
    private var distanceDailyNum = 0f
    //日程字体的矩阵数据和中心到基线的距离
    private var distanceSchedule = 0f
    //水印字体的矩阵数据和中心到基线的距离
    private var distanceWaterMark = 0f
    //农历文字的矩阵数据和中心到基线的距离
    private var distanceLunarText = 0f



    init {
        LunarCalendar.init(context)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth =
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val measuredHeight =
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        if (measuredWidth == 0 || measuredHeight == 0) {
            val size = measuredWidth.coerceAtLeast(measuredHeight)
            setMeasuredDimension(size, size)
            return
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //view的宽高
        mWidth = w.toFloat()
        mHeight = h.toFloat()

        //每一格的宽高
        averageWidth = mWidth / 7
        averageHeight = mHeight / 6

        //水印文字的大小
        rawSizeWaterMarkText = averageHeight * 3 / 4
        sizeWaterMarkText = rawSizeWaterMarkText * radioTextSize

        //数字日期文字的大小
        rawSizeDailyNumText = averageHeight / 9
        sizeDailyNumText = rawSizeDailyNumText * radioTextSize

        //日程文字大小
        rawSizeScheduleText = rawSizeDailyNumText.coerceAtMost(averageWidth / 6)
        sizeScheduleText = rawSizeScheduleText * radioTextSize

        //农历文字大小
        rawSizeLunarText = (averageWidth / 5).coerceAtMost(rawSizeDailyNumText * 2 / 3)
        sizeLunarText = rawSizeLunarText * radioTextSize

        //日期（农历加公历）选中时候的圆形半径
        radiusDailyText = averageHeight / 6

        heightSpaceScheduleItemRect = (rawSizeDailyNumText * 2 - rawSizeScheduleText) / 6
        heightScheduleItemRect = (rawSizeDailyNumText - heightSpaceScheduleItemRect) * 2

        //提前计算，防止每次OnDraw都计算一次
        heightDoubleSpaceScheduleItemRect = 2 * heightSpaceScheduleItemRect
        heightFourTimesSpaceScheduleItemRect = 4 * heightSpaceScheduleItemRect

        thisMonthCellDayTextPaint.textSize = sizeDailyNumText
        otherMonthCellDayTextPaint.textSize = sizeDailyNumText
        selectCellTextPaint.textSize = sizeDailyNumText
        todayCellTextPaint.textSize = sizeDailyNumText
        thisMonthLunarTextPaint.textSize = sizeLunarText
        otherMonthLunarTextPaint.textSize = sizeLunarText
        todayLunarTextPaint.textSize = sizeLunarText
        selectLunarTextPaint.textSize = sizeLunarText
        beforeDayCellNameTextPaint.textSize = sizeScheduleText
        thisMonthCellNameTextPaint.textSize = sizeScheduleText
        remainingNNameTextPaint.textSize = sizeScheduleText
        monthWaterMarkPaint.textSize = sizeWaterMarkText

        //日期字体的矩阵数据和中心到基线的距离
        val fontMetricsDailyNum: Paint.FontMetrics = thisMonthCellDayTextPaint.fontMetrics
        distanceDailyNum = (fontMetricsDailyNum.bottom - fontMetricsDailyNum.top) / 2 - fontMetricsDailyNum.bottom

        //农历文字的矩阵数据和中心到基线的距离
        val fontMetricsLunarText: Paint.FontMetrics = thisMonthLunarTextPaint.fontMetrics
        distanceLunarText = (fontMetricsLunarText.bottom - fontMetricsLunarText.top) / 2 - fontMetricsLunarText.bottom

        //日程字体的矩阵数据和中心到基线的距离
        val fontMetricsSchedule: Paint.FontMetrics = thisMonthCellNameTextPaint.fontMetrics
        distanceSchedule = (fontMetricsSchedule.bottom - fontMetricsSchedule.top) / 2 - fontMetricsSchedule.bottom

        //水印字体的矩阵数据和中心到基线的距离
        val fontMetricsWaterMark: Paint.FontMetrics = monthWaterMarkPaint.fontMetrics
        distanceWaterMark = (fontMetricsWaterMark.bottom - fontMetricsWaterMark.top) / 2 - fontMetricsWaterMark.bottom
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //如果月视图在顶图层，则需要下面代码，用来透明下层图层
        //canvas.drawColor(Color.TRANSPARENT);
        upIncrement = -openPercent * openUpIndexY * averageHeight
        downIncrement = openPercent * (6 - openUpIndexY - 2) * averageHeight

        canvas?.apply {
            //绘制每日格子的背景
            drawCellsBg(this)
            //绘制月份水印
            drawWaterMark(this)
            //绘制每格的日期文本
            drawCellDateText(this)
            //绘制每格的日程文本
            drawCellScheduleText(this)
            //绘制今日的格子
            drawTodayItem(this)
            //绘制选中的格子
            drawSelectItem(this)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchIndexX = (it.x / averageWidth).toInt()
                    touchIndexY = (it.y / averageHeight).toInt()
                    if (isOpen) {
                        //不在打开后的日期区域，不响应触摸事件
                        if (!(selectIndexY == 5 && touchIndexY == 0
                                        || selectIndexY != 5 && (touchIndexY == 0 || touchIndexY == 5))) {
                            return false
                        }
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (lockAnimator) {
                        return true
                    }
                    if (isClick(it.x, it.y)) {
                        touchIndexX = (it.x / averageWidth).toInt()
                        touchIndexY = (it.y / averageHeight).toInt()
                        if (!isOpen) {
                            selectIndexX = touchIndexX
                            selectIndexY = touchIndexY
                            openUpIndexX = selectIndexX
                            openUpIndexY = selectIndexY
                            startAnimator(FLAG_ANIMATOR_JUST_OPEN)
                        } else {
                            when {
                                //打开后点击的不是第一行
                                touchIndexY != 0 -> {
                                    openUpIndexX = selectIndexX
                                    openUpIndexY = selectIndexY
                                    selectIndexX = touchIndexX
                                    selectIndexY += 1
                                    startAnimator(FLAG_ANIMATOR_UNJUST_CLOSE)
                                }
                                //点击第一行但不同列
                                touchIndexX != selectIndexX -> {
                                    selectIndexX = touchIndexX
                                    openUpIndexX = selectIndexX
                                    openUpIndexY = selectIndexY
                                    invalidate()
                                }
                                //点击选中的格子
                                else -> {
                                    startAnimator(FLAG_ANIMATOR_JUST_CLOSE)
                                }
                            }
                        }
                    }
                    onDaySelect?.let { it1 -> it1(dates[selectIndexX + selectIndexY * 7], selectIndexX + selectIndexY * 7) }
                    return true
                }
                else -> {

                }
            }
        }

        return super.onTouchEvent(event)
    }




    private fun drawCellsBg(canvas: Canvas) {
        //上半部分
        for (i in 0..openUpIndexY) {
            for (j in 0..6) {
                canvas.drawRect(
                        averageWidth * j,
                        averageHeight * i + upIncrement,
                        averageWidth * (j + 1),
                        averageHeight * (i + 1) + upIncrement,
                        monthCellBgPaint)
            }
        }
        //下半部分
        for (i in openUpIndexY + 1..5) {
            for (j in 0..6) {
                canvas.drawRect(
                        averageWidth * j,
                        averageHeight * i + downIncrement,
                        averageWidth * (j + 1),
                        averageHeight * (i + 1) + downIncrement,
                        monthCellBgPaint)
            }
        }
    }

    private fun drawWaterMark(canvas: Canvas?){
        canvas?.drawText(
                thisYear.toString(),
                mWidth / 2,
                averageHeight * (1.5f + 3) + distanceWaterMark,
                monthWaterMarkPaint)

        canvas?.drawText(
                if (thisMonth < 10) "0$thisMonth" else thisMonth.toString(),
                mWidth / 2,
                averageHeight * (1.5f) + distanceWaterMark,
                monthWaterMarkPaint)
    }


    private fun drawTodayItem(canvas: Canvas) {
        if(todayPosition == -1){
            return
        }
        if (openUpIndexY >= todayIndexY) {
            canvas.drawCircle(
                (0.5f + todayIndexX) * averageWidth,
                averageHeight * todayIndexY + upIncrement + radiusDailyText,
                radiusDailyText,
                todayCellTextBgPaint
            )
            canvas.drawText(
                "今",
                averageWidth * (0.5f + todayIndexX),
                averageHeight * todayIndexY + distanceDailyNum * 2.5f + upIncrement,
                todayCellTextPaint
            )
            canvas.drawText(
                LunarCalendar.getLunarText(
                        dates[todayPosition][0],
                        dates[todayPosition][1],
                        dates[todayPosition][2]
                ),
                (averageWidth * (0.5 + todayIndexX)).toFloat(),
                averageHeight * todayIndexY + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + upIncrement,
                todayLunarTextPaint
            )
        } else {
            canvas.drawCircle(
                (0.5f + todayIndexX) * averageWidth,
                averageHeight * todayIndexY + radiusDailyText + downIncrement,
                radiusDailyText,
                todayCellTextBgPaint
            )
            canvas.drawText(
                "今",
                averageWidth * (0.5f + todayIndexX),
                averageHeight * todayIndexY + distanceDailyNum * 3 + downIncrement,
                todayCellTextPaint
            )
            canvas.drawText(
                    LunarCalendar.getLunarText(
                        dates[todayPosition][0],
                        dates[todayPosition][1],
                        dates[todayPosition][2]
                ),
                (averageWidth * (0.5 + todayIndexX)).toFloat(),
                averageHeight * todayIndexY + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + downIncrement,
                todayLunarTextPaint
            )
        }
    }


    private fun drawSelectItem(canvas: Canvas) {
        if (!isOpen) {
            return
        }
        val selectPosition = selectIndexX + openUpIndexY * 7
        canvas.drawRect(
            averageWidth * selectIndexX,
            averageHeight * (openUpIndexY + oneThird) + upIncrement + 30,
            averageWidth * (selectIndexX + 1),
            averageHeight * (openUpIndexY + 1) + upIncrement,
            selectCellBgPaint
        )

        //画箭头
        canvas.drawLine(
            averageWidth * (selectIndexX + 0.35f),
            averageHeight * (openUpIndexY + 0.72f) + upIncrement,
            averageWidth * (selectIndexX + 0.5f),
            averageHeight * (openUpIndexY + 0.65f) + upIncrement,
            arrowPaint
        )
        canvas.drawLine(
            averageWidth * (selectIndexX + 0.5f),
            averageHeight * (openUpIndexY + 0.65f) + upIncrement,
            averageWidth * (selectIndexX + 0.65f),
            averageHeight * (openUpIndexY + 0.72f) + upIncrement,
            arrowPaint
        )

        canvas.drawCircle(
            (0.5f + selectIndexX) * averageWidth,
            averageHeight * openUpIndexY + upIncrement + radiusDailyText,
            radiusDailyText,
            selectCellTextBgPaint
        )
        canvas.drawText(
            if (selectIndexX == todayIndexX && openUpIndexY == todayIndexY) "今" else dates[selectIndexX + openUpIndexY * 7][2].toString(),
            averageWidth * (0.5f + selectIndexX),
            averageHeight * openUpIndexY + distanceDailyNum * 2.5f + upIncrement,
            selectCellTextPaint
        )
        canvas.drawText(
                LunarCalendar.getLunarText(
                    dates[selectPosition][0],
                    dates[selectPosition][1],
                    dates[selectPosition][2]
            ),
            (averageWidth * (0.5 + selectIndexX)).toFloat(),
            averageHeight * openUpIndexY + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + upIncrement,
            selectLunarTextPaint
        )
    }


    private fun drawScheduleRect(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            paint: Paint,
            nameList: List<String>,
            statusList: List<Boolean>
    ) {
        when (nameList.size) {
            0 -> {

            }
            1 -> {
                canvas.drawRect(left, top, right, bottom, paint)
                if (statusList[0]) {
                    canvas.drawLine(
                        left,
                        top + (bottom - top) / 2,
                        right,
                        top + (bottom - top) / 2,
                        nameCancelLinePaint
                    )
                }
            }
            else -> {
                canvas.drawRect(left, top, right, bottom, paint)
                canvas.drawRect(
                    left,
                    top + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect,
                    right,
                    bottom + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect,
                    paint
                )
                if (statusList[0]) {
                    canvas.drawLine(
                        left,
                        top + (bottom - top) / 2,
                        right,
                        top + (bottom - top) / 2,
                        nameCancelLinePaint
                    )
                }
                if (statusList[1]) {
                    canvas.drawLine(
                        left,
                        top + (bottom - top) / 2 + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect,
                        right,
                        top + (bottom - top) / 2 + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect,
                        nameCancelLinePaint
                    )
                }
            }
        }
    }


    private fun drawScheduleText(
        canvas: Canvas,
        x: Float,
        y: Float,
        scheduleEntities: List<String>,
        paint: Paint,
        remainingNPaint: Paint
    ) {
        when (scheduleEntities.size) {
            0 -> {

            }
            1 -> canvas.drawText(
                getShortName(
                    scheduleEntities[0]
                ), x, y, paint
            )
            2 -> {
                canvas.drawText(
                    getShortName(
                        scheduleEntities[0]
                    ), x, y, paint
                )
                canvas.drawText(
                    getShortName(
                        scheduleEntities[1]
                    ), x, y + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect, paint
                )
            }
            else -> {
                canvas.drawText(
                    getShortName(
                        scheduleEntities[0]
                    ), x, y, paint
                )
                canvas.drawText(
                    getShortName(
                        scheduleEntities[1]
                    ), x, y + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect, paint
                )
                canvas.drawText(
                    "还有" + (scheduleEntities.size - 2) + "项",
                    x,
                    y + heightScheduleItemRect * 2 + heightFourTimesSpaceScheduleItemRect,
                    remainingNPaint
                )
            }
        }
    }

    private fun drawCellDateText(canvas: Canvas) {
        //上半部分
        for (i in 0..openUpIndexY) {
            for (j in 0..6) {
                if (i == todayIndexY && j == todayIndexX) {
                    continue
                }
                if (dates[i * 7 + j][4] == 1) {
                    canvas.drawText(
                        dates[i * 7 + j][2].toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + upIncrement,
                        thisMonthCellDayTextPaint
                    )
                    canvas.drawText(
                            LunarCalendar.getLunarText(
                            dates[i * 7 + j][0],
                            dates[i * 7 + j][1],
                            dates[i * 7 + j][2]
                        ),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + upIncrement,
                        thisMonthLunarTextPaint
                    )
                } else {
                    canvas.drawText(
                        dates[i * 7 + j][2].toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + upIncrement,
                        otherMonthCellDayTextPaint
                    )
                    canvas.drawText(
                            LunarCalendar.getLunarText(
                            dates[i * 7 + j][0],
                            dates[i * 7 + j][1],
                            dates[i * 7 + j][2]
                        ),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + upIncrement,
                        otherMonthLunarTextPaint
                    )
                }
            }
        }

        //下半部分
        for (i in openUpIndexY + 1..5) {
            for (j in 0..6) {
                if (i == todayIndexY && j == todayIndexX) {
                    continue
                }
                if (dates[i * 7 + j][4] == 1) {
                    canvas.drawText(
                        dates[i * 7 + j][2].toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + downIncrement,
                        thisMonthCellDayTextPaint
                    )
                    canvas.drawText(
                            LunarCalendar.getLunarText(
                                dates[i * 7 + j][0],
                                dates[i * 7 + j][1],
                                dates[i * 7 + j][2]
                        ),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + downIncrement,
                        thisMonthLunarTextPaint
                    )
                } else {
                    canvas.drawText(
                        dates[i * 7 + j][2].toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + downIncrement,
                        otherMonthCellDayTextPaint
                    )
                    canvas.drawText(
                            LunarCalendar.getLunarText(
                            dates[i * 7 + j][0],
                            dates[i * 7 + j][1],
                            dates[i * 7 + j][2]
                        ),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + downIncrement,
                        otherMonthLunarTextPaint
                    )
                }
            }
        }
    }


    //画每个格子的日程
    private fun drawCellScheduleText(canvas: Canvas) {
        //上半部分
        for (i in 0..openUpIndexY) {
            for (j in 0..6) {
                if (i == selectIndexY && j == selectIndexX && isOpen) {
                    continue
                }
                if (i * 7 + j >= todayPosition) {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + upIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + upIncrement,
                        thisMonthCellNameBgPaint,
                            nameLists[i * 7 + j],
                            statusLists[i * 7 + j]
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + upIncrement,
                            nameLists[i * 7 + j],
                        thisMonthCellNameTextPaint,
                        remainingNNameTextPaint
                    )
                } else {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + upIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + upIncrement,
                        beforeDayCellNameBgPaint,
                            nameLists[i * 7 + j],
                            statusLists[i * 7 + j]
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + upIncrement,
                            nameLists[i * 7 + j],
                        beforeDayCellNameTextPaint,
                        remainingNNameTextPaint
                    )
                }
            }
        }

        //下半部分
        for (i in openUpIndexY + 1..5) {
            for (j in 0..6) {
                if (i * 7 + j >= todayPosition) {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + downIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + downIncrement,
                        thisMonthCellNameBgPaint,
                            nameLists[i * 7 + j],
                            statusLists[i * 7 + j]
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + downIncrement,
                            nameLists[i * 7 + j],
                        thisMonthCellNameTextPaint,
                        remainingNNameTextPaint
                    )
                } else {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + downIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + downIncrement,
                        beforeDayCellNameBgPaint,
                            nameLists[i * 7 + j],
                            statusLists[i * 7 + j]
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + downIncrement,
                            nameLists[i * 7 + j],
                        beforeDayCellNameTextPaint,
                        remainingNNameTextPaint
                    )
                }
            }
        }
    }


    private fun startAnimator(flag: Int){
        lockAnimator = true
        when(flag){
            FLAG_ANIMATOR_JUST_OPEN -> justOpenAnimator
            FLAG_ANIMATOR_JUST_CLOSE -> justCloseAnimator
            FLAG_ANIMATOR_UNJUST_CLOSE -> unjustCloseAnimator
            else -> unjustOpenAnimator
        }.start()
    }

    //检测按下和弹起是否同一个格子
    private fun isClick(x: Float, y: Float): Boolean{
        return ((x / averageWidth).toInt() == touchIndexX
                && (y / averageHeight).toInt() == touchIndexY)
    }


    /**
     * 对外公开接口
     */
    var onJustOpenStart : (() -> Unit)? = null
    var onJustOpenEnd : (() -> Unit)? = null
    var onJustCloseStart : (() -> Unit)? = null
    var onJustCloseEnd : (() -> Unit)? = null
    var onUnjustOpenStart : (() -> Unit)? = null
    var onUnjustOpenEnd : (() -> Unit)? = null
    var onUnjustCloseStart : (() -> Unit)? = null
    var onUnjustCloseEnd : (() -> Unit)? = null
    var onDaySelect : ((date: IntArray, posOfMonth: Int) -> Unit)? = null

    //设置名称和状态列表
    fun setNameAndStatusList(name: ArrayList<ArrayList<String>>, status: ArrayList<ArrayList<Boolean>>){
        nameLists = name
        statusLists = status
        invalidate()
    }

}