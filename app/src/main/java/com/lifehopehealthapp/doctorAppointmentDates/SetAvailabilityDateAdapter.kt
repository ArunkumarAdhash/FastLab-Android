package com.lifehopehealthapp.doctorAppointmentDates

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.ListOfDate
import com.lifehopehealthapp.utils.Constants.TIMESTAMP_DATE
import com.lifehopehealthapp.utils.Constants.TIMESTAMP_MONTH
import com.lifehopehealthapp.utils.Utils.getDay
import java.util.*

class SetAvailabilityDateAdapter(
    val mContext: SetAppointmentDateActivity,
    val mAvailabilityDate: ArrayList<ListOfDate>
) : RecyclerView.Adapter<SetAvailabilityDateAdapter.ViewHolder>() {

    private var row_index: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SetAvailabilityDateAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_date, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: SetAvailabilityDateAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.textviewMonth.setText(getDay(mAvailabilityDate[position].date!!, TIMESTAMP_MONTH!!))
        holder.textViewDate.setText(getDay(mAvailabilityDate[position].date!!, TIMESTAMP_DATE!!))

        holder.layoutDateView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
        }

        if (row_index == position) {
            holder.layoutDateView.setBackgroundResource(R.drawable.divider_select_date_border)
            holder.textViewDate.setTextColor(mContext!!.resources.getColor(R.color.white))
            holder.textviewMonth.setTextColor(mContext!!.resources.getColor(R.color.white))
        } else {
            holder.layoutDateView.setBackgroundResource(R.drawable.divider_unselect_date_border)
            holder.textViewDate.setTextColor(mContext!!.resources.getColor(R.color.black))
            holder.textviewMonth.setTextColor(mContext!!.resources.getColor(R.color.black))
        }
        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(mAvailabilityDate)
        Log.e("Finial---->", json.toString())
    }

    override fun getItemCount(): Int {
        return mAvailabilityDate.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate = itemView.findViewById(R.id.textviewDate) as AppCompatTextView
        val textviewMonth = itemView.findViewById(R.id.textviewDay) as AppCompatTextView
        val layoutDateView = itemView.findViewById(R.id.layout_DateView) as ConstraintLayout
    }
}