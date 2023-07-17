package com.lifehopehealthapp.vitals.VitalsHistory

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.VitalsDatum
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils

class VitalsHistoryAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var isLoadingAdded: Boolean = false
    private var mData: ArrayList<VitalsDatum>? = null
    private val LOADING = 1
    private val ITEM = 0

    init {
        mData = ArrayList()
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM
        } else {
            if (position == mData!!.size - 1 && isLoadingAdded) LOADING else ITEM
        }
    }

    fun add(r: VitalsDatum) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }

    fun getItem(position: Int): VitalsDatum? {
        return mData!![position]
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = mData!!.size - 1
        val result: VitalsDatum? = getItem(position)

        if (result != null) {
            mData!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(VitalsDatum())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vitals_category_list, parent, false)
        return ViewHolder(v)*/
        var viewHolder: RecyclerView.ViewHolder? = null

        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem: View =
                    inflater.inflate(R.layout.item_vitals_category_list, parent, false)
                viewHolder = ItemsProductView(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingView(viewLoading)
            }

        }
        return viewHolder!!

    }

    class LoadingView(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mRootMainLAY: RelativeLayout? = null

        var aProgressBar: ProgressBar? = null

        init {


            aProgressBar = itemView.findViewById(R.id.loadmore_progress) as ProgressBar


        }
    }

    override fun onBindViewHolder(aViewHolder: RecyclerView.ViewHolder, position: Int) {
        val aProductItem: VitalsDatum = mData!![position]
        when (getItemViewType(position)) {
            ITEM -> {
                val holder = aViewHolder as ItemsProductView?
                holder!!.imageviewNextArrow.isVisible = false
                holder.textViewVitalsName.text = aProductItem.getShortName()
                holder.textViewVitalsData.text = aProductItem.value
                holder.imageViewVitalImage.loadSvg(aProductItem.imagePath)
                holder.textViewLastVitalsDate.text = Utils.getDateCurrentTimeZone(
                    aProductItem.date!!.toLong(),
                    Constants.TIMESTAMPTODATE_TWO!!
                )
            }
            LOADING -> {
                /* val loadingVH = aViewHolder as LoadingView?
                 loadingVH!!.aProgressBar!!.visibility = View.VISIBLE*/
            }
        }
    }

    class ItemsProductView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewVitalsName =
            itemView.findViewById(R.id.textViewVitalsName) as AppCompatTextView
        val textViewVitalsData =
            itemView.findViewById(R.id.textViewVitalsData) as AppCompatTextView
        val textViewLastVitalsDate =
            itemView.findViewById(R.id.textViewLastVitalsDate) as AppCompatTextView
        val imageViewVitalImage =
            itemView.findViewById(R.id.imageViewVitalImage) as AppCompatImageView
        val imageviewNextArrow =
            itemView.findViewById(R.id.imageviewNextArrow) as AppCompatImageView
    }

    fun addAll(moveResults: List<VitalsDatum>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }
}