package com.lifehopehealthapp.symptomSearch.DiagnosisResult

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.RecommendedTests

class DiagnosisResultAdapter(

    diagnosisResultActivity1: List<RecommendedTests>
) : RecyclerView.Adapter<DiagnosisResultAdapter.ViewHolder>() {

    private var mData: List<RecommendedTests>
    //private var mContext: DiagnosisResultActivity


    init {
        mData = diagnosisResultActivity1
       // mContext = diagnosisResultActivity
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiagnosisResultAdapter.ViewHolder {
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