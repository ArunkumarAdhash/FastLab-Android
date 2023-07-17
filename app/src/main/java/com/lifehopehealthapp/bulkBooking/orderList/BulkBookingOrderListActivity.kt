package com.lifehopehealthapp.bulkBooking.orderList

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityBulkbookingOrderlistBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.terms
import com.lifehopehealthapp.utils.TextChangedListener
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class BulkBookingOrderListActivity :
    BaseActivity<BulkBookingOrderListViewModel, BulkBookingOrderListModel>(),
    CompoundButton.OnCheckedChangeListener {

    private var OfferAmount: Int? = 0
    private var OfferAmount1: Long? = 0
    private var discount: String? = ""
    private var descriptions: String? = ""
    private var titleName: String? = ""
    private var TwoYrdiscount: String? = ""
    private var TwoYrdescription: String? = ""
    private var TwoYrtitleName: String? = ""
    private var OneYrdiscount: String? = ""
    private var OneYrdescription: String? = ""
    private var OneYrtitleName: String? = ""
    private var OneMonthdiscount: String? = ""
    private var OneMonthdescription: String? = ""
    private var OneMonthtitleName: String? = ""
    private var userToken: String? = ""
    private var testCategoryID: String? = ""
    private var locality: String? = ""
    private var orderDate: Int? = 0
    private var paymentName: String? = ""
    private var amount: Int? = 0
    private var testCount: Int? = 0
    private var primaryName: String? = ""
    private var address: String? = ""
    private var state: String? = ""
    private var testTypeID: String? = ""
    private var phoneNo: String? = ""
    private var latLong: String? = ""
    private var location: String? = ""
    private var Currentlatlong: String? = ""
    private var organizationType: String? = ""
    private var title: String? = ""
    private var email: String? = ""
    private var contactname: String? = ""
    private var PaymentAmount: String? = ""
    private var offerSelected: Boolean = false

    private var NoOfStudent: String? = ""
    private var NoOfTeacher: String? = ""
    private var NoOfCoaches: String? = ""
    private var NoOfDrivers: String? = ""

    private var spinnerStageTwo: Int? = 0
    private var spinnerStageThree: Int? = 0
    private var spinnerStageFour: Int? = 0
    private var spinnerStageFive: Int? = 0

    private var editTextStageSecondTotalValue: Long? = 0
    private var editTextStageThirdTotalValue: Long? = 0
    private var editTextStageFourthTotalValue: Long? = 0
    private var editTextStageFifthTotalValue: Long? = 0

    private var totalMonthlyTestForPeople: Long? = 0
    private var totalMonthlyTestForSurface: Long? = 0
    private var totalCostForPeople: Long? = 0
    private var totalCostForSurface: Long? = 0
    private var totalCostForMonthly: Long? = 0

    private var stageOneAns: ArrayList<StageOne> = ArrayList()
    private var stageTwoAns: ArrayList<StageTwo> = ArrayList()
    private var stageThreeAns: ArrayList<StageThree> = ArrayList()
    private var stageFourAns: ArrayList<StageFour> = ArrayList()
    private var stageFiveAns: ArrayList<StageFive> = ArrayList()
    private var mListData: ArrayList<Organization> = ArrayList()
    private var Payment: ArrayList<Payments> = ArrayList()

    private var duplicateStageTwoAns: ArrayList<StageTwo> = ArrayList()
    private var duplicateStageThreeAns: ArrayList<StageThree> = ArrayList()
    private var duplicateStageFourAns: ArrayList<StageFour> = ArrayList()
    private var duplicateStageFiveAns: ArrayList<StageFive> = ArrayList()

    private var orderSummary: ArrayList<OrderSummary> = ArrayList()
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var jsonData: String? = ""
    private var contractListsString: String? = ""
    private var progressDialog: Dialog? = null

    private var OneYrDiscount: Int? = 0
    private var TwoYrDiscount: Int? = 0
    private var OfferID: Int? = 0

    private var stageFiveValue1: Int? = 0
    private var stageFiveValue2: Int? = 0
    private var stageFourValue1: Int? = 0
    private var stageFourValue2: Int? = 0
    private var stageThreeValue1: Int? = 0
    private var stageThreeValue2: Int? = 0
    private var stageTwoValue1: Int? = 0
    private var stageTwoValue2: Int? = 0
    private var mOrganizationID: Int? = 0

    private lateinit var binding: ActivityBulkbookingOrderlistBinding

    override fun getViewModel() = BulkBookingOrderListViewModel::class.java

    override fun getActivityRepository() =
        BulkBookingOrderListModel(
            remoteDataSource.buildApi(APIManager::class.java),
            PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBulkbookingOrderlistBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)

        token = mPrefs!!.authToken

        binding.radioButton1.setOnCheckedChangeListener(this);
        binding.radioButton2.setOnCheckedChangeListener(this);
        binding.radioButton3.setOnCheckedChangeListener(this);

        val extras = intent.extras
        if (extras != null) {
            jsonData = extras.getString("json")
            NoOfStudent = extras.getString("NoOfStudent")
            NoOfTeacher = extras.getString("NoOfTeacher")
            NoOfCoaches = extras.getString("NoOfCoaches")
            NoOfDrivers = extras.getString("NoOfDrivers")

            mOrganizationID = extras.getInt("organizationID")
            spinnerStageTwo = extras.getInt("spinnerStageTwo")
            spinnerStageThree = extras.getInt("spinnerStageThree")
            spinnerStageFour = extras.getInt("spinnerStageFour")
            spinnerStageFive = extras.getInt("spinnerStageFive")

            editTextStageSecondTotalValue = extras.getLong("editTextStageSecondTotalValue")
            editTextStageThirdTotalValue = extras.getLong("editTextStageThirdTotalValue")
            editTextStageFourthTotalValue = extras.getLong("editTextStageFourthTotalValue")
            editTextStageFifthTotalValue = extras.getLong("editTextStageFifthTotalValue")

            contractListsString = extras.getString("contractListsString")

            ParshingJSON(jsonData)
            stageFiveValue1 = stageFiveAns[1].value!!.toInt()
            stageFiveValue2 = stageFiveAns[2].value!!.toInt()
            stageFourValue1 = stageFourAns[1].value!!.toInt()
            stageFourValue2 = stageFourAns[2].value!!.toInt()
            stageThreeValue1 = stageThreeAns[1].value!!.toInt()
            stageThreeValue2 = stageThreeAns[2].value!!.toInt()
            stageTwoValue1 = stageTwoAns[1].value!!.toInt()
            stageTwoValue2 = stageTwoAns[2].value!!.toInt()

            Log.e("finial-->", stageFiveValue1.toString() + "" + stageFiveValue2)
            val jsonArray = JSONArray(contractListsString)
            val arrSize = jsonArray.length()
            val sub: List<Int> = ArrayList(arrSize)
            for (i in 0 until arrSize) {
                val jsonObject = jsonArray.getJSONObject(i)
                if (i == 0) {
                    binding.textViewOffer.setText(jsonObject.get("description").toString())
                    binding.radioButton1.setText(jsonObject.get("discount").toString())
                    binding.textViewOneYrContractLabel.setText(
                        jsonObject.get("titleName").toString()
                    )
                    OneYrtitleName = jsonObject.get("titleName").toString()
                    OneYrdescription = jsonObject.get("description").toString()
                    OneYrdiscount = jsonObject.get("discount").toString()
                } else if (i == 1) {
                    binding.textViewOffer1.setText(jsonObject.get("description").toString())
                    binding.radioButton2.setText(jsonObject.get("discount").toString())
                    val Id = jsonObject.get("Id")
                    binding.textViewTwoYrContractLabel.setText(
                        jsonObject.get("titleName").toString()
                    )
                    val discountdata = jsonObject.get("discount").toString()
                    val removeChar = discountdata.replace("% offer ", "")
                    TwoYrtitleName = jsonObject.get("titleName").toString()
                    TwoYrdescription = jsonObject.get("description").toString()
                    TwoYrdiscount = jsonObject.get("discount").toString()
                } else if (i == 2) {
                    binding.textViewOffer2.setText(jsonObject.get("description").toString())
                    binding.radioButton3.setText(jsonObject.get("discount").toString())
                    val Id = jsonObject.get("Id")
                    binding.textViewOneMonthContractLabel.setText(
                        jsonObject.get("titleName").toString()
                    )
                    val discountdata = jsonObject.get("discount").toString()
                    val removeChar = discountdata.replace("% offer ", "")
                    OneMonthtitleName = jsonObject.get("titleName").toString()
                    OneMonthdescription = jsonObject.get("description").toString()
                    OneMonthdiscount = jsonObject.get("discount").toString()
                }

            }

            binding.layoutNoOfStudent.hint = stageTwoAns[0].header
            binding.layoutNoOfTeacher.hint = stageThreeAns[0].header
            binding.layoutNoOfCoaches.hint = stageFourAns[0].header
            binding.layoutNoOfDrivers.hint = stageFiveAns[0].header

            binding.editTextNoOfStudent.setText(NoOfStudent)
            binding.editTextNoOfTeacher.setText(NoOfTeacher)
            binding.editTextNoOfCoaches.setText(NoOfCoaches)
            binding.editTextNoOfDrivers.setText(NoOfDrivers)


            PaymentAmount = totalCostForMonthly.toString()


            val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("#,###,###,###")
            val formattedString: String = formatter.format(totalCostForMonthly)
            binding.textViewMonthlyTestAmount.setText("$ " + formattedString)
            binding.textViewSaveAmount2.setText("$ " + formattedString)

            val formatter1: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter1.applyPattern("#,###,###,###")
            val formattedString1: String = formatter.format(totalCostForPeople)
            binding.textViewTestAmount.setText("$ " + formattedString1 + "/Month")

            val formatter2: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter2.applyPattern("#,###,###,###")
            val formattedString2: String = formatter.format(totalCostForSurface)
            binding.textViewSurfaceTestAmount.setText("$ " + formattedString2 + "/Month")

            val formatter3: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter3.applyPattern("#,###,###,###")
            val formattedString3: String = formatter.format(totalMonthlyTestForPeople)
            binding.textViewAmount.setText(formattedString3)

            val formatter4: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter4.applyPattern("#,###,###,###")
            val formattedString4: String = formatter.format(totalMonthlyTestForSurface)
            binding.textViewAmount1.setText(formattedString4)

            binding.editTextStudentFrequency.setText(spinnerStageTwo.toString())
            binding.editTextTeacherFrequency.setText(spinnerStageThree.toString())
            binding.editTextCoachesFrequency.setText(spinnerStageFour.toString())
            binding.editTextDriversFrequency.setText(spinnerStageFive.toString())

            getValueCalculation()
        }
        editTextValidation()

        val wordtoSpan: Spannable =
            SpannableString(getString(R.string.text_i_Agree))
        val teremsAndCondition: ClickableSpan =
            object : ClickableSpan() {
                override fun onClick(textView: View) {
                    showCheckboxPopup()
                }
            }
        wordtoSpan.setSpan(teremsAndCondition, 8, 26, 0)
        wordtoSpan.setSpan(
            ForegroundColorSpan(
                resources
                    .getColor(R.color.link_color)
            ), 8, 26, 0
        )
        binding.textViewTerms.setMovementMethod(LinkMovementMethod.getInstance())
        binding.textViewTerms.setText(wordtoSpan, TextView.BufferType.SPANNABLE)


        binding.imageViewBackArrow.setOnClickListener {

            val editModel = BulkTestingEditModel()
            editModel.NoOfCoaches = NoOfCoaches
            editModel.NoOfDrivers = NoOfDrivers
            editModel.NoOfStudent = NoOfStudent
            editModel.NoOfTeacher = NoOfTeacher
            editModel.spinnerStageTwo = spinnerStageTwo
            editModel.spinnerStageThree = spinnerStageThree
            editModel.spinnerStageFour = spinnerStageFour
            editModel.spinnerStageFive = spinnerStageFive
            EventBus.getDefault().post(editModel)
            finish()
        }

        binding.buttonProceedToPay.setOnClickListener {
            if (binding.checkBoxCondition.isChecked) {
                val studentData = binding.editTextNoOfStudent.text.toString()
                val studentFrequency = binding.editTextStudentFrequency.text.toString()
                val teacherData = binding.editTextNoOfTeacher.text.toString()
                val teacherFrequency = binding.editTextTeacherFrequency.text.toString()
                val coachesData = binding.editTextNoOfCoaches.text.toString()
                val coachesFrequnecy = binding.editTextCoachesFrequency.text.toString()
                val driverData = binding.editTextNoOfDrivers.text.toString()
                val driverFrequency = binding.editTextDriversFrequency.text.toString()

                if ((!studentData.equals("")) && (!studentFrequency.equals("")) && (!teacherData.equals(
                        ""
                    )) && (!teacherFrequency.equals("")) && (!coachesData.equals("")) && (!coachesFrequnecy.equals(
                        ""
                    )) && (!driverData.equals("")) && (!driverFrequency.equals("")) && (studentData.toInt() != 0) && (studentFrequency.toInt() != 0) && (teacherData.toInt() != 0) && (teacherFrequency.toInt() != 0) && (coachesData.toInt() != 0) && (coachesFrequnecy.toInt() != 0) && (driverData.toInt() != 0) &&
                    (driverFrequency.toInt() != 0)
                ) {

                    if ((driverFrequency.toInt() <= 4) && (coachesFrequnecy.toInt() <= 4) && (teacherFrequency.toInt() <= 4) && (studentFrequency.toInt() <= 4)){
                        duplicateStageFiveAns.clear()
                        duplicateStageFourAns.clear()
                        duplicateStageThreeAns.clear()
                        duplicateStageTwoAns.clear()

                        val stagefiveOne = StageFive()
                        stagefiveOne.id = stageFiveAns[0].id
                        stagefiveOne.header = stageFiveAns[0].header
                        stagefiveOne.value = NoOfCoaches
                        duplicateStageFiveAns.add(stagefiveOne)
                        val stagefiveTwo = StageFive()
                        stagefiveTwo.id = stageFiveAns[1].id
                        stagefiveTwo.header = stageFiveAns[1].header
                        stagefiveTwo.value = stageFiveValue1.toString()
                        duplicateStageFiveAns.add(stagefiveTwo)
                        val stagefiveThree = StageFive()
                        stagefiveThree.id = stageFiveAns[2].id
                        stagefiveThree.header = stageFiveAns[2].header
                        stagefiveThree.value = stageFiveValue2.toString()
                        duplicateStageFiveAns.add(stagefiveThree)
                        val stagefiveFour = StageFive()
                        stagefiveFour.id = stageFiveAns[3].id
                        stagefiveFour.header = stageFiveAns[3].header
                        stagefiveFour.value = spinnerStageFive.toString()
                        duplicateStageFiveAns.add(stagefiveFour)
                        val stagefiveFive = StageFive()
                        stagefiveFive.id = stageFiveAns[4].id
                        stagefiveFive.header = stageFiveAns[4].header
                        stagefiveFive.value = editTextStageFifthTotalValue.toString()
                        duplicateStageFiveAns.add(stagefiveFive)


                        val stagefourOne = StageFour()
                        stagefourOne.id = stageFourAns[0].id
                        stagefourOne.header = stageFourAns[0].header
                        stagefourOne.value = NoOfDrivers
                        duplicateStageFourAns.add(stagefourOne)
                        val stagefourTwo = StageFour()
                        stagefourTwo.id = stageFourAns[1].id
                        stagefourTwo.header = stageFourAns[1].header
                        stagefourTwo.value = stageFourValue1.toString()
                        duplicateStageFourAns.add(stagefourTwo)
                        val stagefourThree = StageFour()
                        stagefourThree.id = stageFourAns[2].id
                        stagefourThree.header = stageFourAns[2].header
                        stagefourThree.value = stageFourValue2.toString()
                        duplicateStageFourAns.add(stagefourThree)
                        val stagefourFour = StageFour()
                        stagefourFour.id = stageFourAns[3].id
                        stagefourFour.header = stageFourAns[3].header
                        stagefourFour.value = spinnerStageFour.toString()
                        duplicateStageFourAns.add(stagefourFour)
                        val stagefourFive = StageFour()
                        stagefourFive.id = stageFourAns[4].id
                        stagefourFive.header = stageFourAns[4].header
                        stagefourFive.value = editTextStageFourthTotalValue.toString()
                        duplicateStageFourAns.add(stagefourFive)

                        val stagethreeOne = StageThree()
                        stagethreeOne.id = stageThreeAns[0].id
                        stagethreeOne.header = stageThreeAns[0].header
                        stagethreeOne.value = NoOfTeacher
                        duplicateStageThreeAns.add(stagethreeOne)
                        val stagethreeTwo = StageThree()
                        stagethreeTwo.id = stageThreeAns[1].id
                        stagethreeTwo.header = stageThreeAns[1].header
                        stagethreeTwo.value = stageThreeValue1.toString()
                        duplicateStageThreeAns.add(stagethreeTwo)
                        val stagethreeThree = StageThree()
                        stagethreeThree.id = stageThreeAns[2].id
                        stagethreeThree.header = stageThreeAns[2].header
                        stagethreeThree.value = stageThreeValue2.toString()
                        duplicateStageThreeAns.add(stagethreeThree)
                        val stagethreeFour = StageThree()
                        stagethreeFour.id = stageThreeAns[3].id
                        stagethreeFour.header = stageThreeAns[3].header
                        stagethreeFour.value = spinnerStageThree.toString()
                        duplicateStageThreeAns.add(stagethreeFour)
                        val stagethreeFive = StageThree()
                        stagethreeFive.id = stageThreeAns[4].id
                        stagethreeFive.header = stageThreeAns[4].header
                        stagethreeFive.value = editTextStageThirdTotalValue.toString()
                        duplicateStageThreeAns.add(stagethreeFive)


                        val stagetwoOne = StageTwo()
                        stagetwoOne.id = stageTwoAns[0].id
                        stagetwoOne.header = stageTwoAns[0].header
                        stagetwoOne.value = NoOfStudent
                        duplicateStageTwoAns.add(stagetwoOne)
                        val stagetwoTwo = StageTwo()
                        stagetwoTwo.id = stageTwoAns[1].id
                        stagetwoTwo.header = stageTwoAns[1].header
                        stagetwoTwo.value = stageTwoValue1.toString()
                        duplicateStageTwoAns.add(stagetwoTwo)
                        val stagetwoThree = StageTwo()
                        stagetwoThree.id = stageTwoAns[2].id
                        stagetwoThree.header = stageTwoAns[2].header
                        stagetwoThree.value = stageTwoValue2.toString()
                        duplicateStageTwoAns.add(stagetwoThree)
                        val stagetwoFour = StageTwo()
                        stagetwoFour.id = stageTwoAns[3].id
                        stagetwoFour.header = stageTwoAns[3].header
                        stagetwoFour.value = spinnerStageTwo.toString()
                        duplicateStageTwoAns.add(stagetwoFour)
                        val stagetwoFive = StageTwo()
                        stagetwoFive.id = stageTwoAns[4].id
                        stagetwoFive.header = stageTwoAns[4].header
                        stagetwoFive.value = editTextStageSecondTotalValue.toString()
                        duplicateStageTwoAns.add(stagetwoFive)


                        val req = BulkBookingOrderAPIRequest()
                        req.setUserFCMToken(userToken)
                        req.setTestCategoryId(testCategoryID)
                        req.setLocality(locality)
                        req.setOrderDate(orderDate)
                        val payment = Payments()
                        val mAmount = PaymentAmount!!.replace(",", "")
                        payment.amount = mAmount.toLong()
                        payment.testCount = testCount
                        payment.paymentName = paymentName
                        Payment.clear()
                        Payment.add(payment)
                        req.setPayment(Payment)
                        req.setPrimaryName(primaryName)
                        val member = Memberes()
                        req.setMembers(listOf(member))
                        req.setAddress(address)
                        req.setScheduleDate(0)
                        req.setState(state)
                        req.setTestTypeID(testTypeID)
                        var mMobile = phoneNo
                        mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")
                        req.setPhoneNo(mMobile)
                        req.setImagePath("")
                        req.setLatLong(latLong)
                        req.setIosimagepath("")
                        req.setLocation(location)
                        req.setCurrentlatlong(Currentlatlong)
                        req.setBookingType(1)

                        val listData = Organization()
                        listData.stageOne = stageOneAns
                        listData.stageTwo = duplicateStageTwoAns
                        listData.stageThree = duplicateStageThreeAns
                        listData.stageFour = duplicateStageFourAns
                        listData.stageFive = duplicateStageFiveAns
                        mListData.add(listData)
                        req.setOrganizationList(mListData)

                        req.setOrganizationType(organizationType)
                        req.setOrganizationID(mOrganizationID)
                        val data = OrderSummary()
                        data.key = "Total Cost People Tested"
                        val testamount = binding.textViewTestAmount.text.toString()
                        val removeChar = testamount.replace("/Month", " ")
                        data.value = removeChar.replace("$", "").replace(",", "").trim()
                        data.id = "1"
                        orderSummary.add(data)
                        val data1 = OrderSummary()
                        data1.key = "Total Monthly Test for People"
                        val mamout = binding.textViewAmount.text.toString()
                        data1.value = mamout.replace("$", "").replace(",", "").trim()
                        data1.id = "4"
                        orderSummary.add(data1)
                        val data2 = OrderSummary()
                        data2.key = "Total Cost for Surfaces Tested"
                        val removeChar1 =
                            binding.textViewSurfaceTestAmount.text.toString().replace("/Month", " ")
                        data2.value = removeChar1.replace("$", "").replace(",", "").trim()
                        data2.id = "2"
                        orderSummary.add(data2)
                        val data3 = OrderSummary()
                        data3.key = "Total Monthly Tests for Surface"
                        val mSurface = binding.textViewAmount1.text.toString()
                        data3.value = mSurface.replace("$", "").replace(",", "").trim()
                        data3.id = "5"
                        orderSummary.add(data3)
                        val data4 = OrderSummary()
                        data4.key = "Total Cost per Monthly"
                        val removeChar2 =
                            binding.textViewMonthlyTestAmount.text.toString().replace("$", "")
                                .replace(",", "").trim()
                        data4.value = removeChar2
                        data4.id = "3"
                        orderSummary.add(data4)
                        req.setOrderSummary(orderSummary)

                        req.setTitle(title)
                        req.setEmail(email)
                        req.setContactname(contactname)

                        val contract = ContractList()
                        contract.id = OfferID
                        contract.description = descriptions
                        contract.discount = discount
                        contract.titleName = titleName
                        req.setContractList(contract)

                        val gson = Gson()
                        var json: String? = ""
                        json = gson.toJson(req)
                        Log.e("OMGGGG", json)

                        val aJsonParser = JsonParser()
                        val aJsonObject = aJsonParser.parse(json) as JsonObject
                        Log.e("aJsonObject", aJsonObject.toString())
                        if (Utils.isNetworkAvailable(this)) {
                            if (progressDialog == null) {
                                progressDialog = Utils.getDialog(this)
                            }
                            progressDialog!!.show()
                            viewModel.getOrderBooking(token!!, aJsonObject)

                            viewModel.Response1!!.observe(this, androidx.lifecycle.Observer {
                                when (it) {
                                    is Resource.Success -> {
                                        if (progressDialog != null && progressDialog!!.isShowing) {
                                            progressDialog!!.dismiss()
                                        }
                                        if (it.value.getStatusCode() == 200) {
                                            val url: String = it.value.getValue()!!.data!!.link!!
                                            val order_id: String =
                                                it.value.getValue()!!.data!!.orderID!!
                                            Log.e("orderID", it.value.getValue()!!.data!!.orderID!!)
                                            Utils.webViewData("bulk", url, order_id, this)
                                            //finish()
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
                                            Utils.showToast(
                                                this,
                                                it.value.getValue()!!.message.toString(),
                                                true
                                            )
                                        }
                                    }

                                    is Resource.GenericError -> {
                                        Utils.showToast(this, it.error!!.message.toString(), true)
                                        if (progressDialog != null && progressDialog!!.isShowing) {
                                            progressDialog!!.dismiss()
                                        }
                                        if (it.code == 400) {
                                            Utils.clearSharedPreferences(this)
                                            val intent = Intent(this, SplashActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            val extras = Bundle()
                                            extras.putString("LogOut", "LogOut")
                                            intent.putExtras(extras)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            startActivity(intent)
                                            Utils.showToast(this, "User doesn't exist", true)
                                        } else if (it.code == 401) {
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
                    }else{
                        Utils.showToast(this, "Please enter frequency range  from 1 to 4", true)
                    }
                } else {
                    Utils.showToast(this, resources.getString(R.string.toast_empty_data), true)
                }
            } else {
                Utils.showToast(this, getString(R.string.toast_bulk_booking_terms), true)
            }
        }
    }

    private fun showCheckboxPopup() {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_terms_condition)
        val heading: com.lifehopehealthapp.widgets.LifeHopenTextView =
            alertDialog.findViewById(R.id.textview_dialog_heading)
        val headingTxt: com.lifehopehealthapp.widgets.LifeHopenTextView =
            alertDialog.findViewById(R.id.textview_header)
        headingTxt.text = resources.getString(R.string.terms_conditions)
        heading.setText(mPrefs!!.terms)
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)

        posTv.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun ParshingJSON(jsonData: String?) {
        val jsonObj = JSONObject(jsonData!!)
        userToken = jsonObj.get("userFCMToken").toString()
        testCategoryID = jsonObj.get("testCategoryId").toString()
        locality = jsonObj.get("locality").toString()
        orderDate = jsonObj.get("orderDate").toString().toInt()
        primaryName = jsonObj.get("primaryName").toString()
        address = jsonObj.get("address").toString()
        state = jsonObj.get("state").toString()
        testTypeID = jsonObj.get("testTypeID").toString()
        phoneNo = jsonObj.get("phoneNo").toString()
        latLong = jsonObj.get("latLong").toString()
        location = jsonObj.get("location").toString()
        Currentlatlong = jsonObj.get("Currentlatlong").toString()
        organizationType = jsonObj.get("organizationType").toString()
        title = jsonObj.get("title").toString()
        email = jsonObj.get("email").toString()
        contactname = jsonObj.get("contactname").toString()
        val jsonarray1 = jsonObj.getJSONArray("payment")
        for (j in 0 until jsonarray1.length()) {
            val object2: JSONObject = jsonarray1.getJSONObject(j)
            paymentName = object2.getString("paymentName")
            amount = object2.getInt("amount")
            testCount = object2.getInt("testCount")
        }

        val jsonArray = jsonObj.getJSONArray("organizationList")
        for (i in 0 until jsonArray.length()) {
            val object2: JSONObject = jsonArray.getJSONObject(i)
            Log.e("object2", object2.toString())

            val array1 = object2.getJSONArray("StageFive")
            for (j in 0 until array1.length()) {
                val object1: JSONObject = array1.getJSONObject(j)
                val req = StageFive()
                req.id = object1.getString("id")
                req.header = object1.getString("header")
                req.value = object1.getString("value")
                stageFiveAns.add(req)
            }
            Log.e("five--->", stageFiveAns.size.toString())
            val array2 = object2.getJSONArray("StageFour")
            for (j in 0 until array2.length()) {
                val object1: JSONObject = array2.getJSONObject(j)
                val req = StageFour()
                req.id = object1.getString("id")
                req.header = object1.getString("header")
                req.value = object1.getString("value")
                stageFourAns.add(req)
            }
            Log.e("five--->", stageFourAns.size.toString())
            val array3 = object2.getJSONArray("StageThree")
            for (j in 0 until array3.length()) {
                val object1: JSONObject = array3.getJSONObject(j)
                val req = StageThree()
                req.id = object1.getString("id")
                req.header = object1.getString("header")
                req.value = object1.getString("value")
                stageThreeAns.add(req)
            }
            Log.e("five--->", stageThreeAns.size.toString())
            val array4 = object2.getJSONArray("StageTwo")
            for (j in 0 until array4.length()) {
                val object1: JSONObject = array4.getJSONObject(j)
                val req = StageTwo()
                req.id = object1.getString("id")
                req.header = object1.getString("header")
                req.value = object1.getString("value")
                stageTwoAns.add(req)
            }
            Log.e("five--->", stageTwoAns.size.toString())
            val array5 = object2.getJSONArray("StageOne")
            for (j in 0 until array5.length()) {
                val object1: JSONObject = array5.getJSONObject(j)
                val req = StageOne()
                req.id = object1.getString("id")
                req.header = object1.getString("header")
                req.value = object1.getString("value")
                stageOneAns.add(req)
            }
            Log.e("five--->", stageOneAns.size.toString())
        }
    }

    private fun getValueCalculation() {

        totalMonthlyTestForPeople =
            (NoOfStudent!!.toLong() * spinnerStageTwo!!) + (NoOfTeacher!!.toLong() * spinnerStageThree!!) + (NoOfCoaches!!.toLong() * spinnerStageFour!!) + (NoOfDrivers!!.toLong() * spinnerStageFive!!)

        var totalmonth =
            editTextStageSecondTotalValue!! + editTextStageThirdTotalValue!! + editTextStageFourthTotalValue!! + editTextStageFifthTotalValue!!

        totalMonthlyTestForSurface = totalmonth - totalMonthlyTestForPeople!!
        Log.e(
            "totalmonth->",
            editTextStageSecondTotalValue.toString() + "," + editTextStageThirdTotalValue + "," + editTextStageFourthTotalValue + "," + editTextStageFifthTotalValue
        )
        totalCostForPeople = 10 * totalMonthlyTestForPeople!!
        totalCostForSurface = 5 * totalMonthlyTestForSurface!!

        totalCostForMonthly = totalCostForPeople!! + totalCostForSurface!!
        PaymentAmount = totalCostForMonthly.toString()

        /*binding.textViewAmount.setText(totalMonthlyTestForPeople.toString())
        binding.textViewAmount1.setText(totalMonthlyTestForSurface.toString())
        binding.textViewMonthlyTestAmount.setText("$ " + totalCostForMonthly.toString())
        binding.textViewTestAmount.setText("$ " + totalCostForPeople.toString() + "/Month")
        binding.textViewSurfaceTestAmount.setText("$ " + totalCostForSurface.toString() + "/Month")
*/

        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###,###,###")
        val formattedString: String = formatter.format(totalCostForMonthly)
        binding.textViewMonthlyTestAmount.setText("$ " + formattedString)
        binding.textViewSaveAmount2.setText("$ " + formattedString)

        val formatter1: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter1.applyPattern("#,###,###,###")
        val formattedString1: String = formatter.format(totalCostForPeople)
        binding.textViewTestAmount.setText("$ " + formattedString1 + "/Month")

        val formatter2: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter2.applyPattern("#,###,###,###")
        val formattedString2: String = formatter.format(totalCostForSurface)
        binding.textViewSurfaceTestAmount.setText("$ " + formattedString2 + "/Month")

        val formatter3: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter3.applyPattern("#,###,###,###")
        val formattedString3: String = formatter.format(totalMonthlyTestForPeople)
        binding.textViewAmount.setText(formattedString3)

        val formatter4: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter4.applyPattern("#,###,###,###")
        val formattedString4: String = formatter.format(totalMonthlyTestForSurface)
        binding.textViewAmount1.setText(formattedString4)

        TwoYrDiscount = 50
        OneYrDiscount = 25
        val oneYr = totalCostForMonthly!! * 12
        val twoYr = totalCostForMonthly!! * 24

        val off = (TwoYrDiscount!! * twoYr / 100)
        val saving = twoYr - off

        val formatter8: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter8.applyPattern("#,###,###,###")
        val formattedString8: String = formatter.format(twoYr)
        binding.textViewOfferAmount1.setText("$ " + formattedString8.toString())

        binding.textViewOfferAmount1.setPaintFlags(binding.textViewOfferAmount1.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        OfferAmount1 = saving
        val formatter5: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter5.applyPattern("#,###,###,###")
        val formattedString5: String = formatter.format(saving)
        binding.textViewSaveAmount1.setText("$ " + formattedString5)

        val off1 = (OneYrDiscount!! * oneYr / 100)
        val saving1 = oneYr - off1

        val formatter7: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter7.applyPattern("#,###,###,###")
        val formattedString7: String = formatter.format(oneYr)
        binding.textViewOfferAmount.setText("$ " + formattedString7.toString())
        binding.textViewOfferAmount.setPaintFlags(binding.textViewOfferAmount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        OfferAmount = saving1.toInt()
        val formatter6: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter6.applyPattern("#,###,###,###")
        val formattedString6: String = formatter.format(saving1)
        binding.textViewSaveAmount.setText("$ " + formattedString6)


        /*binding.editTextStudentFrequency.setText(spinnerStageTwo.toString())
        binding.editTextTeacherFrequency.setText(spinnerStageThree.toString())
        binding.editTextCoachesFrequency.setText(spinnerStageFour.toString())
        binding.editTextDriversFrequency.setText(spinnerStageFive.toString())*/
    }

    private fun editTextValidation() {
        binding.editTextNoOfStudent.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextNoOfStudent) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString().toInt() != 0)
                ) {
                    NoOfStudent = target.text.toString()
                    editTextStageSecondTotalValue =
                        (NoOfStudent!!.toLong() + stageTwoValue1!! + stageTwoValue2!!) * spinnerStageTwo!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextNoOfStudent.setError("Enter Valid Data")
                }
            }
        })

        binding.editTextStudentFrequency.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextStudentFrequency) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString()
                        .toInt() <= 4) && (target.text.toString().toInt() != 0)
                ) {
                    spinnerStageTwo = target.text.toString().toInt()
                    editTextStageSecondTotalValue =
                        (NoOfStudent!!.toLong() + stageTwoValue1!! + stageTwoValue2!!) * spinnerStageTwo!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextStudentFrequency.setError("Please enter frequency range  from 1 to 4")
                }
            }
        })

        binding.editTextNoOfTeacher.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextNoOfTeacher) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString().toInt() != 0)
                ) {
                    NoOfTeacher = target.text.toString()
                    editTextStageThirdTotalValue =
                        (NoOfTeacher!!.toLong() + stageThreeValue1!! + stageThreeValue2!!) * spinnerStageThree!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextNoOfTeacher.setError("Enter Valid Data")
                }
            }
        })

        binding.editTextTeacherFrequency.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextTeacherFrequency) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString()
                        .toInt() <= 4) && (target.text.toString().toInt() != 0)
                ) {
                    spinnerStageThree = target.text.toString().toInt()
                    editTextStageThirdTotalValue =
                        (NoOfTeacher!!.toLong() + stageThreeValue1!! + stageThreeValue2!!) * spinnerStageThree!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextTeacherFrequency.setError("Please enter frequency range  from 1 to 4")
                }
            }
        })

        binding.editTextNoOfCoaches.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextNoOfCoaches) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString().toInt() != 0)
                ) {
                    NoOfCoaches = target.text.toString()
                    editTextStageFourthTotalValue =
                        (NoOfCoaches!!.toLong() + stageFiveValue1!! + stageFiveValue2!!) * spinnerStageFour!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextNoOfCoaches.setError("Enter Valid Data")
                }

            }
        })

        binding.editTextCoachesFrequency.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextCoachesFrequency) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString()
                        .toInt() <= 4) && (target.text.toString().toInt() != 0)
                ) {
                    spinnerStageFour = target.text.toString().toInt()
                    editTextStageFourthTotalValue =
                        (NoOfCoaches!!.toLong() + stageFiveValue1!! + stageFiveValue2!!) * spinnerStageFour!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextCoachesFrequency.setError("Please enter frequency range  from 1 to 4")
                }
            }
        })

        binding.editTextNoOfDrivers.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextNoOfDrivers) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString().toInt() != 0)
                ) {
                    NoOfDrivers = target.text.toString()
                    editTextStageFifthTotalValue =
                        (NoOfDrivers!!.toLong() + stageFourValue1!! + stageFourValue2!!) * spinnerStageFive!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextNoOfDrivers.setError("Enter Valid Data")
                }
            }
        })

        binding.editTextDriversFrequency.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextDriversFrequency) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("") && (target.text.toString()
                        .toInt() <= 4) && (target.text.toString().toInt() != 0)
                ) {
                    spinnerStageFive = target.text.toString().toInt()
                    editTextStageFifthTotalValue =
                        (NoOfDrivers!!.toLong() + stageFourValue1!! + stageFourValue2!!) * spinnerStageFive!!
                    Log.e("total", editTextStageSecondTotalValue.toString())
                    getValueCalculation()
                } else {
                    binding.editTextDriversFrequency.setError("Please enter frequency range  from 1 to 4")
                }
            }
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            offerSelected = true
            if (buttonView!!.getId() == R.id.radioButton1) {
                PaymentAmount = OfferAmount.toString()
                binding.radioButton2.setChecked(false)
                binding.radioButton3.setChecked(false)
                OfferID = 1
                titleName = OneYrtitleName
                descriptions = OneYrdescription
                discount = OneYrdiscount
            }
            if (buttonView.getId() == R.id.radioButton2) {
                binding.radioButton1.setChecked(false)
                binding.radioButton3.setChecked(false)
                OfferID = 2
                titleName = TwoYrtitleName
                descriptions = TwoYrdescription
                discount = TwoYrdiscount
                PaymentAmount = OfferAmount1.toString()
            }
            if (buttonView.getId() == R.id.radioButton3) {
                binding.radioButton1.setChecked(false)
                binding.radioButton2.setChecked(false)
                OfferID = 3
                titleName = OneMonthtitleName
                descriptions = OneMonthdescription
                discount = OneMonthdiscount
                PaymentAmount = totalCostForMonthly.toString()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val editModel = BulkTestingEditModel()
        editModel.NoOfCoaches = NoOfCoaches
        editModel.NoOfDrivers = NoOfDrivers
        editModel.NoOfStudent = NoOfStudent
        editModel.NoOfTeacher = NoOfTeacher
        editModel.spinnerStageTwo = spinnerStageTwo
        editModel.spinnerStageThree = spinnerStageThree
        editModel.spinnerStageFour = spinnerStageFour
        editModel.spinnerStageFive = spinnerStageFive

        EventBus.getDefault().post(editModel)
        finish()
    }
}