package com.lifehopehealthapp.trackTestOrders

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DataEvent
import com.lifehopehealthapp.ResponseModel.TrackOrderResponse
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.utils.timeLine.TimelineView
import com.lifehopehealthapp.utils.timeLine.VectorDrawableUtils
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.greenrobot.eventbus.EventBus


class TrackOrderTimeLineAdapter(
    private val mFeedList: List<TrackOrderResponse.TrackList>,
    context: TrackOrderActivity
) :
    RecyclerView.Adapter<TrackOrderTimeLineAdapter.TimeLineViewHolder>() {
    private var context: TrackOrderActivity = context

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return TimeLineViewHolder(
            mLayoutInflater.inflate(R.layout.item_timeline, parent, false),
            viewType
        )
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        // timeLineModel.status = 0 --> not approved (green) - normal line
        // timeLineModel.status = 1 --> approved (grey) - dotted line
        // timeLineModel.status = 2 --> rejected (red) - normal line
        //timeLineModel.trackType = 0 --> remove shipping input fields
        //timeLineModel.trackType = 1 --> show shipping input fields


        when (timeLineModel.status) {
            0 -> {
                holder.timeline.lineStyle = 1
                when (position) {
                    mFeedList.size - 1 -> {
                        holder.timeline.setStartLineColor(R.color.track_inactive, 2, 0)
                        holder.timeline.setEndLineColor(R.color.track_inactive, 2)
                    }
                    0 -> {
                        holder.timeline.setStartLineColor(R.color.track_inactive, 1, 0)
                        holder.timeline.setEndLineColor(R.color.track_inactive, 1)
                    }
                    else -> {
                        holder.timeline.setStartLineColor(R.color.track_inactive, 0, 0)
                        holder.timeline.setEndLineColor(R.color.track_inactive, 0)
                    }
                }

                setMarker(holder, R.drawable.ic_track_in_active_tick_icon, 0)
            }
            1 -> {

                setMarker(holder, R.drawable.ic_track_active_tick_icon, 0)
                when (position) {
                    mFeedList.size - 1 -> {
                        holder.timeline.setStartLineColor(R.color.track_active, 2, 1)
                        holder.timeline.setEndLineColor(R.color.track_active, 2)
                    }
                    0 -> {
                        holder.timeline.setStartLineColor(R.color.track_active, 1, 1)
                        holder.timeline.setEndLineColor(R.color.track_active, 1)
                    }
                    else -> {
                        holder.timeline.setStartLineColor(R.color.track_active, 0, 1)
                        holder.timeline.setEndLineColor(R.color.track_active, 0)
                    }
                }
                holder.timeline.lineStyle = 0


            }
            2 -> {
                setMarker(holder, R.drawable.ic_track_in_progress_tick_icon, 0)
                when (position) {
                    mFeedList.size - 1 -> {
                        holder.timeline.setStartLineColor(R.color.track_in_progress, 2, 2)
                        holder.timeline.setEndLineColor(R.color.track_in_progress, 2)
                    }
                    0 -> {
                        holder.timeline.setStartLineColor(R.color.track_in_progress, 1, 2)
                        holder.timeline.setEndLineColor(R.color.track_in_progress, 1)
                    }
                    else -> {
                        holder.timeline.setStartLineColor(R.color.track_in_progress, 0, 2)
                        holder.timeline.setEndLineColor(R.color.track_in_progress, 0)
                    }
                }
                holder.timeline.lineStyle = 0

            }
            3 -> {
                setMarker(holder, R.drawable.ic_track_rejected_icon, 0)
                when (position) {
                    mFeedList.size - 1 -> {
                        holder.timeline.setStartLineColor(R.color.track_rejected, 2, 3)
                        holder.timeline.setEndLineColor(R.color.track_rejected, 2)
                    }
                    0 -> {
                        holder.timeline.setStartLineColor(R.color.track_rejected, 1, 3)
                        holder.timeline.setEndLineColor(R.color.track_rejected, 1)
                    }
                    else -> {
                        holder.timeline.setStartLineColor(R.color.track_rejected, 0, 3)
                        holder.timeline.setEndLineColor(R.color.track_rejected, 0)
                    }
                }
                holder.timeline.lineStyle = 0
            }

        }

        holder.title.text = timeLineModel.statusName
        holder.desc.text = timeLineModel.statusDesc
        holder.contactPerson.text = timeLineModel.contactPerson
        if (timeLineModel.getLabName().equals("")) {
            holder.labName.isVisible = false
        } else {
            holder.labName.isVisible = true
            holder.labName.text = context.getString(R.string.lab_name) + timeLineModel.getLabName()
        }
        if (timeLineModel.getArrivingTime()!!.equals(0.0)) {
            holder.arrivingTime.isVisible = false
        } else {
            holder.arrivingTime.isVisible = true
            Log.e("arrivingTime", timeLineModel.arrivingTime.toString())
            holder.arrivingTime.text =
                context.getString(R.string.arriving_time) + Utils.getDateCurrentTimeZone(
                    timeLineModel.arrivingTime!!.toLong(),
                    Constants.TIME_STAMPTO_DATE_ONE!!
                )
        }

        if (timeLineModel.date!!.toLong() != 0L) {
            holder.date.visibility = View.VISIBLE
            holder.date.text = Utils.getDateCurrentTimeZone(
                timeLineModel.date!!.toLong()!!,
                Constants.TIME_STAMPTO_DATE_ONE!!
            )
        } else {
            holder.date.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(timeLineModel.trackId) || !TextUtils.isEmpty(timeLineModel.mobileNumber) || !TextUtils.isEmpty(
                timeLineModel.shipmentId
            )
        )
            holder.trackIdMobile.visibility = View.VISIBLE
        else
            holder.trackIdMobile.visibility = View.GONE

        Log.e("trackType", timeLineModel.trackType.toString())
        if (timeLineModel.trackType == 1) {
            if (!TextUtils.isEmpty(timeLineModel.shipmentId)) {
                holder.shippingLay.visibility = View.GONE
                holder.trackIdMobile.text = timeLineModel.shipmentId
            } else {
                holder.detailLay.visibility = View.GONE

                holder.shippingLay.visibility = View.VISIBLE
            }
        } else {
            holder.detailLay.visibility = View.VISIBLE
            holder.shippingLay.visibility = View.GONE
            if (!TextUtils.isEmpty(timeLineModel.trackId)) {
                holder.trackIdMobile.text = timeLineModel.trackId
                if (!TextUtils.isEmpty(timeLineModel.trackLink)) {
                    holder.trackIdMobile.setOnClickListener(View.OnClickListener {
                        try {
                            val uri: Uri =
                                Uri.parse(timeLineModel.trackLink) // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
            }
            if (!TextUtils.isEmpty(timeLineModel.mobileNumber)) {
                holder.trackIdMobile.text =
                    context.getString(R.string.phone) + " " + timeLineModel.mobileNumber
                holder.trackIdMobile.movementMethod = BetterLinkMovementMethod.newInstance()
                Linkify.addLinks(holder.trackIdMobile, Linkify.PHONE_NUMBERS)
            }
        }

        holder.submitShipping.setOnClickListener {
            if (!TextUtils.isEmpty(holder.shippingTrackId.text) && !TextUtils.isEmpty(holder.shippingName.text)) {
                val data = DataEvent()
                data.shippingID = holder.shippingTrackId.text!!.trim().toString()
                data.shippingName = holder.shippingName.text!!.trim().toString()
                //data.id = /*timeLineModel.id!!*/"1"
                EventBus.getDefault().post(data)
            } else {

                if(TextUtils.isEmpty(holder.shippingName.text))
                {
                    Utils.showToast(context, context.getString(R.string.enter_the_shipment_name), true)
                }

                    else  if(TextUtils.isEmpty(holder.shippingTrackId.text))
                {
                    Utils.showToast(context, context.getString(R.string.enter_the_shipment_id), true)
                }



               // Utils.showToast(context, "Enter Valid Data", true)
            }

        }

    }

    private fun setMarker(holder: TimeLineViewHolder, drawableResId: Int, colorFilter: Int) {

        holder.timeline.marker =
            VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId)

    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {

        val timeline = itemView.findViewById(R.id.timeline) as TimelineView

        val detailLay = itemView.findViewById(R.id.detail_lay) as LinearLayout


        val shippingLay = itemView.findViewById(R.id.shipping_lay) as LinearLayout


        val title = itemView.findViewById(R.id.text_timeline_title) as AppCompatTextView
        val desc = itemView.findViewById(R.id.text_timeline_desc) as AppCompatTextView
        val contactPerson =
            itemView.findViewById(R.id.text_timeline_contact_name) as AppCompatTextView
        val date = itemView.findViewById(R.id.text_timeline_date) as AppCompatTextView
        val labName = itemView.findViewById(R.id.text_timeline_labName) as AppCompatTextView
        val trackIdMobile =
            itemView.findViewById(R.id.text_timeline_track_id_mobile) as AppCompatTextView
        val arrivingTime =
            itemView.findViewById(R.id.text_timeline_arrivingTime) as AppCompatTextView
        val shippingName = itemView.findViewById(R.id.edt_shipping_name) as AppCompatEditText
        val shippingTrackId = itemView.findViewById(R.id.edt_shipping_id) as AppCompatEditText
        val submitShipping = itemView.findViewById(R.id.btn_submit) as FloatingActionButton


        init {
            timeline.initLine(viewType)
        }
    }

}
