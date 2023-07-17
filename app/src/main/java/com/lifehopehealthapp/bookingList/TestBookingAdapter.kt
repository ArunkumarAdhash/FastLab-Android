package com.lifehopehealthapp.bookingList

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.TestListResponse
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class TestBookingAdapter(
    var mData: ArrayList<TestListResponse.TestList>, testBookingActivity: TestBookingActivity,
) :
    RecyclerView.Adapter<TestBookingAdapter.ViewHolder>() {

    private var lastPosition = -1

    //private var mData: ArrayList<TestListResponse.TestList> = null
    private var mContext: TestBookingActivity = testBookingActivity

    var decimalWithTwoDigit: DecimalFormat = DecimalFormat("#,###")


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textViewHeading.text = mData.get(position).getCategoryName()
        holder.textViewContent.text = mData.get(position).getCategoryDescription()
        holder.imageviewTestImage.loadSvg(mData.get(position).getImagePath())

        Log.e("isBookingType",mData[position].actualDescription.toString())
        if (mData[position].isBookingType == 0) {
            holder.textViewTestAmount.setText("$" + decimalWithTwoDigit.format(mData!!.get(position).getPrice()))
        }else{
            holder.textViewTestAmount.setText("")
        }
        setAnimation(holder.itemView, position)
    }

    fun checkdata(data: ArrayList<TestListResponse.TestList>) {
        if (data.size != 0) {
            mData = data
            notifyDataSetChanged()
            Log.e("mDataSize", mData!!.size.toString())
        }

    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewHeading =
            itemView.findViewById(R.id.textViewTestHeading) as AppCompatTextView
        val textViewContent =
            itemView.findViewById(R.id.textViewTestContent) as AppCompatTextView
        val textViewTestAmount =
            itemView.findViewById(R.id.textViewTestAmount) as AppCompatTextView
        val buttonBookTest = itemView.findViewById(R.id.buttonBookNow) as AppCompatButton
        val imageviewTestImage =
            itemView.findViewById(R.id.imageViewTestImage) as AppCompatImageView
        val layout = itemView.findViewById(R.id.layoutController) as ConstraintLayout
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

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