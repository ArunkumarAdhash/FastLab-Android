package com.lifehopehealthapp.covidstatus

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.CovidStatusResponse
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Utils
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class CovidListPagingAdapter(
    aDummyList: ArrayList<CovidStatusResponse.Data>, val mContext: CovidStatusActivity,
    s3BucketName: String?,
    secretKey: String?,
    accessKey: String?,
    s3BucketRegion: String?
) :
/*PagingDataAdapter<CovidStatusResponse.Data, CovidListPagingAdapter.ViewHolder>(
    DiffUtilCallBack()
)*/RecyclerView.Adapter<CovidListPagingAdapter.ViewHolder>() {

    private var mS3BucketRegion: String? = s3BucketRegion
    private var mAccessKey: String? = accessKey
    private var mSecretKey: String? = secretKey
    private var mS3BucketNAme: String? = s3BucketName
    private var mData: MutableList<CovidStatusResponse.Data>?


    init {
        this.mData = aDummyList
    }

    /*class DiffUtilCallBack : DiffUtil.ItemCallback<CovidStatusResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: CovidStatusResponse.Data,
            newItem: CovidStatusResponse.Data
        ): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(
            oldItem: CovidStatusResponse.Data,
            newItem: CovidStatusResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CovidListPagingAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_covid_status_list, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: CovidListPagingAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.textview_name.text = mData!![position].patientName
        holder.textview_order_id.text =
            mContext.resources.getString(R.string.order_id) + " " + mData!![position].orderNo


        holder.textview_order_date.text = Utils.getDateCurrentTimeZone(
            mData!![position].orderDate!!.toLong(),
            Constants.TIMESTAMPTODATE_TWO!!
        ).toString()

        holder.ivCovidStatus.setBackgroundResource(if (mData!![position].isStatus == 1) R.drawable.ic_positive else R.drawable.ic_negative)



        holder.cardViewItem.setOnClickListener {
            val result = getItem(position)!!.imagePath
            val separated: List<String> = result!!.split("/")
            Log.e("filePAth", separated[5])
            Presigned(
                separated[5],
                mS3BucketNAme,
                mData!![position].orderId,
                mContext,
                mSecretKey,
                mAccessKey
            )

        }
    }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val textview_name =
            itemView.findViewById(R.id.textview_name) as AppCompatTextView


        val textview_order_date =
            itemView.findViewById(R.id.textview_order_date) as AppCompatTextView

        val textview_order_id =
            itemView.findViewById(R.id.textview_order_id) as AppCompatTextView

        val ivCovidStatus =
            itemView.findViewById(R.id.ivCovidStatus) as AppCompatImageView
        val cardViewItem =
            itemView.findViewById(R.id.cardViewItem) as CardView
    }


    fun Presigned(
        filePath: String,
        s3BucketName: String?,
        orderId: String?,
        mContext: Context,
        secrerKey: String?,
        accessKey: String?
    ) {
        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val credentials = BasicAWSCredentials(
            accessKey, secrerKey
        )
        val s3 = AmazonS3Client(credentials)
        java.security.Security.setProperty("networkaddress.cache.ttl", "60")
        if (mS3BucketRegion!!.contains("us-west-1")) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_1))
        } else if (mS3BucketRegion!!.contains("us-west-2")) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_2))
        } else {
            s3.setRegion(Region.getRegion(Regions.US_EAST_2))
        }

        try {

            // Set the presigned URL to expire after one hour.
            val expiration = Date()
            var expTimeMillis = expiration.time
            expTimeMillis += 100000 * 60.toLong()
            expiration.time = expTimeMillis

            // Generate the presigned URL.
            println("Generating pre-signed URL.")
            Log.e("ReqKey-->", Constants.PDF_Bucket_Name + orderId + "/" + filePath)
            val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                GeneratePresignedUrlRequest(
                    s3BucketName, "report/" + orderId + "/" + filePath
                )
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
            Log.e("Pre-Signed URL:", url.toString())
            val separated: List<String> = url.toString().split("pdf")
            Log.e(
                "separated",
                separated[0] + "pdf"
            )
            try {
                val pdfIntent =
                    Intent(Intent.ACTION_VIEW)
                pdfIntent.setDataAndType(Uri.parse(url.toString()), "application/pdf")
                pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                mContext.startActivity(pdfIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    mContext,
                    "No Application available to view PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }


            println("Pre-Signed URL: $url")
        } catch (e: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
            Log.e("AmazonServiceException", e.printStackTrace().toString())
        } catch (e: AmazonClientException) {
            Log.e("AmazonClientException", e.printStackTrace().toString())
        }
    }

    fun addAllSearch(results: java.util.ArrayList<CovidStatusResponse.Data>) {
        for (result in results) {
            add(result)
        }
    }

    fun add(r: CovidStatusResponse.Data) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    fun getItem(position: Int): CovidStatusResponse.Data {
        return mData!![position]
    }
}