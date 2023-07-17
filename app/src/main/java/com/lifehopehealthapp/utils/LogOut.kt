package com.lifehopehealthapp.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lifehopehealthapp.R
import com.lifehopehealthapp.dashboard.LogOutResponse
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.retrofitService.RemoteDataSource
import com.lifehopehealthapp.splash.SplashActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LogOut(context: Context) {
    lateinit var retroService: APIManager
    protected val remoteDataSource = RemoteDataSource()
    private var progressDoalog: Dialog? = null
    var context: Context? = context
    var intent: Intent? = null
    private var mGmailSign: GoogleSignInClient? = null

    init {
        retroService = remoteDataSource.buildApi(APIManager::class.java)
        val aGso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getResources().getString(R.string.web_client_id))
            .requestEmail()
            .build()

        mGmailSign = GoogleSignIn.getClient(context, aGso)

    }

    fun calLogOut(token: String?) {
        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(context)
        }
        progressDoalog!!.show()

        FacebookSdk.sdkInitialize(context)
        //FB Logout
        LoginManager.getInstance().logOut()
        //Gmail Logout
        if (mGmailSign != null) {
            mGmailSign!!.signOut()
        }

        Utils.clearSharedPreferences(context!!)
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val extras = Bundle()
        extras.putString("LogOut", "LogOut")
        intent.putExtras(extras)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context!!.startActivity(intent)

        if (progressDoalog != null && progressDoalog!!.isShowing) {
            progressDoalog!!.dismiss()
        }


        val data: String = token.toString()
        retroService.logout(data)
        val call = retroService.logout(data)


        call!!.enqueue(object : Callback<LogOutResponse?> {
            override fun onResponse(
                @NonNull call: Call<LogOutResponse?>,
                @NonNull response: Response<LogOutResponse?>
            ) {
                if (progressDoalog != null && progressDoalog!!.isShowing) {
                    progressDoalog!!.dismiss()
                }

                val aResp: LogOutResponse? = response.body()
                if (aResp != null) {
                    if (aResp.getStatusCode() == 200) {
                        /*FacebookSdk.sdkInitialize(context)
                        //FB Logout
                        LoginManager.getInstance().logOut()
                        //Gmail Logout
                        if (mGmailSign != null) {
                            mGmailSign!!.signOut()
                        }

                        Utils.clearSharedPreferences(context!!)
                        val intent = Intent(context, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val extras = Bundle()
                        extras.putString("LogOut", "LogOut")
                        intent.putExtras(extras)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context!!.startActivity(intent)*/
                    }

                }
            }

            override fun onFailure(
                @NonNull call: Call<LogOutResponse?>,
                @NonNull t: Throwable
            ) {
                if (progressDoalog != null && progressDoalog!!.isShowing) {
                    progressDoalog!!.dismiss()
                }
            }
        })
    }

    /* fun clearSharedPreferences(ctx: Context) {
         val dir = File(ctx.filesDir.parent + "/local_store/")
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
     }*/
}