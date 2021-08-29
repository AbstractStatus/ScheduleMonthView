package com.abstractstatus.viewlibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abstractstatus.viewlibrary.R
import com.abstractstatus.viewlibrary.entity.ScheduleEntity
import com.abstractstatus.viewlibrary.view.OnlyCircleView


/**
 ** Created by AbstractStatus at 2021/8/22 10:07.
 */
class ScheduleListAdapter : RecyclerView.Adapter<ScheduleListAdapter.ScheduleListViewHolder>(){
    private var scheduleList: List<ScheduleEntity> = ArrayList()

    interface OnScheduleItemClickListener {
        fun onScheduleItemClick(schedule_id: String?, type: Int)
    }





    inner class ScheduleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var circleScheduleType: OnlyCircleView? = itemView.findViewById(R.id.circle_schedule_type);
        var tvScheduleName: TextView? = itemView.findViewById(R.id.tv_schedule_name)
        var tvScheduleTime: TextView? = itemView.findViewById(R.id.tv_schedule_time)
        var tvScheduleLocation: TextView? = itemView.findViewById(R.id.tv_schedule_location)
        var tvScheduleSource: TextView? = itemView.findViewById(R.id.tv_schedule_source)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleListViewHolder {
        return ScheduleListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.month_item_schedule, parent, false))
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduleListViewHolder, position: Int) {
        val scheduleEntity = scheduleList[position]

        holder.tvScheduleName?.text = scheduleEntity.scheduleName
        holder.tvScheduleTime?.text = scheduleEntity.scheduleTime
        if (scheduleEntity.scheduleLocation.isNotEmpty()) {
            holder.tvScheduleLocation?.text = scheduleEntity.scheduleLocation
        } else {
            holder.tvScheduleLocation?.visibility = View.GONE
        }

        when (scheduleEntity.scheduleType) {
            0 -> {
                holder.tvScheduleSource?.visibility = View.GONE
            }
            1 -> {
                holder.tvScheduleSource?.text = "来自手机"
            }
            else -> {
            }
        }

        holder.itemView.setOnClickListener {

        }

    }
}