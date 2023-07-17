package com.lifehopehealthapp.bulkBookingNew

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrganizationTitleNewResponse

class BulkBookingSurfaceSelectionAdapter(
    val mContext: Context,
    private var mTitle: ArrayList<OrganizationTitleNewResponse.Surface>,
    private var bulkBookingSelectionListRemove: BulkBookingSurfaceListAdapter.BulkBookingSelectionListRemove,
    private var bulkBookingSelectionListUpdate: BulkBookingSelectionListUpdate,
    private var bulkBookingSelectionListCleared: BulkBookingSelectionListValueCleared

) : RecyclerView.Adapter<BulkBookingSurfaceSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bulk_booking_surface_selection_list, parent, false)
        return ViewHolder(v)
    }
    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {


        holder.layoutSelectionValues.hint = mContext.getString(R.string.number_of)+" "+mTitle.get(position).titleName+"*"

        holder.etPeopleNumber.text=Editable.Factory.getInstance().newEditable("")

        holder.etPeopleNumber.setTag(position)

       /* if(holder.etPeopleNumber.getTag() as Int == mTitle.size-1)
            holder.etPeopleNumber.imeOptions = EditorInfo.IME_ACTION_DONE
        else
            holder.etPeopleNumber.imeOptions = EditorInfo.IME_ACTION_NEXT*/


        //println("position::::"+"recycler_view : "+holder.etPeopleNumber.getTag() as Int)

        if(mTitle.get(holder.etPeopleNumber.getTag() as Int).isSelected)
        {
           /* val params = holder.itemView.layoutParams
            params.height = 100
            holder.itemView.layoutParams = params*/
            holder.horizontalChildLay.isVisible=true

           // holder.itemView.isVisible=true

            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


        }
        else
        {
            holder.horizontalChildLay.isVisible=false
           /* val params = holder.itemView.layoutParams
            params.height = 0
            holder.itemView.layoutParams = params*/

            //holder.itemView.isVisible=false
            holder.itemView.setLayoutParams(RecyclerView.LayoutParams(0, 0))
        }

       // holder.horizontalParentLay.isVisible = mTitle.get(position).isSelected



        holder.etPeopleNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                val strValue: String = s.toString()




                if(strValue.trim().isNotEmpty()) {

                    mTitle.get(holder.etPeopleNumber.getTag() as Int).value=strValue.toInt()

                    bulkBookingSelectionListUpdate.bulkBookingSelectionValueUpdate(mTitle.get(holder.etPeopleNumber.getTag() as Int),holder.etPeopleNumber.getTag() as Int)



                }


                if(strValue.trim().isEmpty()) {
                    bulkBookingSelectionListCleared.bulkBookingSelectionValueCleared(mTitle.get(holder.etPeopleNumber.getTag() as Int),holder.etPeopleNumber.getTag() as Int)
                }




            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })


//



        holder.tvClose.setOnClickListener {

            bulkBookingSelectionListRemove.bulkBookingSelectionValueRemove(mTitle.get(holder.etPeopleNumber.getTag() as Int),holder.etPeopleNumber.getTag() as Int)
        }





    }

    override fun getItemCount(): Int {

        return mTitle.size

    }





    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutSelectionValues =
            itemView.findViewById(R.id.layoutSelectionValues) as TextInputLayout
        val tvClose =
            itemView.findViewById(R.id.imgClose) as AppCompatImageView

        val horizontalParentLay =
            itemView.findViewById(R.id.horizontalParentLay) as LinearLayout

        val horizontalChildLay =
            itemView.findViewById(R.id.childHorizontalLay) as LinearLayout

        val etPeopleNumber =
        itemView.findViewById(R.id.etPeopleNumber) as TextInputEditText







    }


    interface BulkBookingSelectionListUpdate {
        fun bulkBookingSelectionValueUpdate(titleValue : OrganizationTitleNewResponse.Surface,position: Int)
    }

    interface BulkBookingSelectionListValueCleared {
        fun bulkBookingSelectionValueCleared(titleValue : OrganizationTitleNewResponse.Surface,position: Int)
    }

    interface BulkBookingSelectionListRemove {
        fun bulkBookingSelectionValueRemove(titleValue : OrganizationTitleNewResponse.Surface,position: Int)
    }
}