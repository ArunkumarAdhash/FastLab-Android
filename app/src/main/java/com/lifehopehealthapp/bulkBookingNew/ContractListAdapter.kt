package com.lifehopehealthapp.bulkBookingNew

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrganizationTitleNewResponse
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class ContractListAdapter(
    val mContext: Context,
    private var mContractList: ArrayList<OrganizationTitleNewResponse.ContractList>,
    private var finalAmount: Float, private var bulkBookingContractList: BulkBookingContractList
) : RecyclerView.Adapter<ContractListAdapter.ViewHolder>() {

    private var paymentGatewayAmount = 0f
    private var lastChecked: AppCompatRadioButton? = null
    private var lastCheckedPos = 0
    var finalDiscountAmount:Float = 0.0F
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_contract_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.textViewContractLabel.text = mContractList.get(position).titleName

        holder.radioButton.text = mContractList.get(position).discount

        holder.radioButton.setTag(position)

        holder.textViewDescription.text = mContractList.get(position).description

        /* lastChecked = holder.radioButton
         lastCheckedPos = 0*/
        if (mContractList.get(position).id == 3) {
            holder.textViewOfferAmount.setText(
                NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(finalAmount)
            )
            holder.textViewSavingAmount.isVisible = false
            holder.textViewTotalAmount.isVisible = false
        } else {
            holder.textViewOfferAmount.isVisible = true
            holder.textViewTotalAmount.isVisible = true

            val originalAmount = finalAmount * mContractList[position].month!!

            holder.textViewTotalAmount.setText(
                NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(originalAmount)
            )
            holder.textViewTotalAmount.setPaintFlags(holder.textViewOfferAmount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            val parts: List<String> = mContractList[position].discount!!.split("%")

           // println("discount:::" + parts[0])

            val discountAmount = (originalAmount * parts[0].toInt()) / 100
            val payingAmount = originalAmount - discountAmount

            holder.textViewOfferAmount.setText(
                NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(payingAmount)
            )
            finalDiscountAmount = payingAmount
            //Log.e("paying_amt::",""+payingAmount)
           // Log.e("paying_amt_12::",""+payingAmount/12)


            val savingAmount = originalAmount - payingAmount

            holder.textViewSavingAmount.setText(
                "Savings on this plan : " + NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(savingAmount)
            )

            paymentGatewayAmount = (payingAmount / mContractList[position].month!!)


        }








        holder.radioButton.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {


                if (mContractList.get(position).id == 3) {
                    holder.textViewSavingAmount.isVisible = false
                    bulkBookingContractList.bulkBookingContractList(
                        mContractList[position],
                        position,
                        finalAmount
                    )
                } else {
                    holder.textViewSavingAmount.isVisible = true

                   // Log.e("paying_amt::text",""+finalDiscountAmount)


                    bulkBookingContractList.bulkBookingContractList(
                        mContractList[position],
                        position,
                        holder.textViewOfferAmount.text.toString().replaceFirst("$","").replace(",","").toFloat()/mContractList[position].month!!
                    )
                   // Log.e("paying_month",""+mContractList[position].month)
                 // Log.e("paying_per_month_amount",""+holder.textViewOfferAmount.text.toString().replaceFirst("$","").replace(",","").toFloat()/mContractList[position].month!!)
                }
            }
            val cb: AppCompatRadioButton = holder.radioButton
            val clickedPos = (cb.getTag() as Int).toInt()

            if (cb.isChecked()) {
                if (lastChecked != null) {
                    lastChecked!!.setChecked(false)
                    if (mContractList.get(position).id == 3) {
                        holder.textViewSavingAmount.isVisible = false
                    } else {
                        holder.textViewSavingAmount.isVisible = true
                    }

                }
                lastChecked = cb
                lastCheckedPos = clickedPos
            } else {
                lastChecked = null
                holder.textViewSavingAmount.isVisible = false
            }

            //notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int {

        return mContractList.size

    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButton =
            itemView.findViewById(R.id.radioButton) as AppCompatRadioButton

        val textViewContractLabel =
            itemView.findViewById(R.id.textViewContractLabel) as AppCompatTextView
        val textViewDescription =
            itemView.findViewById(R.id.textViewDescription) as AppCompatTextView

        val textViewOfferAmount =
            itemView.findViewById(R.id.textViewOfferAmount) as AppCompatTextView
        val textViewTotalAmount =
            itemView.findViewById(R.id.textViewTotalAmount) as AppCompatTextView
        val textViewSavingAmount =
            itemView.findViewById(R.id.textViewSavingAmount) as AppCompatTextView

    }

    interface BulkBookingContractList {
        fun bulkBookingContractList(
            contractValue: OrganizationTitleNewResponse.ContractList,
            position: Int,
            paymentGatewayAmount: Float
        )
    }



}