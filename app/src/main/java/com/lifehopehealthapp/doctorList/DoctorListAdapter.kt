package com.lifehopehealthapp.doctorList

import android.content.Intent
import android.content.SharedPreferences
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
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
import com.lifehopehealthapp.doctorDetailPage.DoctorDetailActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.Constants.DOCTOR_ID
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class DoctorListAdapter(
    val mContext: DoctorListActivity,
    aDummyList: ArrayList<DoctorListResponse.Datum>
) :
    /*PagingDataAdapter<DoctorListResponse.Datum, DoctorListAdapter.ViewHolder>(
        DiffUtilCallBack()
    )*/ RecyclerView.Adapter<DoctorListAdapter.ViewHolder>() {

    private var mPrefs: SharedPreferences? = null
    private var mData: MutableList<DoctorListResponse.Datum>?

    init {
        mPrefs = PreferenceHelper.defaultPreference(mContext)
        this.mData = aDummyList
    }

    /*class DiffUtilCallBack : DiffUtil.ItemCallback<DoctorListResponse.Datum>() {
        override fun areItemsTheSame(
            oldItem: DoctorListResponse.Datum,
            newItem: DoctorListResponse.Datum
        ): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: DoctorListResponse.Datum,
            newItem: DoctorListResponse.Datum
        ): Boolean {
            return oldItem == newItem
        }

    }*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_list, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDoctorProfilePicture =
            itemView.findViewById(R.id.ivDoctorProfilePicture) as CircleImageView
        val cardDoctorInfo =
            itemView.findViewById(R.id.cardDoctorInfo) as CardView
        val textViewDoctorName =
            itemView.findViewById(R.id.textViewDoctorName) as AppCompatTextView
        val textViewDoctorDepartment =
            itemView.findViewById(R.id.textViewDoctorDepartment) as AppCompatTextView
        val textViewDoctorLocation =
            itemView.findViewById(R.id.textViewDoctorLocation) as AppCompatTextView
        val textViewDoctorQualification =
            itemView.findViewById(R.id.textViewDoctorQualification) as AppCompatTextView
        val textViewDoctorExp =
            itemView.findViewById(R.id.textViewDoctorExp) as AppCompatTextView
        val textViewRatingValue =
            itemView.findViewById(R.id.textViewRatingValue) as AppCompatTextView
        val ratingBarPatient =
            itemView.findViewById(R.id.ratingBarPatient) as AppCompatRatingBar
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewDoctorName.setText(mData!![position].getDoctorName())
        holder.textViewDoctorDepartment.setText(mData!![position].getCategoryName())
        holder.textViewDoctorLocation.setText(mData!![position].getLocation()!!.trim())
        if (mData!![position].getYearOfExperience().toInt()==1){
//            holder.textViewDoctorExp.setText(getItem(position)!!.getYearOfExperience() +" Year of Experience")
            holder.textViewDoctorExp.setText(mData!![position].getYearOfExperience() + if (mData!![position].experienceMonth.toInt()>0)" +Yrs Experience" else " Year of Experience")

        }else{
            holder.textViewDoctorExp.setText(mData!![position].getYearOfExperience() + if (mData!![position].experienceMonth.toInt()>0)" +Yrs Experience" else " Yrs Experience")
        }
        holder.textViewDoctorQualification.setText(mData!![position].getEducation())

        holder.cardDoctorInfo.setOnClickListener {

            //Log.e("omgg->",position.toString())
            //Utils.showToast(mContext, getItem(position)!!.getId().toString(),true)
            val intent = Intent(mContext, DoctorDetailActivity::class.java)
            intent.putExtra(DOCTOR_ID, mData!![position].getId())
            mContext.startActivity(intent)
        }

        getDoctorImage(
            mData!![position].getProfilePic()!!,
            mPrefs!!.s3BucketName,
            mData!![position].getId(),
            Utils.decryption(mPrefs!!.w3SecretKey),
            Utils.decryption(mPrefs!!.w3AccessKey),
            mPrefs!!.s3BucketRegion,
            holder.ivDoctorProfilePicture
        )
    }

    fun getDoctorImage(
        filePath: String,
        s3BucketName: String?,
        userID: String?,
        secrerKey: String?,
        accessKey: String?,
        s3BucketRegion: String?,
        ivDoctorProfilePicture: CircleImageView
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
            val url: URL
            // Generate the presigned URL.
            println("Generating pre-signed URL.")
            val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                GeneratePresignedUrlRequest(
                    s3BucketName,
                    Constants.Doctor_Image_Bucket_Name + userID + "/" + filePath
                )
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            url = s3.generatePresignedUrl(generatePresignedUrlRequest)
            Glide.with(mContext).load(url)
                .placeholder(R.drawable.ic_profile_no_image)
                .error(R.drawable.ic_profile_no_image)
                .into(ivDoctorProfilePicture)
            Log.e("Pre-Signed URL:", url.toString())
            println("Pre-Signed URL: $url")
        } catch (e: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
        }
    }

    fun addAllSearch(results: ArrayList<DoctorListResponse.Datum>) {
        for (result in results) {
            add(result)
        }
    }

    fun add(r: DoctorListResponse.Datum) {
        mData!!.add(r)
        notifyItemInserted(mData!!.size - 1)
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size

        } else return 0
    }

    fun getItem(position: Int): DoctorListResponse.Datum {
        return mData!![position]
    }
}