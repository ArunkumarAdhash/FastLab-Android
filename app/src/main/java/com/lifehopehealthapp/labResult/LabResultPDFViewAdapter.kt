package com.lifehopehealthapp.labResult

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
import com.lifehopehealthapp.ResponseModel.LabResultPDFResponse
import com.lifehopehealthapp.utils.Constants
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class LabResultPDFViewAdapter(
    aDummyList: ArrayList<LabResultPDFResponse.Datum>,
    labResultActivity: LabResultActivity,
    categoryName: String?,
    s3BucketName: String?,
    secrtKey: String?,
    accessKey: String?,
    userID: String?,
    profilepic: String,
    s3BucketRegion: String
) : /*PagingDataAdapter<LabResultPDFResponse.Datum, LabResultPDFViewAdapter.ViewHolder>(
    DiffUtilCallBack()*/ RecyclerView.Adapter<LabResultPDFViewAdapter.ViewHolder>() {

    private var mS3BucketRegion: String
    private var mProfilePicture: String
    private var mUserId: String?
    private var mAccessKey: String?
    private var mSecrtKey: String?
    private var mS3BucketNAme: String?
    private var title: String?
    private var mContext: LabResultActivity
    private var mData: MutableList<LabResultPDFResponse.Datum>?

    init {
        this.mData = aDummyList
        mContext = labResultActivity
        title = categoryName
        mS3BucketNAme = s3BucketName
        mSecrtKey = secrtKey
        mAccessKey = accessKey
        mUserId = userID
        mProfilePicture = profilepic
        mS3BucketRegion = s3BucketRegion
    }

    /*class DiffUtilCallBack : DiffUtil.ItemCallback<LabResultPDFResponse.Datum>() {
        override fun areItemsTheSame(
            oldItem: LabResultPDFResponse.Datum,
            newItem: LabResultPDFResponse.Datum
        ): Boolean {
            return oldItem.getOrderNo() == newItem.getOrderNo()
        }

        override fun areContentsTheSame(
            oldItem: LabResultPDFResponse.Datum,
            newItem: LabResultPDFResponse.Datum
        ): Boolean {
            return oldItem.getOrderNo() == newItem.getOrderNo()
                    && oldItem.getOrderId() == newItem.getOrderId()
        }

    }*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lab_pdf_document, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LabResultPDFViewAdapter.ViewHolder, position: Int) {
        holder.textViewOrderID.text =
            mContext.resources.getString(R.string.text_order_id) + " "+mData!![position].orderNo

        holder.textviewPatientName.text = mData!![position].getPatientName()

        holder.cardPDFView.setOnClickListener {
            val result = mData!![position].getImagePath()
            val separated: List<String> = result!!.split("/")
            Log.e("filePAth", separated[5])
            Presigned(
                separated[5],
                mS3BucketNAme,
                mData!![position].getOrderId(),
                mContext,
                mSecrtKey,
                mAccessKey
            )

        }
        holder.textViewOrderDate.text = com.lifehopehealthapp.utils.Utils.getDateCurrentTimeZone(
            mData!![position].orderDate!!.toLong(), Constants.TIMESTAMPTODATE_THREE!!
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewOrderID =
            itemView.findViewById(R.id.textviewOrderID) as AppCompatTextView
        val textViewOrderDate =
            itemView.findViewById(R.id.textviewOrderDate) as AppCompatTextView
        val cardPDFView =
            itemView.findViewById(R.id.cardViewPDFView) as CardView
        val textviewPatientName =
            itemView.findViewById(R.id.textviewPatientName) as AppCompatTextView
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
            StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        val credentials = BasicAWSCredentials(
            accessKey, secrerKey
        )
        val s3 = AmazonS3Client(credentials)
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        if (mS3BucketRegion.contains("us-west-1")) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_1))
        } else if (mS3BucketRegion.contains("us-west-2")) {
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
            /*Log.e(
                "separated",
                separated[0] + "pdf"
            )*/

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

            /*val obj = URL(url.toString())
            val conn: HttpURLConnection = obj.openConnection() as HttpURLConnection
            conn.setRequestMethod("GET")
            val response: Int = conn.getResponseCode()
            if (response === 200) {
                //Reading the response to a StringBuffer
                val responseReader = Scanner(conn.inputStream)
                val buffer = StringBuffer()
                while (responseReader.hasNextLine()) {
                    buffer.append(
                        """
                            ${responseReader.nextLine()}
                            
                            """.trimIndent()
                    )
                }
                responseReader.close()
            }
            var mUrl: URL? = null
            try {
                mUrl = URL(url.toString())
                //RetrivePDFfromUrl().execute(mUrl.toString())
            } catch (e: MalformedURLException) {
                println("The URL is not valid.")
                Log.e("error->", e.message.toString())
            }*/
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

    fun addAllSearch(results: ArrayList<LabResultPDFResponse.Datum>) {
        for (result in results) {
            add(result)
        }
    }

    fun add(r: LabResultPDFResponse.Datum) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    fun getItem(position: Int): LabResultPDFResponse.Datum {
        return mData!![position]
    }
}