package com.lifehopehealthapp.notificationList

import android.content.Context
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DoctorListResponse
import com.lifehopehealthapp.ResponseModel.NotificationListResponse
import com.lifehopehealthapp.ResponseModel.NotificationUpdateModel
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class NotificationAdapter(
    notificationListActivity: NotificationListActivity,
    aDummyList: ArrayList<NotificationListResponse.Datum>
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationVH>() {

    private var mData: MutableList<NotificationListResponse.Datum> = ArrayList()
    var mContext: NotificationListActivity

    init {
        this.mContext = notificationListActivity
        this.mData = aDummyList
    }


    fun addAllSearch(results: ArrayList<NotificationListResponse.Datum>) {
        for (result in results) {
            add(result)
        }
    }

    fun add(r: NotificationListResponse.Datum) {
        mData.add(r)
        notifyItemInserted(mData.size - 1)
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData.size

        } else return 0
    }

    class NotificationVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var textViewTitle: AppCompatTextView
        lateinit var textViewTitleDesc: AppCompatTextView
        lateinit var textViewStatus: AppCompatTextView
        lateinit var imageViewUserPhoto: CircleImageView
        lateinit var imageViewAccepted: AppCompatImageView
        lateinit var imageViewRejected: AppCompatImageView

        init {
            findViews(itemView)
        }

        private fun findViews(itemView: View) {
            textViewTitle = itemView.findViewById(R.id.textViewTitle) as AppCompatTextView
            textViewTitleDesc = itemView.findViewById(R.id.textViewTitleDesc) as AppCompatTextView
            textViewStatus = itemView.findViewById(R.id.textViewStatus) as AppCompatTextView
            imageViewUserPhoto = itemView.findViewById(R.id.imageViewUserPhoto) as CircleImageView
            imageViewAccepted = itemView.findViewById(R.id.imageViewAccepted) as AppCompatImageView
            imageViewRejected = itemView.findViewById(R.id.imageViewRejected) as AppCompatImageView
        }

    }

    override fun onBindViewHolder(holder: NotificationVH, position: Int) {
        holder.textViewTitle.text = mData[position].title
        holder.textViewTitleDesc.text = Utils.getDateCurrentTimeZone(
            mData[position].date!!.toLong(),
            Constants.TIMESTAMP_VITALS_DETAILS!!
        )
        Utils.loadSvg(
            mData[position].image,
            mContext,
            holder.imageViewUserPhoto
        )

        if (mData[position].isStatus == 1) {
            holder.imageViewRejected.isVisible = false
            holder.imageViewAccepted.isVisible = false
            holder.textViewStatus.text = "Accepted"
            holder.textViewStatus.setTextColor(mContext.resources.getColor(R.color.white))
            holder.textViewStatus.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.notification_accept_bg))
        } else if (mData[position].isStatus == 2) {
            holder.imageViewRejected.isVisible = false
            holder.imageViewAccepted.isVisible = false
            holder.textViewStatus.text = "Rejected"
            holder.textViewStatus.setTextColor(mContext.resources.getColor(R.color.white))
            holder.textViewStatus.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.notification_reject_bg))
        } else {
            holder.imageViewRejected.isVisible = true
            holder.imageViewAccepted.isVisible = true
            holder.textViewStatus.isVisible = false
        }
        holder.textViewTitle.text = mData[position].title
        holder.textViewTitleDesc.text = Utils.getDateCurrentTimeZone(
            mData[position].date!!.toLong(),
            Constants.TIMESTAMPTODATE_THREE!!
        )

        holder.imageViewAccepted.setOnClickListener {
            val data = NotificationUpdateModel()
            data.id = mData[position].id
            data.status = 1
            EventBus.getDefault().post(data)
        }

        holder.imageViewRejected.setOnClickListener {
            val data = NotificationUpdateModel()
            data.id = mData[position].id
            data.status = 2
            EventBus.getDefault().post(data)
        }
        PresignedImage(
            mData[position].image!!,
            mContext.mPrefs!!.s3BucketName,
            mContext.mPrefs!!.userID,
            mContext,
            Utils.decryption(mContext.mPrefs!!.w3SecretKey),
            Utils.decryption(mContext.mPrefs!!.w3AccessKey),
            mContext.mPrefs!!.s3BucketRegion,
            holder.imageViewUserPhoto
        )
    }


    fun PresignedImage(
        filePath: String,
        s3BucketName: String?,
        userID: String?,
        mContext: Context,
        secrerKey: String?,
        accessKey: String?,
        s3BucketRegion: String?,
        imageViewUserPhoto: CircleImageView
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
            expTimeMillis += 1000 * 60 * 60.toLong()
            expiration.time = expTimeMillis

            // Generate the presigned URL.
            val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                GeneratePresignedUrlRequest(
                    s3BucketName,
                    Constants.Image_Bucket_Name + userID + "/" + filePath
                )
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
            Glide.with(mContext).load(url).placeholder(R.drawable.ic_profile_no_image)
                .skipMemoryCache(true)
                .into(imageViewUserPhoto)
            Log.e("Pre-Signed URL:", url.toString())
            println("Pre-Signed URL: $url")
        } catch (e: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_list, parent, false)
        return NotificationVH(v)
    }

}