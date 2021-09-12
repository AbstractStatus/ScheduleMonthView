package com.abstractstatus.viewlibrary.util

import java.util.*

/**
 ** Created by AbstractStatus at 2021/8/22 10:01.
 */


object CalendarUtil {
    //默认月视图显示6 * 7天
    private const val DEFAULT_MONTH_VIEW_DAYS_NUM = 42

    fun get42DaysDataByMonthPosition(pos: Int): Array<IntArray> {
        val calendar = Calendar.getInstance()
        val res = Array(DEFAULT_MONTH_VIEW_DAYS_NUM) { IntArray(5) }
        val nowYearAndMonth = getNowYearAndMonth()
        val yearAndMonth = getAddYearAndMonth(nowYearAndMonth[0], nowYearAndMonth[1], pos - MonthViewDelegate.startMonthPos)
        calendar[yearAndMonth[0], yearAndMonth[1] - 1] = 1
        val curMonthFirstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.add(Calendar.DATE, -curMonthFirstDayOfWeek)
        for (i in 0 until DEFAULT_MONTH_VIEW_DAYS_NUM) {
            calendar.add(Calendar.DATE, 1)
            res[i] = getOneDayData(calendar, yearAndMonth[1])
        }
        return res
    }

    //获取某日在月视图内的pos, 0~42
    fun getTodayPosition(dates: Array<IntArray>): Int {
        val curTimeInfo = getCurTimeIntArrInfo()
        for (i in 0 until DEFAULT_MONTH_VIEW_DAYS_NUM) {
            if (dates[i][0] == curTimeInfo[0] && dates[i][1] == curTimeInfo[1] && dates[i][2] == curTimeInfo[3]) {
                return i
            }
        }
        return -1
    }

    //日期的绝对id
    fun getRawPosition(year: Int, month: Int, dayOfMonth: Int): Int {
        return dayOfMonth + month * 100 + (year - 2000) * 10000
    }

    //获取当前的年和月
    fun getNowYearAndMonth(): IntArray {
        val calendar = Calendar.getInstance()
        return intArrayOf(calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1)
    }

    //根据年和月的数据获取上一个年和月的数据
    fun getPreYearAndMonth(year: Int, month: Int): IntArray {
        return getAddYearAndMonth(year, month, -1)
    }

    //根据年和月的数据获取下一个年和月的数据
    fun getNextYearAndMonth(year: Int, month: Int): IntArray {
        return getAddYearAndMonth(year, month, 1)
    }

    //根据年和月的数据获取月差值年和月的数据
    fun getAddYearAndMonth(year: Int, month: Int, addMonth: Int): IntArray {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        calendar.add(Calendar.MONTH, addMonth)
        return intArrayOf(calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1)
    }

    //获取某个月份42天的第一天日历数据
    fun get42FirstDateByYearAndMonth(year: Int, month: Int): IntArray {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        val curMonthFirstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.add(Calendar.DATE, -curMonthFirstDayOfWeek)
        calendar.add(Calendar.DATE, 1)
        return getOneDayData(calendar)
    }

    //获取某个月份42天的最后天日历数据
    fun get42LastDateByYearAndMonth(year: Int, month: Int): IntArray {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        val curMonthFirstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.add(Calendar.DATE, -curMonthFirstDayOfWeek)
        for (i in 0 until DEFAULT_MONTH_VIEW_DAYS_NUM) {
            calendar.add(Calendar.DATE, 1)
        }
        return getOneDayData(calendar)
    }

    //获取某个月份42天的某一天的日历数据
    fun get42PosDateByYearAndMonth(year: Int, month: Int, pos: Int): IntArray {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        val curMonthFirstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.add(Calendar.DATE, -curMonthFirstDayOfWeek)
        for (i in 0 until pos + 1) {
            calendar.add(Calendar.DATE, 1)
        }
        return getOneDayData(calendar)
    }

    /**
     * 获取某个月份的42天日历数据
     * @param year
     * @param month 从1开始
     * @return
     */
    fun get42DaysDataByYearAndMonth(year: Int, month: Int): Array<IntArray> {
        val calendar = Calendar.getInstance()
        val res = Array(DEFAULT_MONTH_VIEW_DAYS_NUM) { IntArray(5) }
        calendar[year, month - 1] = 1
        val curMonthFirstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.add(Calendar.DATE, -curMonthFirstDayOfWeek)
        for (i in 0 until DEFAULT_MONTH_VIEW_DAYS_NUM) {
            calendar.add(Calendar.DATE, 1)
            res[i] = getOneDayData(calendar, month)
        }
        return res
    }

    /**
     * 获取当前月份的42天日历数据
     * @return [[year, month, dayOfMonth, dayOfWeek],
     * [year, month, dayOfMonth, dayOfWeek]...]
     */
    fun get42DaysDataByCurMonth(): Array<IntArray> {
        val calendar = Calendar.getInstance()
        val res = Array(DEFAULT_MONTH_VIEW_DAYS_NUM) { IntArray(4) }

        //从0开始计算的月
        val curMonth = calendar[Calendar.MONTH]
        val curYear = calendar[Calendar.YEAR]
        calendar[curYear, curMonth] = 1
        val curMonthFirstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.add(Calendar.DATE, -curMonthFirstDayOfWeek)
        for (i in 0 until DEFAULT_MONTH_VIEW_DAYS_NUM) {
            calendar.add(Calendar.DATE, 1)
            res[i] = getOneDayData(calendar, curMonth + 1)
        }
        return res
    }

    //获取某天的日历数据，包括年、月、日、星期
    fun getOneDayData(calendar: Calendar): IntArray {
        val res = IntArray(4)
        res[0] = calendar[Calendar.YEAR]
        res[1] = calendar[Calendar.MONTH] + 1
        res[2] = calendar[Calendar.DAY_OF_MONTH]
        res[3] = calendar[Calendar.DAY_OF_WEEK] - 1
        return res
    }

    //获取某天的日历数据，包括年、月、日、星期、是否本月
    fun getOneDayData(calendar: Calendar, month: Int): IntArray {
        val res = IntArray(5)
        res[0] = calendar[Calendar.YEAR]
        res[1] = calendar[Calendar.MONTH] + 1
        res[2] = calendar[Calendar.DAY_OF_MONTH]
        res[3] = calendar[Calendar.DAY_OF_WEEK] - 1
        res[4] = if (res[1] == month) 1 else 0
        return res
    }

    /**
     * 获取当前时间所有日历数据
     * -----------------------
     * 注意：
     * Month返回从0开始，故调用需要加一
     * DayOfWeek是星期几，从星期天开始计算，且1代表星期天
     * DAY_OF_WEEK_IN_MONTH表示从本月最开始的周日计算，现在是第几个星期
     * HOUR_OF_DAY是24小时制
     * HOUR是12小时制
     * MILLISECOND不是long类型那个，是这一秒内多少毫秒了
     */
    fun getCurTimeIntArrInfo(): IntArray {
        val calendar = Calendar.getInstance()
        val res = IntArray(12)
        res[0] = calendar[Calendar.YEAR]
        res[1] = calendar[Calendar.MONTH] + 1
        res[2] = calendar[Calendar.DAY_OF_YEAR]
        res[3] = calendar[Calendar.DAY_OF_MONTH]
        res[4] = calendar[Calendar.DAY_OF_WEEK]
        res[5] = calendar[Calendar.DAY_OF_WEEK_IN_MONTH]
        res[6] = calendar[Calendar.HOUR_OF_DAY]
        res[7] = calendar[Calendar.HOUR]
        res[8] = calendar[Calendar.MINUTE]
        res[9] = calendar[Calendar.SECOND]
        res[10] = calendar[Calendar.MILLISECOND]
        res[11] = calendar[Calendar.DATE]
        return res
    }

    //打印当前时间所有日历数据
    fun getCurTimeStringInfo(): String {
        val curTimeIntArrInfo = getCurTimeIntArrInfo()
        return """
            
            YEAR  ${curTimeIntArrInfo[0]}
            MONTH  ${curTimeIntArrInfo[1]}
            DAY_OF_YEAR  ${curTimeIntArrInfo[2]}
            DAY_OF_MONTH  ${curTimeIntArrInfo[3]}
            DAY_OF_WEEK  ${curTimeIntArrInfo[4]}
            DAY_OF_WEEK_IN_MONTH  ${curTimeIntArrInfo[5]}
            HOUR_OF_DAY  ${curTimeIntArrInfo[6]}
            HOUR  ${curTimeIntArrInfo[7]}
            MINUTE  ${curTimeIntArrInfo[8]}
            SECOND  ${curTimeIntArrInfo[9]}
            MILLISECOND  ${curTimeIntArrInfo[10]}
            DATE  ${curTimeIntArrInfo[11]}
            """.trimIndent()
    }

    //打印年月日
    fun formatToYearMonthDay(year: Int, month: Int, dayOfMonth: Int): String {
        return (year.toString() + "-" + (if (month < 10) "0$month" else month)
                + "-" + if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth)
    }

    /**
     * 打印年月日-星期几
     * @param year 年
     * @param month 月
     * @param dayOfMonth 日
     * @param dayOfWeek 从周日算起
     */
    fun formatToYearMonthDayAndOfWeek(year: Int, month: Int, dayOfMonth: Int, dayOfWeek: Int): String {
        return (year.toString() + "-" + month + "-" + dayOfMonth + "-"
                + WeekDay.values()[dayOfWeek - 1].toString())
    }

    //打印二维整形数组
    fun format2DIntArr(arr: Array<IntArray?>?): String {
        val n1 = arr?.size ?: 0
        val n2 = if (arr != null && arr[0] != null) arr[0]?.size else 0
        val res = StringBuilder()
        for (i1 in 0 until n1) {
            for (i2 in 0 until n2!!) {
                res.append(arr!![i1]!![i2]).append("  ")
            }
            res.append("\n")
        }
        return res.toString()
    }

    /**
     * 测试日历单例对象的数据获取是否一致
     * 单例，结果相同
     */
    fun get10000TimesMillisecond(): String {
        val calendar = Calendar.getInstance()
        val res = StringBuilder()
        for (i in 0..9999) {
            res.append("\n").append(calendar[Calendar.MILLISECOND])
        }
        return res.toString()
    }

    //xxxx-xx-xx
    fun decodeDate(s: String): IntArray {
        val res = IntArray(3)
        val ss = s.split("-".toRegex()).toTypedArray()
        for (i in res.indices) {
            res[i] = ss[i].toInt()
        }
        return res
    }

    //xx:xx:xx
    fun decodeTime(s: String): IntArray {
        val res = IntArray(3)
        val ss = s.split(":".toRegex()).toTypedArray()
        for (i in res.indices) {
            res[i] = ss[i].toInt()
        }
        return res
    }

    //xxxx-xx-xx xx:xx:xx
    fun decodeDateTime(s: String): IntArray {
        val res = IntArray(6)
        val ss = s.split(" ".toRegex()).toTypedArray()
        val res1 = decodeDate(ss[0])
        val res2 = decodeTime(ss[1])
        for (i in res.indices) {
            if (i < 3) {
                res[i] = res1[i]
            } else {
                res[i] = res2[i - 3]
            }
        }
        return res
    }

    //年月日时分秒
    fun getCurTime(): IntArray {
        val calendar = Calendar.getInstance()
        val res = IntArray(6)
        res[0] = calendar[Calendar.YEAR]
        res[1] = calendar[Calendar.MONTH] + 1
        res[2] = calendar[Calendar.DAY_OF_MONTH]
        res[3] = calendar[Calendar.HOUR_OF_DAY]
        res[4] = calendar[Calendar.MINUTE]
        res[5] = calendar[Calendar.SECOND]
        return res
    }

    //默认星期从周日开始
    enum class WeekDay {
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }
}
