package com.lifehopehealthapp.vitals.Demo

import android.content.Context
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
import com.lifehopehealthapp.ResponseModel.OrderListData


class PaginationAdapter(demo: Demo) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private val ITEM = 0
    private val LOADING = 1

    private var movies: ArrayList<OrderListData>? = null
    private var context: Context? = null

    init {
        this.context = demo
        movies = ArrayList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        val viewItem: View =
            inflater.inflate(R.layout.item_vitals_category_list, parent, false)
        viewHolder = ItemsProductView(viewItem)


        return viewHolder
    }

    override fun getItemCount(): Int {
        return if (movies == null) 0 else movies!!.size
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (position == movies!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }
*/
    override fun onBindViewHolder(aViewHolder: RecyclerView.ViewHolder, position: Int) {
        val aProductItem: OrderListData = movies!![position]
        when (getItemViewType(position)) {
            ITEM -> {
                val holder = aViewHolder as ItemsProductView?
                /*if (aProductItem.getShortName() != "" && aProductItem.value != "" && aProductItem.date != 0 && aProductItem.imagePath != "") {
                    holder!!.imageviewNextArrow.isVisible = false
                    holder.textViewLastVitalsDate.text = Utils.getDateCurrentTimeZone(
                        aProductItem.date!!.toLong(),
                        Constants.TIMESTAMPTODATE_TWO!!
                    )
                    holder.imageViewVitalImage.loadSvg(aProductItem.imagePath)
                    Log.e("value1", aProductItem.value.toString())
                } else {
                    Log.e("value2", aProductItem.value.toString())
                }*/
                holder!!.textViewVitalsName.text = aProductItem.orderId
                holder.textViewVitalsData.text = aProductItem.testName

            }
            LOADING -> {

            }
        }
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    /**
     * Main list's content ViewHolder
     */
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

    fun add(mc: OrderListData?) {
        movies!!.add(mc!!)
        notifyItemInserted(movies!!.size - 1)
    }

    fun addAll(mcList: ArrayList<OrderListData>) {
        for (mc in mcList) {
            add(mc)
        }
    }


//    fun add(mc: VitalsDatum?) {
//        movies!!.add(mc!!)
//        notifyItemInserted(movies!!.size - 1)
//    }
//
//    fun addAll(mcList: ArrayList<VitalsDatum>) {
//        for (mc in mcList) {
//            add(mc)
//        }
//    }

/*
    fun remove(city: VitalsDatum?) {
        val position = movies!!.indexOf(city)
        if (position > -1) {
            movies!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        add(VitalsDatum())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = movies!!.size - 1
        val item: VitalsDatum? = getItem(position)
        if (item != null) {
            movies!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): VitalsDatum? {
        return movies!![position]
    }
*/

}