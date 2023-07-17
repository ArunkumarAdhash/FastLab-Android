package com.lifehopehealthapp.productcart

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.ProductListResponse
import com.lifehopehealthapp.ResponseModel.ProductListValue
import com.lifehopehealthapp.utils.itembutton.ItemNumberButton
import com.zerobranch.layout.SwipeLayout
import kotlinx.android.synthetic.main.layout_number_button.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductCartListAdapter(
     mContext: Context,
    aDummyList: ArrayList<ProductListResponse.Datum>) : RecyclerView.Adapter<ProductCartListAdapter.ViewHolder>() {

    private var mData: ArrayList<ProductListResponse.Datum> = ArrayList()
    private  var mContext: Context

    init {
        this.mContext = mContext
        this.mData = aDummyList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart_list, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {


        holder.btnQuantityValue.setTag(position)
        holder.btnQuantityValue.setNumber(mData[position].totalQuantity.toString())


        holder.tvPrice.text= NumberFormat.getCurrencyInstance(
            Locale("en", "US")
        ).format(mData[position].totalPrice)

        holder.tvTitle.text=mData[position].title

        Glide.with(mContext).load(mData[position].imagePath)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.ivProductImage)


        if(mData.get(holder.btnQuantityValue.getTag() as Int).isSelected)
        {

            holder.horizontalChildLay.isVisible=true
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


        }
        else
        {
            holder.horizontalChildLay.isVisible=false
            holder.itemView.setLayoutParams(RecyclerView.LayoutParams(0, 0))
        }


        holder.btnQuantityValue.number_counter.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                val strValue: String = s.toString()

                if(strValue.toInt()==0) {

                  //  productValueCleared.productValueCleared(mProductListSelectedValue.get(holder.btnQuantityValue.getTag() as Int),holder.btnQuantityValue.getTag() as Int)


                }

               if(strValue.toInt()>0) {

                   /* mProductListSelectedValue.get(holder.btnQuantityValue.getTag() as Int).value=strValue.toInt()
                    productValueUpdate.productValueUpdate(mProductListSelectedValue.get(holder.btnQuantityValue.getTag() as Int),holder.btnQuantityValue.getTag() as Int)*/
                }




            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
        holder.cardViewItem.setOnClickListener {
          //  mContext.startActivity(Intent(mContext,ProductDetailActivity::class.java))
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

        val horizontalChildLay =
            itemView.findViewById(R.id.swipe_layout) as SwipeLayout

        val tvTitle =
            itemView.findViewById(R.id.tvProductTitle) as AppCompatTextView

        val tvPrice =
            itemView.findViewById(R.id.tvProductPrice) as AppCompatTextView

        val ivProductImage =
            itemView.findViewById(R.id.productCartImage) as AppCompatImageView

    }

    interface ProductListUpdate {
        fun productValueUpdate(value : ProductListValue, position: Int)
    }

    interface ProductListValueCleared {
        fun productValueCleared(value : ProductListValue, position: Int)
    }


}