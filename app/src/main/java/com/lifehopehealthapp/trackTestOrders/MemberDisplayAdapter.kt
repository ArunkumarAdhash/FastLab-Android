package com.lifehopehealthapp.trackTestOrders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.TrackOrderResponse
import java.util.ArrayList

class MemberDisplayAdapter(
    mMember: ArrayList<TrackOrderResponse.MemberList>,
    mContext: Context?
) : RecyclerView.Adapter<MemberDisplayAdapter.ViewHolder>() {
    private var mData: ArrayList<TrackOrderResponse.MemberList>
    private var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_list, parent, false)
        return ViewHolder(v)
    }

    init {
        mData = mMember
        context = mContext
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.text = mData[position].patientName
        holder.textViewAge.text = mData[position].age.toString()
        holder.textViewGender.text = mData[position].gender
        holder.imageviewedit.isVisible = false
        holder.imageviewremove.isVisible = false
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName =
            itemView.findViewById(R.id.textview_user_name) as AppCompatTextView
        val textViewAge =
            itemView.findViewById(R.id.textview_user_age) as AppCompatTextView
        val textViewGender =
            itemView.findViewById(R.id.textview_user_gender) as AppCompatTextView
        val imageviewedit =
            itemView.findViewById(R.id.imageview_edit) as AppCompatImageView
        val imageviewremove =
            itemView.findViewById(R.id.imageview_remove) as AppCompatImageView
    }
}