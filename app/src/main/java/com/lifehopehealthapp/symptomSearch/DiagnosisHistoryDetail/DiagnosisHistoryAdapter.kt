package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.RecommendedTest

class DiagnosisHistoryAdapter(recommendedTests: List<RecommendedTest?>) :
    RecyclerView.Adapter<DiagnosisHistoryAdapter.ViewHolder>() {
    private var mData: List<RecommendedTest>
    //private var mContext: DiagnosisResultActivity


    init {
        mData = recommendedTests as List<RecommendedTest>
        // mContext = diagnosisResultActivity
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis_result, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewDiagnosisName.text = mData[position].name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDiagnosisName =
            itemView.findViewById(R.id.textview_test_title) as AppCompatTextView
        /*val imageViewDiagnosisImage =
            itemView.findViewById(R.id.item_img) as AppCompatImageView*/
    }

}