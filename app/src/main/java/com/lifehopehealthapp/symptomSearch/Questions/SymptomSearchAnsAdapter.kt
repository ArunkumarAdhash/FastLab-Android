package com.lifehopehealthapp.symptomSearch.Questions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.Option
import com.lifehopehealthapp.ResponseModel.SelectQuesAnsModel
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class SymptomSearchAnsAdapter(
    mSelectMrngTime: ArrayList<Option>,
    selectedAnswers: ArrayList<Option>,
    applicationContext: Context,
    recyclerviewAnsList: RecyclerView
) : RecyclerView.Adapter<SymptomSearchAnsAdapter.ViewHolder>() {

    private var getRecyclerView: RecyclerView
    private var mDataMrng: List<Option>? = null
    private var lastPosition = -1
    private var mSelectedAnswers: ArrayList<Option>? = null
    private var mContext: Context? = null


    init {
        getRecyclerView = recyclerviewAnsList
        mDataMrng = mSelectMrngTime
        mSelectedAnswers = selectedAnswers
        mContext = applicationContext
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answers, parent, false)
        return ViewHolder(
            v
        )
    }

    override fun getItemCount(): Int {
        return if (mDataMrng == null) 0 else mDataMrng!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datavalue = mDataMrng!![position].option
        if (datavalue.equals("No")) {
            holder.txtOption.text = " " + datavalue.toString()
        } else {
            holder.txtOption.text = datavalue.toString()
        }


        //holder.txtOption.text = datavalue.toString()

        holder.txtOption.setOnClickListener {
            getRecyclerView.scrollToPosition(position)
            getRecyclerView.smoothScrollToPosition(itemCount - 1)
            notifyDataSetChanged()

            /* val loadQues = SelectQuesAnsModel()
             loadQues.mSelectData = "QuestionLoader"
             loadQues.mSelectItem = 2
             EventBus.getDefault().post(loadQues)*/

            EventBus.getDefault().post(mDataMrng!![position].option)
            val loadAns = SelectQuesAnsModel()
            loadAns.mSelectData = "AnswerLoader"
            loadAns.mSelectItem = 1
            EventBus.getDefault().post(loadAns)
        }
//        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOption = itemView.findViewById(R.id.txt_option) as AppCompatTextView
    }
}