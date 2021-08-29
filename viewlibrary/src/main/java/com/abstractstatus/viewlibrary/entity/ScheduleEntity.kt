package com.abstractstatus.viewlibrary.entity

/**
 ** Created by AbstractStatus at 2021/8/22 9:58.
 */
data class ScheduleEntity(
        var scheduleId: String,
        var scheduleType: Int,
        var scheduleName: String,
        var scheduleTime: String,
        var scheduleLocation: String,
        var scheduleStatus: Int) {

}