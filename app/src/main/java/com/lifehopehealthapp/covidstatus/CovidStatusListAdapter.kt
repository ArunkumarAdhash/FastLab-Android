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


class CovidStatusListAdapter(
    val mData: ArrayList<CovidStatusResponse.Data>,
    val mContext: CovidStatusActivity,
    s3BucketName: String?,
    secrtKey: String?,
    accessKey: String?,
    s3BucketRegion: String
    ) : RecyclerView.Adapter<CovidStatusListAdapter.ViewHolder>() {

    private var mS3BucketRegion: String
    private var mAccessKey: String?
    private var mSecrtKey: String?
    private var mS3BucketNAme: String?


    init {
        mS3BucketNAme = s3BucketName
        mSecrtKey = secrtKey
        mAccessKey = accessKey
        mS3BucketRegion = s3BucketRegion
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CovidStatusListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_covid_status_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: CovidStatusListAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.textview_name.text = mData.get(position)!!.patientName
        holder.textview_order_id.text = "Order ID: "+mData.get(position)!!.orderNo + "-"+ Utils.getDateFromUTCTimestamp(
            mData.get(position)!!.orderDate!!.toLong(),
            Constants.TIMESTAMPTODATE_TWO
        ).toString()


        holder.ivCovidStatus.setBackgroundResource(if(mData.get(position).isStatus==1) R.drawable.ic_positive else R.drawable.ic_negative)



        holder.cardViewItem.setOnClickListener {
            val result = mData[position]!!.imagePath
            val separated: List<String> = result!!.split("/")
            Log.e("filePAth", separated[5])
            Presigned(
                separated[5],
                mS3BucketNAme,
                mData[position]!!.orderId,
                mContext,
                mSecrtKey,
                mAccessKey
            )

        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val textview_name =
            itemView.findViewById(R.id.textview_name) as AppCompatTextView

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



}