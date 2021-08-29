package com.abstractstatus.viewlibrary.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.abstractstatus.viewlibrary.entity.ScheduleEntity
import com.abstractstatus.viewlibrary.util.LunarUtil

/**
 ** Created by AbstractStatus at 2021/8/21 21:39.
 */
class MonthItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object{
        private const val TAG = "MonthItemView"
        private const val oneThird: Float = 1f / 3f
        private const val FLAG_ANIMATOR_JUST_OPEN = 1
        private const val FLAG_ANIMATOR_JUST_CLOSE = 2
        private const val FLAG_ANIMATOR_UNJUST_CLOSE = 3
        private const val FLAG_ANIMATOR_UNJUST_OPEN = 4

        //字符串压缩成5个字符
        private fun getShortName(s: String): String {
            var s = s
            if (s.length >= 5) {
                s = s.substring(0, 4)
                s += ".."
            }
            return s
        }
    }

    private var mWidth: Float = 0f
    private var mHeight: Float = 0f

    private var averageWidth: Float = 0f
    private var averageHeight: Float = 0f

    private var lockAnimator: Boolean = false
    private var isOpen: Boolean = false

    private var touchIndexX: Int = -1
    private var touchIndexY: Int = -1

    private var selectIndexX: Int = -1
    private var selectIndexY: Int = -1

    private var todayIndexX: Int = -1
    private var todayIndexY: Int = -1

    private var openUpIndexX: Int = -1
    private var openUpIndexY: Int = -1

    //本月本年
    private var thisMonth: Int = 0
    private var thisYear: Int = 0

    //0 ~ 41
    private var todayPosition:Int = -1

    private var dates = Array(42){
        arrayOf(2021, 9, 4, 4, 5)
    }

    private var scheduleNames: Array<String> = Array(42){
        "你好啊啊啊啊"
    }

    private var scheduleLists = ArrayList<ArrayList<ScheduleEntity>>().apply {
        for(i in 0..41){
            val list = ArrayList<ScheduleEntity>()
            repeat(5){
                list.add(ScheduleEntity(
                    "哈哈啊哈哈哈哈",
                    1,
                    "哈哈啊哈哈哈哈",
                    "哈哈啊哈哈哈哈",
                    "哈哈啊哈哈哈哈",
                    2))
            }
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
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
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
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
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
                startAnimator(FLAG_ANIMATOR_UNJUST_OPEN)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
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
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
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
    private val thisMonthCellScheduleBgPaint = Paint().apply {
        color = 0xEAF9F1
        alpha = 0xFF
    }

    //本月每日日程文本
    private val thisMonthCellScheduleTextPaint = Paint().apply {
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

    //其他月的每日日程文本背景
    private val otherMonthCellScheduleBgPaint = Paint().apply {
        color = 0xCCFFFF
        alpha = 0x77
    }

    //其他月的每日日程文本
    private val otherMonthCellScheduleTextPaint = Paint().apply {
        color = 0x009966
        alpha = 0x77
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
    private val selectDayCellTextBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0x196EFF
        alpha = 0xFF
    }

    //选中的日期文本
    private val selectDayCellTextPaint = Paint().apply {
        color = 0xFFFFFF
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //选中的格子背景
    private val selectMonthCellBgPaint = Paint().apply {
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
    private val selectMonthLunarTextPaint = Paint().apply {
        color = 0xFFFFFF
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //今日的农历文字
    private val todayMonthLunarTextPaint = Paint().apply {
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
    private val scheduleCancelLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = 0x000000
        alpha = 0xFF
    }

    //其他月的每日日程文本背景
    private val beforeDayCellScheduleBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = 0xEAF9F1
        alpha = 0x7F
    }

    //其他月的每日日程文本
    private val beforeDayCellScheduleTextPaint = Paint().apply {
        color = 0x1C8E4F
        alpha = 0x7F
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    //还有n项的文本颜色
    private val remainingNScheduleTextPaint = Paint().apply {
        color = 0xA5A8AD
        alpha = 0xFF
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }


    //textSize和distance（字体底部基线到字体正中心的垂直距离）的关系为 100 : 39.257813
    //现在假设textSize为100时，字的大小为80 = 40 * 2，反求出的textSize偏小
    private val radioTextSize = 1.25f

    //单个日程字数
    private val numSingleScheduleWord = 0

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
        LunarUtil.init(context)
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
        rawSizeScheduleText = Math.min(rawSizeDailyNumText, averageWidth / 6)
        sizeScheduleText = rawSizeScheduleText * radioTextSize

        //农历文字大小
        rawSizeLunarText = Math.min(averageWidth / 5, rawSizeDailyNumText * 2 / 3)
        sizeLunarText = rawSizeLunarText * radioTextSize

        //日期（农历加公历）选中时候的圆形半径
        radiusDailyText = averageHeight / 6

        heightSpaceScheduleItemRect = (rawSizeDailyNumText * 2 - rawSizeScheduleText) / 6
        heightScheduleItemRect = (rawSizeDailyNumText - heightSpaceScheduleItemRect) * 2

        //提前计算，防止每次OnDraw都计算一次
        heightDoubleSpaceScheduleItemRect = 2 * heightSpaceScheduleItemRect
        heightFourTimesSpaceScheduleItemRect = 4 * heightSpaceScheduleItemRect

        thisMonthCellDayTextPaint.textSize = sizeDailyNumText
        thisMonthCellScheduleTextPaint.textSize = sizeScheduleText
        selectDayCellTextPaint.textSize = sizeDailyNumText
        todayCellTextPaint.textSize = sizeDailyNumText
        otherMonthCellDayTextPaint.textSize = sizeDailyNumText
        otherMonthCellScheduleTextPaint.textSize = sizeScheduleText
        monthWaterMarkPaint.textSize = sizeWaterMarkText * 2
        thisMonthLunarTextPaint.textSize = sizeLunarText
        otherMonthLunarTextPaint.textSize = sizeLunarText
        todayMonthLunarTextPaint.textSize = sizeLunarText
        selectMonthLunarTextPaint.textSize = sizeLunarText
        beforeDayCellScheduleTextPaint.textSize = sizeScheduleText
        remainingNScheduleTextPaint.textSize = sizeScheduleText

        //日期字体的矩阵数据和中心到基线的距离
        val fontMetricsDailyNum: Paint.FontMetrics = thisMonthCellDayTextPaint.fontMetrics
        distanceDailyNum = (fontMetricsDailyNum.bottom - fontMetricsDailyNum.top) / 2 - fontMetricsDailyNum.bottom

        //农历文字的矩阵数据和中心到基线的距离
        val fontMetricsLunarText: Paint.FontMetrics = thisMonthLunarTextPaint.fontMetrics
        distanceLunarText = (fontMetricsLunarText.bottom - fontMetricsLunarText.top) / 2 - fontMetricsLunarText.bottom

        //日程字体的矩阵数据和中心到基线的距离
        val fontMetricsSchedule: Paint.FontMetrics = thisMonthCellScheduleTextPaint.fontMetrics
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

            //绘制每格的日期文本
            drawCellDateText(this)
            //绘制每格的日程文本
            //绘制每格的日程文本
            drawCellScheduleText(this)
            //绘制今日的格子
            //绘制今日的格子
            if (!(todayIndexX == openUpIndexX && todayIndexY == openUpIndexY && isOpen)) {
                drawTodayItem(this)
            }
            //绘制选中的格子
            //绘制选中的格子
            drawSelectItem(this)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                touchIndexX = (event.x / averageWidth).toInt()
                touchIndexY = (event.y / averageHeight).toInt()
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
                if (isClick(event.x, event.y)) {
                    touchIndexX = (event.x / averageWidth).toInt()
                    touchIndexY = (event.y / averageHeight).toInt()
                    if (!isOpen) {
                        selectIndexX = touchIndexX
                        selectIndexY = touchIndexY
                        openUpIndexX = selectIndexX
                        openUpIndexY = selectIndexY
                        startAnimator(FLAG_ANIMATOR_JUST_OPEN)
                    } else {
                        //打开后点击的不是第一行
                        when {
                            touchIndexY != 0 -> {
                                openUpIndexX = selectIndexX
                                openUpIndexY = selectIndexY
                                selectIndexX = touchIndexX
                                selectIndexY += 1
                                startAnimator(FLAG_ANIMATOR_UNJUST_CLOSE)
                            }
                            touchIndexX != selectIndexX -> {
                                selectIndexX = touchIndexX
                                openUpIndexX = selectIndexX
                                openUpIndexY = selectIndexY
                            }
                            else -> {
                                startAnimator(FLAG_ANIMATOR_JUST_CLOSE)
                            }
                        }
                    }
                }
                return true
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

    }


    private fun drawTodayItem(canvas: Canvas) {
        if (todayPosition < 0) {
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
                LunarUtil.getLunarText(
                    dates[todayPosition].get(0),
                    dates[todayPosition].get(1),
                    dates[todayPosition].get(2)
                ),
                (averageWidth * (0.5 + todayIndexX)).toFloat(),
                averageHeight * todayIndexY + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + upIncrement,
                todayMonthLunarTextPaint
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
                LunarUtil.getLunarText(
                    dates[todayPosition].get(0),
                    dates[todayPosition].get(1),
                    dates[todayPosition].get(2)
                ),
                (averageWidth * (0.5 + todayIndexX)).toFloat(),
                averageHeight * todayIndexY + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + downIncrement,
                todayMonthLunarTextPaint
            )
        }
    }


    private fun drawSelectItem(canvas: Canvas) {
        if (!(dates.isNotEmpty() && openUpIndexX >= 0 && openUpIndexY >= 0 && isOpen)) {
            return
        }
        val selectPosition = openUpIndexX + openUpIndexY * 7
        canvas.drawRect(
            averageWidth * openUpIndexX,
            averageHeight * (openUpIndexY + oneThird) + upIncrement + 30,
            averageWidth * (openUpIndexX + 1),
            averageHeight * (openUpIndexY + 1) + upIncrement,
            selectMonthCellBgPaint
        )

        //画箭头
        canvas.drawLine(
            averageWidth * (openUpIndexX + 0.35f),
            averageHeight * (openUpIndexY + 0.72f) + upIncrement,
            averageWidth * (openUpIndexX + 0.5f),
            averageHeight * (openUpIndexY + 0.65f) + upIncrement,
            arrowPaint
        )
        canvas.drawLine(
            averageWidth * (openUpIndexX + 0.5f),
            averageHeight * (openUpIndexY + 0.65f) + upIncrement,
            averageWidth * (openUpIndexX + 0.65f),
            averageHeight * (openUpIndexY + 0.72f) + upIncrement,
            arrowPaint
        )

        canvas.drawCircle(
            (0.5f + openUpIndexX) * averageWidth,
            averageHeight * openUpIndexY + upIncrement + radiusDailyText,
            radiusDailyText,
            selectDayCellTextBgPaint
        )
        canvas.drawText(
            if (openUpIndexX == todayIndexX && openUpIndexY == todayIndexY) "今" else dates[selectIndexX + openUpIndexY * 7][2].toString(),
            averageWidth * (0.5f + openUpIndexX),
            averageHeight * openUpIndexY + distanceDailyNum * 2.5f + upIncrement,
            selectDayCellTextPaint
        )
        canvas.drawText(
            LunarUtil.getLunarText(
                dates[selectPosition].get(0),
                dates[selectPosition].get(1),
                dates[selectPosition].get(2)
            ),
            (averageWidth * (0.5 + openUpIndexX)).toFloat(),
            averageHeight * openUpIndexY + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + upIncrement,
            selectMonthLunarTextPaint
        )
    }


    private fun drawScheduleRect(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint,
        scheduleEntities: List<ScheduleEntity>?
    ) {
        if (scheduleEntities == null) {
            return
        }
        when (scheduleEntities.size) {
            0 -> {
            }
            1 -> {
                canvas.drawRect(left, top, right, bottom, paint)
                if (scheduleEntities[0].scheduleStatus == 2) {
                    canvas.drawLine(
                        left,
                        top + (bottom - top) / 2,
                        right,
                        top + (bottom - top) / 2,
                        scheduleCancelLinePaint
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
                if (scheduleEntities[0].scheduleStatus == 2) {
                    canvas.drawLine(
                        left,
                        top + (bottom - top) / 2,
                        right,
                        top + (bottom - top) / 2,
                        scheduleCancelLinePaint
                    )
                }
                if (scheduleEntities[1].scheduleStatus == 2) {
                    canvas.drawLine(
                        left,
                        top + (bottom - top) / 2 + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect,
                        right,
                        top + (bottom - top) / 2 + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect,
                        scheduleCancelLinePaint
                    )
                }
            }
        }
    }


    private fun drawScheduleText(
        canvas: Canvas,
        x: Float,
        y: Float,
        scheduleEntities: List<ScheduleEntity>,
        paint: Paint,
        remainingNPaint: Paint
    ) {
        when (scheduleEntities.size) {
            0 -> {
            }
            1 -> canvas.drawText(
                getShortName(
                    scheduleEntities[0].scheduleName
                ), x, y, paint
            )
            2 -> {
                canvas.drawText(
                    getShortName(
                        scheduleEntities[0].scheduleName
                    ), x, y, paint
                )
                canvas.drawText(
                    getShortName(
                        scheduleEntities[1].scheduleName
                    ), x, y + heightScheduleItemRect + heightDoubleSpaceScheduleItemRect, paint
                )
            }
            else -> {
                canvas.drawText(
                    getShortName(
                        scheduleEntities[0].scheduleName
                    ), x, y, paint
                )
                canvas.drawText(
                    getShortName(
                        scheduleEntities[1].scheduleName
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
        if (dates == null || dates.isEmpty()) {
            return
        }

        //上半部分
        for (i in 0..openUpIndexY) {
            for (j in 0..6) {
                if (i == todayIndexY && j == todayIndexX) {
                    continue
                }
                if (dates[i * 7 + j].get(4) == 1) {
                    canvas.drawText(
                        dates[i * 7 + j].get(2).toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + upIncrement,
                        thisMonthCellDayTextPaint
                    )
                    canvas.drawText(
                        LunarUtil.getLunarText(
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
                        LunarUtil.getLunarText(
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
                        dates[i * 7 + j].get(2).toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + downIncrement,
                        thisMonthCellDayTextPaint
                    )
                    canvas.drawText(
                        LunarUtil.getLunarText(
                            dates[i * 7 + j].get(0),
                            dates[i * 7 + j].get(1),
                            dates[i * 7 + j].get(2)
                        ),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 3.5f + 2.5f * distanceLunarText + downIncrement,
                        thisMonthLunarTextPaint
                    )
                } else {
                    canvas.drawText(
                        dates[i * 7 + j].get(2).toString(),
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * i + distanceDailyNum * 2.5f + downIncrement,
                        otherMonthCellDayTextPaint
                    )
                    canvas.drawText(
                        LunarUtil.getLunarText(
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
        if (scheduleLists == null || scheduleLists.size == 0 || dates == null || dates.size == 0
        ) {
            return
        }

        //上半部分
        for (i in 0..openUpIndexY) {
            for (j in 0..6) {
                if (i == openUpIndexY && j == openUpIndexX && isOpen) {
                    continue
                }
                if (i * 7 + j >= todayPosition) {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + upIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + upIncrement,
                        thisMonthCellScheduleBgPaint,
                        scheduleLists.get(i * 7 + j)
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + upIncrement,
                        scheduleLists.get(i * 7 + j),
                        thisMonthCellScheduleTextPaint,
                        remainingNScheduleTextPaint
                    )
                } else {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + upIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + upIncrement,
                        beforeDayCellScheduleBgPaint,
                        scheduleLists.get(i * 7 + j)
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + upIncrement,
                        scheduleLists.get(i * 7 + j),
                        beforeDayCellScheduleTextPaint,
                        remainingNScheduleTextPaint
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
                        thisMonthCellScheduleBgPaint,
                        scheduleLists.get(i * 7 + j)
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + downIncrement,
                        scheduleLists.get(i * 7 + j),
                        thisMonthCellScheduleTextPaint,
                        remainingNScheduleTextPaint
                    )
                } else {
                    drawScheduleRect(
                        canvas,
                        j * averageWidth,
                        (i + oneThird) * averageHeight + heightSpaceScheduleItemRect + downIncrement,
                        (j + 1) * averageWidth,
                        (i.toFloat() + oneThird) * averageHeight + heightSpaceScheduleItemRect + heightScheduleItemRect + downIncrement,
                        beforeDayCellScheduleBgPaint,
                        scheduleLists.get(i * 7 + j)
                    )
                    drawScheduleText(
                        canvas,
                        (averageWidth * (0.5 + j)).toFloat(),
                        averageHeight * (i + oneThird) + heightSpaceScheduleItemRect + heightScheduleItemRect / 2 + distanceSchedule + downIncrement,
                        scheduleLists.get(i * 7 + j),
                        beforeDayCellScheduleTextPaint,
                        remainingNScheduleTextPaint
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

}