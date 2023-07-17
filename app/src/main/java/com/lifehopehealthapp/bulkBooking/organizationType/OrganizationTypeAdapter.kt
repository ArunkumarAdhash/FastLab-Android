package com.lifehopehealthapp.bulkBooking.organizationType

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrganizationListResponse
import com.lifehopehealthapp.utils.Utils

class OrganizationTypeAdapter(
    organizationType: List<OrganizationListResponse.OrganizationType>,
    organizationTypeActivity: OrganizationTypeActivity
) : RecyclerView.Adapter<OrganizationTypeAdapter.ViewHolder>() {

    private var mContext: OrganizationTypeActivity
    private var mData: List<OrganizationListResponse.OrganizationType>

    val image = arrayOf(
        R.drawable.ic_school_grey,
        R.drawable.corporate_grey,
        R.drawable.old_age_home_grey
    )

    init {
        mData = organizationType
        mContext = organizationTypeActivity
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewDiagnosisName.text = mData[position].getCategoryName()
        Log.e("type--->", mData[position].getCategoryName().toString())
        if (mData[position].isAvailable == 0) {
            Utils.loadSvg(
                mData[position].imagePath,
                mContext,
                holder.imageViewDiagnosisImage
            )
        } else {
            Utils.loadSvg(
                mData[position].getImagePathGrey(),
                mContext,
                holder.imageViewDiagnosisImage
            )
            //holder.imageViewDiagnosisImage.alpha = 0.5f
            holder.textViewDiagnosisName.alpha = 0.5f
        }
        /*if (position == 0) {

        } else {
            Glide
                .with(mContext)
                .load(image[position])
                .placeholder(R.drawable.ic_no_image)
                .into(holder.imageViewDiagnosisImage)
        }*/
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDiagnosisName =
            itemView.findViewById(R.id.item_title) as AppCompatTextView
        val imageViewDiagnosisImage =
            itemView.findViewById(R.id.item_img) as AppCompatImageView
    }

}