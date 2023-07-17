package com.lifehopehealthapp.orderSummary

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.AddOnMemberModel
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus

class AddMemberAdapter(
    userData: ArrayList<String>,
    orderSummaryActivity: Context,

    ) : RecyclerView.Adapter<AddMemberAdapter.ViewHolder>() {
    private var mContext: Context
    private var mData: ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_list, parent, false)
        return ViewHolder(v)
    }

    init {
        mData = userData
        mContext = orderSummaryActivity
    }

    override fun getItemCount(): Int {
        Log.e("mData->", mData.size.toString())
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val separated: List<String> = mData[position].split(",")

        holder.textViewName.text = separated[0]
        holder.textViewAge.text = separated[1]
        holder.textViewGender.text = separated[2]

        holder.imageviewremove.setOnClickListener {
            removeItem(position)
        }
        holder.imageviewedit.setOnClickListener {
            showPopUp(mData[position], position, mContext)
        }
    }

    private fun showPopUp(s: String, position: Int, mContext: Context) {
        var setUserName: String = ""
        var setUserAge: String = ""
        var setUserGender: String = ""
        var passData: String? = ""
        var genderName: ArrayList<String>? = null
        var genderData: String? = ""
        var userNameData: String? = ""
        var userAgeData: String? = ""
        val alertDialog = Dialog(mContext)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_add_members)
        val userName: TextInputEditText = alertDialog.findViewById(R.id.edittext_name)
        val userAge: TextInputEditText = alertDialog.findViewById(R.id.edittext_age)
        val gender: Spinner = alertDialog.findViewById(R.id.spinner_gender)
        genderName = ArrayList<String>()
        genderName.add("Select Gender")
        genderName.add("Male")
        genderName.add("Female")

        val animalsArray: List<String> = s.split(",")
        userName.setText(animalsArray[0])
        userAge.setText(animalsArray[1])

        setUserName = animalsArray[0]
        setUserAge = animalsArray[1]

        val adapter = ArrayAdapter(
            this.mContext,
            android.R.layout.simple_spinner_item, genderName
        )
        gender.adapter = adapter

        for (position in 0 until genderName.size) {
            if (genderName[position] == animalsArray[2]) {
                gender.setSelection(position)
                setUserGender = gender.selectedItem.toString()
            }
        }

        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        posTv.text = "Update"
        posTv.setOnClickListener(View.OnClickListener {
            userNameData = userName.text.toString().trim()
            userAgeData = userAge.text.toString().trim()
            genderData = gender.selectedItem.toString()

            if (!userNameData!!.contentEquals("")) {
                if (!userAgeData!!.contentEquals("")) {
                    val limiAge: Int = userAgeData!!.toInt()
                    if (limiAge <= 120) {
                        if (!genderData!!.equals("Select Gender")) {

                            if (userNameData.equals(setUserName) && userAgeData.equals(setUserAge) && genderData.equals(setUserGender)){
                                alertDialog.dismiss()
                            }else{
                                passData = userNameData + "," + userAgeData + "," + genderData
                                mData.add(passData!!)
                                removeItem(position)
                                alertDialog.dismiss()
                                notifyDataSetChanged()
                            }

                        } else {
                            Utils.showToast(
                                mContext as Activity,
                                "Select your Gender",
                                true
                            )
                        }
                    } else {
                        Utils.showToast(
                            mContext as Activity,
                            mContext.getString(R.string.toast_enter_valid_age),
                            true
                        )
                    }
                } else {
                    Utils.showToast(mContext as Activity, "Enter Age", true)
                }
            } else {
                Utils.showToast(mContext as Activity, "Enter Name", true)
            }
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun removeItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mData.size)
        val total: Int = mData.size
        val data = AddOnMemberModel()
        data.count = total
        EventBus.getDefault().post(data)
        notifyDataSetChanged()
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