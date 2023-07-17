package com.lifehopehealthapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PreferenceHelper {

    /* private val dataStore: DataStore<Preferences>

         init {
             dataStore = applicationContext.createDataStore(
                 name = "local_store"
             )
         }*/
    private val KEY_AUTH = "key_auth"
    private val KEY_terms = "key_terms"
    private val KEY_NOTIFICATION_COUNT = "key_notification_count"
    private val KEY_PLACEHOLDER = "key_placeholder"
    private val KEY_SECRET_W3 = "key_w3_secret"
    private val KEY_ACCESS_W3 = "key_w3_access"
    private val KEY_S3_BUCKET_NAME = "key_s3_bucketname"
    private val KEY_S3_BUCKET_OLD_IMG = "key_s3_bucket_oldimg"
    private val KEY_OLD_PROFILE_IMG = "key_profile_oldimg"
    private val KEY_S3_BUCKET_URL = "key_s3_url"
    private val KEY_S3_BUCKET_REGION = "key_s3_region"
    private val KEY_REFRESH_TOKEN = "key_refersh"
    private val KEY_TOKEN = "key_token"
    private val KEY_FCMTOKEN = "key_fcmtoken"
    private val KEY_FIRST_NAME = "key_first_name"
    private val KEY_LAST_NAME = "key_lastname"
    private val KEY_EMAIL = "key_email"
    private val KEY_PROFILE_IMAGE = "key_profileimage"
    private val KEY_PROFILE_STATUS = "key_profilestatus"
    private val KEY_MOBILE_NO = "key_mobile"
    private val KEY_T_C = "key_termsandCondition"
    private val KEY_PRIVACY = "key_privacyPolicy"
    private val KEY_USER_STATUS = "key_checkuser"
    private val KEY_VIDEO_CALL_TOKEN = "key_videocall_token"
    private val KEY_OTP_VERIFICATION = "key_otpverification"
    private val KEY_WEATHER = "key_weather"
    private val KEY_INTERNAL_OTP = "key_localotp"
    private val KEY_ADDRESS = "key_address"
    private val KEY_GENDER = "key_gender"
    private val KEY_LATLNG = "key_latlng"
    private val KEY_ADDRESS_LATLNG = "key_address_latlng"

    private val KEY_API_RESPONSE = "key_settings_api_response"
    private val KEY_FCM_TOKEN = "key_fcm"
    private val KEY_CITY_NAME = "key_cityname"
    private val KEY_STATE_NAME = "key_statename"
    private val KEY_USER_ID = "key_userid"
    private val KEY_USER_CONTACT = "key_contact_support"
    private val KEY_WEATHER_APP_ID = "key_weather_app_id"


    private val KEY_ADD_ON_LIMIIT = "key_userlimit"
    private val KEY_USER_AGE = "key_age"
    private val KEY_USER_DOB = "key_dob"

    private val KEY_PRODUCT_DELIVERY_ADDRESS = "key_product_delivery_address"


    val editMe = null
    fun defaultPreference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    inline fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair.first
        val value = pair.second
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
                it.apply()
                edit().clear()
                edit().apply()
            }
        }
    var SharedPreferences.firstname
        get() = getString(KEY_FIRST_NAME, "")
        set(value) {
            editMe {
                it.putString(KEY_FIRST_NAME, value)
            }
        }
    var SharedPreferences.fcmToken
        get() = getString(KEY_FCMTOKEN, "")
        set(value) {
            editMe {
                it.putString(KEY_FCMTOKEN, value)
            }
        }
    var SharedPreferences.lastname
        get() = getString(KEY_LAST_NAME, "")
        set(value) {
            editMe {
                it.putString(KEY_LAST_NAME, value)
            }
        }

    var SharedPreferences.authToken
        get() = getString(KEY_AUTH, "")
        set(value) {
            editMe {
                it.putString(KEY_AUTH, value)
            }
        }

    var SharedPreferences.terms
        get() = getString(KEY_terms, "")
        set(value) {
            editMe {
                it.putString(KEY_terms, value)
            }
        }

    var SharedPreferences.notificationCount
        get() = getInt(KEY_NOTIFICATION_COUNT, 0)
        set(value) {
            editMe {
                it.putInt(KEY_NOTIFICATION_COUNT, value)
            }
        }


    var SharedPreferences.placeholder
        get() = getString(KEY_PLACEHOLDER, "")
        set(value) {
            editMe {
                it.putString(KEY_PLACEHOLDER, value)
            }
        }


    var SharedPreferences.w3AccessKey
        get() = getString(KEY_ACCESS_W3, "")
        set(value) {
            editMe {
                it.putString(KEY_ACCESS_W3, value)
            }
        }

    var SharedPreferences.w3SecretKey
        get() = getString(KEY_SECRET_W3, "")
        set(value) {
            editMe {
                it.putString(KEY_SECRET_W3, value)
            }
        }

    var SharedPreferences.s3BucketName
        get() = getString(KEY_S3_BUCKET_NAME, "")
        set(value) {
            editMe {
                it.putString(KEY_S3_BUCKET_NAME, value)
            }
        }

    var SharedPreferences.s3BuckeoldprofileImg
        get() = getString(KEY_S3_BUCKET_OLD_IMG, "")
        set(value) {
            editMe {
                it.putString(KEY_S3_BUCKET_OLD_IMG, value)
            }
        }

    var SharedPreferences.profileOldImg
        get() = getString(KEY_OLD_PROFILE_IMG, "")
        set(value) {
            editMe {
                it.putString(KEY_OLD_PROFILE_IMG, value)
            }
        }


    var SharedPreferences.s3BucketRegion
        get() = getString(KEY_S3_BUCKET_REGION, "")
        set(value) {
            editMe {
                it.putString(KEY_S3_BUCKET_REGION, value)
            }
        }

    var SharedPreferences.refreshToken
        get() = getString(KEY_REFRESH_TOKEN, "")
        set(value) {
            editMe {
                it.putString(KEY_REFRESH_TOKEN, value)
            }
        }

    var SharedPreferences.accessToken
        get() = getString(KEY_TOKEN, "")
        set(value) {
            editMe {
                it.putString(KEY_TOKEN, value)
            }
        }

    var SharedPreferences.email
        get() = getString(KEY_EMAIL, "")
        set(value) {
            editMe {
                it.putString(KEY_EMAIL, value)
            }
        }

    var SharedPreferences.profile
        get() = getString(KEY_PROFILE_IMAGE, "")
        set(value) {
            editMe {
                it.putString(KEY_PROFILE_IMAGE, value)
            }
        }

    var SharedPreferences.checkProfileStatus
        get() = getString(KEY_PROFILE_STATUS, "")
        set(value) {
            editMe {
                it.putString(KEY_PROFILE_STATUS, value)
            }
        }

    var SharedPreferences.saveMobileNo
        get() = getString(KEY_MOBILE_NO, "")
        set(value) {
            editMe {
                it.putString(KEY_MOBILE_NO, value)
            }
        }

    var SharedPreferences.termsandcondition
        get() = getString(KEY_T_C, "")
        set(value) {
            editMe {
                it.putString(KEY_T_C, value)
            }
        }

    var SharedPreferences.privacypolicy
        get() = getString(KEY_PRIVACY, "")
        set(value) {
            editMe {
                it.putString(KEY_PRIVACY, value)
            }
        }

    var SharedPreferences.videoCallToken
        get() = getString(KEY_VIDEO_CALL_TOKEN, "")
        set(value) {
            editMe {
                it.putString(KEY_VIDEO_CALL_TOKEN, value)
            }
        }

    var SharedPreferences.checkuserstatus
        get() = getString(KEY_USER_STATUS, "")
        set(value) {
            editMe {
                it.putString(KEY_USER_STATUS, value)
            }
        }
    var SharedPreferences.currentweather
        get() = getString(KEY_WEATHER, "")
        set(value) {
            editMe {
                it.putString(KEY_WEATHER, value)
            }
        }

     //newly added
    var SharedPreferences.productDeliveryAddress
        get() = getString(KEY_PRODUCT_DELIVERY_ADDRESS, "")
        set(value) {
            editMe {
                it.putString(KEY_PRODUCT_DELIVERY_ADDRESS, value)
            }
        }

    var SharedPreferences.isVerify
        get() = getInt(KEY_OTP_VERIFICATION, 0)
        set(value) {
            editMe {
                //it.put(USER_PASSWORD to value)
                it.putInt(KEY_OTP_VERIFICATION, value)
            }
        }
    var SharedPreferences.dob
        get() = getInt(KEY_USER_DOB, 0)
        set(value) {
            editMe {
                //it.put(USER_PASSWORD to value)
                it.putInt(KEY_USER_DOB, value)
            }
        }

    var SharedPreferences.localOTPcheck
        get() = getString(KEY_INTERNAL_OTP, "")
        set(value) {
            editMe {
                it.putString(KEY_INTERNAL_OTP, value)
            }
        }

    var SharedPreferences.address
        get() = getString(KEY_ADDRESS, "")
        set(value) {
            editMe {
                it.putString(KEY_ADDRESS, value)
            }
        }
    var SharedPreferences.userGender
        get() = getString(KEY_GENDER, "")
        set(value) {
            editMe {
                it.putString(KEY_GENDER, value)
            }
        }

    var SharedPreferences.userAge
        get() = getInt(KEY_USER_AGE, 0)
        set(value) {
            editMe {
                //it.put(USER_PASSWORD to value)
                it.putInt(KEY_USER_AGE, value)
            }
        }

    var SharedPreferences.latlng
        get() = getString(KEY_LATLNG, "")
        set(value) {
            editMe {
                it.putString(KEY_LATLNG, value)
            }
        }

    var SharedPreferences.addresslatlng
        get() = getString(KEY_ADDRESS_LATLNG, "")
        set(value) {
            editMe {
                it.putString(KEY_ADDRESS_LATLNG, value)
            }
        }


    var SharedPreferences.userlimit
        get() = getInt(KEY_ADD_ON_LIMIIT, 0)
        set(value) {
            editMe {
                //it.put(USER_PASSWORD to value)
                it.putInt(KEY_ADD_ON_LIMIIT, value)
            }
        }

    var SharedPreferences.saveSettingsAPI
        get() = getString(KEY_API_RESPONSE, "")
        set(value) {
            editMe {
                it.putString(KEY_API_RESPONSE, value)
            }
        }

    var SharedPreferences.saveFCMToken
        get() = getString(KEY_FCM_TOKEN, "")
        set(value) {
            editMe {
                it.putString(KEY_FCM_TOKEN, value)
            }
        }

    var SharedPreferences.cityName
        get() = getString(KEY_CITY_NAME, "")
        set(value) {
            editMe {
                it.putString(KEY_CITY_NAME, value)
            }
        }

    var SharedPreferences.stateName
        get() = getString(KEY_STATE_NAME, "")
        set(value) {
            editMe {
                it.putString(KEY_STATE_NAME, value)
            }
        }


    var SharedPreferences.userID
        get() = getString(KEY_USER_ID, "")
        set(value) {
            editMe {
                it.putString(KEY_USER_ID, value)
            }
        }

    var SharedPreferences.contactSupport
        get() = getString(KEY_USER_CONTACT, "")
        set(value) {
            editMe {
                it.putString(KEY_USER_CONTACT, value)
            }
        }

    var SharedPreferences.weatherAPPID
        get() = getString(KEY_WEATHER_APP_ID, "")
        set(value) {
            editMe {
                it.putString(KEY_WEATHER_APP_ID, value)
            }
        }

}