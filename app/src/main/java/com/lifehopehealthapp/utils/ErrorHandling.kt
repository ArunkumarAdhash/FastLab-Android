package com.lifehopehealthapp.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.NonNull
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.RefreshTokenModel
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.retrofitService.RemoteDataSource
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ErrorHandling(context: Context) {
    lateinit var retroService: APIManager
    protected val remoteDataSource = RemoteDataSource()
    private var progressDoalog: Dialog? = null
    var context: Context? = context
    var intent: Intent? = null
    private var mPrefs: SharedPreferences? = null

    init {
        retroService = remoteDataSource.buildApi(APIManager::class.java)
        mPrefs = PreferenceHelper.defaultPreference(context)
    }

    fun onErrorHandling(token: String, jsonData: JsonObject) {
        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(context)
        }
        progressDoalog!!.show()

        retroService.refreshToken(token, jsonData)

        val call = retroService.refreshToken(token, jsonData)

        call.enqueue(object : Callback<RefreshTokenModel?> {
            override fun onResponse(
                @NonNull call: Call<RefreshTokenModel?>,
                @NonNull response: Response<RefreshTokenModel?>
            ) {
                if (progressDoalog != null && progressDoalog!!.isShowing) {
                    progressDoalog!!.dismiss()
                }
                val aResp: RefreshTokenModel? = response.body()
                if (aResp != null) {
                    if (aResp.getStatusCode() == 200) {
                        val intent = Intent(context, DashBoardActivity::class.java)
                        val extra = Bundle()
                        extra.putString(
                            Constants.REFRESH_TOKEN,
                            response.body()!!.getValue()!!.data!!.refreshToken!!
                        )
                        extra.putString(
                            Constants.NEW_TOKEN,
                            response.body()!!.getValue()!!.data!!.token!!
                        )
                        mPrefs!!.authToken =
                            "Bearer " + response.body()!!.getValue()!!.data!!.token!!
                        mPrefs!!.refreshToken = response.body()!!.getValue()!!.data!!.refreshToken!!
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putExtras(extra)
                        context!!.startActivity(intent)
                    } else {

                    }
                }
            }

            override fun onFailure(
                @NonNull call: Call<RefreshTokenModel?>,
                @NonNull t: Throwable
            ) {
                if (progressDoalog != null && progressDoalog!!.isShowing) {
                    progressDoalog!!.dismiss()
                }
            }
        })
    }
}