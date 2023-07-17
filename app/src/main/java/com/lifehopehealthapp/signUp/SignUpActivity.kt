package com.lifehopehealthapp.signUp


import `in`.androidhunt.otp.AutoDetectOTP
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.*
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.view.ActionMode

import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.facebook.*
import com.facebook.FacebookSdk.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.SettingsRequest
import com.lifehopehealthapp.WebViewActivity
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivitySignUpBinding
import com.lifehopehealthapp.login.LogInActivity
import com.lifehopehealthapp.mobileOtpVerify.MobileOTPVerifyActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.contactSupport
import com.lifehopehealthapp.utils.PreferenceHelper.email
import com.lifehopehealthapp.utils.PreferenceHelper.fcmToken
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.privacypolicy
import com.lifehopehealthapp.utils.PreferenceHelper.profile
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.termsandcondition
import com.lifehopehealthapp.utils.PreferenceHelper.userlimit
import com.lifehopehealthapp.utils.PreferenceHelper.videoCallToken
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.PreferenceHelper.weatherAPPID
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.LifeHopeButton
import com.lifehopehealthapp.widgets.LifeHopenTextView
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : BaseActivity<SignUpViewModel, SignupModel>(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding

    private var autoDetectOTP: AutoDetectOTP? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private var mProfileTracker: ProfileTracker? = null
    private var Access_token: String? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var accToken: AccessToken? = null
    private lateinit var twitterDialog: Dialog
    private var appUpdateDialog: Dialog? = null
    private var mAlertDialog: Dialog? = null

    //twitter
    private var id = ""
    private var handle = ""
    private var name = ""
    private var email = ""
    private var profilePicURL = ""
    private var accessToken = ""

    private var firstName = ""
    private var lastName = ""
    private var profileImage = ""
    private var countryCode: String? = ""
    private var privacyUrl = ""
    private var terms = ""
    private var apiResponse: String? = ""
    private var forceUpdate: Boolean? = false
    private var updateTitle: String? = ""
    private var updateUrl: String? = ""
    private var updateMsg: String? = ""
    var mPrefs: SharedPreferences? = null

    private fun getStringResourceByName(context: Context, resourceName: String): String? {
        try {
            val packageName = packageName
            val resId = context.resources.getIdentifier(resourceName, "string", packageName)
            Log.i("Resource", "Resource Id: $resId")
            return getString(resId)
        }catch (ex : Exception){
            Log.e("Error", ex.message!!)
        }
        return null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        FacebookSdk.sdkInitialize(getApplicationContext())
        setAutoLogAppEventsEnabled(true)
        setAdvertiserIDCollectionEnabled(true)
        //setAutoInitEnabled(true)
        fullyInitialize()
        mPrefs = PreferenceHelper.defaultPreference(this)
        binding.imageGoogle.setOnClickListener(this)
        binding.imageFacebook.setOnClickListener(this)
        binding.imageTwitter.setOnClickListener(this)
        //functianality for gmail login
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContentView(binding.root)
        test()

        binding.buttonSignUp.text = getStringResourceByName(this,"button_sign_up")
       /* binding.edittextMobileNumber.customSelectionActionModeCallback = object : Callback{


            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(
                mode: android.view.ActionMode?,
                item: MenuItem?
            ): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {
            }


        }*/


        binding.edittextMobileNumber.requestFocus()


        binding.edittextMobileNumber.setOnLongClickListener {
            true
        }

        binding.edittextMobileNumber.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            private var backspacingFlag = false

            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private var editedFlag = false

            //we need to mark the cursor position and restore it after the edition
            private var cursorComplement = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length - binding.edittextMobileNumber.getSelectionStart()
                //we check if the user ir inputing or erasing a character
                backspacingFlag = if (count > after) {
                    true
                } else {
                    false
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // nothing to do here =D
            }

            override fun afterTextChanged(s: Editable) {
                val string = s.toString()
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                val phone = string.replace("[^\\d]".toRegex(), "")

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if (!editedFlag) {

                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: (999) 999-999
                    if (phone.length >= 6 && !backspacingFlag) {
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true
                        //here is the core. we substring the raw digits and add the mask as convenient
                        val ans = "(" + phone.substring(0, 3) + ") " + phone.substring(
                            3,
                            6
                        ) + "-" + phone.substring(6)
                        binding.edittextMobileNumber.setText(ans)
                        //we deliver the cursor to its original position relative to the end of the string
                        binding.edittextMobileNumber.setSelection(binding.edittextMobileNumber.getText()!!.length - cursorComplement)

                        //we end at the most simple case, when just one character mask is needed
                        //example: 99999 <- 3+ digits already typed
                        // masked: (999) 99
                    } else if (phone.length >= 3 && !backspacingFlag) {
                        editedFlag = true
                        val ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3)
                        binding.edittextMobileNumber.setText(ans)
                        binding.edittextMobileNumber.setSelection(binding.edittextMobileNumber.getText()!!.length - cursorComplement)
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false
                }
            }
        })




        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {

        } else {

        }
        getKeyHash()
        getFCMToken()
        // Initialize Facebook Login button
        binding.edittextMobileNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        callbackManager = CallbackManager.Factory.create()
        //binding.buttonFacebookLogin.setReadPermissions("email", "public_profile")
        binding.buttonFacebookLogin.setPermissions(Arrays.asList("email", "public_profile"))

        binding.buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.e("FBLOGIN", "facebook:onSuccess:$loginResult")

                try {
                    if (Profile.getCurrentProfile() == null) {
                        /* mProfileTracker = object : ProfileTracker() {
                             override fun onCurrentProfileChanged(
                                 oldProfile: Profile,
                                 currentProfile: Profile
                             ) {
                                 this.stopTracking();
                                 Profile.setCurrentProfile(currentProfile);
                                 mProfileTracker!!.stopTracking()
                                 Profile.setCurrentProfile(currentProfile)
                                 Log.e("FBLOGINprofile", currentProfile.firstName)
                                 Access_token = loginResult.accessToken.token

                                 Handler().postDelayed({


                                 }, 500)
                             }
                         }*/
                    } else {
                        /*val profile = Profile.getCurrentProfile()
                        Log.e("FBLOGINprofile1", profile.firstName + " " + profile.lastName)
                        firstName = profile.firstName
                        lastName = profile.lastName
                        profileImage = profile.getProfilePictureUri(500, 500).toString()
                        lifecycleScope.launch {
                            viewModel.userFirstName(token = firstName)
                        }
                        lifecycleScope.launch {
                            viewModel.userLastName(token = lastName)
                        }
                        lifecycleScope.launch {
                            viewModel.profileImage(token = profileImage)
                        }
                        intent = Intent(this@SignUpActivity, LogInActivity::class.java)
                        startActivity(intent)

                        Log.e("FBLOGINproPic", profile.getProfilePictureUri(500, 500).toString())
                        Access_token = loginResult.accessToken.token
                        Handler().postDelayed({

                        }, 500)*/
                    }
                } catch (e: Exception) {

                }
                handleFacebookAccessToken(loginResult.accessToken)
            }


            override fun onCancel() {
                Log.d("TAG", "facebook:onCancel")
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }

            override fun onError(error: FacebookException) {
                Log.d("TAG", "facebook:onError", error)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        })

        val wordtoSpan: Spannable =
            SpannableString("By proceeding you agree to our T&C and Privacy Policy")
        privacyUrl = mPrefs!!.privacypolicy.toString()
        terms = mPrefs!!.termsandcondition.toString()
        apiResponse = mPrefs!!.saveSettingsAPI
        val teremsAndCondition: ClickableSpan =
            object : ClickableSpan() {
                override fun onClick(textView: View) {
                    if (mPrefs!!.termsandcondition.equals("")) {
                        Utils.showToast(this@SignUpActivity, "", true)
                    } else {
                        intent = Intent(this@SignUpActivity, WebViewActivity::class.java)
                        val aBundle = Bundle()
                        aBundle.putString(
                            Constants.PAGE_TITLE,
                            resources.getString(R.string.terms_conditions)
                        )
                        aBundle.putString(Constants.PAGE_URL, mPrefs!!.termsandcondition)
                        intent.putExtras(aBundle)
                        startActivity(intent)
                        //finish()
                    }

                }
            }

        val privacy: ClickableSpan =
            object : ClickableSpan() {
                override fun onClick(textView: View) {
                    if (mPrefs!!.privacypolicy.equals("")) {
                        Utils.showToast(this@SignUpActivity, "", true)
                    } else {
                        intent = Intent(this@SignUpActivity, WebViewActivity::class.java)
                        val aBundle = Bundle()
                        aBundle.putString(
                            Constants.PAGE_TITLE,
                            resources.getString(R.string.privacy_policy)
                        )
                        aBundle.putString(Constants.PAGE_URL, mPrefs!!.privacypolicy)
                        intent.putExtras(aBundle)
                        startActivity(intent)
                        //finish()
                    }

                }
            }

        wordtoSpan.setSpan(teremsAndCondition, 31, 34, 0)
        wordtoSpan.setSpan(privacy, 39, 53, 0)
        wordtoSpan.setSpan(
            ForegroundColorSpan(
                resources
                    .getColor(R.color.link_color)
            ), 31, 34, 0
        )
        wordtoSpan.setSpan(
            ForegroundColorSpan(
                resources
                    .getColor(R.color.link_color)
            ), 39, 53, 0
        )

        binding.textPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance())
        binding.textPrivacyPolicy.setText(wordtoSpan, TextView.BufferType.SPANNABLE)


        /*binding.ccp.registerPhoneNumberTextView(binding.edittextMobileNumber)
        autoDetectOTP = AutoDetectOTP(this)
        autoDetectOTP!!.requestPhoneNoHint()
*/
        binding.buttonSignUp.setOnClickListener {
            if (apiResponse!!.contentEquals("")) {
                Utils.showToast(this, resources.getString(R.string.toast_tech_issue), true)
                finish()
                startActivity(intent)
            } else {
                var mobilenumber = binding.edittextMobileNumber.text.toString().trim()
//                var str = mobilenumber
//                str = str.replace("[^a-zA-Z0-9]".toRegex(), "")
//                Log.e("str", str)
                if (Utils.isValidPhoneNumber( mobilenumber.replace("[^a-zA-Z0-9]".toRegex(), "")) ) {

                    if (Utils.isNetworkAvailable(this@SignUpActivity)) {
                        alertPopup()
                    }
                    else {
                        noInternetAlert(resources.getString(R.string.no_internet_msg), this@SignUpActivity)
                    }

                } else {
                    if(mobilenumber.replace("[^a-zA-Z0-9]".toRegex(), "").length==0)
                    {
                        binding.edittextMobileNumber.setError("Enter the mobile number")
                    }
                    else
                    {
                        binding.edittextMobileNumber.setError("Enter Valid Mobile Number")
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        callSettingsAPI()
    }

    private fun setEditTextMaxLength(et: EditText, maxLength: Int) {
        et.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength)))
    }

    private fun callSettingsAPI() {
        if (Utils.isNetworkAvailable(this)) {
            val request = SettingsRequest(type = "android")
            viewModel.getDetails(request)

            viewModel.dataResponse.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (it.value.getStatusCode() == 200) {
                            val gson = Gson()
                            apiResponse = gson.toJson(it.value.getValue()!!.data)
                            Log.e("apiResponse", apiResponse.toString())
                            countryCode = it.value.getValue()!!.data!!.countryCode

                            mPrefs!!.saveSettingsAPI = apiResponse.toString()
                            mPrefs!!.w3AccessKey = it.value.getValue()!!.data!!.getAccessKey()
                            mPrefs!!.w3SecretKey = it.value.getValue()!!.data!!.getSecretKey()
                            mPrefs!!.s3BucketName = it.value.getValue()!!.data!!.getBucketName()
                            Log.e("mPrefs", mPrefs!!.saveSettingsAPI.toString())
                            mPrefs!!.videoCallToken = it.value.getValue()!!.data!!.getCallToken()
                            mPrefs!!.termsandcondition =
                                it.value.getValue()!!.data!!.termsandCondition.toString()
                            mPrefs!!.privacypolicy =
                                it.value.getValue()!!.data!!.privacyPolicy.toString()
                            mPrefs!!.userlimit = it.value.getValue()!!.data!!.addOnLimit!!
                            mPrefs!!.weatherAPPID = it.value.getValue()!!.data!!.weatherApiKey!!
                            mPrefs!!.contactSupport = it.value.getValue()!!.data!!.contactSupport!!

                            forceUpdate = it.value.getValue()!!.data!!.versionInfo!!.forceUpdate
                            updateTitle = it.value.getValue()!!.data!!.versionInfo!!.updateMsg
                            updateMsg = it.value.getValue()!!.data!!.versionInfo!!.updateTitle
                            updateUrl = it.value.getValue()!!.data!!.versionInfo!!.playStoreUrl
                            if (it.value.getValue()!!.data!!.versionInfo!!.latestVersion!! > Utils.getAppVersionCode()) {
                                notifyAlert(Constants.APP_UPDATE_ALERT)
                            }
                        } else if (it.value.getStatusCode() == 401) {
                            var data = RefreshAPIRequest()
                            data.token = mPrefs!!.authToken
                            data.refreshToken = mPrefs!!.refreshToken

                            val gson = Gson()
                            var json: String? = ""
                            json = gson.toJson(data)
                            Log.e("Model---->", json.toString())

                            val aJsonParser = JsonParser()
                            val aJsonObject = aJsonParser.parse(json) as JsonObject

                            val refresh = ErrorHandling(this)
                            refresh.onErrorHandling(mPrefs!!.authToken!!, aJsonObject)
                        } else {
                            Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
                        }
                    }

                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(), true)

                        if (it.code == 401) {
                            val data = RefreshAPIRequest()
                            val removeChar = mPrefs!!.authToken!!.replace("Bearer ", "")
                            data.token = removeChar
                            data.refreshToken = mPrefs!!.refreshToken

                            val gson = Gson()
                            var json: String? = ""
                            json = gson.toJson(data)
                            Log.e("Model---->", json.toString())

                            val aJsonParser = JsonParser()
                            val aJsonObject = aJsonParser.parse(json) as JsonObject

                            val refresh = ErrorHandling(this)
                            refresh.onErrorHandling(mPrefs!!.authToken!!, aJsonObject)
                        } else {

                        }
                    }
                }
            })
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }


    }

    private fun getFCMToken() {

        val addOnCompleteListener = FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        return
                    }

                    // Get new FCM registration token
                    val token: String = task.result!!
                    mPrefs!!.fcmToken = token
                    Log.e("newToken", token)
                }
            })

    }

    private fun alertPopup() {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_view)
        val layout: TextView = alertDialog.findViewById(R.id.textview_header)
        layout.isVisible = true
        var mobilenumber = binding.edittextMobileNumber.text.toString().trim()
        layout.setText(resources.getString(R.string.text_heading) + countryCode + " " + mobilenumber)

        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        posTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            var str = mobilenumber
            str = str.replace("[^a-zA-Z0-9]".toRegex(), "")
            Log.e("str1", str)
            mobilenumber = binding.edittextMobileNumber.text.toString().trim()
            intent = Intent(this, MobileOTPVerifyActivity::class.java)
            intent.putExtra("mobilenumber", str)
            startActivity(intent)
           // finish()
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    /*suspend fun isLoggedIn(): Boolean {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val accessToken = sharedPref.getString("oauth_token", "")
        val accessTokenSecret = sharedPref.getString("oauth_token_secret", "")

        val builder = ConfigurationBuilder()
        builder.setOAuthConsumerKey(Constants.CONSUMER_KEY)
            .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
            .setOAuthAccessToken(accessToken)
            .setOAuthAccessTokenSecret(accessTokenSecret)
        val config = builder.build()
        val factory = TwitterFactory(config)
        val twitter = factory.instance
        try {
            withContext(Dispatchers.IO) { twitter.verifyCredentials() }
            return true
        } catch (e: Exception) {
            return false
        }
    }*/
    fun test() {
        /* val datetime = Calendar.getInstance()
         datetime.setTimeZone(TimeZone.getTimeZone("GMT-6"))
         datetime.setTimeInMillis(())*/
//calendar.setTime(<--date object of gmt date-->);
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = 1639483200 * 1000L
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

//Here you say to java the initial timezone. This is the secret

//Here you say to java the initial timezone. This is the secret
        sdf.timeZone = TimeZone.getTimeZone("GMT-5")
//Will print in UTC
//Will print in UTC
        println("TestTime--> GMT" + sdf.format(calendar.time))

//Here you set to your timezone

//Here you set to your timezone
//        sdf.timeZone = TimeZone.getDefault()
        sdf.timeZone =   TimeZone.getTimeZone("GMT-6")
//Will print on your default Timezone
//Will print on your default Timezone
//        println(sdf.format(calendar.time))
        println("TestTime-->" + sdf.format(calendar.time))
    }

    private fun getKeyHash() {

        try {
            val info: PackageInfo = packageManager.getPackageInfo(
                "com.lifehopehealthapp",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                System.out.println(
                    "MY_KEY_HASH--> " + Base64.encodeToString(
                        md.digest(),
                        Base64.DEFAULT
                    )
                )
                //Log.e("MY_KEY_HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

    }

    private fun handleFacebookAccessToken(token: com.facebook.AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }

        val request =
            GraphRequest.newMeRequest(token) { fbJSONObject: JSONObject?, response: GraphResponse? ->
                try {
                    Log.e("JSON", response.toString())
                    val data: JSONObject = response!!.getJSONObject()
                    Log.e("email", data.has("email").toString())
                    Log.e("first_name", data.getString("first_name"))
                    Log.e("last_name", data.getString("last_name"))
//                    Log.e("email", data.getString("email"))
                    firstName = data.getString("first_name")
                    lastName = data.getString("last_name")
                    if (data.has("email")) {
                        email = data.getString("email")
                    }

                    val userID = data.get("id")
                    //profileImage = profile.getProfilePictureUri(500, 500).toString()
                    mPrefs!!.firstname = firstName
                    mPrefs!!.lastname = lastName
                    mPrefs!!.profile =
                        "https://graph.facebook.com/" + userID + "/picture?type=large"
                    mPrefs!!.email = email

                    intent = Intent(this@SignUpActivity, LogInActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email")
        request.parameters = parameters
        request.executeAsync()
    }


    override fun getViewModel() = SignUpViewModel::class.java

    override fun getActivityRepository() =
        SignupModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GMAIL_LOGIN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        if (resultCode == 1001 || resultCode == 0) {
            Log.e("error", "error")
        } else if ((requestCode == 1000) && (resultCode == -1)) {
            val mPhoneNo = autoDetectOTP!!.getPhoneNo(data).toString()
            binding.ccp.setFullNumber(
                Utils.USFormatMobile(
                    mPhoneNo.toLong()
                )
            )
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task!!.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID", googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)
            firstName = googleFirstName

            mPrefs!!.firstname = googleFirstName
            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)
            lastName = googleLastName

            mPrefs!!.lastname = googleLastName


            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)
            email = googleEmail
            mPrefs!!.email = email

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)



            intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.image_facebook -> {
                binding.buttonFacebookLogin.performClick()
            }
            R.id.image_google -> {
                val signInIntent: Intent = googleSignInClient!!.getSignInIntent()
                startActivityForResult(signInIntent, Constants.GMAIL_LOGIN)
            }

            R.id.image_twitter -> {
                //getRequestToken()
                /*client.cancelAuthorize()
                twitterLoginButton.performClick()*/
            }
        }
    }

    /*private fun getRequestToken() {

        GlobalScope.launch(Dispatchers.Default) {
            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(Constants.CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
                .setIncludeEmailEnabled(true)
            val config = builder.build()
            val factory = TwitterFactory(config)
            twitter = factory.instance
            try {
                val requestToken = twitter.oAuthRequestToken
                withContext(Dispatchers.Main) {
                    setupTwitterWebviewDialog(requestToken.authorizationURL)
                }
            } catch (e: IllegalStateException) {
                Log.e("ERROR: ", e.toString())
            }
        }
    }*/

    @SuppressLint("SetJavaScriptEnabled")
    fun setupTwitterWebviewDialog(url: String) {
        twitterDialog = Dialog(this)
        val webView = WebView(this)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = TwitterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        twitterDialog.setContentView(webView)
        twitterDialog.show()
    }

    // A client to know about WebView navigations
    // For API 21 and above
    @Suppress("OverridingDeprecatedMember")
    inner class TwitterWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(Constants.CALLBACK_URL)) {
                Log.d("Authorization URL: ", request?.url.toString())
                //handleUrl(request?.url.toString())

                // Close the dialog after getting the oauth_verifier
                if (request?.url.toString().contains(Constants.CALLBACK_URL)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }
    }

    // Get the oauth_verifier
    /*private fun handleUrl(url: String) {
        val uri = Uri.parse(url)
        val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""

        GlobalScope.launch(Dispatchers.Main) {
            accToken = withContext(Dispatchers.IO) {
                twitter.getOAuthAccessToken(oauthVerifier)
            }
            getUserProfile()
        }
    }

    suspend fun getUserProfile() {
        val usr = withContext(Dispatchers.IO) { twitter.verifyCredentials() }

        //Twitter Id
        val twitterId = usr.id.toString()
        Log.d("Twitter Id: ", twitterId)
        id = twitterId

        //Twitter Handle
        val twitterHandle = usr.screenName
        Log.d("Twitter Handle: ", twitterHandle)
        handle = twitterHandle

        //Twitter Name
        val twitterName = usr.name
        Log.d("Twitter Name: ", twitterName)
        name = twitterName

        //Twitter Email
        val twitterEmail = usr.email
        Log.d(
            "Twitter Email: ",
            twitterEmail
                ?: "'Request email address from users' on the Twitter dashboard is disabled"
        )
        email = twitterEmail
            ?: "'Request email address from users' on the Twitter dashboard is disabled"

        // Twitter Profile Pic URL
        val twitterProfilePic = usr.profileImageURLHttps.replace("_normal", "")
        Log.d("Twitter Profile URL: ", twitterProfilePic)
        profilePicURL = twitterProfilePic


        // Twitter Access Token
        Log.d("Twitter Access Token", accToken?.token ?: "")
        accessToken = accToken?.token ?: ""


    }
*/
    override fun onBackPressed() {
        super.onBackPressed()
        val intent =
            Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    /**
     * shown notify Alert popup
     *
     * @param alertType based on type view are updated
     */
    fun notifyAlert(alertType: Int) {
        val view1 = View.inflate(
            this,
            R.layout.alert_update,
            null
        )
        if (appUpdateDialog == null) appUpdateDialog =
            Dialog(this, R.style.dialogwinddow)
        appUpdateDialog!!.setContentView(view1)
        appUpdateDialog!!.setCancelable(false)
        if (appUpdateDialog!!.isShowing()) {
            appUpdateDialog!!.dismiss()
            appUpdateDialog!!.show()
        } else {
            appUpdateDialog!!.show()
        }
        val title: AppCompatTextView = appUpdateDialog!!.findViewById(R.id.title)
        val desc: AppCompatTextView = appUpdateDialog!!.findViewById(R.id.description)
        val button_success: AppCompatTextView = appUpdateDialog!!.findViewById(R.id.button_success)
        val button_failure: AppCompatTextView = appUpdateDialog!!.findViewById(R.id.button_failure)
        if (alertType == Constants.APP_UPDATE_ALERT) {
            title.setText(updateMsg)
            desc.setText(updateTitle)
            desc.setVisibility(View.VISIBLE)
            button_failure.setText(this.getResources().getString(R.string.no_thanks))
            button_failure.setVisibility(if (forceUpdate!!) View.GONE else View.VISIBLE)
            button_success.setText(this.getResources().getString(R.string.update))
            button_success.setOnClickListener({ view ->
                if (Utils.isNetworkAvailable(this)) {
                    val viewIntent = Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse(updateUrl)
                    )
                    startActivity(viewIntent)
                    appUpdateDialog!!.dismiss()
                } else {
                    mAlertDialog = Utils.alert_view_dialogContxt(
                        this,
                        "" + getResources().getString(R.string.message),
                        "" + getResources().getString(R.string.dia_newtwork_des),
                        "" + getResources().getString(R.string.ok),
                        "",
                        true,
                        null,
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() },
                        ""
                    )
                }
            })
            button_failure.setOnClickListener({ v -> appUpdateDialog!!.dismiss() })
        } else {
            button_success.setOnClickListener({ view ->
                if (Utils.isNetworkAvailable(this)) {
                    appUpdateDialog!!.dismiss()
                } else {
                    mAlertDialog = Utils.alert_view_dialogContxt(
                        this,
                        "" + getResources().getString(R.string.message),
                        "" + getResources().getString(R.string.dia_newtwork_des),
                        "" + getResources().getString(R.string.ok),
                        "",
                        true,
                        null,
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() },
                        ""
                    )
                }
            })
            button_failure.setOnClickListener({ v -> appUpdateDialog!!.dismiss() })
        }
    }

    fun disableCopyPasteOperations(editText: EditText) {
        editText.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onCreateActionMode(
                actionMode: ActionMode?,
                menu: Menu?
            ): Boolean { 
                return false
            }

            override fun onPrepareActionMode(
                actionMode: ActionMode?,
                menu: Menu?
            ): Boolean {
                return false
            }

            override fun onActionItemClicked(
                actionMode: ActionMode?,
                item: MenuItem?
            ): Boolean {
                return false
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        })
        editText.isLongClickable = false
        editText.setTextIsSelectable(false)
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
            binding.buttonSignUp.performClick()
        })
        button_failure.setOnClickListener({ view ->
            internetDialog.dismiss()
        })
    }
}

private fun EditText.setCustomSelectionActionModeCallback(callback: ActionMode.Callback) {

}

