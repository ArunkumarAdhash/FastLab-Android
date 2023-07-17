package com.lifehopehealthapp.vitals.BloodGlucose

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DetailList
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import java.util.*

class ChildRecyclerViewAdapter(
    detailList: List<DetailList>?,
    context: Context
) : RecyclerView.Adapter<ChildRecyclerViewAdapter.ViewHolder>() {

    private var mContext: Context
    private var mChildData: List<DetailList>?

    init {
        this.mContext = context
        this.mChildData = detailList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.child_recyclerview_items, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mChildData!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem: DetailList = mChildData!!.get(position)

        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time) / 1000
        val timeStamp =
            currentItem.mealDate!!.toInt() - offsetFromUtc


        holder.textViewDateValue.text = Utils.getDateCurrentTimeZone(
            timeStamp.toLong(),
            Constants.TIMESTAMPTODATE_TWO.toString()
        )

        holder.textViewBloodGlucoseValue.text =
            currentItem.mealValue.toString() + " " + mContext.resources.getString(
                R.string.text_glucose_label
            )

        holder.textViewMealTimeValue.text =
            currentItem.mealType

    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val textViewBloodGlucoseValue =
            itemView.findViewById(R.id.textViewBloodGlucoseValue) as AppCompatTextView
        val textViewDateValue =
            itemView.findViewById(R.id.textViewDateValue) as AppCompatTextView
        val textViewMealTimeValue =
            itemView.findViewById(R.id.textViewMealTimeValue) as AppCompatTextView
    }
}