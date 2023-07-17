package com.lifehopehealthapp.vaccineReport

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DeleteDocumentKey
import com.lifehopehealthapp.ResponseModel.VaccinationListDatum
import com.lifehopehealthapp.WebViewActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.DocumentViewerActivity
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.Utils
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener
import org.greenrobot.eventbus.EventBus
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class VaccineReportListAdapter(
    aDummyList: ArrayList<VaccinationListDatum>,
    val mContext: VaccineReportActivity,
    val s3BucketName: String?,
    val w3SecretKey: String?,
    val w3AccessKey: String?,
    val s3BucketRegion: String?
) : /*PagingDataAdapter<VaccinationListDatum, VaccineReportListAdapter.ViewHolder>(DiffUtilCallBack())*/
    RecyclerView.Adapter<VaccineReportListAdapter.ViewHolder>() {


    /*class DiffUtilCallBack : DiffUtil.ItemCallback<VaccinationListDatum>() {
        override fun areItemsTheSame(
            oldItem: VaccinationListDatum,
            newItem: VaccinationListDatum
        ): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(
            oldItem: VaccinationListDatum,
            newItem: VaccinationListDatum
        ): Boolean {
            return oldItem.getId() == newItem.getId()
                    && oldItem.getName() == newItem.getName()
        }

    }*/

    private var mData: MutableList<VaccinationListDatum>?
    private var mPrefs: SharedPreferences? = null

    init {
        this.mData = aDummyList
        mPrefs = PreferenceHelper.defaultPreference(mContext)
    }

    private var row_index: Int? = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VaccineReportListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccine_report_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: VaccineReportListAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        //holder.imageViewVitalImage.loadSvg(mData?.get(position)!!.fileUrl)
        holder.textViewVitalsName.text = mData?.get(position)!!.name
        Log.e("name->", getItem(position)!!.name!!)
        holder.textViewDate.text = Utils.getDateCurrentTimeZone(
            mData?.get(position)!!.createdDate!!.toLong(),
            Constants.TIMESTAMPTODATE_TWO!!
        )
        holder.swipeLayout.setOnActionsListener(object : SwipeActionsListener {
            override fun onOpen(direction: Int, isContinuous: Boolean) {

            }

            override fun onClose() {
                holder.swipeLayout.close()
            }

        })


        holder.imageViewDelete.setOnClickListener {
            val data = DeleteDocumentKey()
            data.id = mData?.get(position)!!.getId()!!
            data.url = mData?.get(position)!!.getFileUrl()!!
            EventBus.getDefault().post(data)
        }

        holder.cardViewItem.setOnClickListener {
            Presigned(
                "ProfilePic/" + mPrefs!!.userID + "/" + "Vaccination/" + getItem(position)!!.fileUrl!!,
                s3BucketName,
                mContext,
                Utils.decryption(w3SecretKey),
                Utils.decryption(w3AccessKey),
                s3BucketRegion
            )
        }
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewDelete = itemView.findViewById(R.id.right_view) as ImageView
        val swipeLayout = itemView.findViewById(R.id.swipe_layout) as SwipeLayout
        val textViewVitalsName =
            itemView.findViewById(R.id.textViewVitalsName) as AppCompatTextView
        val textViewDate =
            itemView.findViewById(R.id.textViewDate) as AppCompatTextView
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


    fun Presigned(
        filePath: String,
        s3BucketName: String?,
        mContext: Context,
        secrerKey: String?,
        accessKey: String?,
        s3BucketRegion: String?
    ) {
        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        val credentials = BasicAWSCredentials(
            accessKey, secrerKey
        )
        val s3 = AmazonS3Client(credentials)
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        if (s3BucketRegion!!.contains("us-west-1")) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_1))
        } else if (s3BucketRegion.contains("us-west-2")) {
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

            Log.e(
                "ReqKey-->",
                "ProfilePic/b06bf32d-ba50-4ef3-956e-07d78e3b81ad/Vaccination/" + filePath
            )
            val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                GeneratePresignedUrlRequest(
                    s3BucketName, filePath
                )
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
            Log.e("Pre-Signed URL:", url.toString())
            //val separated: List<String> = url.toString().split("pdf")

            if (filePath.contains("jpg") || filePath.contains("png")) {
                val intent = Intent(mContext, WebViewActivity::class.java)
                val aBundle = Bundle()
                aBundle.putString(
                    Constants.PAGE_TITLE,
                    mContext.resources.getString(R.string.text_vaccine_report)
                )
                aBundle.putString(Constants.PAGE_URL, url.toString())
                intent.putExtras(aBundle)
                mContext.startActivity(intent)
            } else if (filePath.contains("pdf")) {
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
            } else {
                val intent = Intent(mContext, DocumentViewerActivity::class.java)
                val aBundle = Bundle()
                aBundle.putString(
                    Constants.PAGE_TITLE,
                    mContext.resources.getString(R.string.text_vaccine_report)
                )
                aBundle.putString(Constants.PAGE_URL, url.toString())
                intent.putExtras(aBundle)
                mContext.startActivity(intent)
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

    fun addAllSearch(results: ArrayList<VaccinationListDatum>) {
        for (result in results) {
            add(result)
        }
    }

    fun ClearDataValue(results: ArrayList<VaccinationListDatum>) {
        mData?.clear()
    }

    fun add(r: VaccinationListDatum) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    fun getItem(position: Int): VaccinationListDatum {
        return mData!![position]
    }
}