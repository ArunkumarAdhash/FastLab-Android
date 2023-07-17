package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.Question

class DiagnosisHistoryDetailAdapter(
    list: List<Question>,
    diagnosisHistoryDetailActivity: DiagnosisHistoryDetailActivity
) : RecyclerView.Adapter<DiagnosisHistoryDetailAdapter.ViewHolder>() {

    private var mContext: DiagnosisHistoryDetailActivity
    private var mData: List<Question>

    init {
        mData = list
        mContext = diagnosisHistoryDetailActivity
    }


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_diagnosis_detail_result, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val count: Int = position + 1
        holder.textviewCount.text = count.toString() + "."
        holder.textViewDiagnosisQues.text = mData[position].question
        holder.imageViewDiagnosisAns.text = mData[position].answerChoosed
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDiagnosisQues =
                itemView.findViewById(R.id.txt_question) as AppCompatTextView
        val imageViewDiagnosisAns =
                itemView.findViewById(R.id.txt_answer) as AppCompatTextView
        val textviewCount =
                itemView.findViewById(R.id.textview_count) as AppCompatTextView
    }
}