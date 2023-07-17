package com.lifehopehealthapp.myTestOrderList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrderListData
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils

class OrderListPagingAdapter(myTestOrderListActivity: MyTestOrderListActivity) :
    PagingDataAdapter<OrderListData, OrderListPagingAdapter.MyViewHolder>(DiffUntilCallBack()) {

    private var mContext: MyTestOrderListActivity

    init {
        mContext = myTestOrderListActivity
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val workPackage = getItem(position)
        if (workPackage != null) {
            holder.bind(getItem(position)!!, mContext)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_order_list, parent, false)
        return MyViewHolder(inflater)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTestHeading =
            itemView.findViewById(R.id.textview_test_heading) as AppCompatTextView
        val textViewDate =
            itemView.findViewById(R.id.textview_test_date) as AppCompatTextView
        val textViewScheduleDate =
            itemView.findViewById(R.id.textview_schedule_time) as AppCompatTextView
        val textviewPrice =
            itemView.findViewById(R.id.textview_price) as AppCompatTextView
        val textviewOrderId =
            itemView.findViewById(R.id.textview_test_orderid) as AppCompatTextView
        val textviewTestType =
            itemView.findViewById(R.id.textview_test_type) as AppCompatTextView
        val textviewTestStatus =
            itemView.findViewById(R.id.textview_test_status) as AppCompatTextView
        val imageViewTestImage =
            itemView.findViewById(R.id.imageview_testImage) as AppCompatImageView
        val textviewTestCount =
            itemView.findViewById(R.id.textview_count) as AppCompatTextView
        val textviewNotes =
            itemView.findViewById(R.id.textviewNotes) as AppCompatTextView
        val layoutTestCount =
            itemView.findViewById(R.id.layout_testcount) as ConstraintLayout
        val layoutStatus = itemView.findViewById(R.id.layout_status) as ConstraintLayout
        val layoutType = itemView.findViewById(R.id.layout_Type) as ConstraintLayout

        fun bind(
            item: OrderListData,
            mContext: MyTestOrderListActivity
        ) {
            var scheduleDate: Int? = 0

            textViewTestHeading.text = item!!.testName
            textviewPrice.text =
                mContext.resources.getString(R.string.text_price) + item.amount.toString()
            textviewOrderId.text =
                mContext.resources.getString(R.string.text_order_id) + item.orderNo.toString()
            Log.e("orderNo", item.orderNo.toString())
            if (item.testImage.equals("")) {
                Utils.showToast(mContext, "", true)
            } else {
                //imageViewTestImage.loadSvg(item.testImage!!)
                Utils.loadSvg(item.testImage!!, mContext, imageViewTestImage)
            }

            Log.e("testImage", item.testImage.toString())

            if (item.getNotes() == null || item.getNotes() == "") {
                textviewNotes.isVisible = false
            } else {
                textviewNotes.isVisible = true
                textviewNotes.text =
                    mContext.resources.getString(R.string.text_notes) + item.getNotes()
            }

            if (item.testType!!.equals("1")) {
                layoutType.isVisible = true
                textviewTestType.text = "S"
            } else {
                layoutType.isVisible = true
                textviewTestType.text = "H"
            }
            if (item.addonCount != 0) {
                layoutTestCount.isVisible = true
                textviewTestCount.text = "+" + item.addonCount.toString()
            } else {
                layoutTestCount.visibility = View.INVISIBLE
            }
            scheduleDate = item.scheduleDate!!
            if (scheduleDate == 0) {
                textViewScheduleDate.isVisible = false
            } else {
                textViewScheduleDate.isVisible = true
                textViewScheduleDate.text =
                    mContext.resources.getString(R.string.text_schedule_txt) + Utils.getDateCurrentTimeZone(
                        item.scheduleDate!!.toLong(),
                        Constants.TIMESTAMPTODATE_TWO!!
                    )
            }

            textViewDate.text =
                Utils.getDateCurrentTimeZone(
                    item.orderDate!!.toLong(),
                    Constants.TIMESTAMPTODATE_TWO!!
                )

            Log.e("orderDate", item.orderDate.toString())
            Log.e("schedule", item.scheduleDate!!.toString())
            Log.e(
                "orderDate1",
                Utils.getDateFromUTCTimestamp(
                    item.orderDate!!.toLong(),
                    Constants.TIMESTAMPTODATE_TWO
                ).toString()
            )


            val STATUS: Int = item.status!!
            when (STATUS) {

                1 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_pending)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_pending
                    )
                }
                2 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_approved)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_approved
                    )
                }
                3 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_rejected)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_rejected
                    )
                }
                4 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_inprogress)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_inprogress
                    )
                }
                5 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_complete)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_completed
                    )
                }
                6 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_declined)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_rejected
                    )
                }
                7 -> {
                    layoutStatus.isVisible = true
                    textviewTestStatus.text =
                        mContext.resources.getString(R.string.text_refunded)
                    layoutStatus.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.divider_pending
                    )
                }
            }
        }
    }

    class DiffUntilCallBack : DiffUtil.ItemCallback<OrderListData>() {
        override fun areItemsTheSame(oldItem: OrderListData, newItem: OrderListData): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderListData, newItem: OrderListData): Boolean {
            return oldItem.testName == newItem.testName && oldItem.testType == newItem.testType && oldItem.orderId == newItem.orderId &&
                    oldItem.addonCount == newItem.addonCount && oldItem.amount == newItem.amount && oldItem.orderDate == newItem.orderDate &&
                    oldItem.scheduleDate == newItem.scheduleDate && oldItem.status == newItem.status && oldItem.testImage == newItem.testImage
        }
    }
}