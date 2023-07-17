package com.lifehopehealthapp.symptomSearch.DiagnosisHistory

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DiagnosisHistoryResponse
import com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail.DiagnosisHistoryDetailActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper

class DiagnosisHistoryAdapter(
    diagnosisHistoryActivity: DiagnosisHistoryActivity,
    aDummyList: ArrayList<DiagnosisHistoryResponse.Datum>
) : RecyclerView.Adapter<DiagnosisHistoryAdapter.ViewHolder>() {

    class DiffUtilCallBack : DiffUtil.ItemCallback<DiagnosisHistoryResponse.Datum>() {
        override fun areItemsTheSame(
            oldItem: DiagnosisHistoryResponse.Datum,
            newItem: DiagnosisHistoryResponse.Datum
        ): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(
            oldItem: DiagnosisHistoryResponse.Datum,
            newItem: DiagnosisHistoryResponse.Datum
        ): Boolean {
            return oldItem.getId() == newItem.getId()
                    && oldItem.getName() == newItem.getName()
        }

    }

    private var mContext: DiagnosisHistoryActivity
    private var mData: MutableList<DiagnosisHistoryResponse.Datum>?


    init {
        this.mData = aDummyList
        mContext = diagnosisHistoryActivity
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewVitalsName =
            itemView.findViewById(R.id.textViewVitalsName) as AppCompatTextView
        val textViewVitalsData =
            itemView.findViewById(R.id.textViewVitalsData) as AppCompatTextView
        val textViewLastVitalsDate =
            itemView.findViewById(R.id.textViewLastVitalsDate) as AppCompatTextView
        val imageViewVitalImage =
            itemView.findViewById(R.id.imageViewVitalImage) as AppCompatImageView
        val cardViewItem =
            itemView.findViewById(R.id.cardViewItem) as CardView
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageViewVitalImage.loadSvg(mData!![position].imagePath)
        holder.textViewVitalsData.isVisible = false
        holder.textViewVitalsName.text = mData!![position].name
        holder.textViewLastVitalsDate.text = Utils.getDateCurrentTimeZone(
            mData!![position].testDate!!.toLong(),
            Constants.TIMESTAMPTODATE_TWO!!
        )

        holder.cardViewItem.setOnClickListener {

            mContext.intent = Intent(
                mContext,
                DiagnosisHistoryDetailActivity::class.java
            )
            mContext.intent.putExtra("diagnosesId", mData!![position].id)
            mContext.intent.putExtra("testName", mData!![position].name)
            mContext.intent.putExtra("testDate", mData!![position].testDate.toString())
            if (Utils.isLollipopHigher() && it != null) {
                val pairs: Array<Pair<View, String>> =
                    TransitionHelper.createSafeTransitionParticipants(
                        mContext,
                        false,
                        Pair(it, mContext.getString(R.string.trans_tool_bar_title))
                    )
                val transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mContext,
                        *pairs
                    )
                mContext.startActivity(mContext.intent, transitionActivityOptions.toBundle())
            } else {
                mContext.startActivity(mContext.intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vitals_category_list, parent, false)
        return ViewHolder(v)
    }

    fun addAllSearch(results: java.util.ArrayList<DiagnosisHistoryResponse.Datum>) {
        for (result in results) {
            add(result)
        }
    }

    fun add(r: DiagnosisHistoryResponse.Datum) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    fun getItem(position: Int): DiagnosisHistoryResponse.Datum {
        return mData!![position]
    }
}