package com.lifehopehealthapp.labResultList

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.LabResultListResponse

class LabResultListAdapter(
    data: List<LabResultListResponse.Datum?>,
    labResultActivity: LabResultListActivity
) : RecyclerView.Adapter<LabResultListAdapter.ViewHolder>() {

    private var mContext: LabResultListActivity
    private var mData: List<LabResultListResponse.Datum?>

    init {
        mData = data
        mContext = labResultActivity
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LabResultListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lab_result, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: LabResultListAdapter.ViewHolder, position: Int) {
        holder.textViewTestName.text = mData[position]!!.categoryName
        holder.imageViewTestImage.loadSvg(mData[position]!!.imagePath)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTestName =
            itemView.findViewById(R.id.textviewTestName) as AppCompatTextView
        val imageViewTestImage =
            itemView.findViewById(R.id.imageviewTestImage) as AppCompatImageView
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }
}