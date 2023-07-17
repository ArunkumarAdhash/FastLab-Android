package com.lifehopehealthapp.utils

import com.lifehopehealthapp.BuildConfig


object Constants {

    //Google Signin

    val TIMESTAMP_MONTH: String? = "MMM"
    var CAMERA_IMAGE_REQ_CODE = 103
    val PAGE_TITLE: String? = "page_title"
    val PAGE_URL: String? = "page_url"
    val ORDER_ID: String? = "order_id"
    val VITALS_ID: String? = "ID"
    val VITALS_NAME: String? = "NAME"
    val VITALS_LAST_DATA: String? = "LAST_DATA"
    val VITALS_DATE: String? = "LAST_DATE"
    val VITALS_IMAGE: String? = "IMAGE"
    val VITALS_GLUCOSE_LIST: String? = "GLUCOSE_LIST"
    const val DOCTOR_ID: String = "DOCTOR_ID"


    val REFRESH_TOKEN: String? = "refreshToken"
    val NEW_TOKEN: String? = "token"


    //AWS
    val Image_Bucket_Name: String = "ProfilePic/"
    val Doctor_Image_Bucket_Name: String = "doctorProfilePic/"
    val PDF_Bucket_Name = "report/"

    //newly added
    val Product_Bucket_Name: String = "Product/"
    const val ImageURL: String = "imageURL"

    const val GMAIL_LOGIN = 101
    const val APP_UPDATE_ALERT = 1

    //twitter signin
    var CONSUMER_KEY = "nLjqWjTobdlg9uqNpDWxSwVhF"
    var CONSUMER_SECRET = "C2rQvSCk5JuewBIlDJkS667DN6vdUdT6PNxiyGL9YlGt9WzngL"
    var CALLBACK_URL = "twitterkit-nLjqWjTobdlg9uqNpDWxSwVhF://"

    val TIME_STAMPTO_DATE_ONE: String? = "dd MMMM 'at' h:mm a"
    val TIMESTAMPTODATE_TWO: String? = "MM/dd/yyyy ',' hh:mm aa"
    val TIMESTAMPTODATE_THREE: String? = "MM/dd/yy ',' hh:mm aa"
    val TIMESTAMP_YEAR: String? = "yyyy"
    val TIMESTAMP_DOB: String? = "MM/dd/yyyy"
    val TIMESTAMP_DATE: String? = "dd"
    val TIMESTAMP_VITALS_DETAILS: String? = "MMM d, yyyy"
    val TIMESTAMP_VITAL_TIME: String? = "hh:mm a"


    // values have to be globally unique
    const val INTENT_ACTION_DISCONNECT =
        BuildConfig.APPLICATION_ID + ".Disconnect"
    const val NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel"
    const val INTENT_CLASS_MAIN_ACTIVITY =
        BuildConfig.APPLICATION_ID + ".MainActivity"

    // values have to be unique within each app
    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001

}