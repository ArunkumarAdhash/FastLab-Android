package com.lifehopehealthapp.symptomSearch.Concerns

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.Gson
import com.lifehopehealthapp.R
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.databinding.ActivityConcernsBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.symptomSearch.DiagnosisResult.DiagnosisResultActivity
import com.lifehopehealthapp.ResponseModel.ProfileDetails
import com.lifehopehealthapp.ResponseModel.QuestionData
import com.lifehopehealthapp.ResponseModel.SymptomSearchQusAnsModel
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.Utils
import org.json.JSONException
import org.json.JSONObject

class ConcernsActivity : BaseActivity<ConcernsViewModel, ConcernsModel>() {

    private var mInputResponse: String? = ""
    private var json: String? = ""
    private var ImageData: String? = ""
    private var selectItems: ArrayList<QuestionData>? = ArrayList()

    override fun getViewModel() = ConcernsViewModel::class.java

    private lateinit var binding: ActivityConcernsBinding

    override fun getActivityRepository() = ConcernsModel(
        remoteDataSource.buildApi(APIManager::class.java),
        preferences = PreferenceHelper
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConcernsBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mInputResponse = intent.getStringExtra("json").toString()
        ImageData = intent.getStringExtra("concernsImage")

        binding.imageViewConcernsImage.loadSvg(ImageData)
        binding.buttonSkip.setOnClickListener {
            intent = Intent(this, DiagnosisResultActivity::class.java)
            intent.putExtra("json", mInputResponse)
            startActivity(intent)
            finish()
        }
        binding.buttonSubmit.setOnClickListener {
            val mConcerns = binding.editTextConcerns.text!!.toString()
            if (mConcerns.equals("")) {
                Utils.showToast(this, resources.getString(R.string.toast_enter_concerns), true)
            } else {
                getModelResponse(binding.editTextConcerns.text.toString())
                intent = Intent(this, DiagnosisResultActivity::class.java)
                intent.putExtra("json", json)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getModelResponse(concerns: String) {
        val data = SymptomSearchQusAnsModel()
        val obj = JSONObject(mInputResponse.toString())
        Log.e("obj", obj.toString())
        data.setDiagnosesId(obj.getString("diagnosesId").toInt())
        data.setDiagnosesName(obj.getString("diagnosesName"))
        data.setConcern(concerns)
        data.setLocation(obj.getString("location"))
        data.setStatus(obj.getString("state"))
        data.setTestDate(obj.getString("testDate").toInt())

        val arry = obj.getJSONArray("question")
        for (i in 0 until arry.length()) {
            val c = arry.getJSONObject(i)
            val dynamicValue = QuestionData()
            try {
                dynamicValue.question = c.getString("question")
                dynamicValue.answer = c.getString("answer")
                dynamicValue.answerChoosed = c.getString("answerChoosed")
                dynamicValue.orderBy = c.getString("orderBy").toInt()
                dynamicValue.isDefault = c.getString("isDefault").toInt()
                selectItems!!.add(dynamicValue)
            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        data.setQuestion(selectItems)

        try {
            val Profile = ProfileDetails()
            val obj1 = obj.getJSONObject("profileDetail")
            Profile.dob = obj1.getString("dob").toInt()
            Profile.firstName = obj1.getString("firstName")
            Profile.lastName = obj1.getString("lastName")
            Profile.gender = obj1.getString("gender")
            Profile.profilePicture = obj1.getString("profilePicture")
            data.setProfileDetail(Profile)
            val gson = Gson()
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())
        } catch (tx: Throwable) {
            Log.e("My App", "Could not parse malformed JSON: \"$mInputResponse\"")
        }
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    override fun onBackPressed() {

    }
}