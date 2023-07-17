package com.lifehopehealthapp.bulkBookingNew

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kofigyan.stateprogressbar.StateProgressBar
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.ResponseModel.Surface
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bulkBooking.mailScreen.ResendMailActivity
import com.lifehopehealthapp.databinding.NewBulkBookingActivityBinding
import com.lifehopehealthapp.myProfile.MapsActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.CustomEditText.GoEditTextListener
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PaymentCompleteActivity
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.fcmToken
import com.lifehopehealthapp.utils.PreferenceHelper.latlng
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.terms
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class NewBulkBookingActivity : BaseActivity<NewBulkBookingViewModel, NewBulkBookingModel>(),
    BulkBookingPeopleListAdapter.BulkBookingSelectionList,
    BulkBookingPeopleListAdapter.BulkBookingSelectionListRemove,
    BulkBookingPeopleSelectionAdapter.BulkBookingSelectionListUpdate,
    BulkBookingPeopleSelectionAdapter.BulkBookingSelectionListValueCleared,

BulkBookingSurfaceListAdapter.BulkBookingSelectionList,
BulkBookingSurfaceListAdapter.BulkBookingSelectionListRemove,
BulkBookingSurfaceSelectionAdapter.BulkBookingSelectionListUpdate,
BulkBookingSurfaceSelectionAdapter.BulkBookingSelectionListValueCleared,CompoundButton.OnCheckedChangeListener,ContractListAdapter.BulkBookingContractList

{

    private lateinit var binding: NewBulkBookingActivityBinding
    override fun getViewModel() = NewBulkBookingViewModel::class.java

    var decimalWithTwoDigit: DecimalFormat = DecimalFormat("#,###")


    private var mSelectedOriginazationID: Int = 0
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var progressDoalog: Dialog? = null

    private var locality: String? = ""
    private var mState: String? = ""
    private var latlng: String? = null

    private var OrganizationTypeName: String? = ""
    private var mobileResponse: String? = ""
    private var emailResponse: String? = ""


    private var testTypeID: String? = ""
    private var testName: String? = ""
    private var testCategoryID: String? = ""
    private var orderBy: Int?=null

    private var mName: String? = ""
    private var mAddress: String? = ""
    private var mobileNumber: String? = ""
    private var email: String? = ""
    private var contactName: String? = ""
    private var title : String?=""

    private var price : Float=0f
    private var surfacePrice : Float=0f

    private var peopleValidationAlert: Boolean = false
    private var surfaceValidationAlert: Boolean = false

    private var contractMonth : Int = 0

    private var paymentGatewayAmount1=0f
    private var paymentGatewayAmount2=0f
    private var paymentGatewayAmount3=0f
    private var progressDialog: Dialog? = null

    private var isPayment : Boolean=true
    private var paymentType : Int= 1

    private var textPaste : Boolean = false

    private var offerSelected: Boolean = false

    lateinit var recyclerViewPeopleList: RecyclerView
    lateinit var peopleTick: AppCompatImageView
    lateinit var peopleClose: AppCompatImageView

    lateinit var recyclerViewSurfaceList: RecyclerView
    lateinit var surfaceTick: AppCompatImageView
    lateinit var surfaceClose: AppCompatImageView


    private var mPeopleList: ArrayList<OrganizationTitleNewResponse.Person> = ArrayList()
    private var mPeopleSelectedValueForApi: ArrayList<PersonList> = ArrayList()
    private var mPeopleSelectedValue: ArrayList<OrganizationTitleNewResponse.Person> = ArrayList()



    private var mSurfaceList: ArrayList<OrganizationTitleNewResponse.Surface> = ArrayList()
    private var mSurfaceSelectedValueForApi: ArrayList<Surface> = ArrayList()
    private var mSurfaceSelectedValue: ArrayList<OrganizationTitleNewResponse.Surface> = ArrayList()

    private var mTestType: ArrayList<OrganizationTitleNewResponse.TestType> = ArrayList()
    private var mTitle: ArrayList<String?> = ArrayList()

    private var mContractList: ArrayList<OrganizationTitleNewResponse.ContractList> = ArrayList()
    private var mContractListForApi: ContractListNew = ContractListNew()



    //dummy
    private var mContractListFor: OrganizationTitleNewResponse.ContractList=OrganizationTitleNewResponse.ContractList()

    private var orderSummary: ArrayList<OrderSummaryNew> = ArrayList()
    private var paymentApiReqValue: ArrayList<PaymentNew> = ArrayList()


    private var count = 0;
    var  loadOneTimePeopleAlert: Boolean =true
    var  loadOneTimeSurfaceAlert: Boolean =true
    lateinit var alertDialogPeople: Dialog
    lateinit var alertDialogSurface: Dialog

    lateinit var bulkBookingPeopleListAdaptor: BulkBookingPeopleListAdapter
    lateinit var bulkBookingPeopleSelectionAdapter: BulkBookingPeopleSelectionAdapter


    lateinit var bulkBookingSurfaceListAdaptor: BulkBookingSurfaceListAdapter
    lateinit var bulkBookingSurfaceSelectionAdapter: BulkBookingSurfaceSelectionAdapter


    lateinit var contractListAdapter: ContractListAdapter



    override fun getActivityRepository() = NewBulkBookingModel(
        remoteDataSource.buildApi(APIManager::class.java),
        PreferenceHelper
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewBulkBookingActivityBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)


        mPrefs = PreferenceHelper.defaultPreference(this)

        token = mPrefs!!.authToken
        // binding.layoutBulkBookingFirstPage.isVisible = true

        OrganizationTypeName = intent.getStringExtra("SelectItemName")
        val data = intent.getStringExtra("SelectedID")
        mSelectedOriginazationID = data!!.toInt()

        isPayment = intent.getBooleanExtra("isPayment", true)


       /* if (!isPayment)
        {
            paymentType =0
        }*/




     //  setValues()






        getAPICall(data.toInt())
        emailAlertPopup(data)
        binding.editTextEmail.setOnClickListener {
            emailAlertPopup(data)
        }

        binding.imageViewBackArrow.setOnClickListener {
            handlePreviousState()


        }

        binding.edittextAddress.setText("")

        binding.edittextAddress.setOnClickListener {
            googleMap()

        }


        binding.radioButton1.setOnCheckedChangeListener(this)
        binding.radioButton2.setOnCheckedChangeListener(this)
        binding.radioButton3.setOnCheckedChangeListener(this)


        bulkBookingPeopleSelectionAdapter = BulkBookingPeopleSelectionAdapter(
            this,
            this.mPeopleList, this, this,this, binding.dynamicRecyclerViewPeopleSelection
        )
        binding.dynamicRecyclerViewPeopleSelection.adapter = bulkBookingPeopleSelectionAdapter





        bulkBookingSurfaceSelectionAdapter = BulkBookingSurfaceSelectionAdapter(
            this,
            this.mSurfaceList, this, this,this
        )
        binding.dynamicRecyclerViewSurfaceSelection.adapter = bulkBookingSurfaceSelectionAdapter



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



        alertDialogPeople = Dialog(this)
        alertDialogSurface = Dialog(this)






        disablePasteOption(binding.editTextMobileNo)






        binding.layoutSchoolName.hint = OrganizationTypeName + " " + getString(R.string.text_name)
        binding.layoutAddress.hint = OrganizationTypeName + " " + getString(R.string.text_address_)
        binding.textViewPageCount.text = "1/4"
        binding.textViewContent.text = OrganizationTypeName + " " + getString(R.string.bulk_booking)
        binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
        binding.stateProgress.enableAnimationToCurrentState(true)
        binding.stateProgress.setMaxStateNumber(StateProgressBar.StateNumber.FOUR)





        binding.editTextMobileNo.addListener(object : GoEditTextListener {
            override fun onUpdate() {
                Log.e("mobile_num5:::","pasted")
            }
        })




        binding.editTextMobileNo.addTextChangedListener(object : TextWatcher {
            var lastChar: String? = null

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                Log.e("mobile_num1:::",s.toString())

                val text: String = s.toString()
                val textLength: Int = s!!.length
                if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" ")) return
                if (textLength == 1) {
                    if (!text.contains("(")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "(").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 5) {
                    if (!text.contains(")")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, ")").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 6) {
                    binding.editTextMobileNo.setText(
                        StringBuilder(text).insert(text.length - 1, " ").toString()
                    )
                    binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                } else if (textLength == 10) {
                    if (!text.contains("-")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 15) {
                    if (text.contains("-")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 18) {
                    if (text.contains("-")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

                Log.e("mobile_num2:::",s.toString())

                    val text: String = s.toString()
                    val textLength: Int = s!!.length
                    if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" ")) return
                    if (textLength == 1) {
                        if (!text.contains("(")) {
                            binding.editTextMobileNo.setText(
                                StringBuilder(text).insert(text.length - 1, "(").toString()
                            )
                            binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                        }
                    } else if (textLength == 5) {
                        if (!text.contains(")")) {
                            binding.editTextMobileNo.setText(
                                StringBuilder(text).insert(text.length - 1, ")").toString()
                            )
                            binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                        }
                    } else if (textLength == 6) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, " ").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    } else if (textLength == 10) {
                        if (!text.contains("-")) {
                            binding.editTextMobileNo.setText(
                                StringBuilder(text).insert(text.length - 1, "-").toString()
                            )
                            binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                        }
                    } else if (textLength == 15) {
                        if (text.contains("-")) {
                            binding.editTextMobileNo.setText(
                                StringBuilder(text).insert(text.length - 1, "-").toString()
                            )
                            binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                        }
                    } else if (textLength == 18) {
                        if (text.contains("-")) {
                            binding.editTextMobileNo.setText(
                                StringBuilder(text).insert(text.length - 1, "-").toString()
                            )
                            binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                        }
                    }
                }

                    })



        binding.buttonNext.setOnClickListener {

            if (binding.stateProgress.currentStateNumber == 1) {



                 mName = binding.editTextSchoolName.text!!.toString()
                 mName = mName!!.trimStart()
                 mAddress = binding.edittextAddress.text!!.toString()
                 contactName = binding.edittextname.text!!.toString()
                 contactName = contactName!!.trimStart()
                 mobileNumber = binding.editTextMobileNo.text!!.toString()
                 email = binding.editTextEmail.text!!.trim().toString()
                 title = binding.spinnerTitle.selectedItem.toString()




                 if (mName!!.isNotEmpty()) {
                     if(mAddress!!.isNotEmpty())
                     {

                         if(contactName!!.isNotEmpty())
                         {

                             if(title!!.isNotEmpty() && title != "Select Title")
                             {

                                 if(mobileNumber!!.isNotEmpty() )
                                 {

                                     if(Utils.isValidPhoneNumber(mobileNumber!!.replace("[^a-zA-Z0-9]".toRegex(), "")))
                                     {
                                         if(email!!.isNotEmpty())
                                         {

                                             if(Utils.isValidEmail(email!!))
                                             {


                                                 if(orderBy==1)
                                                 {

                                                     binding.textViewPageCount.text = "2/4"
                                                 }

                                                 if(orderBy==2)
                                                 {

                                                     binding.textViewPageCount.text = "2/3"
                                                 }

                                                 binding.buttonPrevious.isVisible = true
                                                 binding.buttonNext.isVisible = true
                                                 binding.buttonNext.text=resources.getText(R.string.button_next)
                                                 binding.layoutBulkBookingFirstPage.isVisible = false
                                                 binding.layoutBulkBookingSecondPage.isVisible = true
                                                 binding.layoutBulkBookingThirdPage.isVisible = false
                                                 binding.layoutBulkBookingFourthPage.isVisible = false

                                                 binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                                                 binding.stateProgress.enableAnimationToCurrentState(true)

                                                 if(loadOneTimePeopleAlert) {
                                                     loadOneTimePeopleAlert=false
                                                     showPeopleListItem(mPeopleList)
                                                 }
                                                 else
                                                 {
                                                    /* if (mPeopleList.size > 0) {
                                                         alertDialogPeople.show()
                                                     }*/
                                                 }
                                             }
                                             else
                                             {
                                                 Utils.showToast(
                                                     this,
                                                     getString(R.string.text_enter_email_validation),
                                                     true
                                                 )
                                             }



                                         }
                                         else
                                         {
                                             Utils.showToast(
                                                 this,
                                                 getString(R.string.kindly_enter_your)+" "+OrganizationTypeName!!.lowercase()+" "+getString(R.string.email_new)+" "+getString(R.string.validation_address),
                                                 true
                                             )
                                         }
                                     }

                                     else
                                     {
                                         Utils.showToast(
                                             this,
                                             getString(R.string.toast_valid_mobile),
                                             true
                                         )
                                     }



                                 }
                                 else
                                 {
                                     Utils.showToast(
                                         this,
                                         getString(R.string.enter_your)+" "+getString(R.string.mobile_number_new),
                                         true
                                     )
                                 }


                             }
                             else
                             {
                                 Utils.showToast(
                                     this,
                                     getString(R.string.select_your)+" "+getString(R.string.title_new),
                                     true
                                 )
                             }



                         }
                         else
                         {
                             Utils.showToast(
                                 this,
                                 getString(R.string.enter_your)+" "+getString(R.string.contact_new)+" "+getString(R.string.validation_name),
                                 true
                             )
                         }


                     }
                     else
                     {
                         Utils.showToast(
                             this,
                             getString(R.string.select_your)+" "+OrganizationTypeName!!.lowercase()+" "+getString(R.string.validation_address),
                             true
                         )
                     }

                 }
                 else
                 {
                     Utils.showToast(
                         this,
                         getString(R.string.enter_your)+" "+OrganizationTypeName!!.lowercase()+" "+getString(R.string.validation_name),
                         true
                     )
                 }


               /* if(orderBy==1)
                {

                    binding.textViewPageCount.text = "2/4"
                }

                if(orderBy==2)
                {

                    binding.textViewPageCount.text = "2/3"
                }

                binding.buttonPrevious.isVisible = true
                binding.buttonNext.isVisible = true
                binding.buttonNext.text=resources.getText(R.string.button_next)
                binding.layoutBulkBookingFirstPage.isVisible = false
                binding.layoutBulkBookingSecondPage.isVisible = true
                binding.layoutBulkBookingThirdPage.isVisible = false
                binding.layoutBulkBookingFourthPage.isVisible = false

                binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                binding.stateProgress.enableAnimationToCurrentState(true)

                if(loadOneTimePeopleAlert) {
                    loadOneTimePeopleAlert=false
                    showPeopleListItem(mPeopleList)
                }
                else
                {
                    if (mPeopleList.size > 0) {
                        alertDialogPeople.show()
                    }
                }
*/



            } else if (binding.stateProgress.currentStateNumber == 2) {

                peopleValidationAlert=false

                if(mPeopleSelectedValue.size>0)
                    mPeopleSelectedValue.clear()



                for (i in 0 until mPeopleList.size) {
                  //  println("edittextValues_people1::" + Gson().toJson(mPeopleList[i]))

                    if(mPeopleList[i].isSelected) {
                        mPeopleSelectedValue.add(mPeopleList[i])
                    }


                }

                  for (i in 0 until mPeopleSelectedValue.size) {
                      //  println("edittextValues_people2" + Gson().toJson(mPeopleSelectedValue[i]))
                        if(mPeopleSelectedValue[i].isSelected && mPeopleSelectedValue[i].value==0)
                        {
                            peopleValidationAlert=true
                            break

                        }

                    }



                if(mPeopleSelectedValue.size==0)
                {

                    Utils.showToast(
                        this,
                        getString(R.string.kindly_select_people),
                        true
                    )
                }
                else
                {
                    if(peopleValidationAlert)
                    {
                        Utils.showToast(
                            this,
                            getString(R.string.kindly_enter_the_values),
                            true
                        )
                    }
                    else
                    {


                        if(mPeopleSelectedValueForApi.size>0)
                        {
                            mPeopleSelectedValueForApi.clear()
                        }


                        for (j in 0 until mPeopleSelectedValue.size)
                        {
                          //  println("edittextValues_people3" + Gson().toJson(mPeopleSelectedValue[j]))


                            val apiObject= PersonList()
                            apiObject.id=mPeopleSelectedValue[j].id
                            apiObject.orderBy=mPeopleSelectedValue[j].orderBy
                            apiObject.titleName=mPeopleSelectedValue[j].titleName
                            apiObject.value=mPeopleSelectedValue[j].value

                            mPeopleSelectedValueForApi.add(apiObject)



                        }

                        /*for (k in 0 until mPeopleSelectedValueForApi.size)
                        {
                            println("edittextValues_people4" + Gson().toJson(mPeopleSelectedValueForApi[k]))
                        }*/




                        binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                        binding.stateProgress.enableAnimationToCurrentState(true)

                        if(orderBy==1)
                        {

                            binding.textViewPageCount.text = "3/4"
                            binding.buttonPrevious.isVisible = true
                            binding.buttonNext.isVisible = true
                            binding.buttonNext.text=resources.getText(R.string.button_next)
                            binding.layoutBulkBookingFirstPage.isVisible=false
                            binding.layoutBulkBookingSecondPage.isVisible=false
                            binding.layoutBulkBookingThirdPage.isVisible=true
                            binding.layoutBulkBookingFourthPage.isVisible=false

                            if(loadOneTimeSurfaceAlert) {
                                loadOneTimeSurfaceAlert=false
                                showSurfaceListItem(mSurfaceList)
                            }
                            else
                            {
                               /* if (mSurfaceList.size > 0) {
                                    alertDialogSurface.show()
                                }*/
                            }
                        }

                        if(orderBy==2)
                        {

                            var totalPeopleCount=0

                            for(k in 0 until mPeopleSelectedValueForApi.size)
                            {
                                totalPeopleCount += mPeopleSelectedValueForApi[k].value!!
                            }

                          //  println("totalPeopleCount:::"+ totalPeopleCount)




                            binding.textViewPageCount.text = "3/3"
                            binding.buttonPrevious.isVisible = false
                            binding.buttonNext.isVisible = true

                            if(paymentType==1) {
                                binding.buttonNext.text =
                                    resources.getText(R.string.text_proceed_to_pay)
                            }
                            else {
                                binding.buttonNext.text =
                                    resources.getString(R.string.text_place_order)
                            }

                            binding.layoutBulkBookingFirstPage.isVisible=false
                            binding.layoutBulkBookingSecondPage.isVisible=false
                            binding.layoutBulkBookingThirdPage.isVisible=false
                            binding.layoutBulkBookingFourthPage.isVisible=true

                           
                           
                            binding.textviewPeopleDescription.text=resources.getString(R.string.text_cost_per_people)+" "+NumberFormat.getCurrencyInstance(
                                Locale("en", "US")
                            ).format(price)+")"

                            val finalAmount : Float = price*totalPeopleCount

                            binding.totalTestAmount.text= decimalWithTwoDigit.format(totalPeopleCount)

                           /* binding.textViewPeopleTestAmount.text="$"+decimalWithTwoDigit.format(finalAmount)+"/Month"
                            binding.textViewMonthlyTestAmount.text="$"+decimalWithTwoDigit.format(finalAmount)*/

                           // println("people_amount"+""+decimalWithTwoDigit.format(finalAmount))

                            binding.textViewPeopleTestAmount.text= NumberFormat.getCurrencyInstance(
                                Locale("en", "US")
                            ).format(finalAmount)+"/Month"
                            binding.textViewMonthlyTestAmount.text= NumberFormat.getCurrencyInstance(
                                Locale("en", "US")
                            ).format(finalAmount)


                            binding.textViewAmount.text=decimalWithTwoDigit.format(totalPeopleCount)

                            binding.textViewSurfaceTest.isVisible=false
                            binding.textViewSurfaceDescription.isVisible=false
                            binding.textViewSurfaceTestAmount.isVisible=false
                            binding.llSurfaceLay.isVisible=false


                            binding.layoutPaymentSelection.isVisible=if(isPayment) false else true

                           // binding.layoutPaymentSelection.isVisible=true


                          //  setContractList(finalAmount.toFloat())



                            if(mContractList.size>0) {
                                binding.contractRecyclerView.isVisible=true
                                contractListAdapter = ContractListAdapter(
                                    this, mContractList, finalAmount, this
                                )
                                binding.contractRecyclerView.adapter = contractListAdapter
 }
                            else
                            {
                                binding.contractRecyclerView.isVisible=false
                            }







                            orderSummary= ArrayList()


                            val data1 = OrderSummaryNew()
                            data1.key = resources.getString(R.string.total_cost_for_people_tested)
                            data1.value = finalAmount
                            data1.id = "1"
                            orderSummary.add(data1)


                            val data2 = OrderSummaryNew()
                            data2.key = resources.getString(R.string.total_cost_for_surfaces_tested)
                            data2.value = 0f
                            data2.id = "2"
                            orderSummary.add(data2)



                            val data3 = OrderSummaryNew()
                            data3.key = resources.getString(R.string.total_cost_for_monthly)
                            data3.value = finalAmount
                            data3.id = "3"
                            orderSummary.add(data3)


                            val data4 = OrderSummaryNew()
                            data4.key = resources.getString(R.string.total_monthly_tests_for_people)
                            data4.value = totalPeopleCount
                            data4.id = "4"
                            orderSummary.add(data4)



                            val data5 = OrderSummaryNew()
                            data5.key = resources.getString(R.string.total_monthly_tests_for_surfaces)
                            data5.value = 0
                            data5.id = "5"
                            orderSummary.add(data5)

                            val data6 = OrderSummaryNew()
                            data6.key =  resources.getString(R.string.total_monthly_tests_ordered)
                            data6.value = totalPeopleCount
                            data6.id = "6"
                            orderSummary.add(data6)


                            mContractListForApi= ContractListNew()

                            if(mContractList.size>0)
                            {
                                for (i in 0 until mContractList.size) {


                                    if(mContractList[i].id==3)
                                    {
                                        mContractListForApi.description = mContractList[i].description
                                        mContractListForApi.discount = mContractList[i].discount
                                        mContractListForApi.id = mContractList[i].id
                                        mContractListForApi.titleName = mContractList[i].titleName
                                        contractMonth= mContractList[i].month!!
                                    }


                                }



                            }
                            else
                            {
                                if(mContractList.size==0)
                                {
                                    mContractListForApi.description ="Total cost for 1 month"
                                    mContractListForApi.discount = "Offer not applied"
                                    mContractListForApi.id = 3
                                    mContractListForApi.titleName = "None"
                                    contractMonth= 1
                                }
                            }






                            paymentApiReqValue=ArrayList()

                            val payment = PaymentNew()
                            payment.amount =finalAmount.toFloat()
                            payment.testCount = 1
                            payment.paymentName =testName

                            paymentApiReqValue.add(payment)








                        }






                    }

                }










            }


            else if(binding.stateProgress.currentStateNumber == 3)
            {






               if(orderBy==1)
               {
                   surfaceValidationAlert=false

                   if(mSurfaceSelectedValue.size>0)
                       mSurfaceSelectedValue.clear()



                   for (i in 0 until mSurfaceList.size) {
                     //  println("edittextValues_surface1::" + Gson().toJson(mSurfaceList[i]))

                       if(mSurfaceList[i].isSelected) {
                           mSurfaceSelectedValue.add(mSurfaceList[i])
                       }


                   }

                   for (i in 0 until mSurfaceSelectedValue.size) {
                     //  println("edittextValues_surface2::" + Gson().toJson(mSurfaceSelectedValue[i]))
                       if(mSurfaceSelectedValue[i].isSelected && mSurfaceSelectedValue[i].value==0)
                       {
                           surfaceValidationAlert=true
                           break

                       }

                   }



                   if(mSurfaceSelectedValue.size==0)
                   {


                       Utils.showToast(
                           this,
                           getString(R.string.kindly_select_surface),
                           true
                       )
                   }
                   else
                   {
                       if(surfaceValidationAlert)
                       {
                           Utils.showToast(
                               this,
                               getString(R.string.kindly_enter_the_values),
                               true
                           )
                       }
                       else
                       {


                           if(mSurfaceSelectedValueForApi.size>0)
                           {
                               mSurfaceSelectedValueForApi.clear()
                           }




                           for (j in 0 until mSurfaceSelectedValue.size)
                           {
                              // println("edittextValues_surface3" + Gson().toJson(mSurfaceSelectedValue[j]))


                               val apiObject= Surface()
                               apiObject.id=mSurfaceSelectedValue[j].id
                               apiObject.orderBy=mSurfaceSelectedValue[j].orderBy
                               apiObject.titleName=mSurfaceSelectedValue[j].titleName
                               apiObject.value=mSurfaceSelectedValue[j].value

                               mSurfaceSelectedValueForApi.add(apiObject)



                           }

                          /* for (k in 0 until mSurfaceSelectedValueForApi.size)
                           {
                               println("edittextValues_surface4" + Gson().toJson(mSurfaceSelectedValueForApi[k]))
                           }*/


                           binding.buttonPrevious.isVisible = false
                           binding.buttonNext.isVisible = true
                           binding.buttonNext.text=resources.getText(R.string.button_next)
                           binding.layoutBulkBookingFirstPage.isVisible=false
                           binding.layoutBulkBookingSecondPage.isVisible=false
                           binding.layoutBulkBookingThirdPage.isVisible=false
                           binding.layoutBulkBookingFourthPage.isVisible=true


                           if(paymentType==1) {
                               binding.buttonNext.text =
                                   resources.getText(R.string.text_proceed_to_pay)
                           }
                           else {
                               binding.buttonNext.text =
                                   resources.getString(R.string.text_place_order)
                           }

                           binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
                           binding.stateProgress.enableAnimationToCurrentState(true)

                           binding.textViewPageCount.text = "4/4"


                           var totalPeopleCount=0f
                           var totalSurfaceCount=0f

                           for(k in 0 until mPeopleSelectedValueForApi.size)
                           {
                               totalPeopleCount += mPeopleSelectedValueForApi[k].value!!
                           }

                         //  println("totalPeopleCount2:::"+ totalPeopleCount)

                           for(l in 0 until mSurfaceSelectedValueForApi.size)
                           {
                               totalSurfaceCount += mSurfaceSelectedValueForApi[l].value!!
                           }

                          // println("totalSurfaceCount:::"+ totalSurfaceCount)



                           binding.textviewPeopleDescription.text=resources.getString(R.string.text_cost_per_people)+" "+NumberFormat.getCurrencyInstance(
                               Locale("en", "US")
                           ).format(price)+")"

                           binding.textViewSurfaceDescription.text=resources.getString(R.string.text_cost_surface)+" "+NumberFormat.getCurrencyInstance(
                               Locale("en", "US")
                           ).format(surfacePrice)+")"



                           val finalPeopleAmount: Float =price*totalPeopleCount
                           val finalSurfaceAmount : Float =surfacePrice*totalSurfaceCount
                           val totalAmount=finalPeopleAmount+finalSurfaceAmount

                           val totalTestAmount = totalPeopleCount + totalSurfaceCount

                          /* binding.textViewPeopleTestAmount.text="$"+decimalWithTwoDigit.format(finalPeopleAmount)+"/Month"
                           binding.textViewSurfaceTestAmount.text="$"+decimalWithTwoDigit.format(finalSurfaceAmount)+"/Month"
                            binding.textViewMonthlyTestAmount.text="$"+decimalWithTwoDigit.format(totalAmount)
                           */


                           binding.textViewPeopleTestAmount.text= NumberFormat.getCurrencyInstance(
                               Locale("en", "US")
                           ).format(finalPeopleAmount)+"/Month"
                           binding.textViewSurfaceTestAmount.text=NumberFormat.getCurrencyInstance(
                               Locale("en", "US")
                           ).format(finalSurfaceAmount)+"/Month"


                           binding.textViewMonthlyTestAmount.text=NumberFormat.getCurrencyInstance(
                               Locale("en", "US")
                           ).format(totalAmount)



                           binding.totalTestAmount.text= decimalWithTwoDigit.format(totalTestAmount)
                           binding.textViewAmount.text=decimalWithTwoDigit.format(totalPeopleCount)
                           binding.textViewAmount1.text=decimalWithTwoDigit.format(totalSurfaceCount)

                           binding.layoutPaymentSelection.isVisible=if(isPayment) false else true

                          // binding.layoutPaymentSelection.isVisible=true

                          // setContractList(totalAmount.toFloat())

                           contractListAdapter = ContractListAdapter(
                               this, mContractList, totalAmount.toFloat(),this)
                           binding.contractRecyclerView.adapter = contractListAdapter



                           binding.textViewSurfaceTest.isVisible=true
                           binding.textViewSurfaceDescription.isVisible=true
                           binding.textViewSurfaceTestAmount.isVisible=true
                           binding.llSurfaceLay.isVisible=true



                           orderSummary= ArrayList()




                           val data1 = OrderSummaryNew()
                           data1.key = resources.getString(R.string.total_cost_for_people_tested)
                           data1.value = finalPeopleAmount
                           data1.id = "1"
                           orderSummary.add(data1)


                           val data2 = OrderSummaryNew()
                           data2.key = resources.getString(R.string.total_cost_for_surfaces_tested)
                           data2.value = finalSurfaceAmount
                           data2.id = "2"
                           orderSummary.add(data2)



                           val data3 = OrderSummaryNew()
                           data3.key = resources.getString(R.string.total_cost_for_monthly)
                           data3.value = totalAmount
                           data3.id = "3"
                           orderSummary.add(data3)


                           val data4 = OrderSummaryNew()
                           data4.key = resources.getString(R.string.total_monthly_tests_for_people)
                           data4.value = totalPeopleCount.toInt()
                           data4.id = "4"
                           orderSummary.add(data4)



                           val data5 = OrderSummaryNew()
                           data5.key = resources.getString(R.string.total_monthly_tests_for_surfaces)
                           data5.value = totalSurfaceCount.toInt()
                           data5.id = "5"
                           orderSummary.add(data5)

                           val data6 = OrderSummaryNew()
                           data6.key = resources.getString(R.string.total_monthly_tests_ordered)
                           data6.value = totalTestAmount.toInt()
                           data6.id = "6"
                           orderSummary.add(data6)


                           mContractListForApi= ContractListNew()

                           if(mContractList.size>0)
                           {
                               for (i in 0 until mContractList.size) {


                                   if(mContractList[i].id==3)
                                   {
                                       mContractListForApi.description = mContractList[i].description
                                       mContractListForApi.discount = mContractList[i].discount
                                       mContractListForApi.id = mContractList[i].id
                                       mContractListForApi.titleName = mContractList[i].titleName
                                       contractMonth= mContractList[i].month!!
                                   }


                               }



                           }
                           else
                           {
                               if(mContractList.size==0)
                               {
                                   mContractListForApi.description ="Total cost for 1 month"
                                   mContractListForApi.discount = "Offer not applied"
                                   mContractListForApi.id = 3
                                   mContractListForApi.titleName = "None"
                                   contractMonth= 1
                               }
                           }


                           paymentApiReqValue=ArrayList()

                           val payment = PaymentNew()
                           payment.amount =totalAmount.toFloat()
                           payment.testCount = 1
                           payment.paymentName =testName

                           paymentApiReqValue.add(payment)



                       }

                   }

               }

                else if(orderBy==2)
                {





                        if(binding.checkBoxCondition.isChecked)
                        {

                            val currentTimestamp = System.currentTimeMillis() / 1000

                            val req = BulkOrderNewRequest()

                            req.setAddress(mAddress)
                            req.setContactname(contactName)
                            req.setCurrentlatlong(mPrefs!!.latlng)
                            req.setEmail(email)
                            req.setImagePath("")
                            req.setIosimagepath("")
                            req.setLatLong(latlng)
                            req.setLocality(locality)
                            req.setLocation(locality)

                            req.setOrganizationId(mSelectedOriginazationID)
                            req.setOrganizationType(OrganizationTypeName)

                            req.setPhoneNo(mobileNumber!!.replace("[^a-zA-Z0-9]".toRegex(), ""))
                            req.setPrimaryName(mName)
                            req.setState(mState)
                            req.setOrganizationTitle(title)
                            req.setTestCategoryId(testCategoryID)
                            req.setTestTypeID(testTypeID)
                            req.setTitle(testName)

                            req.setPeopleList(mPeopleSelectedValueForApi)
                            req.setSurfaceList(ArrayList())

                            req.setOrderDate(currentTimestamp.toInt())


                            req.setUserFCMToken(mPrefs!!.fcmToken)

                            req.setPaymentType(paymentType)

                            req.setBookingType(paymentType)

                            req.setContractList(mContractListForApi)

                            req.setContractMonth(contractMonth)

                            req.setOrderSummary(orderSummary)

                            req.setPayment(paymentApiReqValue)



                            bulkBookingApiCall(req)


                          //  println("people_List::" + Gson().toJson(req))
                        }
                        else
                        {
                            Utils.showToast(this, getString(R.string.toast_bulk_booking_terms), true)
                        }










                }



               }


            else if(binding.stateProgress.currentStateNumber == 4)
            {




                    if(binding.checkBoxCondition.isChecked)
                    {
                        val currentTimestamp = System.currentTimeMillis() / 1000

                        val req = BulkOrderNewRequest()

                        req.setAddress(mAddress)
                        req.setContactname(contactName)
                        req.setCurrentlatlong(mPrefs!!.latlng)
                        req.setEmail(email)
                        req.setImagePath("")
                        req.setIosimagepath("")
                        req.setLatLong(latlng)
                        req.setLocality(locality)
                        req.setLocation(locality)




                        req.setOrganizationId(mSelectedOriginazationID)
                        req.setOrganizationType(OrganizationTypeName)

                        req.setPhoneNo(mobileNumber!!.replace("[^a-zA-Z0-9]".toRegex(), ""))
                        req.setPrimaryName(mName)
                        req.setState(mState)

                        req.setOrganizationTitle(title)

                        req.setTestCategoryId(testCategoryID)
                        req.setTestTypeID(testTypeID)
                        req.setTitle(testName)

                        req.setPeopleList(mPeopleSelectedValueForApi)
                        req.setSurfaceList(mSurfaceSelectedValueForApi)


                        req.setUserFCMToken(mPrefs!!.fcmToken)

                        req.setPaymentType(paymentType)
                        req.setBookingType(paymentType)

                        req.setContractList(mContractListForApi)

                        req.setContractMonth(contractMonth)

                        req.setOrderDate(currentTimestamp.toInt())

                        req.setOrderSummary(orderSummary)

                        req.setPayment(paymentApiReqValue)


                        bulkBookingApiCall(req)

                      //  println("people_List::" + Gson().toJson(req))
                    }
                    else
                    {
                        Utils.showToast(this, getString(R.string.toast_bulk_booking_terms), true)
                    }





            }





        }


        binding.buttonPrevious.setOnClickListener {

           handlePreviousState()

        }





        binding.ivPeopleShowAll.setOnClickListener {

            if (mPeopleList.size > 0) {
                alertDialogPeople.show()
            }


        }

        binding.ivSurfaceShowAll.setOnClickListener {

            if (mSurfaceList.size > 0) {
                alertDialogSurface.show()
            }


        }




        binding.layoutSpitTest.setOnClickListener {


            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(30, 50, 30, 0)
            binding.stateProgress.setLayoutParams(params)




            if( mTestType.size>1) {
                testTypeID = mTestType.get(1).testTypeId
                testCategoryID = mTestType.get(1).testCategoryId
                orderBy = mTestType.get(1).orderBy
                testName=mTestType.get(1).categoryName
                price= mTestType.get(1).price!!.toFloat()
                surfacePrice= mTestType.get(1).surfacePrice!!.toFloat()
            }


          //  println("orderid_spit:::"+orderBy)


            if(mSurfaceSelectedValueForApi.size>0)
                mSurfaceSelectedValueForApi.clear()


            binding.layoutSpitTest.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_right_select
                )
            )
            binding.layoutSwabTest.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_layout_left_select
                )
            )
            binding.textViewSpitTest.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textViewSwabTest.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )

            binding.stateProgress.setMaxStateNumber(StateProgressBar.StateNumber.THREE)
            binding.stateProgress.enableAnimationToCurrentState(true)
            binding.textViewPageCount.text = "1/3"


        }
        binding.layoutSwabTest.setOnClickListener {



            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 50, 0, 0)
            binding.stateProgress.setLayoutParams(params)




            if( mTestType.size>0) {
                testTypeID = mTestType.get(0).testTypeId
                testCategoryID = mTestType.get(0).testCategoryId
                orderBy = mTestType.get(0).orderBy
                testName=mTestType.get(0).categoryName
                price= mTestType.get(0).price!!.toFloat()
                surfacePrice= mTestType.get(0).surfacePrice!!.toFloat()
            }

          //  println("orderid_swab:::"+orderBy)



            if(mTestType.size==1)
            {
                binding.layoutSwabTest.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_button_border
                    )
                )

                binding.textViewSwabTest.setTextColor(ContextCompat.getColor(this, R.color.white))

            }

            else
            {

                binding.layoutSpitTest.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_layout_right_select
                    )
                )
                binding.textViewSpitTest.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                binding.layoutSwabTest.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_left_select
                    )
                )

                binding.textViewSwabTest.setTextColor(ContextCompat.getColor(this, R.color.white))

            }





            if(orderBy==1)
            {
                binding.stateProgress.setMaxStateNumber(StateProgressBar.StateNumber.FOUR)
                binding.stateProgress.enableAnimationToCurrentState(true)
                binding.textViewPageCount.text = "1/4"
            }

            if(orderBy==2)
            {
                binding.stateProgress.setMaxStateNumber(StateProgressBar.StateNumber.THREE)
                binding.stateProgress.enableAnimationToCurrentState(true)
                binding.textViewPageCount.text = "1/3"
            }



        }



        binding.layoutOtherPayment.setOnClickListener {



            binding.buttonNext.text=resources.getString(R.string.text_place_order)

            paymentType=0

            binding.layoutOtherPayment.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_right_select
                )
            )
            binding.layoutStripePayment.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_layout_left_select
                )
            )
            binding.textViewOtherPayment.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textViewStripe.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )

            /*binding.textViewOtherPayment.text=resources.getString(R.string.other_payments)
            binding.textViewStripe.text=resources.getString(R.string.online_payment)*/




        }
        binding.layoutStripePayment.setOnClickListener {

            paymentType=1


            binding.buttonNext.text=resources.getString(R.string.text_proceed_to_pay)

            binding.layoutStripePayment.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_left_select
                    )
                )

            binding.textViewStripe.setTextColor(ContextCompat.getColor(this, R.color.white))



                binding.layoutOtherPayment.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_layout_right_select
                    )
                )

            binding.textViewOtherPayment.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )



           /*  binding.textViewOtherPayment.text=resources.getString(R.string.other_payments)
            binding.textViewStripe.text=resources.getString(R.string.online_payment)*/



            }










    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disablePasteOption(editTextMobileNo: TextInputEditText) {
        editTextMobileNo.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {

                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {

            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {

                when (item!!.itemId) {
                    android.R.id.copy -> {

                        return true
                    }
                    android.R.id.cut ->
                        return true
                    android.R.id.paste ->
                    {
                        editTextMobileNo.text!!.clear()
                        return true
                    }
                    else -> {

                    }
                }

                return false
            }

        }
        editTextMobileNo.isLongClickable = false
        editTextMobileNo.setTextIsSelectable(false)

       /* editTextMobileNo.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP === event.action) {
                editTextMobileNo.text!!.clear()
            }
            true
        }*/


    }





    private fun setValues() {
        mContractListFor= OrganizationTitleNewResponse.ContractList()




        mContractListFor.description = "Total cost for testing per month with 1 year term discount applied"
        mContractListFor.discount = "25% offer"
        mContractListFor.id = 1
        mContractListFor.titleName ="1 Year Contract"
        mContractListFor.month= 12

           mContractList.add(mContractListFor)


        mContractListFor= OrganizationTitleNewResponse.ContractList()


        mContractListFor.description = "Total cost for testing per month with 2 year term discount applied"
        mContractListFor.discount = "50% offer"
        mContractListFor.id = 2
        mContractListFor.titleName ="2 Year Contract"
        mContractListFor.month= 24

        mContractList.add(mContractListFor)



        mContractListFor= OrganizationTitleNewResponse.ContractList()



        mContractListFor.description = "Total cost for 1 month"
        mContractListFor.discount = "Offer not applied"
        mContractListFor.id = 3
        mContractListFor.titleName ="None"
        mContractListFor.month= 1

        mContractList.add(mContractListFor)


      //  println("array_check:::"+Gson().toJson(mContractList))


    }

    private fun bulkBookingApiCall(req: BulkOrderNewRequest) {

        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(req)


        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(json) as JsonObject


        if (Utils.isNetworkAvailable(this)) {
            if (progressDialog == null) {
                progressDialog = Utils.getDialog(this)
            }
            progressDialog!!.show()
            viewModel.getOrderBooking(token!!, aJsonObject)



            viewModel.Response3!!.observe(this, {
                when (it) {
                    is Resource.Success -> {
                      //  viewModel.Response3!!.cancelCall()

                        //viewModel.Response3!!.call()
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {

                            Log.e("OMGGGG", Gson().toJson(req))


                            if(it.value.getValue()!!.data!!.bookingType==1)
                            {
                                val url: String = it.value.getValue()!!.data!!.link!!
                                val order_id: String =
                                    it.value.getValue()!!.data!!.orderID!!
                                Log.e("orderID", it.value.getValue()!!.data!!.orderID!!)
                                Utils.webViewData("bulk", url, order_id, this)
                            }
                            else
                            {

                                val descriptionToShow: String? = it.value.getValue()!!.data!!.confirmResponse!!.description



                                if (it.value.getValue()!!.data!!.confirmResponse!!.status == 0 ||
                                    it.value.getValue()!!.data!!.confirmResponse!!.status == 1 ||
                                    it.value.getValue()!!.data!!.confirmResponse!!.status == 2 ||
                                    it.value.getValue()!!.data!!.confirmResponse!!.status == 3)
                                    {


                                        intent = Intent(
                                        this@NewBulkBookingActivity,
                                        PaymentCompleteActivity::class.java
                                    )
                                    intent.putExtra(
                                        "Status",
                                        descriptionToShow
                                    )
                                    startActivity(intent)
                                    finish()

                                }
                                else if (it.value.getValue()!!.data!!.confirmResponse!!.status == 4) {

                                    val emailContent :String= it.value.getValue()!!.data!!.confirmResponse!!.emailContent!!.toString()
                                    val orderID=it.value.getValue()!!.data!!.orderID!!
                                    val paymentStatus= it.value.getValue()!!.data!!.confirmResponse!!.status

                                    intent = Intent(
                                        this@NewBulkBookingActivity,
                                        ResendMailActivity::class.java
                                    )
                                    intent.putExtra("orderID", orderID)
                                    intent.putExtra("paymentStatus", paymentStatus)
                                    intent.putExtra("bookingType", "0")
                                    intent.putExtra("bulkbooking", "1")
                                    intent.putExtra("emailContent", emailContent)
                                    startActivity(intent)
                                    finish()
                                }



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
                            Utils.showToast(
                                this,
                                it.value.getValue()!!.message.toString(),
                                true
                            )
                        }

                    }

                    is Resource.GenericError -> {
                      //  Utils.showToast(this, it.error!!.message.toString(), true)
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

    }

    private fun setContractList(finalAmount: Float) {



        for (i in 0 until mContractList.size) {

            if (i == 0) {
                binding.textViewOffer.setText(mContractList[i].description)
                binding.radioButton1.setText(mContractList[i].discount)
                binding.textViewOneYrContractLabel.setText(
                    mContractList[i].titleName
                )


                val originalAmount = finalAmount * mContractList[i].month!!

                binding.textViewOfferAmount.setText("$"+originalAmount.toString())
                binding.textViewOfferAmount.setPaintFlags(binding.textViewOfferAmount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

                val parts: List<String> =  mContractList[i].discount!!.split("%")

               // println("discount:::"+parts[0])

                val discountAmount = (originalAmount * parts[0].toInt() ) / 100
                val payingAmount = originalAmount - discountAmount

                binding.textViewSaveAmount.setText("$"+payingAmount)


                val savingAmount = originalAmount - payingAmount

                binding.textViewSavingsTwentyFive.setText("Savings on this plan :"+" $"+savingAmount)


                paymentGatewayAmount1= (payingAmount/mContractList[i].month!!)




            } else if (i == 1) {
                binding.textViewOffer1.setText(mContractList[i].description)
                binding.radioButton2.setText(mContractList[i].discount)
                binding.textViewTwoYrContractLabel.setText(
                    mContractList[i].titleName
                )



                val originalAmount = finalAmount * mContractList[i].month!!

                binding.textViewOfferAmount1.setText("$"+originalAmount.toString())
                binding.textViewOfferAmount1.setPaintFlags(binding.textViewOfferAmount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

                val parts: List<String> =  mContractList[i].discount!!.split("%")

              //  println("discount:::"+parts[0])

                val discountAmount = (originalAmount * parts[0].toInt() ) / 100
                val payingAmount = originalAmount - discountAmount

                binding.textViewSaveAmount1.setText("$"+payingAmount)


                val savingAmount = originalAmount - payingAmount

                binding.textViewSavingsfifty.setText("Savings on this plan :"+" $"+savingAmount)

                paymentGatewayAmount2= (payingAmount/mContractList[i].month!!)


            } else if (i == 2) {
                binding.textViewOffer2.setText(mContractList[i].description)
                binding.radioButton3.setText(mContractList[i].discount)

                               binding.textViewOneMonthContractLabel.setText(
                                   mContractList[i].titleName
                )



                binding.textViewSaveAmount2.setText("$"+finalAmount.toString())
                paymentGatewayAmount3= finalAmount

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

    private fun handlePreviousState() {

        if(binding.stateProgress.currentStateNumber==1)
        {
            finish()
        }
        else if (binding.stateProgress.currentStateNumber == 2) {
            binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
            binding.stateProgress.enableAnimationToCurrentState(true)
            binding.layoutBulkBookingFirstPage.isVisible = true
            binding.layoutBulkBookingSecondPage.isVisible = false
            binding.layoutBulkBookingThirdPage.isVisible = false
            binding.layoutBulkBookingFourthPage.isVisible = false
            binding.buttonPrevious.isVisible = false
            binding.buttonNext.isVisible = true
            binding.buttonNext.text=resources.getText(R.string.button_next)

            if(orderBy==1) {
                binding.textViewPageCount.text = "1/4"
            }

            if(orderBy==2)
            {
                binding.textViewPageCount.text = "1/3"
            }



        } else if (binding.stateProgress.currentStateNumber == 3) {
            binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
            binding.stateProgress.enableAnimationToCurrentState(true)

            binding.layoutBulkBookingFirstPage.isVisible = false
            binding.layoutBulkBookingSecondPage.isVisible = true
            binding.layoutBulkBookingThirdPage.isVisible = false
            binding.layoutBulkBookingFourthPage.isVisible = false
            binding.buttonPrevious.isVisible = true
            binding.buttonNext.isVisible = true
            binding.buttonNext.text=resources.getText(R.string.button_next)


            if(orderBy==1) {
                binding.textViewPageCount.text = "2/4"

            }

            if(orderBy==2)
            {
                binding.textViewPageCount.text = "2/3"

            }


        } else if (binding.stateProgress.currentStateNumber == 4) {
            binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
            binding.stateProgress.enableAnimationToCurrentState(true)
            binding.layoutBulkBookingFirstPage.isVisible = false
            binding.layoutBulkBookingSecondPage.isVisible = false
            binding.layoutBulkBookingThirdPage.isVisible = true
            binding.layoutBulkBookingFourthPage.isVisible = false
            binding.buttonPrevious.isVisible = true
            binding.buttonNext.isVisible = true
            binding.buttonNext.text=resources.getText(R.string.button_next)

            binding.textViewPageCount.text = "3/4"


        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showPeopleListItem(
        mPeopleList: ArrayList<OrganizationTitleNewResponse.Person>) {
        val lp = WindowManager.LayoutParams()
        val window = alertDialogPeople.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialogPeople.setContentView(R.layout.alert_dialog_bulk_booking_people_list)
        recyclerViewPeopleList = alertDialogPeople.findViewById(R.id.recyclerViewPeopleList)
        peopleTick = alertDialogPeople.findViewById(R.id.btnTick)
        peopleClose = alertDialogPeople.findViewById(R.id.btnClose)

        alertDialogPeople.setCanceledOnTouchOutside(false)
        alertDialogPeople.setCancelable(false)


        bulkBookingPeopleListAdaptor = BulkBookingPeopleListAdapter(
            this,
            mPeopleList,
            this,
            this
        )
        recyclerViewPeopleList.adapter = bulkBookingPeopleListAdaptor


        peopleTick.setOnClickListener {

            alertDialogPeople.dismiss()


        }


        peopleClose.setOnClickListener {

            alertDialogPeople.dismiss()


        }


        var count = 0


        for(i in 0 until mPeopleList.size) {
            if(!mPeopleList[i].isSelected)
            {
                count++

            }
        }

        if(count==mPeopleList.size)
        {

            peopleClose.isVisible=true
            peopleTick.isVisible=false
        }
        else
        {

            peopleClose.isVisible=false
            peopleTick.isVisible=true
        }


        alertDialogPeople.show()




        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.TOP
        window.attributes = lp
    }




    @SuppressLint("NotifyDataSetChanged")
    private fun showSurfaceListItem(
        mSurfaceList: ArrayList<OrganizationTitleNewResponse.Surface>
    ) {
        val lp = WindowManager.LayoutParams()
        val window = alertDialogSurface.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialogSurface.setContentView(R.layout.alert_dialog_bulk_booking_surface_list)
        recyclerViewSurfaceList = alertDialogSurface.findViewById(R.id.recyclerViewSurfaceList)
        surfaceTick = alertDialogSurface.findViewById(R.id.btnTick)
        surfaceClose = alertDialogSurface.findViewById(R.id.btnClose)
        alertDialogSurface.setCanceledOnTouchOutside(false)
        alertDialogSurface.setCancelable(false)


        bulkBookingSurfaceListAdaptor = BulkBookingSurfaceListAdapter(
            this,
            mSurfaceList,
            this,
            this
        )
        recyclerViewSurfaceList.adapter = bulkBookingSurfaceListAdaptor


        surfaceTick.setOnClickListener {

            alertDialogSurface.dismiss()


        }

        surfaceClose.setOnClickListener {

            alertDialogSurface.dismiss()


        }


        var count = 0


        for(i in 0 until mSurfaceList.size) {
            if(!mSurfaceList[i].isSelected)
            {
                count++

            }
        }

        if(count==mSurfaceList.size)
        {
            surfaceClose.isVisible=true
            surfaceTick.isVisible=false
        }
        else
        {
            surfaceClose.isVisible=false
            surfaceTick.isVisible=true
        }


        alertDialogSurface.show()

        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.TOP
        window.attributes = lp
    }





    private fun emailAlertPopup(mID: String) {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        alertDialog.setCancelable(false)
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_email_validation)
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        val editTextEmail: EditText = alertDialog.findViewById(R.id.editTextEmail)
        val imgClearEmail: ImageView = alertDialog.findViewById(R.id.email_clear)

        val textViewTitle: AppCompatTextView = alertDialog.findViewById(R.id.textViewTitle)

         textViewTitle.text=resources.getString(R.string.text_mail)+" "+OrganizationTypeName!!.lowercase()+" "+resources.getString(R.string.text_email_address)

        if (emailResponse!!.isNotEmpty()) {
            imgClearEmail.visibility = View.VISIBLE
        } else {
            imgClearEmail.visibility = View.GONE
        }
        imgClearEmail.setOnClickListener {
            editTextEmail.setText("")
            editTextEmail.requestFocus()
            imgClearEmail.visibility = View.GONE
        }
        editTextEmail.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    imgClearEmail.visibility = View.GONE
                }
                return false
            }
        })



        editTextEmail.setText(emailResponse)
        posTv.setOnClickListener({
            val mEmail = editTextEmail.text.trim().toString()
            if (!mEmail.equals("")) {
                if (Utils.isValidEmail(mEmail)) {
                    callAPI(alertDialog, mEmail, mID)
                  //  hideKeyboard(this@NewBulkBookingActivity)
                    alertDialog.dismiss()
                } else {
                    editTextEmail.setError(resources.getString(R.string.text_enter_email_validation))
                }
            } else {
                editTextEmail.setError(resources.getString(R.string.text_enter_email))
            }
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.text = resources.getString(R.string.button_cancel)
        negTv.setOnClickListener {
            if (emailResponse!!.isNotEmpty()) {
                alertDialog.dismiss()
            } else {
                alertDialog.dismiss()
                finish()
            }

        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun callAPI(alertDialog: Dialog, email: String, mID: String) {


        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()


            val data = VerifyEmailRequest()
            data.setEmail(email)
            data.setOrganizationId(mID.toInt())
            viewModel.getUserDetails(token!!, data)

            viewModel.Response2!!.observe(this, {
                when (it) {
                    is Resource.Success -> {

                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            alertDialog.dismiss()
                            mobileResponse =
                                it.value.getValue()!!.data!!.getMobileNumber().toString()
                                    .replace("(-+.^:,)", "")
                            emailResponse = it.value.getValue()!!.data!!.getEmail()
                            binding.editTextEmail.setText(emailResponse)
                            binding.editTextSchoolName.setText("")
                            binding.edittextAddress.setText("")
                            binding.edittextname.setText("")
                            binding.editTextMobileNo.setText("")
                            if (it.value.getValue()!!.data!!.getMobileNumber()!!.equals("")) {
                                binding.editTextSchoolName.isEnabled = true
                                binding.edittextAddress.isEnabled = true
                                binding.edittextname.isEnabled = true
                                binding.editTextMobileNo.isEnabled = true
                            } else {

                                binding.edittextname.setText(it.value.getValue()!!.data!!.getFirstName())
                                binding.edittextAddress.setText(
                                    it.value.getValue()!!.data!!.getAddress().toString()
                                )
                                getLocationDetails(
                                    it.value.getValue()!!.data!!.getAddress().toString()
                                )
                                binding.editTextMobileNo.setText(
                                    Utils.USFormatMobile(
                                        mobileResponse!!.replace(
                                            Regex("\\D"),
                                            ""
                                        ).toLong()
                                    )
                                )
                                binding.editTextSchoolName.setText(it.value.getValue()!!.data!!.getOrganizationName())

                                binding.editTextSchoolName.isEnabled = false
                                binding.edittextAddress.isEnabled = false
                                binding.edittextname.isEnabled = false
                                binding.editTextMobileNo.isEnabled = false


                            }

                          /*  //newly added
                            if(it.value.getValue()!!.data!!.getTestType().equals("1"))
                                {
                                    binding.layoutSwabTest.performClick()
                                }
                            else
                            {
                                binding.layoutSpitTest.performClick()
                            }*/

                        }


                    }

                    is Resource.GenericError -> {
                        alertDialog.show()
                        Utils.showToast(this, it.error!!.message!!, true)
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
        }

        else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }


    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View = activity.getCurrentFocus()!!
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getAPICall(data: Int?) {

        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()


            val request = OrganizationTitleRequest()
            request.organizationId = data

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(request)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject
            Log.e("aJsonObject", aJsonObject.toString())
            viewModel.getTitle(token!!, aJsonObject)

            viewModel.Response1!!.observe(
                this@NewBulkBookingActivity,
                androidx.lifecycle.Observer {
                    when (it) {
                        is Resource.Success -> {
                            if (progressDoalog != null && progressDoalog!!.isShowing) {
                                progressDoalog!!.dismiss()
                            }
                            if (it.value.getStatusCode() == 200) {
                                mTitle.clear()
                                mTitle.add(0, "Select Title")

                                for (i in 0 until it.value.getValue()!!.data!!.titleList!!.size) {
                                    val title: String? =
                                        it.value.getValue()!!.data!!.titleList!![i].titleName
                                    mTitle.add(title)

                                }

                                for (i in 0 until it.value.getValue()!!.data!!.people!!.size) {
                                    mPeopleList.add(it.value.getValue()!!.data!!.people!![i])
                                }

                                for (i in 0 until it.value.getValue()!!.data!!.surface!!.size) {
                                    mSurfaceList.add(it.value.getValue()!!.data!!.surface!![i])
                                }


                                //newly hided
                                for (i in 0 until it.value.getValue()!!.data!!.contractLists!!.size) {
                                    mContractList.add(it.value.getValue()!!.data!!.contractLists!![i])
                                }


                                if (mTitle.size > 0) {
                                    val adapter = ArrayAdapter(
                                        this@NewBulkBookingActivity,
                                        android.R.layout.simple_spinner_item,
                                        mTitle
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerTitle.setAdapter(adapter)
                                }


                                for (i in it.value.getValue()!!.data!!.testType!!.indices) {
                                    val type: OrganizationTitleNewResponse.TestType =
                                        it.value.getValue()!!.data!!.testType!![i]
                                    mTestType.add(type)

                                    if (i == 0) {
                                        testTypeID = mTestType[0].testTypeId
                                        testCategoryID = mTestType[0].testCategoryId
                                        orderBy = mTestType[0].orderBy
                                        testName = mTestType.get(0).categoryName
                                        price = mTestType.get(0).price!!.toFloat()
                                        surfacePrice = mTestType.get(0).surfacePrice!!.toFloat()
                                    }

                                   // println("orderid_loop:::" + orderBy)

                                }


                                if (mTestType.size == 2) {
                                    binding.layoutTestSelection.isVisible = true
                                    binding.layoutSpitTest.isVisible = true
                                    binding.layoutSwabTest.isVisible = true

                                    binding.textViewSwabTest.text = mTestType[0].categoryName
                                    binding.textViewSpitTest.text = mTestType[1].categoryName
                                }

                                if (mTestType.size == 1) {

                                    binding.layoutTestSelection.isVisible = true
                                    binding.layoutSpitTest.isVisible = false
                                    binding.layoutSwabTest.isVisible = true
                                    binding.textViewSwabTest.text = mTestType[0].categoryName


                                    binding.layoutSwabTest.setBackground(
                                        ContextCompat.getDrawable(
                                            this,
                                            R.drawable.divider_button_border
                                        )
                                    )

                                    binding.textViewSwabTest.setTextColor(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.white
                                        )
                                    )

                                    if (orderBy == 1) {
                                        binding.stateProgress.setMaxStateNumber(StateProgressBar.StateNumber.FOUR)
                                        binding.stateProgress.enableAnimationToCurrentState(true)
                                        binding.textViewPageCount.text = "1/4"
                                    }

                                    if (orderBy == 2) {
                                        binding.stateProgress.setMaxStateNumber(StateProgressBar.StateNumber.THREE)
                                        binding.stateProgress.enableAnimationToCurrentState(true)
                                        binding.textViewPageCount.text = "1/3"
                                    }


                                }

                                if (mTestType.size == 0) {
                                    binding.layoutTestSelection.isVisible = false
                                    binding.layoutSpitTest.isVisible = false
                                    binding.layoutSwabTest.isVisible = false
                                }




                                binding.layoutBulkBookingFirstPage.isVisible = true
                                binding.layoutButtonView.isVisible = true
                                binding.buttonPrevious.isVisible = false
                                binding.buttonNext.isVisible = true
                                binding.buttonNext.text = resources.getText(R.string.button_next)


                            }
                        }
                        is Resource.GenericError -> {
                            if (progressDoalog != null && progressDoalog!!.isShowing) {
                                progressDoalog!!.dismiss()
                            }
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
        }
        else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }


    private fun googleMap() {
        val str = binding.edittextAddress.text
        if (str!!.contains("LandMark")) {
            val separated: List<String> = str.split(":")
            Log.e("LandMark", separated[1])
            intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("LandMark", separated[1])
            startActivity(intent)
        } else {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        binding.edittextAddress.setText(event)
        getLocationDetails(event)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        val clipBoard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val data: ClipData = ClipData.newPlainText("", "")
        clipBoard.setPrimaryClip(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    private fun getLocationDetails(event: String) {
        val coder = Geocoder(this@NewBulkBookingActivity, Locale.ENGLISH)
        //val coder = Geocoder(context)
        var address: List<Address?>

        try {
            // May throw an IOException
            address = coder.getFromLocationName(event, 5)
            if (address == null) {
                return
            }
            val location = address[0]!!
            val latlngs = LatLng(location.latitude, location.longitude)
            latlng = location.latitude.toString() + "," + location.longitude

            val addresses =
                coder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.size > 0) {
                val fetchedAddress = addresses[0]
                val strAddress = StringBuilder()
                locality = addresses[0].getLocality()
                mState = addresses[0].adminArea
                Log.e("stateName", mState!!)
                for (i in 0 until fetchedAddress.maxAddressLineIndex) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                }
            } else {
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }




    @SuppressLint("NotifyDataSetChanged")
    override fun bulkBookingSelectionValue(
        titleValue: OrganizationTitleNewResponse.Person, position: Int
    ) {

       // println("recycler_edittext_Values3:::"+position)
        mPeopleList[position].isSelected = true
        binding.dynamicRecyclerViewPeopleSelection.adapter!!.notifyItemChanged(position)


        binding.dynamicRecyclerViewPeopleSelection.smoothScrollToPosition(1000)

        peopleClose.isVisible=false
        peopleTick.isVisible=true



    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bulkBookingSelectionValueRemove(
        titleValue: OrganizationTitleNewResponse.Person, position: Int
    ) {

      //  println("recycler_edittext_Values4:::"+position)

        mPeopleList[position].isSelected = false
        mPeopleList[position].value=0

        binding.dynamicRecyclerViewPeopleSelection.adapter!!.notifyItemChanged(position)

        Handler(Looper.myLooper()!!).postDelayed({
            recyclerViewPeopleList.adapter!!.notifyItemChanged(position)
        }, 0)



        var count = 0


        for(i in 0 until mPeopleList.size) {
            if(!mPeopleList[i].isSelected)
            {
                count++

            }
        }

        if(count==mPeopleList.size)
        {

            peopleClose.isVisible=true
            peopleTick.isVisible=false
        }
        else
        {

            peopleClose.isVisible=false
            peopleTick.isVisible=true
        }



    }


    override fun bulkBookingSelectionValueUpdate(
        titleValue: OrganizationTitleNewResponse.Person,
        position: Int
    ) {
        mPeopleList[position].value = titleValue.value

    }


    override fun bulkBookingSelectionValueCleared(
        titleValue: OrganizationTitleNewResponse.Person,
        position: Int
    ) {
        mPeopleList[position].value = 0

    }

    override fun bulkBookingSelectionValue(
        titleValue: OrganizationTitleNewResponse.Surface,
        position: Int
    ) {
        mSurfaceList[position].isSelected = true
        binding.dynamicRecyclerViewSurfaceSelection.adapter!!.notifyItemChanged(position)


        binding.dynamicRecyclerViewSurfaceSelection.smoothScrollToPosition(1000)

        surfaceClose.isVisible=false
        surfaceTick.isVisible=true
    }

    override fun bulkBookingSelectionValueRemove(
        titleValue: OrganizationTitleNewResponse.Surface,
        position: Int
    ) {
        mSurfaceList[position].isSelected = false
        mSurfaceList[position].value=0

        binding.dynamicRecyclerViewSurfaceSelection.adapter!!.notifyItemChanged(position)

        Handler(Looper.myLooper()!!).postDelayed({
            recyclerViewSurfaceList.adapter!!.notifyItemChanged(position)
        }, 0)

        var count = 0


        for(i in 0 until mSurfaceList.size) {
            if(!mSurfaceList[i].isSelected)
            {
                count++

            }
        }

        if(count==mSurfaceList.size)
        {
            surfaceClose.isVisible=true
            surfaceTick.isVisible=false
        }
        else
        {
            surfaceClose.isVisible=false
            surfaceTick.isVisible=true
        }


    }

    override fun bulkBookingSelectionValueUpdate(
        titleValue: OrganizationTitleNewResponse.Surface,
        position: Int
    ) {
        mSurfaceList[position].value = titleValue.value
    }

    override fun bulkBookingSelectionValueCleared(
        titleValue: OrganizationTitleNewResponse.Surface,
        position: Int
    ) {
        mSurfaceList[position].value = 0
    }


    override fun onBackPressed() {
        handlePreviousState()


    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            offerSelected = true
            if (buttonView!!.getId() == R.id.radioButton1) {

                binding.radioButton2.setChecked(false)
                binding.radioButton3.setChecked(false)

                binding.textViewSavingsTwentyFive.isVisible=true
                binding.textViewSavingsfifty.isVisible=false

                mContractListForApi= ContractListNew()

                if(mContractList.size>0) {

                    mContractListForApi.description = mContractList[0].description
                    mContractListForApi.discount = mContractList[0].discount
                    mContractListForApi.id = mContractList[0].id
                    mContractListForApi.titleName = mContractList[0].titleName
                    contractMonth= mContractList[0].month!!



                }


                paymentApiReqValue=ArrayList()
                val payment = PaymentNew()
                payment.amount =paymentGatewayAmount1
                payment.testCount = 1
                payment.paymentName =testName

                paymentApiReqValue.add(payment)



            }
            if (buttonView.getId() == R.id.radioButton2) {
                binding.radioButton1.setChecked(false)
                binding.radioButton3.setChecked(false)

                binding.textViewSavingsTwentyFive.isVisible=false
                binding.textViewSavingsfifty.isVisible=true

                mContractListForApi= ContractListNew()

                if(mContractList.size>1)
                {

                    mContractListForApi.description = mContractList[1].description
                    mContractListForApi.discount = mContractList[1].discount
                    mContractListForApi.id = mContractList[1].id
                    mContractListForApi.titleName = mContractList[1].titleName
                    contractMonth= mContractList[1].month!!

                }


                paymentApiReqValue=ArrayList()
                val payment = PaymentNew()
                payment.amount =paymentGatewayAmount2
                payment.testCount = 1
                payment.paymentName =testName

                paymentApiReqValue.add(payment)




            }
            if (buttonView.getId() == R.id.radioButton3) {
                binding.radioButton1.setChecked(false)
                binding.radioButton2.setChecked(false)


                binding.textViewSavingsTwentyFive.isVisible=false
                binding.textViewSavingsfifty.isVisible=false

                mContractListForApi= ContractListNew()

                if(mContractList.size>2)
                {

                    mContractListForApi.description = mContractList[2].description
                    mContractListForApi.discount = mContractList[2].discount
                    mContractListForApi.id = mContractList[2].id
                    mContractListForApi.titleName = mContractList[2].titleName
                    contractMonth= mContractList[2].month!!

                }

                paymentApiReqValue=ArrayList()
                val payment = PaymentNew()
                payment.amount =paymentGatewayAmount3
                payment.testCount = 1
                payment.paymentName =testName

                paymentApiReqValue.add(payment)


            }
        }
    }

    override fun bulkBookingContractList(
        contractValue: OrganizationTitleNewResponse.ContractList,
        position: Int,paymentGatewayAmount:Float
    ) {

        mContractListForApi= ContractListNew()

            mContractListForApi.description =contractValue.description
            mContractListForApi.discount =contractValue.discount
            mContractListForApi.id =contractValue.id
            mContractListForApi.titleName = contractValue.titleName
            contractMonth= contractValue.month!!


        paymentApiReqValue=ArrayList()
        val payment = PaymentNew()
        payment.amount =paymentGatewayAmount
        payment.testCount = 1
        payment.paymentName =testName

        paymentApiReqValue.add(payment)



       // println("contractList1:::"+Gson().toJson(mContractListForApi))
       // println("contractList2:::"+Gson().toJson(paymentApiReqValue))
      //  println("contractList3:::"+contractMonth)
       // println("contractList4:::"+paymentGatewayAmount)
    }








}