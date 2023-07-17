package com.lifehopehealthapp.scheduleDateTime

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.Morning
import com.lifehopehealthapp.ResponseModel.ScheduleTimeModel
import org.greenrobot.eventbus.EventBus
import java.util.*

class ScheduleTimeAdapter(
    mSelectMrngTime: ArrayList<Morning>,
    selectSession: Context,
) :
    RecyclerView.Adapter<ScheduleTimeAdapter.ViewHolder>() {


    private var mDataMrng: List<Morning>? = null
    private var row_index: Int? = null
    private var mContext: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_time, parent, false)
        return ViewHolder(v)
    }

    init {
        mDataMrng = mSelectMrngTime
        mContext = selectSession
    }

    override fun getItemCount(): Int {
        return if (mDataMrng == null) 0 else mDataMrng!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val datavalue = mDataMrng!![position].time
        holder.textViewDay.text = datavalue

        holder.layoutTime.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            var selectTime = ScheduleTimeModel()
            selectTime.selectTime = mDataMrng!![position].time
            EventBus.getDefault().post(selectTime)
        }
        if (row_index == position) {
            if (mDataMrng!![position].isAvailable == 0) {
            } else {
                Log.e("row_", position.toString())
                holder.layoutTime.setBackgroundResource(R.drawable.divider_time_select)
                holder.textViewDay.setTextColor(mContext!!.resources.getColor(R.color.white))
            }

        } else {
            holder.layoutTime.setBackgroundResource(R.drawable.alert_bg)
            holder.textViewDay.setTextColor(mContext!!.resources.getColor(R.color.black))
        }

        if (mDataMrng!![position].isAvailable == 0) {
            Log.e("row_", position.toString())
            holder.layoutTime.setBackgroundResource(R.drawable.divider_time_block)
            holder.textViewDay.setTextColor(mContext!!.resources.getColor(R.color.black))
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDay = itemView.findViewById(R.id.textviewTime) as AppCompatTextView
        val layoutTime = itemView.findViewById(R.id.layoutTime) as LinearLayout
    }
}