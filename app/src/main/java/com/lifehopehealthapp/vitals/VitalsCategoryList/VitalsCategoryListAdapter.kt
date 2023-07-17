package com.lifehopehealthapp.vitals.VitalsCategoryList

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.VitalsCategoryListResponse
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import java.util.*

class VitalsCategoryListAdapter(data: List<VitalsCategoryListResponse.VitalMenu?>?) :
    RecyclerView.Adapter<VitalsCategoryListAdapter.ViewHolder>() {

    private var mData: List<VitalsCategoryListResponse.VitalMenu?>?

    init {
        mData = data
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vitals_category_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageViewVitalImage.loadSvg(mData!![position]!!.imagePath)
        holder.textViewVitalsName.text = mData!![position]!!.shortName

        if (mData!![position]!!.lastRecordDate == 0) {

        } else {
            when (mData!![position]!!.id) {
                2 -> if (mData!![position]!!.lastData.equals("")) {

                } else {
                    holder.textViewVitalsData.text =
                        mData!![position]!!.lastData.toString() + " BPM"
                }
                3 -> if (mData!![position]!!.lastData.equals("")) {

                } else {
                    holder.textViewVitalsData.text = mData!![position]!!.lastData.toString() + " °F"
                }
                4 -> if (mData!![position]!!.lastData.equals("")) {

                } else {
                    holder.textViewVitalsData.text = mData!![position]!!.lastData.toString() + " %"
                }

                5 -> if (mData!![position]!!.lastData.equals("")) {

                } else {
                    holder.textViewVitalsData.text =
                        mData!![position]!!.lastData.toString() + " \nmmHg"
                }

                6 -> if (mData!![position]!!.lastData.equals("")) {

                } else {
                    holder.textViewVitalsData.text =
                        mData!![position]!!.lastData.toString() + " lbs"
                }

                7 -> if (mData!![position]!!.lastData.equals("")) {

                } else {
                    holder.textViewVitalsData.text =
                        mData!![position]!!.lastData.toString() + " mg/dL"
                }
            }
        }

        if (mData!![position]!!.lastRecordDate == 0) {
            holder.textViewLastVitalsDate.isVisible = false
        } else {
            val tz = TimeZone.getDefault()
            val now = Date()
            val offsetFromUtc = tz.getOffset(now.time) / 1000
            val timeStamp =
                mData!![position]!!.lastRecordDate!!.toInt() - offsetFromUtc

            if (mData!![position]!!.id == 7) {
                holder.textViewLastVitalsDate.text = Utils.getDateCurrentTimeZone(
                    timeStamp.toLong(),
                    Constants.TIMESTAMP_DOB!!
                )
            } else {
                holder.textViewLastVitalsDate.text = Utils.getDateCurrentTimeZone(
                    timeStamp.toLong(),
                    Constants.TIMESTAMPTODATE_TWO!!
                )
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewVitalsName =
            itemView.findViewById(R.id.textViewVitalsName) as AppCompatTextView
        val textViewVitalsData =
            itemView.findViewById(R.id.textViewVitalsData) as AppCompatTextView
        val textViewLastVitalsDate =
            itemView.findViewById(R.id.textViewLastVitalsDate) as AppCompatTextView
        val imageViewVitalImage =
            itemView.findViewById(R.id.imageViewVitalImage) as AppCompatImageView
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }
}