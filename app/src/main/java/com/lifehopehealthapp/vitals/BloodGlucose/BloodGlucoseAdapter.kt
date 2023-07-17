package com.lifehopehealthapp.vitals.BloodGlucose

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.BarDataList
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import java.util.*

class BloodGlucoseAdapter(
    barDataList: List<BarDataList?>?,
    bloodGlucoseActivity: BloodGlucoseActivity
) : RecyclerView.Adapter<BloodGlucoseAdapter.ViewHolder>() {

    private var mData: List<BarDataList?>?
    private var mContext: BloodGlucoseActivity

    init {
        this.mContext = bloodGlucoseActivity
        this.mData = barDataList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blood_glucose, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time) / 1000
        val timeStamp =mData?.get(position)!!.currentDate!!.toInt() - offsetFromUtc

        holder.textViewDateTime.text = Utils.getDateCurrentTimeZone(
            timeStamp.toLong(),
            Constants.TIMESTAMP_DOB.toString()
        )

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        holder.childRecyclerView.layoutManager = layoutManager
        holder.childRecyclerView.setHasFixedSize(true)

        val childRecyclerViewAdapter =
            ChildRecyclerViewAdapter(
                mData!![position]!!.detailList,
                holder.childRecyclerView.context,

            )
        holder.childRecyclerView.adapter = childRecyclerViewAdapter
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDateTime =
            itemView.findViewById(R.id.textViewDateTime) as AppCompatTextView
        val childRecyclerView = itemView.findViewById(R.id.recyclerViewChildData) as RecyclerView

    }

}