/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abstractstatus.viewlibrary.util.lunar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;


import androidx.annotation.Nullable;

import com.abstractstatus.viewlibrary.R;
import java.util.Date;

/**
 * Google规范化的属性委托,
 * 代码量多，但是不影响阅读性
 */
final class CalendarViewDelegate {

    /**
     * 周起始：周日
     */
    static final int WEEK_START_WITH_SUN = 1;

    /**
     * 周起始：周一
     */
    static final int WEEK_START_WITH_MON = 2;

    /**
     * 周起始：周六
     */
    static final int WEEK_START_WITH_SAT = 7;

    /**
     * 默认选择日期1号first_day_of_month
     */
    static final int FIRST_DAY_OF_MONTH = 0;

    /**
     * 跟随上个月last_select_day
     */
    static final int LAST_MONTH_VIEW_SELECT_DAY = 1;

    /**
     * 跟随上个月last_select_day_ignore_current忽视今天
     */
    static final int LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT = 2;

    private int mDefaultCalendarSelectDay;

    /**
     * 周起始
     */
    private int mWeekStart;

    /**
     * 全部显示
     */
    static final int MODE_ALL_MONTH = 0;
    /**
     * 仅显示当前月份
     */
    static final int MODE_ONLY_CURRENT_MONTH = 1;

    /**
     * 自适应显示，不会多出一行，但是会自动填充
     */
    static final int MODE_FIT_MONTH = 2;

    /**
     * 月份显示模式
     */
    private int mMonthViewShowMode;


    /**
     * 默认选择模式
     */
    static final int SELECT_MODE_DEFAULT = 0;

    /**
     * 单选模式
     */
    static final int SELECT_MODE_SINGLE = 1;

    /**
     * 范围选择模式
     */
    static final int SELECT_MODE_RANGE = 2;

    /**
     * 多选模式
     */
    static final int SELECT_MODE_MULTI = 3;

    /**
     * 选择模式
     */
    private int mSelectMode;


    /**
     * 支持转换的最小农历年份
     */
    static final int MIN_YEAR = 1900;
    /**
     * 支持转换的最大农历年份
     */
    private static final int MAX_YEAR = 2099;

    /**
     * 各种字体颜色，看名字知道对应的地方
     */
    private int mCurDayTextColor,
            mCurDayLunarTextColor,
            mWeekTextColor,
            mSchemeTextColor,
            mSchemeLunarTextColor,
            mOtherMonthTextColor,
            mCurrentMonthTextColor,
            mSelectedTextColor,
            mSelectedLunarTextColor,
            mCurMonthLunarTextColor,
            mOtherMonthLunarTextColor;

    private boolean preventLongPressedSelected;

    /**
     * 日历内部左右padding
     */
    private int mCalendarPadding;

    /**
     * 年视图字体大小
     */
    private int mYearViewMonthTextSize,
            mYearViewDayTextSize,
            mYearViewWeekTextSize;

    /**
     * 年视图月份高度和周的高度
     */
    private int mYearViewMonthHeight,
            mYearViewWeekHeight;

    /**
     * 年视图一些margin和padding
     */
    private int mYearViewPadding,
            mYearViewMonthMarginTop,
            mYearViewMonthMarginBottom;

    /**
     * 年视图字体和标记颜色
     */
    private int mYearViewMonthTextColor,
            mYearViewDayTextColor,
            mYearViewSchemeTextColor,
            mYearViewSelectTextColor,
            mYearViewCurDayTextColor,
            mYearViewWeekTextColor;

    /**
     * 星期栏的背景、线的背景、年份背景
     */
    private int mWeekLineBackground,
            mYearViewBackground,
            mWeekBackground;

    /**
     * 星期栏Line margin
     */
    private int mWeekLineMargin;

    /**
     * 星期栏字体大小
     */
    private int mWeekTextSize;

    /**
     * 标记的主题色和选中的主题色
     */
    private int mSchemeThemeColor, mSelectedThemeColor;


    /**
     * 自定义的日历路径
     */
    private String mMonthViewClassPath;

    /**
     * 自定义周视图路径
     */
    private String mWeekViewClassPath;

    /**
     * 自定义年视图路径
     */
    private String mYearViewClassPath;

    /**
     * 自定义周栏路径
     */
    private String mWeekBarClassPath;

    /**
     * 标记文本
     */
    private String mSchemeText;

    /**
     * 最小年份和最大年份
     */
    private int mMinYear, mMaxYear;

    /**
     * 最小年份和最大年份对应最小月份和最大月份
     * when you want set 2015-07 to 2017-08
     */
    private int mMinYearMonth, mMaxYearMonth;

    /**
     * 最小年份和最大年份对应最小天和最大天数
     * when you want set like 2015-07-08 to 2017-08-30
     */
    private int mMinYearDay, mMaxYearDay;

    /**
     * 日期和农历文本大小
     */
    private int mDayTextSize, mLunarTextSize;

    /**
     * 日历卡的项高度
     */
    private int mCalendarItemHeight;

    /**
     * 是否是全屏日历
     */
    private boolean isFullScreenCalendar;

    /**
     * 星期栏的高度
     */
    private int mWeekBarHeight;

    /**
     * 今天的日子
     */
    private Calendar mCurrentDate;


    private boolean mMonthViewScrollable,
            mWeekViewScrollable,
            mYearViewScrollable;

    /**
     * 当前月份和周视图的item位置
     */
    int mCurrentMonthViewItem;

    /**
     * 保存标记位置
     */
    Calendar mIndexCalendar;


    private int mMaxMultiSelectSize;


    private int mMinSelectRange, mMaxSelectRange;

    CalendarViewDelegate(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        LunarCalendar.init(context);

        mCalendarPadding = (int) array.getDimension(R.styleable.CalendarView_calendar_padding, 0);
        mSchemeTextColor = array.getColor(R.styleable.CalendarView_scheme_text_color, 0xFFFFFFFF);
        mSchemeLunarTextColor = array.getColor(R.styleable.CalendarView_scheme_lunar_text_color, 0xFFe1e1e1);
        mSchemeThemeColor = array.getColor(R.styleable.CalendarView_scheme_theme_color, 0x50CFCFCF);
        mMonthViewClassPath = array.getString(R.styleable.CalendarView_month_view);
        mYearViewClassPath = array.getString(R.styleable.CalendarView_year_view);
        mWeekViewClassPath = array.getString(R.styleable.CalendarView_week_view);
        mWeekBarClassPath = array.getString(R.styleable.CalendarView_week_bar_view);
        mWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_week_text_size,
                CalendarUtil.dipToPx(context, 12));
        mWeekBarHeight = (int) array.getDimension(R.styleable.CalendarView_week_bar_height,
                CalendarUtil.dipToPx(context, 40));
        mWeekLineMargin = (int) array.getDimension(R.styleable.CalendarView_week_line_margin,
                CalendarUtil.dipToPx(context, 0));

        mSchemeText = array.getString(R.styleable.CalendarView_scheme_text);
        if (TextUtils.isEmpty(mSchemeText)) {
            mSchemeText = " ";
        }

        mMonthViewScrollable = array.getBoolean(R.styleable.CalendarView_month_view_scrollable, true);
        mWeekViewScrollable = array.getBoolean(R.styleable.CalendarView_week_view_scrollable, true);
        mYearViewScrollable = array.getBoolean(R.styleable.CalendarView_year_view_scrollable, true);

        mDefaultCalendarSelectDay = array.getInt(R.styleable.CalendarView_month_view_auto_select_day,
                FIRST_DAY_OF_MONTH);

        mMonthViewShowMode = array.getInt(R.styleable.CalendarView_month_view_show_mode, MODE_ALL_MONTH);
        mWeekStart = array.getInt(R.styleable.CalendarView_week_start_with, WEEK_START_WITH_SUN);
        mSelectMode = array.getInt(R.styleable.CalendarView_select_mode, SELECT_MODE_DEFAULT);
        mMaxMultiSelectSize = array.getInt(R.styleable.CalendarView_max_multi_select_size, Integer.MAX_VALUE);
        mMinSelectRange = array.getInt(R.styleable.CalendarView_min_select_range, -1);
        mMaxSelectRange = array.getInt(R.styleable.CalendarView_max_select_range, -1);
        setSelectRange(mMinSelectRange, mMaxSelectRange);

        mWeekBackground = array.getColor(R.styleable.CalendarView_week_background, Color.WHITE);
        mWeekLineBackground = array.getColor(R.styleable.CalendarView_week_line_background, Color.TRANSPARENT);
        mYearViewBackground = array.getColor(R.styleable.CalendarView_year_view_background, Color.WHITE);
        mWeekTextColor = array.getColor(R.styleable.CalendarView_week_text_color, 0xFF333333);

        mCurDayTextColor = array.getColor(R.styleable.CalendarView_current_day_text_color, Color.parseColor("#197dff"));
        mCurDayLunarTextColor = array.getColor(R.styleable.CalendarView_current_day_lunar_text_color, Color.parseColor("#197dff"));

        mSelectedThemeColor = array.getColor(R.styleable.CalendarView_selected_theme_color, 0x50CFCFCF);
        mSelectedTextColor = array.getColor(R.styleable.CalendarView_selected_text_color, 0xFF111111);

        mSelectedLunarTextColor = array.getColor(R.styleable.CalendarView_selected_lunar_text_color, 0xFF111111);
        mCurrentMonthTextColor = array.getColor(R.styleable.CalendarView_current_month_text_color, 0xFF111111);
        mOtherMonthTextColor = array.getColor(R.styleable.CalendarView_other_month_text_color, 0xFFe1e1e1);

        mCurMonthLunarTextColor = array.getColor(R.styleable.CalendarView_current_month_lunar_text_color, 0xffe1e1e1);
        mOtherMonthLunarTextColor = array.getColor(R.styleable.CalendarView_other_month_lunar_text_color, 0xffe1e1e1);
        mMinYear = array.getInt(R.styleable.CalendarView_min_year, 1971);
        mMaxYear = array.getInt(R.styleable.CalendarView_max_year, 2055);
        mMinYearMonth = array.getInt(R.styleable.CalendarView_min_year_month, 1);
        mMaxYearMonth = array.getInt(R.styleable.CalendarView_max_year_month, 12);
        mMinYearDay = array.getInt(R.styleable.CalendarView_min_year_day, 1);
        mMaxYearDay = array.getInt(R.styleable.CalendarView_max_year_day, -1);

        mDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_day_text_size,
                CalendarUtil.dipToPx(context, 16));
        mLunarTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_lunar_text_size,
                CalendarUtil.dipToPx(context, 10));
        mCalendarItemHeight = (int) array.getDimension(R.styleable.CalendarView_calendar_height,
                CalendarUtil.dipToPx(context, 56));
        isFullScreenCalendar = array.getBoolean(R.styleable.CalendarView_calendar_match_parent, false);

        //年视图相关
        mYearViewMonthTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_text_size,
                CalendarUtil.dipToPx(context, 18));
        mYearViewDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_day_text_size,
                CalendarUtil.dipToPx(context, 7));
        mYearViewMonthTextColor = array.getColor(R.styleable.CalendarView_year_view_month_text_color, 0xFF111111);
        mYearViewDayTextColor = array.getColor(R.styleable.CalendarView_year_view_day_text_color, 0xFF111111);
        mYearViewSchemeTextColor = array.getColor(R.styleable.CalendarView_year_view_scheme_color, mSchemeThemeColor);
        mYearViewWeekTextColor = array.getColor(R.styleable.CalendarView_year_view_week_text_color, 0xFF333333);
        mYearViewCurDayTextColor = array.getColor(R.styleable.CalendarView_year_view_current_day_text_color, mCurDayTextColor);
        mYearViewSelectTextColor = array.getColor(R.styleable.CalendarView_year_view_select_text_color, 0xFF333333);
        mYearViewWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_text_size,
                CalendarUtil.dipToPx(context, 8));
        mYearViewMonthHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_height,
                CalendarUtil.dipToPx(context, 32));
        mYearViewWeekHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_height,
                CalendarUtil.dipToPx(context, 0));

        mYearViewPadding = (int) array.getDimension(R.styleable.CalendarView_year_view_padding,
                CalendarUtil.dipToPx(context, 6));
        mYearViewMonthMarginTop = (int) array.getDimension(R.styleable.CalendarView_year_view_month_margin_top,
                CalendarUtil.dipToPx(context, 4));
        mYearViewMonthMarginBottom = (int) array.getDimension(R.styleable.CalendarView_year_view_month_margin_bottom,
                CalendarUtil.dipToPx(context, 4));

        if (mMinYear <= MIN_YEAR) mMinYear = MIN_YEAR;
        if (mMaxYear >= MAX_YEAR) mMaxYear = MAX_YEAR;
        array.recycle();
        init();
    }

    private void init() {
        mCurrentDate = new Calendar();
        Date d = new Date();
        mCurrentDate.setYear(CalendarUtil.getDate("yyyy", d));
        mCurrentDate.setMonth(CalendarUtil.getDate("MM", d));
        mCurrentDate.setDay(CalendarUtil.getDate("dd", d));
        mCurrentDate.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(mCurrentDate);
        setRange(mMinYear, mMinYearMonth, mMaxYear, mMaxYearMonth);
    }


    private void setRange(int minYear, int minYearMonth,
                          int maxYear, int maxYearMonth) {
        this.mMinYear = minYear;
        this.mMinYearMonth = minYearMonth;
        this.mMaxYear = maxYear;
        this.mMaxYearMonth = maxYearMonth;
        if (this.mMaxYear < mCurrentDate.getYear()) {
            this.mMaxYear = mCurrentDate.getYear();
        }
        if (this.mMaxYearDay == -1) {
            this.mMaxYearDay = CalendarUtil.getMonthDaysCount(this.mMaxYear, mMaxYearMonth);
        }
        int y = mCurrentDate.getYear() - this.mMinYear;
        mCurrentMonthViewItem = 12 * y + mCurrentDate.getMonth() - this.mMinYearMonth;
    }

    int getMinYear() {
        return mMinYear;
    }

    int getMaxYear() {
        return mMaxYear;
    }

    int getMinYearMonth() {
        return mMinYearMonth;
    }

    int getMaxYearMonth() {
        return mMaxYearMonth;
    }


    int getWeekStart() {
        return mWeekStart;
    }


    int getDefaultCalendarSelectDay() {
        return mDefaultCalendarSelectDay;
    }


    final void setSelectRange(int minRange, int maxRange) {
        if (minRange > maxRange && maxRange > 0) {
            mMaxSelectRange = minRange;
            mMinSelectRange = minRange;
            return;
        }
        if (minRange <= 0) {
            mMinSelectRange = -1;
        } else {
            mMinSelectRange = minRange;
        }
        if (maxRange <= 0) {
            mMaxSelectRange = -1;
        } else {
            mMaxSelectRange = maxRange;
        }
    }

    Calendar getCurrentDay() {
        return mCurrentDate;
    }

    int getMinYearDay() {
        return mMinYearDay;
    }

    int getMaxYearDay() {
        return mMaxYearDay;
    }

    Calendar createCurrentDate() {
        Calendar calendar = new Calendar();
        calendar.setYear(mCurrentDate.getYear());
        calendar.setWeek(mCurrentDate.getWeek());
        calendar.setMonth(mCurrentDate.getMonth());
        calendar.setDay(mCurrentDate.getDay());
        calendar.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    final Calendar getMinRangeCalendar() {
        Calendar calendar = new Calendar();
        calendar.setYear(mMinYear);
        calendar.setMonth(mMinYearMonth);
        calendar.setDay(mMinYearDay);
        calendar.setCurrentDay(calendar.equals(mCurrentDate));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    @SuppressWarnings("unused")
    final Calendar getMaxRangeCalendar() {
        Calendar calendar = new Calendar();
        calendar.setYear(mMaxYear);
        calendar.setMonth(mMaxYearMonth);
        calendar.setDay(mMaxYearDay);
        calendar.setCurrentDay(calendar.equals(mCurrentDate));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }


}
