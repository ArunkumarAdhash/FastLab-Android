package com.lifehopehealthapp.symptomSearch.Questions

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivitySymptomSearchBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.symptomSearch.Concerns.ConcernsActivity
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.cityName
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.stateName
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class SymptomSearchActivity : BaseActivity<SymptomSearchViewModel, SymptomSearchModel>() {

    private lateinit var binding: ActivitySymptomSearchBinding

    private var progressDoalog: Dialog? = null
    private var token: String? = ""
    private var selectedAnswer: String? = ""
    private var SymptomID: String? = ""
    private var SymptomName: String? = ""
    private var selectedData: ArrayList<String>? = ArrayList<String>()
    private var selectItems: ArrayList<QuestionData>? = ArrayList()
    private var img: String? = ""
    //private var userDetails: ArrayList<UserData>? = ArrayList()

    private var mData: ArrayList<Questionses>? = ArrayList<Questionses>()
    private var mQuestions: ArrayList<Datum>? = ArrayList<Datum>()
    private var mAnswers: ArrayList<Option>? = ArrayList<Option>()
    private var selectedAnswers: ArrayList<Option>? = ArrayList<Option>()
    private var adapter: SymptomSearchQuestionAdapter? = null
    private var mAdapter: SymptomSearchAnsAdapter? = null
    private var row_index: Int = 1
    private var row_index1: Int = 0
    private var json: String? = ""
    private var ProfileData: String? = ""
    private var ProfileModelData: String? = ""
    private var mProfile: ProfileDetail? = null
    private var mPrefs: SharedPreferences? = null

    override fun getViewModel() = SymptomSearchViewModel::class.java

    override fun getActivityRepository() =
        SymptomSearchModel(
            remoteDataSource.buildApi(
                APIManager::class.java
            ), PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySymptomSearchBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        SymptomID = intent.getStringExtra("symptomID").toString()
        SymptomName = intent.getStringExtra("symptomName").toString()
        ProfileData = intent.getStringExtra("json")
        val Profile = intent.getStringExtra("Profile")
        mProfile = Gson().fromJson(Profile, ProfileDetail::class.java)
        Log.e("SymptomID", SymptomID.toString())
        binding.textviewTitle.text = SymptomName + " " + resources.getString(R.string.text_question)
        getAPICall(SymptomID!!.toInt())
        /*binding.recyclerviewAnsList.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )*/

        binding.recyclerviewAnsList.layoutManager = GridLayoutManager(this, 2)

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
        loadanim()

    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context: Context = recyclerView.context
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        TransitionHelper.removeActivityFromTransitionManager(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: Int) {

//        val text: String = java.lang.String.format(mailCount)

    }

    @SuppressLint("StringFormatMatches")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        selectedAnswer = event
        adapter!!.loadAnswer(selectedAnswer!!)

        if (row_index != mData!!.size) {
            loadNextAns(row_index++)
        }
        val sizedata: Int? = mData!!.size - 1
        val totalCount = mData!!.size
        if (sizedata == row_index1) {
            val quesCount: String = row_index1.toString()
            binding.txtQuestionTotal.setText(resources.getString(R.string.corona_ques_count) + totalCount.toString() + " of " + totalCount.toString())
            binding.questionsProgressBar.progress = totalCount.toFloat()
            getModelData(row_index1++, selectedAnswer!!, true)
            binding.recyclerviewAnsList.isVisible = false
            intent = Intent(this, ConcernsActivity::class.java)
            intent.putExtra("json", json)
            intent.putExtra("concernsImage", img)
            startActivity(intent)
            finish()
        } else {
            getModelData(row_index1++, selectedAnswer!!, false)
            Log.e("questionCount", row_index1.toString())
            val quesCount: String = (row_index1 + 1).toString()
            binding.txtQuestionTotal.setText(resources.getString(R.string.corona_ques_count) + " " + quesCount + " of " + totalCount.toString())

            binding.questionsProgressBar.progress = ((row_index1 + 1) * 10).toFloat()

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: SelectQuesAnsModel) {
        if (event.mSelectData.equals("QuestionLoader") && event.mSelectItem == 2) {
            binding.recyclerviewAnsList.visibility = View.VISIBLE
        } else if (event.mSelectData.equals("AnswerLoader") && event.mSelectItem == 1) {
            binding.recyclerviewAnsList.visibility = View.GONE
        }
    }

    private fun getModelData(position: Int, selectAns: String, profile: Boolean) {
        val currentTimestamp = System.currentTimeMillis() / 1000
        var data = SymptomSearchQusAnsModel()
        data.setDiagnosesId(SymptomID!!.toInt())
        data.setDiagnosesName(SymptomName)
        data.location = mPrefs!!.cityName
        data.state = mPrefs!!.stateName
        data.testDate = currentTimestamp.toInt()
        var dynamicValue = QuestionData()
        dynamicValue.question = mData!![position].question
        dynamicValue.answer = mData!![position].answer
        dynamicValue.answerChoosed = selectAns
        dynamicValue.orderBy = mData!![position].orderBy
        dynamicValue.isDefault = mData!![position].isDefault
        selectItems!!.add(dynamicValue)
        data.setQuestion(selectItems)

        if (profile) {
            try {
                val Profile = ProfileDetails()
                val obj = JSONObject(ProfileData.toString())
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
                Log.e("My App", "Could not parse malformed JSON: \"$ProfileData\"")
            }
        }

    }

    private fun getAPICall(id: Int) {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            token = mPrefs!!.authToken
            //token = runBlocking { viewModel.getKey().toString() }

            val tsLong = System.currentTimeMillis() / 1000
            val timeStamp = tsLong.toString()
            Log.e("Timestm", timeStamp)
            val data = SymptomSearchAPIModel()
            data.diagnosesId = id

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject
            Log.e("aJsonObject", aJsonObject.toString())
            viewModel.getQuestionList(token!!, aJsonObject)

            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            img = it.value.getValue()!!.data!!.getImagePath()
                            binding.textViewLabel.text = it.value.getValue()!!.data!!.getHeader()
                            binding.textviewTestDescription.text =
                                it.value.getValue()!!.data!!.getDescription()
                            for (k in it.value.getValue()!!.data!!.question!!.indices) {
                                Log.e("date", it.value.getValue()!!.data!!.question!![k]!!.answer!!)
                                var data: Questionses = Questionses()
                                data.answer = it.value.getValue()!!.data!!.question!![k]!!.answer!!
                                data.direction = it.value.getValue()!!.data!!.question!![k]!!.direction
                                data.isDefault = it.value.getValue()!!.data!!.question!![k]!!.isDefault
                                data.orderBy = it.value.getValue()!!.data!!.question!![k]!!.orderBy
                                data.question = it.value.getValue()!!.data!!.question!![k]!!.question
                                data.options = it.value.getValue()!!.data!!.question!![k]!!.options
                                data.setImagePath(it.value.getValue()!!.data!!.question!![k]!!.getImagePath())

                                mData!!.add(data)
                            }
                            binding.questionsProgressBar.max = ((mData!!.size * 10).toFloat())


                            for (l in mData!![0].options!!.indices) {
                                var mrng: Option? =
                                    Option()
                                mrng!!.direction = mData!![0].options!![l]!!.direction!!
                                mrng.option = mData!![0].options!![l]!!.option!!
                                mAnswers!!.add(mrng)
                            }

                            binding.recyclerviewQuestionsList.layoutManager = LinearLayoutManager(
                                this,
                                RecyclerView.VERTICAL,
                                false
                            )


                            mData!![0].question?.let { it1 ->
                                selectedData!!.add(it1)
                                Log.e("question", mData!![0].question.toString())
                            }
                            selectedAnswers!!.add(mAnswers!![0])
                            adapter = SymptomSearchQuestionAdapter(
                                this,
                                mData,
                                selectedData,
                                0,
                                binding.recyclerviewQuestionsList
                            )
                            binding.txtQuestionTotal.setText(resources.getString(R.string.corona_ques_count) + " 1" + " of " + mData!!.size.toString())
                            binding.recyclerviewQuestionsList.adapter = adapter
                            adapter!!.notifyDataSetChanged()
                            mAdapter = SymptomSearchAnsAdapter(
                                mAnswers!!,
                                selectedAnswers!!,
                                applicationContext, binding.recyclerviewAnsList
                            )
                            binding.recyclerviewAnsList.adapter = mAdapter

                            mAdapter!!.notifyDataSetChanged()
                            runLayoutAnimation(binding.recyclerviewAnsList)

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
                        Utils.showToast(this, it.error!!.message.toString(),true)
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
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

    fun loadNextAns(position: Int) {
        mAnswers!!.clear()
        for (l in mData!![position].options!!.indices) {
            var mrng: Option? =
                Option()
            mrng!!.direction = mData!![position].options!![l]!!.direction!!
            mrng.option = mData!![position].options!![l]!!.option!!
            mAnswers!!.add(mrng)
        }
        mAdapter = SymptomSearchAnsAdapter(
            mAnswers!!,
            selectedAnswers!!,
            applicationContext,
            binding.recyclerviewAnsList
        )
        binding.recyclerviewAnsList.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        binding.recyclerviewAnsList.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
        binding.recyclerviewAnsList.smoothScrollToPosition(position - 1)
        runLayoutAnimation(binding.recyclerviewAnsList)
    }

    fun loadanim() {
        val resId: Int = R.anim.layout_animation
        val animation = AnimationUtils.loadLayoutAnimation(this, resId)
        binding.recyclerviewAnsList.setLayoutAnimation(animation)
    }

    fun loadNextQuestion(position: Int) {
        mData!!.clear()
        adapter = SymptomSearchQuestionAdapter(
            this,
            mData,
            selectedData, position,
            binding.recyclerviewQuestionsList
        )
    }

    class SnapHelperOneByOne : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager,
            velocityX: Int,
            velocityY: Int
        ): Int {
            if (layoutManager !is ScrollVectorProvider) {
                return RecyclerView.NO_POSITION
            }
            val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
            val myLayoutManager = layoutManager as LinearLayoutManager
            val position1 = myLayoutManager.findFirstVisibleItemPosition()
            val position2 = myLayoutManager.findLastVisibleItemPosition()
            var currentPosition = layoutManager.getPosition(currentView)
            if (velocityX > 400) {
                currentPosition = position2
            } else if (velocityX < 400) {
                currentPosition = position1
            }
            return if (currentPosition == RecyclerView.NO_POSITION) {
                RecyclerView.NO_POSITION
            } else currentPosition
        }
    }
}