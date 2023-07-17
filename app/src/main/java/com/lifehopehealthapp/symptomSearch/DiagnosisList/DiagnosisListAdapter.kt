package com.lifehopehealthapp.symptomSearch.DiagnosisList

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

class DiagnosisListAdapter(
    data: List<Datums?>,
    diagnosisListActivity: DiagnosisListActivity
) : RecyclerView.Adapter<DiagnosisListAdapter.ViewHolder>() {

    private var mContext: DiagnosisListActivity
    private var mData: List<Datums?>

    init {
        mData = data
        mContext = diagnosisListActivity
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: DiagnosisListAdapter.ViewHolder, position: Int) {
        holder.textViewDiagnosisName.text = mData[position]!!.name
        holder.imageViewDiagnosisImage.loadSvg(mData[position]!!.imagePath)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDiagnosisName =
            itemView.findViewById(R.id.item_title) as AppCompatTextView
        val imageViewDiagnosisImage =
            itemView.findViewById(R.id.item_img) as AppCompatImageView
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }
}