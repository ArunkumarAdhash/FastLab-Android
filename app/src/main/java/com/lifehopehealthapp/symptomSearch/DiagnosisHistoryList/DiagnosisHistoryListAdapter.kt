package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryList

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.Datums

class DiagnosisHistoryListAdapter(
    data: List<Datums>?,
    diagnosisHistoryListActivity: DiagnosisHistoryListActivity
) : RecyclerView.Adapter<DiagnosisHistoryListAdapter.ViewHolder>() {


    private var mData: List<Datums>?
    private var mContext: DiagnosisHistoryListActivity

    init {
        mContext = diagnosisHistoryListActivity
        mData = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis_history_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageViewVitalImage.loadSvg(mData?.get(position)!!.imagePath)
        holder.textViewVitalsName.text = mData?.get(position)!!.name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewVitalsName =
            itemView.findViewById(R.id.textViewVitalsName) as AppCompatTextView
        val imageViewVitalImage =
            itemView.findViewById(R.id.imageViewVitalImage) as AppCompatImageView
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }
}