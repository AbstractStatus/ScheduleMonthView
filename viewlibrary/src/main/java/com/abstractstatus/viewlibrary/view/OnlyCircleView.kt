package com.abstractstatus.viewlibrary.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 ** Created by AbstractStatus at 2021/8/21 22:59.
 */
class OnlyCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0f
    private var mHeight = 0f
    private var radius = 0f
    private var color: Int = 0
    set(value) {
        field = value
        circlePaint.color = field
        invalidate()
    }

    private val circlePaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth =
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val measuredHeight =
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        if (measuredWidth == 0 || measuredHeight == 0) {
            val size = Math.max(measuredWidth, measuredHeight)
            setMeasuredDimension(size, size)
            return
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat();
        mHeight = h.toFloat();

        radius = (mWidth / 2).coerceAtMost(mHeight / 2);
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(
            mWidth / 2,
            mHeight / 2,
            radius,
            circlePaint);
    }





}