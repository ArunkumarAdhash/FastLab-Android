package com.lifehopehealthapp.doctorAppointmentDates

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.ListOfDate
import com.lifehopehealthapp.ResponseModel.MorningSlot
import com.lifehopehealthapp.ResponseModel.SaveDoctorAppointment
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SetAvailabilityTimeAdapter(
    val mSelectMrngTime: ArrayList<MorningSlot>,
    val mContext: Context,
    val mAvailabilityTime: ArrayList<ListOfDate>,
    val selectSession: String,
    val SELECTPOSITION: Int
) : RecyclerView.Adapter<SetAvailabilityTimeAdapter.ViewHolder>() {

    private var row_index: Int? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SetAvailabilityTimeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_time, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(
        holder: SetAvailabilityTimeAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

        val mStartTime = mSelectMrngTime[position].startTime!!.replace("+00:00", ".000Z");
        val mEndTime = mSelectMrngTime[position].endTime!!.replace("+00:00", ".000Z");

        Log.e(
            "Slot->",
            mSelectMrngTime[position].endTime!! + " " + mSelectMrngTime[position].startTime!! + " " + mSelectMrngTime[position].slotId!!
        )

        val startTime: LocalDateTime =
            LocalDateTime.parse(mStartTime, inputFormatter)
        val mStartTiming: String = outputFormatter.format(startTime)

        val endTime: LocalDateTime =
            LocalDateTime.parse(mEndTime, inputFormatter)
        val mEndTiming: String = outputFormatter.format(endTime)

        holder.textViewDay.setText(
            mStartTiming
        )

        holder.layoutTime.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
        }
        if (row_index == position) {
            if (mSelectMrngTime[position].isActive!!) {
            } else {
                val data = SaveDoctorAppointment()
                data.mStartTime = mSelectMrngTime[position].startTime!!
                data.mEndTime = mSelectMrngTime[position].endTime!!
                data.mSlotID = mSelectMrngTime[position].slotId!!
                EventBus.getDefault().post(data)
                holder.layoutTime.setBackgroundResource(R.drawable.divider_time_select)
                holder.textViewDay.setTextColor(mContext.resources.getColor(R.color.white))
            }

        } else {
            holder.layoutTime.setBackgroundResource(R.drawable.alert_bg)
            holder.textViewDay.setTextColor(mContext.resources.getColor(R.color.black))
        }

        if (mSelectMrngTime[position].isActive!!) {
            Log.e("row_", position.toString())
            holder.layoutTime.setBackgroundResource(R.drawable.divider_time_block)
            holder.textViewDay.setTextColor(mContext.resources.getColor(R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return mSelectMrngTime.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDay = itemView.findViewById(R.id.textviewTime) as AppCompatTextView
        val layoutTime = itemView.findViewById(R.id.layoutTime) as LinearLayout
    }
}