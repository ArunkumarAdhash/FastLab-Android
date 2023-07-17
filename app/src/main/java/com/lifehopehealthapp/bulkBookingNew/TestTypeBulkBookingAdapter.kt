package com.lifehopehealthapp.bulkBookingNew

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrganizationTitleNewResponse

class TestTypeBulkBookingAdapter(
    val mContext: Context,
    private var mTitle: ArrayList<OrganizationTitleNewResponse.TestType>,
) : RecyclerView.Adapter<TestTypeBulkBookingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_test_type_bulk_booking, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {


        holder.tvTestType.text = mTitle.get(position).categoryName



        holder.tvTestType.setOnClickListener {
          // mTitle.get(position).isSelected=!mTitle.get(position).isSelected



                if(position==0)
                {
                    if(position==0)
                    {
                        holder.testTypeLay.background =
                            mContext.getDrawable(R.drawable.divider_left_select)
                        holder.tvTestType.setTextColor(mContext.resources.getColor(R.color.white))

                    }
                    else
                    {
                        holder.testTypeLay.background =
                            mContext.getDrawable(R.drawable.divider_layout_right_select)
                        holder.tvTestType.setTextColor(mContext.resources.getColor(R.color.purple_500))
                    }
                }

                if(position==1)
                {
                    if(position==0)
                    {
                        holder.testTypeLay.background =
                            mContext.getDrawable(R.drawable.divider_layout_left_select)
                        holder.tvTestType.setTextColor(mContext.resources.getColor(R.color.purple_500))

                    }
                    else
                    {
                        holder.testTypeLay.background =
                            mContext.getDrawable(R.drawable.divider_right_select)
                        holder.tvTestType.setTextColor(mContext.resources.getColor(R.color.white))
                    }
                }




        }


    }

    override fun getItemCount(): Int {


        return mTitle.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTestType =
            itemView.findViewById(R.id.tvTestType) as AppCompatTextView
        val testTypeLay =
            itemView.findViewById(R.id.testTypeLay) as LinearLayout

    }


}