package com.lifehopehealthapp.trackTestOrders

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.TotalPriceModel
import com.lifehopehealthapp.ResponseModel.TrackOrderResponse
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class PaymentDetailsAdapter(
    mPayment: ArrayList<TrackOrderResponse.PaymentList>,
    mContext: Context?
) : RecyclerView.Adapter<PaymentDetailsAdapter.ViewHolder>() {
    private var mData: ArrayList<TrackOrderResponse.PaymentList>
    private var context: Context? = null
    private var Total: Double? = 0.00
    private val amount =
        DecimalFormat("0.00")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentDetailsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_list, parent, false)
        return ViewHolder(v)
    }

    init {
        mData = mPayment
        context = mContext
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: PaymentDetailsAdapter.ViewHolder, position: Int) {
        if (position == 0) {
            val count = mData[position].testCount
            if (count == 1) {
                holder.textViewName.text = mData[position].paymentName
            } else {
                holder.textViewName.text =
                    mData[position].paymentName + " x " + mData[position].testCount
            }
        } else {
            holder.textViewName.text = mData[position].paymentName
        }



       //newly hided
       // holder.textViewPrice.text = "$" + mData[position].amount.toString()

        //newly added
        holder.textViewPrice.text = NumberFormat.getCurrencyInstance(
            Locale("en", "US")
        ).format(mData[position].amount)

        Total = Total!! + mData[position].amount!!
        Log.e("Total amount", mData[position].amount.toString())
        var data = TotalPriceModel()
        data.mTotalAmount = Total
        EventBus.getDefault().post(data)
        Log.e("Total", Total.toString())

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName =
            itemView.findViewById(R.id.textview_label) as AppCompatTextView
        val textViewPrice =
            itemView.findViewById(R.id.textview_price) as AppCompatTextView

    }
}