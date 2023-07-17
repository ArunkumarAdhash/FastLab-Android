package com.lifehopehealthapp.dashBoard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.lifehopehealthapp.R
import com.smarteist.autoimageslider.SliderViewAdapter


class ImageSliderAdapter(context: Context) :
    SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH>() {
    private val context: Context = context


    private var titleList = context.resources.getStringArray(R.array.slider_title_array)
    private var descList = context.resources.getStringArray(R.array.slider_desc_array)

    private var imageList = intArrayOf(
        R.drawable.ic_wear_mask,
        R.drawable.ic_hand_wash,
        R.drawable.ic_keep_distance,
        R.drawable.ic_temp_check
    )


    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(
            R.layout.image_slider_layout_item,
            null
        )
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        viewHolder.textTitle.text = titleList[position].toString()
        viewHolder.textDescription.text = descList[position].toString()
        viewHolder.imageViewBackground.setImageResource(imageList[position])
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return imageList.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var imageViewBackground: AppCompatImageView = itemView.findViewById(R.id.image)
        var textTitle: TextView = itemView.findViewById(R.id.title)
        var textDescription: TextView = itemView.findViewById(R.id.desc)

    }

}

