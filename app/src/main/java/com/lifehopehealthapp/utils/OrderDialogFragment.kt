package com.lifehopehealthapp.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.trackTestOrders.MemberDisplayAdapter
import com.lifehopehealthapp.trackTestOrders.PaymentDetailsAdapter
import com.lifehopehealthapp.ResponseModel.TotalPriceModel
import com.lifehopehealthapp.ResponseModel.TrackOrderResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class OrderDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "OrderDialogFragment"
        private var mPayment: ArrayList<TrackOrderResponse.PaymentList>? = null
        private var mMember: ArrayList<TrackOrderResponse.MemberList>? = null
        private var mContext: Context? = null
        private var adapter: MemberDisplayAdapter? = null
        private var mAdapter: PaymentDetailsAdapter? = null
        private var textTotal: TextView? = null

        fun newInstance(
            memberList: ArrayList<TrackOrderResponse.MemberList>?,
            paymentList: ArrayList<TrackOrderResponse.PaymentList>?, context: Context
        ): OrderDialogFragment {
            val fragment = OrderDialogFragment()
            this.mMember = memberList
            this.mPayment = paymentList
            this.mContext = context
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDialog()!!.getWindow()!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
        val recyclerview: RecyclerView =
            view.findViewById(R.id.recyclerview_user_data) as RecyclerView
        val addOnLayout: CardView = view.findViewById(R.id.card_person) as CardView
        recyclerview.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        adapter = MemberDisplayAdapter(mMember!!, mContext)
        recyclerview.adapter = adapter

        val recyclerviewPayment: RecyclerView =
            view.findViewById(R.id.recyclerview_payment) as RecyclerView
        recyclerviewPayment.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mAdapter = PaymentDetailsAdapter(mPayment!!, mContext)
        recyclerviewPayment.adapter = mAdapter



        if (mMember!!.size == 0) {
            addOnLayout!!.isVisible = false
        } else {
            addOnLayout!!.isVisible = true
        }
        textTotal = view.findViewById(R.id.textview_total_charge) as TextView

        val close: ImageView = view.findViewById(R.id.imageview_close) as ImageView
        close.setOnClickListener {
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TotalPriceModel) {
        val dtime1 =
            DecimalFormat("#.##")
        val i1 = java.lang.Double.valueOf(dtime1.format(event.mTotalAmount!!))



        //textTotal!!.setText("$" + i1)


        textTotal!!.setText(NumberFormat.getCurrencyInstance(
            Locale("en", "US")
        ).format(event.mTotalAmount!!))




        Log.e("textTotal", event.mTotalAmount.toString())
    }
}