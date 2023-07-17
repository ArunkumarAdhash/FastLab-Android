package com.lifehopehealthapp.dashBoard

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.dashboard.Home


class DashBoardMenusAdapter(
    context: Context,
    data: List<Home?>?
) : RecyclerView.Adapter<DashBoardMenusAdapter.DashBoardMenusVH>() {

    private var mContext: Context
    private var mData: List<Home?>?

    init {
        mContext = context
        mData = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashBoardMenusAdapter.DashBoardMenusVH {
        return DashBoardMenusVH(
            LayoutInflater.from(parent.context).inflate(R.layout.dashboard_items, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    override fun onBindViewHolder(
        viewHolder: DashBoardMenusAdapter.DashBoardMenusVH,
        position: Int
    ) {

        viewHolder.textTitle.text = mData!![position]!!.name
        viewHolder.imageViewBackground.loadSvg(mData!![position]!!.imagePath)
        Log.e("imagePath", mData!![position]!!.imagePath.toString())


       /* Glide.with(mContext).load(mData!![position]!!.iOSImagePath)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_no_image)
            .error(R.drawable.ic_no_image)
            .into(viewHolder.imageViewBackground)*/
    }

    inner class DashBoardMenusVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageViewBackground: AppCompatImageView = itemView.findViewById(R.id.item_img)
        var textTitle: TextView = itemView.findViewById(R.id.item_title)

    }

    fun ImageView.loadSvg(url: String?) {
        //newly added 21-01-2022
        /*val requestBuilder: RequestBuilder<PictureDrawable> = GlideToVectorYou
            .init()
            .with(this.context)
            .requestBuilder

        requestBuilder
            .load(url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            *//*.signature(ObjectKey(System.currentTimeMillis()))*//*
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .centerCrop()
            )
            .into(this)*/

        //newly hided 21-01-2022
     GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(0, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)

    }
}