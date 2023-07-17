package com.lifehopehealthapp.productlist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.ProductListResponse
import com.lifehopehealthapp.utils.itembutton.ItemNumberButton
import java.text.NumberFormat
import java.util.*

class ProductListAdapter(
    mContext: Context,
    aDummyList: ArrayList<ProductListResponse.Datum>,
  private var productDetail: ProductDetailInfo,
) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    private var mData: ArrayList<ProductListResponse.Datum> = ArrayList()
    private var mContext: Context



    init {
        this.mContext = mContext
        this.mData = aDummyList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {


        holder.btnQuantityValue.tag = position

        holder.tvPrice.text= NumberFormat.getCurrencyInstance(
            Locale("en", "US")
        ).format(mData[position].amount)

        holder.tvTitle.text=mData[position].title

            Glide.with(mContext).load(mData[position].imagePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(holder.ivProductImage)


        holder.cardViewItem.setOnClickListener {
            productDetail.productDetail(mData[position],position)


        }


    }

    override fun getItemCount(): Int {
        return mData.size

    }






    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnQuantityValue =
            itemView.findViewById(R.id.btnQuantityValue) as ItemNumberButton

        val cardViewItem =
            itemView.findViewById(R.id.cardViewItem) as CardView


        val tvTitle =
            itemView.findViewById(R.id.tvTitle) as AppCompatTextView

        val tvPrice =
            itemView.findViewById(R.id.tvPrice) as AppCompatTextView

        val ivProductImage =
            itemView.findViewById(R.id.ivProductImage) as AppCompatImageView


    }


    fun addAllSearch(results: ArrayList<ProductListResponse.Datum>) {
        for (result in results) {
            add(result)
        }
    }

    fun add(r: ProductListResponse.Datum) {
        mData.add(r)
        notifyItemInserted(mData.size - 1)
    }

    fun update(
        value: Int,
        position: Int,
        recyclerView: XRecyclerView,
        boolValue: Boolean,
        amount: Int
    ) {
        mData[position].totalQuantity=value
        mData[position].isSelected=boolValue
        mData[position].totalPrice=amount
        recyclerView.notifyItemChanged(position,mData)


    }

    fun getArrayList() : ArrayList<ProductListResponse.Datum>{
       return mData
    }



    interface ProductDetailInfo {
        fun productDetail(value : ProductListResponse.Datum, position: Int)
    }


}