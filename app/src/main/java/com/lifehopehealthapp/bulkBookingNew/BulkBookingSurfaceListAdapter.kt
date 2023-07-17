package com.lifehopehealthapp.bulkBookingNew

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrganizationTitleNewResponse

class BulkBookingSurfaceListAdapter(
    val mContext: Context,
    private var mTitle: ArrayList<OrganizationTitleNewResponse.Surface>,
    private var bulkBookingSelectionList: BulkBookingSelectionList,
    private var bulkBookingSelectionListRemove: BulkBookingSelectionListRemove

) : RecyclerView.Adapter<BulkBookingSurfaceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bulk_booking_surface_list, parent, false)
        return ViewHolder(v)
    }
    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {


        holder.tvBulkHeading.text = mTitle.get(position).titleName
        holder.tvBulkHeading.setTag(position)


            if (mTitle.get(holder.tvBulkHeading.getTag() as Int).isSelected) {
                holder.tvBulkHeading.background =
                    mContext.getDrawable(R.drawable.bulk_bg_selection)
                holder.tvBulkHeading.setTextColor(mContext.resources.getColor(R.color.white))
                  //  bulkBookingSelectionList.bulkBookingSelectionValue(mTitle.get(holder.tvBulkHeading.getTag() as Int),holder.tvBulkHeading.getTag() as Int)
            } else {
                holder.tvBulkHeading.background =
                    mContext.getDrawable(R.drawable.symptom_answer_bg)
                holder.tvBulkHeading.setTextColor(mContext.resources.getColor(R.color.purple_500))
                  //  bulkBookingSelectionListRemove.bulkBookingSelectionValueRemove(mTitle.get(holder.tvBulkHeading.getTag() as Int),holder.tvBulkHeading.getTag() as Int)

            }


        holder.tvBulkHeading.setOnClickListener {
            mTitle.get(holder.tvBulkHeading.getTag() as Int).isSelected=!mTitle.get(holder.tvBulkHeading.getTag() as Int).isSelected

            if (mTitle.get(holder.tvBulkHeading.getTag() as Int).isSelected) {
                holder.tvBulkHeading.background =
                    mContext.getDrawable(R.drawable.bulk_bg_selection)
                holder.tvBulkHeading.setTextColor(mContext.resources.getColor(R.color.white))
                    bulkBookingSelectionList.bulkBookingSelectionValue(mTitle.get(holder.tvBulkHeading.getTag() as Int),holder.tvBulkHeading.getTag() as Int)
            } else {
                holder.tvBulkHeading.background =
                    mContext.getDrawable(R.drawable.symptom_answer_bg)
                holder.tvBulkHeading.setTextColor(mContext.resources.getColor(R.color.purple_500))
                    bulkBookingSelectionListRemove.bulkBookingSelectionValueRemove(mTitle.get(holder.tvBulkHeading.getTag() as Int),holder.tvBulkHeading.getTag() as Int)
            }
        }






    }

    override fun getItemCount(): Int {

        return mTitle.size

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBulkHeading =
            itemView.findViewById(R.id.tvBulkHeading) as AppCompatTextView
    }

    interface BulkBookingSelectionList {
        fun bulkBookingSelectionValue(titleValue : OrganizationTitleNewResponse.Surface,position: Int)
    }
    interface BulkBookingSelectionListRemove {
        fun bulkBookingSelectionValueRemove(titleValue : OrganizationTitleNewResponse.Surface,position: Int)
    }
}