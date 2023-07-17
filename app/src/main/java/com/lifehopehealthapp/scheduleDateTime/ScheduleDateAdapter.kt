package com.lifehopehealthapp.scheduleDateTime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.ListData
import java.util.ArrayList

class ScheduleDateAdapter(
    scheduleDateTimeActivity: Context,
    data: ArrayList<ListData>?,
) : RecyclerView.Adapter<ScheduleDateAdapter.ViewHolder>() {

    private var mContext: Context? = null
    private var mData: List<ListData>? = null
    private var row_index: Int = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleDateAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_date, parent, false)
        return ViewHolder(v)
    }

    init {
        mContext = scheduleDateTimeActivity
        mData = data
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    override fun onBindViewHolder(holder: ScheduleDateAdapter.ViewHolder, position: Int) {

        holder.textViewDay.text = mData!![position].dayText
        holder.textViewDate.text = mData!![position].day

        holder.layoutDateView.setOnClickListener {
            row_index = position;
            notifyDataSetChanged();
        }

        if (row_index == position) {
            holder.layoutDateView.setBackgroundResource(R.drawable.divider_select_date_border)
            holder.textViewDay.setTextColor(mContext!!.resources.getColor(R.color.white))
            holder.textViewDate.setTextColor(mContext!!.resources.getColor(R.color.white))
        } else {
            holder.layoutDateView.setBackgroundResource(R.drawable.divider_unselect_date_border)
            holder.textViewDay.setTextColor(mContext!!.resources.getColor(R.color.black))
            holder.textViewDate.setTextColor(mContext!!.resources.getColor(R.color.black))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDay = itemView.findViewById(R.id.textviewDay) as TextView
        val textViewDate = itemView.findViewById(R.id.textviewDate) as TextView
        val layoutDateView = itemView.findViewById(R.id.layout_DateView) as ConstraintLayout
    }
}