package com.lifehopehealthapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.TextUtils
import android.text.format.DateFormat
import android.transition.Slide
import android.transition.TransitionSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.*
import com.facebook.FacebookSdk
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.BuildConfig
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DeleteImageKey
import com.lifehopehealthapp.ResponseModel.DownloadImageViewModel
import com.lifehopehealthapp.ResponseModel.UploadImageKey
import com.lifehopehealthapp.retrofitService.crypto.CryptoHelper
import com.lifehopehealthapp.utils.Utils.BitmapConvert.rotateImage
import com.lifehopehealthapp.webview.WebViewPaymentActivity
import com.lifehopehealthapp.widgets.LifeHopeButton
import com.lifehopehealthapp.widgets.LifeHopenTextView
import org.greenrobot.eventbus.EventBus
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import com.amazonaws.services.s3.model.S3ObjectSummary
import com.lifehopehealthapp.utils.PreferenceHelper.s3BuckeoldprofileImg


object Utils {
    val aBundle = Bundle()

    var mPrefs: SharedPreferences? = null


    /**
     * To check Lollipop verison sdk
     *
     * @return true equal and higher , false for lower 6.0
     */
    fun isLollipopHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun getDay(s: String, format: String): String? {
        try {
            val sdf = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.icu.text.SimpleDateFormat(format)
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun isValidMail(email: String): Boolean {
        val EMAIL_STRING = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return Pattern.compile(EMAIL_STRING).matcher(email).matches()
    }

    fun convertStringToArraylist(str: String): ArrayList<Char>? {
        val charList = ArrayList<Char>()
        for (i in 0 until str.length) {
            charList.add(str[i])
        }
        return charList
    }

    fun View.visible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun getDaysBetweenDates(start: String?, end: String?): Long {
        val dateFormat =
            SimpleDateFormat(Constants.TIMESTAMP_DOB, Locale.ENGLISH)
        val startDate: Date
        val endDate: Date
        var numberOfDays: Long = 0
        try {
            startDate = dateFormat.parse(start)
            endDate = dateFormat.parse(end)
            numberOfDays =
                getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return numberOfDays
    }

    private fun getUnitBetweenDates(
        startDate: Date,
        endDate: Date,
        unit: TimeUnit
    ): Long {
        val timeDiff = endDate.time - startDate.time
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS)
    }

    fun compareWithCurrentDate(dateString: String?): Int {
        val format = SimpleDateFormat("MM/dd/yyyy")
        format.isLenient = false
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 =
                format.parse(getCurrentDateString())
            date2 = format.parse(dateString)
        } catch (e: ParseException) {
            return -2
        }
        return if (date2.compareTo(date1) < 0) {
            -1
        } else if (date2.compareTo(date1) > 0) {
            1
        } else {
            0
        }
    }


    fun decryption(strEncryptedText: String?): String? {
        var strDecryptedText = ""

        try {
            //strDecryptedText = CryptoUtil.decrypt(strEncryptedText)
            strDecryptedText = CryptoHelper.decrypt(strEncryptedText)
            Log.e("strDecryptedText", strDecryptedText)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return strDecryptedText
    }

    fun Presigned(
        filePath: String,
        s3BucketName: String?,
        userID: String?,
        mContext: Context,
        secrerKey: String?,
        accessKey: String?,
        s3BucketRegion: String?,
        selection: String
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
            if (selection.equals("doctor")) {
                val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                    GeneratePresignedUrlRequest(
                        s3BucketName,
                        Constants.Doctor_Image_Bucket_Name + userID + "/" + filePath
                    )
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration)
                url = s3.generatePresignedUrl(generatePresignedUrlRequest)
            }
            else {
                val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                    GeneratePresignedUrlRequest(
                        s3BucketName,
                        Constants.Image_Bucket_Name + userID + "/" + filePath
                    )
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration)
                url = s3.generatePresignedUrl(generatePresignedUrlRequest)
            }

            val data = DownloadImageViewModel()
            data.mUrl = url.toString()
            EventBus.getDefault().post(data)
            Log.e("Pre-Signed URL:", url.toString())
            println("Pre-Signed URL: $url")
        } catch (e: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
        }
    }



    fun deleteAWSObject(
        filePath: String,
        s3BucketName: String?,
        secrerKey: String?,
        accessKey: String?,
        s3BucketRegion: String?, context: Context, currentProfileImage: String
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
//            val deleteObjectRequest = DeleteObjectRequest(s3BucketName + "/", filePath)
//            s3.deleteObject(deleteObjectRequest)
//            s3.deleteObject(DeleteObjectRequest(s3BucketName, filePath))

            //newly added 20-01-2021
            mPrefs = PreferenceHelper.defaultPreference(context)


            // val deleteObjectRequest = DeleteObjectRequest(s3BucketName + "/", filePath)
            //s3.deleteObject(deleteObjectRequest)
            //val DeleteObjectsResult = s3.deleteObject(DeleteObjectRequest(s3BucketName, filePath))

            Thread {
                val object_listing: ObjectListing = s3.listObjects(s3BucketName)
                val iterator: Iterator<*> = object_listing.objectSummaries.iterator()
                while (iterator.hasNext()) {
                    val summary = iterator.next() as S3ObjectSummary
                    // println("ListOfObjects2:::"+summary)
                    //println("ListOfObjects2:::" + summary.key)
                    //  println("ListOfObjects_filepath1:::"+filePath)

                    if (summary.key.equals(filePath)) {
                        println("ListOfObjects_filepath2:::" + filePath)
                        s3.deleteObject(s3BucketName, summary.key)
                        val selectImageKey = DeleteImageKey()
                        selectImageKey.imageID = summary.key
                        EventBus.getDefault().post(selectImageKey)


                        if (currentProfileImage.isNotEmpty())
                            mPrefs!!.s3BuckeoldprofileImg = currentProfileImage

                        println("ListOfObjects3:::" + summary.key)
                    }
                }
            }.start()
        } catch (e: AmazonServiceException) {
            Log.e("AmazonServiceException", e.message.toString())
        } catch (e: Exception) {
            Log.e("AWSDeleteException", e.message.toString())
        }


    }

    private fun getFileExtension1(url: String, mContext: Context): String? {
        val contentResolver: ContentResolver = mContext.getContentResolver()
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.toString())
    }


    private fun getFileExtension(uri: Uri, context: Context): String? {
        val contentResolver: ContentResolver = context.getContentResolver()
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun uploadAWSimage(
        filePath: String,
        s3BucketName: String?,
        userID: String?,
        mContext: Context,
        secrerKey: String?,
        accessKey: String?,
        key: String,
        s3BucketRegion: String?
    ): String {
        var mEndPoint = ""
        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        val credentials = BasicAWSCredentials(
            accessKey,
            secrerKey
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
        //s3.setEndpoint("https://s3.us-west-2.amazonaws.com")

        val transferUtility = TransferUtility.builder()
            .defaultBucket(s3BucketName).context(mContext).s3Client(s3)
            .build()

        val file = File(filePath)
        val fileName = System.currentTimeMillis()

        if (key.equals("")) {
            val uploadObserver =
                transferUtility.upload(
                    s3BucketName,
                    "ProfilePic/" + userID + "/" + userID + "_" + fileName + ".jpg",
                    file,
                    CannedAccessControlList.Private
                )

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        mEndPoint = uploadObserver.key
                        val separated: List<String> = mEndPoint.split("/")
                        Log.e(
                            "separated",
                            separated[0] + " --> " + separated[1] + " --> " + separated[2]
                        )
                        mEndPoint = separated[2]

                        val selectImageKey = UploadImageKey()
                        selectImageKey.imageID = mEndPoint
                        EventBus.getDefault().post(selectImageKey)
                        Log.e(
                            "onStateChanged",
                            uploadObserver.key
                        )
                    }

                }

                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    val done = (((current.toDouble() / total) * 100.0).toInt())
                    Log.e("onProgressChanged", "UPLOAD - - ID: $id, percent done = $done")
                }

                override fun onError(id: Int, ex: Exception) {
                    Log.e("onError", "UPLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
                }
            })
        } else {
            val uploadObserver =
                transferUtility.upload(
                    s3BucketName,
                    "ProfilePic/" + userID + "/" + key + userID + "_" + fileName + ".jpg",
                    file,
                    CannedAccessControlList.Private
                )

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        mEndPoint = uploadObserver.key
                        val separated: List<String> = mEndPoint.split("/")
                        Log.e(
                            "separated",
                            separated[0] + " --> " + separated[1] + " --> " + separated[2]
                        )
                        mEndPoint = separated[2]

                        val selectImageKey = UploadImageKey()
                        selectImageKey.imageID = mEndPoint
                        EventBus.getDefault().post(selectImageKey)

                    }

                }

                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    val done = (((current.toDouble() / total) * 100.0).toInt())
                    Log.e("onProgressChanged", "UPLOAD - - ID: $id, percent done = $done")
                }

                override fun onError(id: Int, ex: Exception) {
                    Log.e("onError", "UPLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
                }
            })
        }
        return mEndPoint
    }

    fun uploadDocumentAWS(
        filePath: String,
        s3BucketName: String?,
        userID: String?,
        mContext: Context,
        secrerKey: String?,
        accessKey: String?,
        fileName: String,
        s3BucketRegion: String?
    ): String {
        var mEndPoint = ""
        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        val credentials = BasicAWSCredentials(
            accessKey,
            secrerKey
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
        //s3.setEndpoint("https://s3.us-west-2.amazonaws.com")

        val transferUtility = TransferUtility.builder()
            .defaultBucket(s3BucketName).context(mContext).s3Client(s3)
            .build()

        val file = File(filePath)


        val uploadObserver =
            transferUtility.upload(
                s3BucketName,
                "ProfilePic/" + userID + "/" + "Vaccination/" + fileName,
                file,
                CannedAccessControlList.Private
            )

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    mEndPoint = uploadObserver.key
                    val separated: List<String> = mEndPoint.split("Vaccination/")
                    Log.e("separated->", separated[0] + " " + separated[1])
                    val selectImageKey = UploadImageKey()
                    selectImageKey.imageID = separated[1]
                    EventBus.getDefault().post(selectImageKey)
                    Log.e(
                        "onStateChanged",
                        uploadObserver.key
                    )
                }

            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                val done = (((current.toDouble() / total) * 100.0).toInt())
                Log.e("onProgressChanged", "UPLOAD - - ID: $id, percent done = $done")
            }

            override fun onError(id: Int, ex: Exception) {
                Log.e("onError", "UPLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
            }
        })

        return mEndPoint
    }

    fun PerfectDecimal(
        str: String,
        MAX_BEFORE_POINT: Int,
        MAX_DECIMAL: Int
    ): String? {
        var str = str
        if (str[0] == '.') str = "0$str"
        val max = str.length
        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && after == false) {
                up++
                if (up > MAX_BEFORE_POINT) return rFinal
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL) return rFinal
            }
            rFinal = rFinal + t
            i++
        }
        return rFinal
    }

    fun getAge(DOByear: Int, DOBmonth: Int, DOBday: Int): Int {
        var age: Int
        val calenderToday = Calendar.getInstance()
        val currentYear = calenderToday[Calendar.YEAR]
        val currentMonth = 1 + calenderToday[Calendar.MONTH]
        val todayDay = calenderToday[Calendar.DAY_OF_MONTH]
        age = currentYear - DOByear
        if (DOBmonth > currentMonth) {
            --age
        } else if (DOBmonth == currentMonth) {
            if (DOBday > todayDay) {
                --age
            }
        }
        return age
    }
    /*fun getAge(year: Int, month: Int, day: Int): String? {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] <= dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }*/

    fun compareDates(date1String: String?, date2String: String?): Int {
        val format = SimpleDateFormat("MM/dd/yyyy")
        format.isLenient = false
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = format.parse(date1String)
            date2 = format.parse(date2String)
        } catch (e: ParseException) {
            return -2
        }
        return if (date2.compareTo(date1) < 0) {
            -1
        } else if (date2.compareTo(date1) > 0) {
            1
        } else {
            0
        }
    }

    fun getCurrentDateString(): String? {
        val dateFormat: java.text.DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date = Date()
        return dateFormat.format(date)
    }

    fun clearSharedPreferences(ctx: Context) {
        val dir = File(ctx.filesDir.parent + "/shared_prefs/")
        val children = dir.list()
        for (i in children.indices) {
            // clear each preference file
            ctx.getSharedPreferences(
                children[i].replace(".xml", ""),
                Context.MODE_PRIVATE
            ).edit().clear().apply()
            //delete the file
            File(dir, children[i]).delete()
        }
    }

    fun USFormatMobile(phoneNum: Long): String? {
        val sb = java.lang.StringBuilder(15)
        val temp = java.lang.StringBuilder(phoneNum.toString())
        while (temp.length < 10) temp.insert(0, "0")
        val chars = temp.toString().toCharArray()
        sb.append("(")
        for (i in chars.indices) {
            if (i == 3) sb.append(") ") else if (i == 6) sb.append("-")
            sb.append(chars[i])
        }
        return sb.toString()
    }

    fun isAppRunning(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos: List<ActivityManager.RunningAppProcessInfo>
        if (activityManager != null) {
            procInfos = activityManager.runningAppProcesses
            for (processInfo in procInfos) {
                if (processInfo.processName == FacebookSdk.getApplicationContext()
                        .packageName
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun getDateCurrentTimeZone(timestamp: Long, pattern: String): String? {
        try {
            val calendar: Calendar = Calendar.getInstance()
            val tz: TimeZone = TimeZone.getTimeZone("UTC")
            calendar.setTimeInMillis(timestamp * 1000)
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()))
            val sdf = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.icu.text.SimpleDateFormat(pattern, Locale.US)
            } else {
                SimpleDateFormat(pattern, Locale.US)
            }
            val currenTimeZone: Date = calendar.getTime() as Date
            return sdf.format(currenTimeZone)
        } catch (e: Exception) {
        }
        return ""
    }

    fun getDateFromUTCTimestamp(
        mTimestamp: Long,
        mDateFormate: String?
    ): String? {
        var date: String? = null
        try {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.timeInMillis = mTimestamp * 1000L
            date = DateFormat.format(mDateFormate, cal.timeInMillis)
                .toString()
            val formatter = SimpleDateFormat(mDateFormate)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(date)
            val dateFormatter = SimpleDateFormat(mDateFormate)
            dateFormatter.timeZone = TimeZone.getDefault()
            date = dateFormatter.format(value)
            return date
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return date
    }

    fun stringToTimeStamp(strTime: String?): Timestamp? {
        var tempTimestamp: Timestamp? = null
        if (strTime != null && strTime != "") {
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd"
            ) //format of date and time you have
            var parsedTimeStamp: Date? = null
            try {
                parsedTimeStamp = dateFormat.parse(strTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            tempTimestamp = Timestamp(parsedTimeStamp!!.time)
        }
        return tempTimestamp
    }

    fun getGMTTimeStampFromDate(datetime: String): Long {
        var timeStamp: Long = 0
        var localTime = Date()
        //val format = "dd-MM-yyyy HH:mm:ss"
        val format = "MM/dd/yyyy"
        val sdfLocalFormat = SimpleDateFormat(format)
        sdfLocalFormat.timeZone = TimeZone.getDefault()
        try {
            localTime = sdfLocalFormat.parse(datetime) as Date
            val cal = Calendar.getInstance(
                TimeZone.getTimeZone("UTC"),
                Locale.getDefault()
            )
            val tz = cal.timeZone
            Log.e("tz", tz.toString())
            cal.time = localTime
            Log.e("cal.time", cal.time.toString())

            timeStamp = localTime.time / 1000
            Log.e("localTime", localTime.time.toString())
            Log.d(
                "GMT TimeStamp: ", " Date TimegmtTime: " + datetime
                        + ", GMT TimeStamp : " + localTime.time
            )
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return timeStamp
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    fun isValidPhoneNumbers(target: CharSequence): Boolean {
        return if (target.length <= 10 && target.length >= 10) {
            true
        } else if (target.startsWith("0")) {
            false
        } else {
            false
        }
    }

    fun isValidPhoneNumber(target: CharSequence): Boolean {
        return when {
            target.length != 10 -> {
                return false
            }
            target.startsWith("0") -> {
                return false
            }
            else -> {
                return true
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        val expression = "^[\\w\\.]+@([\\w]+\\.)+[A-Z]{2,7}$"
        val inputString: CharSequence = email
        val pattern =
            Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(inputString)
        return if (matcher.matches()) {
            true
        } else {
            false
        }
    }

    fun getJsonObjectValue(aValue: String): JsonObject? {
        val aJsonParser = JsonParser()
        return aJsonParser.parse(aValue) as JsonObject
    }

    fun getDate(time_stamp_server: Long): String? {
        val formatter = SimpleDateFormat("dd-mm-yyyy")
        return formatter.format(time_stamp_server)
    }

    fun getAppVersion(): String? {

        return BuildConfig.VERSION_NAME
    }

    fun getAppVersionCode(): Int {
        return BuildConfig.VERSION_CODE
    }

    fun getDeviceOS(): String? {
        val aOSName = Build.VERSION.RELEASE
        Log.e("APP", "DeviceOS$aOSName")
        return aOSName
    }

    fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    fun noInternetAlert(msg: String?, context: Context) {
        val view1 =
            View.inflate(context, R.layout.alert_no_internet, null)
        val internetDialog =
            Dialog(context, R.style.dialogwinddow)
        internetDialog.setContentView(view1)
        internetDialog.setCancelable(false)
        internetDialog?.dismiss()
        internetDialog.show()
        val viewGap =
            internetDialog.findViewById<View>(R.id.view_gap)
        viewGap.visibility = View.VISIBLE
        val title: LifeHopenTextView = internetDialog.findViewById(R.id.tv_title)
        title.setText(msg)
        val button_success: LifeHopeButton = internetDialog.findViewById(R.id.button_success)
        button_success.setText(context.getResources().getString(R.string.retry))
        val button_failure: LifeHopeButton = internetDialog.findViewById(R.id.button_failure)
        button_failure.setVisibility(View.VISIBLE)
        button_success.setOnClickListener({ view ->
            internetDialog.dismiss()
        })
        button_failure.setOnClickListener({ view ->
            internetDialog.dismiss()
        })
    }

    fun getManufacture(): String? {
        val aBrand = Build.BRAND
        val aDevice = Build.DEVICE
        val aModel = Build.MODEL
        val aManufacture = Build.MANUFACTURER
        val aProduct = Build.PRODUCT
        Log.e(
            "APP",
            "aBrand" + aBrand + "aDevice" + aDevice + "aModel" + aModel + "aManufacture" + aManufacture +
                    "aManufacture" + aProduct
        )
        return aModel
    }

    @SuppressLint("HardwareIds")
    fun getDeviceToken(aContext: Context): String? {
        val aDeviceToken = Settings.Secure.getString(
            aContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.e("APP", "DEVICE TOKEN$aDeviceToken")
        return aDeviceToken
    }

    fun getDialog(context: Context?): Dialog? {
        val loaderDialog = Dialog(context!!)
        try {
            val lp =
                WindowManager.LayoutParams()
            val window = loaderDialog.window
            loaderDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.requestFeature(Window.FEATURE_NO_TITLE)
            loaderDialog.setContentView(R.layout.dialog_progress)
            loaderDialog.setCanceledOnTouchOutside(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return loaderDialog
    }

    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var totalDuration = totalDuration
        var currentDuration = 0
        totalDuration = (totalDuration / 1000)
        currentDuration = (progress.toDouble() / 100 * totalDuration).toInt()

        // return current duration in milliseconds
        return currentDuration * 1000
    }


    fun showAlert(
        context: Context?,
        showLoader: Int,
        title: String?,
        message: String?,
        posTxt: String?,
        posClickCallListener: DialogInterface.OnClickListener?,
        negTxt: String?,
        negClickCallListener: DialogInterface.OnClickListener?,
        isCancelable: Boolean,
        @DrawableRes titleLeftDrawableId: Int
    ): Dialog? {
        val alertDialog = Dialog(context!!)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_view)
        alertDialog.setCancelable(isCancelable)
        alertDialog.setCanceledOnTouchOutside(isCancelable)

        val titleTv: AppCompatTextView = alertDialog.findViewById(R.id.text_heading)
        titleTv.setText(title)
        if (titleLeftDrawableId != 0) {
            titleTv.setCompoundDrawablesWithIntrinsicBounds(titleLeftDrawableId, 0, 0, 0)
        }

        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        posTv.setAllCaps(false)
        if (!TextUtils.isEmpty(posTxt)) {
            posTv.setText(posTxt)
            posTv.setOnClickListener(View.OnClickListener {
                if (posClickCallListener != null) {
                    posClickCallListener.onClick(alertDialog, 0)
                } else {
                    alertDialog.dismiss()
                }
            })
        } else {
            posTv.setVisibility(View.GONE)
        }
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.setAllCaps(false)
        if (!TextUtils.isEmpty(negTxt)) {
            negTv.setText(negTxt)
            negTv.setOnClickListener(View.OnClickListener {
                if (negClickCallListener != null) {
                    negClickCallListener.onClick(alertDialog, 0)
                } else {
                    alertDialog.dismiss()
                }
            })
        } else {
            negTv.setVisibility(View.GONE)
        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.gravity = Gravity.BOTTOM
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        return alertDialog
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showToast(
        activity: Activity,
        message: String,
        status: Boolean
    ) {
        if (activity.isFinishing) {
            return
        }
        val view: View? = null
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(R.layout.layout_toast, null)
        val text: AppCompatTextView = layout.findViewById(R.id.textview_response)
        val background1: ConstraintLayout = layout.findViewById(R.id.layout_background_color)
        if (status) {
            background1.setBackgroundResource(R.drawable.divider_toast_bg)
        } else {
            background1.setBackgroundResource(R.drawable.divider_button_line_border)
        }
        text.setText(message)
        val toast = Toast(activity.applicationContext)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.setView(layout)
        toast.show()
    }

    /*fun webViewData(title: String, url: String, context: Context, view: View) {
        aBundle.putString(Constants.PAGE_TITLE, title)
        aBundle.putString(Constants.PAGE_URL, url)
        WebViewActivity.startActivity(context as Activity, aBundle, view)
    }*/

    fun webViewData(title: String, url: String, order_id: String, context: Context) {
        aBundle.putString(Constants.PAGE_TITLE, title)
        aBundle.putString(Constants.PAGE_URL, url)
        aBundle.putString(Constants.ORDER_ID, order_id)
        WebViewPaymentActivity.startActivity(context as Activity, aBundle)
    }

    /**
     * Lollipop Animation
     *
     * @param window  getWindow()
     * @param context current instance context
     */
    fun setupWindowAnimations(window: Window, context: Context) {
        /*Slide enterTransition = new Slide();
       enterTransition.setSlideEdge(Gravity.RIGHT);*/
        if (isLollipopHigher()) {
            val enterTransition = TransitionSet()
            enterTransition.addTransition(Slide(Gravity.RIGHT))
            //            enterTransition.excludeTarget(R.id.header_lay, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true)
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true)
            enterTransition.duration =
                context.resources.getInteger(R.integer.anim_duration_long).toLong()
            window.enterTransition = enterTransition
            /*ChangeTransform explode = new ChangeTransform();
            explode.setDuration(context.getResources().getInteger(R.integer.anim_duration_long_medium));*/
        }
    }

    /* public static AlertDialog alert_view_dialogContxt(final Context mContext, String title, String message,
                             String success_txt, String failure_txt,
                             Boolean cancelable_val, final DialogInterface.OnClickListener postive_dialogInterface, final DialogInterface.OnClickListener negative_dialogInterface, final String s) {

         if (!((Activity) mContext).isFinishing()) {

             if (mContext != null) {
                 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

                 LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                 View dialogView = inflater.inflate(R.layout.alert_no_internet, null);
                 dialog.setView(dialogView);

                 dialog.setCancelable(cancelable_val);
 //                dialog.setMessage(message);
                 dialog.setPositiveButton(success_txt, postive_dialogInterface)
                         .setNegativeButton(failure_txt, negative_dialogInterface);
                 if (alert != null && alert.isShowing())
                     alert.dismiss();
                 alert = dialog.create();

                 alert.setOnShowListener(new DialogInterface.OnShowListener() {
                     @Override
                     public void onShow(DialogInterface arg0) {
                         if (alert != null) {
                             alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.dark_grey));
                             alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                         }
                     }
                 });


                 alert.show();
             }
         }


         return alert;
     }*/
    fun alert_view_dialogContxt(
        mContext: Context?,
        title: String?,
        message: String?,
        success_txt: String?,
        failure_txt: String?,
        cancelable_val: Boolean?,
        postive_dialogInterface: DialogInterface.OnClickListener?,
        negative_dialogInterface: DialogInterface.OnClickListener?,
        s: String?
    ): AlertDialog? {
        var dialog: Dialog? = null
        val alert: AlertDialog? = null
        try {
            if (!(mContext as Activity?)!!.isFinishing) {
                if (mContext != null) {
                    val view1 =
                        View.inflate(mContext, R.layout.alert_no_internet, null)
                    if (dialog == null) dialog =
                        Dialog(mContext, R.style.dialogwinddow)
                    /*LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_no_internet, null);*/dialog.setContentView(
                        view1
                    )
                    dialog.setCancelable(cancelable_val!!)
                    val ok: AppCompatButton =
                        dialog.findViewById(R.id.button_success)
                    ok.setOnClickListener(View.OnClickListener { dialog.dismiss() })
                    if (dialog.isShowing()) {
                        dialog.dismiss()
                        //dialog.show();
                    } else {
                        dialog.show()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return alert
    }

    object BitmapConvert {
        @Throws(IOException::class)
        fun createBitmap(getPath: String?): Bitmap {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(getPath)
            val bitmapimg =
                BitmapFactory.decodeStream(url.content as InputStream)
            val stream = ByteArrayOutputStream()
            bitmapimg.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            //            bitmap.recycle();
            val output = Bitmap.createBitmap(
                bitmapimg.width,
                bitmapimg.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(
                0, 0, bitmapimg.width,
                bitmapimg.height
            )
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawCircle(
                bitmapimg.width / 2.toFloat(),
                bitmapimg.height / 2.toFloat(), bitmapimg.width / 2.toFloat(), paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmapimg, rect, rect, paint)
            return rotateImage(output, byteArray)
        }

        @Throws(IOException::class)
        fun rotateImage(
            bitmap: Bitmap,
            byteArray: ByteArray?
        ): Bitmap {
            var rotate = 0
            val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(ByteArrayInputStream(byteArray))
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            val matrix = Matrix()
            matrix.postRotate(rotate.toFloat())
            //            matrix.postScale(100, 100);
            return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true
            )
        }
    }

    fun loadSvg(url: String?, context: Context?, imageView: ImageView) {
        GlideToVectorYou
            .init()
            .with(context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), imageView)
    }

    @Throws(IOException::class)
    fun createBitmap(getPath: String?): Bitmap? {
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val url = URL(getPath)
        val bitmapimg =
            BitmapFactory.decodeStream(url.content as InputStream)
        val stream = ByteArrayOutputStream()
        bitmapimg.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        //            bitmap.recycle();
        val output = Bitmap.createBitmap(
            bitmapimg.width,
            bitmapimg.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(
            0, 0, bitmapimg.width,
            bitmapimg.height
        )
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(
            bitmapimg.width / 2.toFloat(),
            bitmapimg.height / 2.toFloat(), bitmapimg.width / 2.toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmapimg, rect, rect, paint)
        return rotateImage(output, byteArray)
    }

    // code used to load image from url
    fun fetchImageForMessage(imageUrl: String): Bitmap? {
        val url: URL
        url = try {
            URL(imageUrl)
        } catch (e: MalformedURLException) {
            Log.e(
                ContentValues.TAG,
                "Malformed image URL in Push Payload: " + e.localizedMessage
            )
            return null
        }
        @SuppressLint("StaticFieldLeak") val task =
            object :
                AsyncTask<URL?, Void?, Bitmap?>() {

                override fun doInBackground(vararg p0: URL?): Bitmap? {
                    return try {
                        val url = p0[0]
                        val connection =
                            url!!.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        val input = connection.inputStream
                        BitmapFactory.decodeStream(input)
                    } catch (e: IOException) {
                        Log.e(
                            ContentValues.TAG,
                            "IO Error loading Message image:" + e.localizedMessage
                        )
                        Bitmap.createBitmap(
                            1,
                            1,
                            Bitmap.Config.ALPHA_8
                        )
                    }
                }
            }.execute(url)
        return try {
            task[5, TimeUnit.SECONDS]
        } catch (e: java.lang.Exception) {
            Log.e(
                ContentValues.TAG,
                "Failed to wait for Message Image in RichPushNotificationExtender: " + e.message
            )
            null
        }
    }


}
