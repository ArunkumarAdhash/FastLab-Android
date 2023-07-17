package com.lifehopehealthapp.myTestOrderList

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrderListData
import com.lifehopehealthapp.trackTestOrders.TrackOrderActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MyTestOrderTestListAdapter(
    aDummyList: ArrayList<OrderListData>?,
    myTestOrderListActivity: MyTestOrderListActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var mContext: MyTestOrderListActivity
    private var mData: MutableList<OrderListData>?
    private var lastPosition = -1
    private var scheduleDate: Int? = 0
    private val LOADING = 1
    private val ITEM = 0
    private var isLoadingAdded: Boolean = false
    var decimalWithTwoDigit: DecimalFormat = DecimalFormat("#,###")

    init {
        mData = aDummyList
        mContext = myTestOrderListActivity
    }


    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM
        } else {
            if (position == mData!!.size - 1 && isLoadingAdded) LOADING else ITEM
        }
    }

    fun add(r: OrderListData) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }


    fun getItem(position: Int): OrderListData? {
        return mData!![position]
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = mData!!.size - 1
        val result: OrderListData? = getItem(position)

        if (result != null) {
            mData!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(OrderListData())
    }

    fun addAll(moveResults: List<OrderListData>) {
        for (result in moveResults) {
            add(result)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_order_list, parent, false)
        return ViewHolder(v)*/
        var viewHolder: RecyclerView.ViewHolder? = null

        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem: View =
                    inflater.inflate(R.layout.item_test_order_list, parent, false)
                viewHolder = ItemsTestView(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingView(viewLoading)
            }

        }
        return viewHolder!!

    }

    class LoadingView(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mRootMainLAY: RelativeLayout? = null

        var aProgressBar: ProgressBar? = null

        init {


            aProgressBar = itemView.findViewById(R.id.loadmore_progress) as ProgressBar


        }
    }


    fun addAllSearch(results: ArrayList<OrderListData>) {
        for (result in results) {
            add(result)
        }
    }


    @SuppressLint("NewApi")
    override fun onBindViewHolder(aViewHolder: RecyclerView.ViewHolder, position: Int) {
        val aProductItem: OrderListData? = mData!![position]
        when (getItemViewType(position)) {
            ITEM -> {
                val holder = aViewHolder as ItemsTestView?

                holder!!.textViewTestHeading.text = mData!![position].testName


              /*  holder.textviewPrice.text =
                    mContext.resources.getString(R.string.text_price) + decimalWithTwoDigit.format(mData!![position].amount)*/


                holder.textviewPrice.text  =
                    mContext.resources.getString(R.string.text_price_new) +" "+ NumberFormat.getCurrencyInstance(
                        Locale("en", "US")
                    ).format(mData!![position].amount)


                holder.textviewOrderId.text =
                        mContext.resources.getString(R.string.text_order_id) + " "+mData!![position].orderNo!!
                Log.e("orderNo", mData!![position].orderNo.toString())
                if (mData!![position].testImage.equals("")) {
                    Utils.showToast(mContext, "", true)
                } else {
                    holder.imageViewTestImage.loadSvg(mData!![position].testImage!!)
                }

                Log.e("testImage", mData!![position].testImage.toString())

                if (mData!![position]!!.getNotes() == null || mData!![position]!!.getNotes() == "") {
                    holder.textviewNotes.isVisible = false
                } else {
                    holder.textviewNotes.isVisible = true
                    holder.textviewNotes.text =
                        mContext.resources.getString(R.string.text_notes) +" "+ mData!![position]!!.getNotes()
                }

                if (mData!![position]!!.testType == 1) {
                    holder.layoutType.isVisible = true
                    holder.textviewTestType.text = "S"
                } else {
                    holder.layoutType.isVisible = true
                    holder.textviewTestType.text = "H"
                }
                if (mData!![position]!!.addonCount != 0) {
                    holder.layoutTestCount.isVisible = true
                    holder.textviewTestCount.text = "+" + mData!![position]!!.addonCount.toString()
                } else {
                    holder.layoutTestCount.visibility = View.INVISIBLE
                }
                scheduleDate = mData!![position]!!.scheduleDate!!
                if (scheduleDate == 0) {
                    holder.textViewScheduleDate.isVisible = false
                } else {
                    holder.textViewScheduleDate.isVisible = true
                    holder.textViewScheduleDate.text =
                        mContext.resources.getString(R.string.text_schedule_txt) + Utils.getDateCurrentTimeZone(
                            mData!![position]!!.scheduleDate!!.toLong(),
                            Constants.TIMESTAMPTODATE_TWO!!
                        )
                }

                holder.textViewDate.text =
                    Utils.getDateCurrentTimeZone(
                        mData!![position].orderDate!!.toLong(),
                        Constants.TIMESTAMPTODATE_TWO!!
                    )

                holder.cardViewItem.setOnClickListener {
                    mContext.intent =
                        Intent(mContext, TrackOrderActivity::class.java)
                    val extras = Bundle()
                    extras.putString("orderId", mData!![position].orderId!!.toString())
                    mContext.intent.putExtras(extras)
                    if (Utils.isLollipopHigher() && it != null) {
                        val pairs: Array<Pair<View, String>> =
                            TransitionHelper.createSafeTransitionParticipants(
                                mContext,
                                false,
                                Pair(it, mContext.getString(R.string.trans_tool_bar_title))
                            )
                        val transitionActivityOptions =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                mContext,
                                *pairs
                            )
                        mContext.startActivity(
                            mContext.intent,
                            transitionActivityOptions.toBundle()
                        )
                    } else {
                        mContext.startActivity(mContext.intent)
                    }
                }
                Log.e("orderDate", mData!![position].orderDate.toString())
                Log.e("schedule", mData!![position]!!.scheduleDate!!.toString())
                Log.e(
                    "orderDate1",
                    Utils.getDateFromUTCTimestamp(
                        mData!![position]!!.orderDate!!.toLong(),
                        Constants.TIMESTAMPTODATE_TWO
                    ).toString()
                )


                val STATUS: Int = mData!![position].status!!
                when (STATUS) {

                    1 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_pending)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_pending
                        )
                    }
                    2 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_approved)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_approved
                        )
                    }
                    3 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_rejected)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_rejected
                        )
                    }
                    4 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_inprogress)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_inprogress
                        )
                    }
                    5 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_complete)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_completed
                        )
                    }
                    6 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_declined)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_rejected
                        )
                    }
                    7 -> {
                        holder.layoutStatus.isVisible = true
                        holder.textviewTestStatus.text =
                            mContext.resources.getString(R.string.text_refunded)
                        holder.layoutStatus.background = ContextCompat.getDrawable(
                            mContext,
                            R.drawable.divider_refund
                        )
                    }
                }

                //setAnimation(holder.itemView, position)
            }
            LOADING -> {
                /* val loadingVH = aViewHolder as LoadingView?
                 loadingVH!!.aProgressBar!!.visibility = View.VISIBLE*/
            }
        }
    }


    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(mContext, R.anim.item_animation_fall_down)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    /*fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }*/

    fun AppCompatImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
}

class ItemsTestView(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    val cardViewItem = itemView.findViewById(R.id.cardViewItem) as CardView
}
