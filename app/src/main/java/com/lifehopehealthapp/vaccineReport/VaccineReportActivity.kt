package com.lifehopehealthapp.vaccineReport

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityVaccineReportBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_DOC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.*

class VaccineReportActivity : BaseActivity<VaccineReportViewModel, VaccineReportModel>() {

    private var aDummyList: ArrayList<VaccinationListDatum> = ArrayList<VaccinationListDatum>()
    private var results: ArrayList<VaccinationListDatum>? = null
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var progressDialog: Dialog? = null
    private var selectedFilePath: String? = ""
    private lateinit var mDocumentImage: AppCompatImageView
    private lateinit var textViewTitle: AppCompatTextView
    private lateinit var layoutDocumentView: ConstraintLayout
    private var uploadAWSName: String? = ""
    private var deleteAWSFile: String? = ""
    private var deleteAWSFileID: String? = ""
    private lateinit var alertDialog: Dialog
    private var mRemarks: String = ""
    private var mDocumentName: String = ""
    private lateinit var mAdapter: VaccineReportListAdapter


    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1
    private var NEXTPAGE: Int = 1

    private var searchJob: Job? = null

    override fun getViewModel() = VaccineReportViewModel::class.java

    override fun getActivityRepository() =
        VaccineReportModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )


    private lateinit var binding: ActivityVaccineReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVaccineReportBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken

        binding.imageViewUpload.setOnClickListener {
            showPopup()
        }

        binding.buttonUpload.setOnClickListener {
            showPopup()
        }

        binding.recyclerViewVaccinationList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        mAdapter = VaccineReportListAdapter(
            aDummyList,
            this,
            mPrefs!!.s3BucketName,
            mPrefs?.w3SecretKey,
            mPrefs?.w3AccessKey,
            mPrefs?.s3BucketRegion
        )

        binding.imageviewClose.setOnClickListener {
            finish()
        }
        binding.recyclerViewVaccinationList.adapter = mAdapter

        getList()
        loadNextPage(1)
        binding.recyclerViewVaccinationList.setPullRefreshEnabled(false)
        binding.recyclerViewVaccinationList.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {
            }


            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    loadNextPage(nextPageNumber!!)
                } else {
                    binding.recyclerViewVaccinationList.setNoMore(false)
                    binding.recyclerViewVaccinationList.refreshComplete()
                }
            }
        })
    }

    private fun loadNextPage(i: Int) {
        if (Utils.isNetworkAvailable(this)) {
            var newData: Int = 0
            if (i == 1) {
                newData = 1
                if (progressDialog == null) {
                    progressDialog = Utils.getDialog(this)
                }
                progressDialog!!.show()
            }else{
                newData = i
            }

            val data = VaccinationListRequest()
            data.pageSize = 10
            data.pageNumber = i

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            viewModel.getVaccinationList(data, token!!)

            viewModel.Response!!.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            try {
                                if (it.value.getValue()!!.data!!.size != 0) {
                                    binding.layoutNoData.isVisible = false
                                    binding.imageViewUpload.isVisible = true
                                    binding.recyclerViewVaccinationList.isVisible = true
                                    TotalPageNumber = it.value.getValue()!!.totalPages
                                    if (nextPageNumber!! <= TotalPageNumber!!) {
                                        if (!it.value.getValue()!!.nextPage.equals("")) {
                                            if (nextPageNumber == TotalPageNumber) {
                                                nextPageNumber = 0
                                            } else {
                                                nextPageNumber = nextPageNumber!! + 1
                                            }

                                        } else {
                                            nextPageNumber = 0
                                        }
                                    }
                                } else {
                                    if (nextPageNumber == 1) {
                                        binding.layoutNoData.isVisible = true
                                        binding.imageViewUpload.isVisible = false
                                        binding.recyclerViewVaccinationList.isVisible = false
                                    } else {

                                    }
                                }
                                results =
                                    it.value.getValue()!!.data as ArrayList<VaccinationListDatum>?
                                if (it.value.getValue()!!.getPageNumber() == 1) {
                                    if (results!!.size > 0) {
                                        mAdapter.ClearDataValue(aDummyList)
                                        mAdapter.addAllSearch(results!!)
                                        mAdapter!!.notifyDataSetChanged()
                                    }
                                } else {
                                    if (results!!.size > 0) {
                                        mAdapter.addAllSearch(results!!)
                                        mAdapter!!.notifyDataSetChanged()
                                    }
                                }
                                if (it.value.getValue()!!.nextPage == null) {
                                    binding.recyclerViewVaccinationList!!.setNoMore(true)
                                } else {
                                    binding.recyclerViewVaccinationList!!.setNoMore(false)
                                }

                                binding.recyclerViewVaccinationList!!.refreshComplete()
                            } catch (e: Exception) {
                                Log.e("Exception", e.toString())
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
                            //Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
                        }

                    }
                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(), true)
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
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
            }
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }


    private fun showPopup() {
        alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        alertDialog.setCancelable(false)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_vaccine_document_upload)
        val userName: TextInputEditText = alertDialog.findViewById(R.id.edittext_name)
        layoutDocumentView = alertDialog.findViewById(R.id.layoutDocumentView)
        mDocumentImage = alertDialog.findViewById(R.id.mDocumentImage)
        textViewTitle = alertDialog.findViewById(R.id.textViewTitle)
        layoutDocumentView.setOnClickListener {
            checkPermissionsDocument()
        }
        mDocumentImage.setOnClickListener {
            checkPermissionsDocument()
        }
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.buttonUpload)
        val negTv: AppCompatImageButton = alertDialog.findViewById(R.id.imageviewClose)
        negTv.setOnClickListener {
            alertDialog.dismiss()
        }
        posTv.setOnClickListener(View.OnClickListener {
            if ((!userName.text.toString().equals(""))) {
                if (!selectedFilePath.equals("")) {
                    mRemarks = userName.text.toString()
                    if (progressDialog == null) {
                        progressDialog = Utils.getDialog(this)
                    }
                    progressDialog!!.show()
                    Utils.uploadDocumentAWS(
                        selectedFilePath!!,
                        mPrefs!!.s3BucketName,
                        mPrefs!!.userID,
                        this@VaccineReportActivity,
                        Utils.decryption(mPrefs!!.w3SecretKey),
                        Utils.decryption(mPrefs!!.w3AccessKey),
                        mDocumentName,
                        mPrefs!!.s3BucketRegion
                    )
                } else {
                    Utils.showToast(this, "Please choose your report", true)
                }

            } else {
                Utils.showToast(this, "Please enter the remarks", true)
            }

        })

        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun uploadDocumentAPI(uploadAWSName: String?, name: String) {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDialog == null) {
                progressDialog = Utils.getDialog(this)
            }
            progressDialog!!.show()
            val data = UploadVaccinationDocumentRequest()
            data.setFileUrl(uploadAWSName)
            data.setName(name)

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            viewModel.uploadVaccinationListDocument(data, token!!)
            viewModel.response1.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            try {
                                alertDialog.dismiss()
                                selectedFilePath = ""
                                mDocumentName = ""
                                loadNextPage(1)
                                mAdapter.notifyDataSetChanged()
                            } catch (e: Exception) {
                                Log.e("", e.toString())
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
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
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
            }
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getList() {
        if (Utils.isNetworkAvailable(this)) {
            /*lifecycleScope.launchWhenCreated {
                viewModel.getVaccinationList(
                    token!!
                ).collectLatest {
                    mAdapter.submitData(it)
                }
            }*/

            /*   if (progressDialog == null) {
                   progressDialog = Utils.getDialog(this)
               }
               progressDialog!!.show()
               val data = VaccinationListRequest()

               val gson = Gson()
               var json: String? = ""
               json = gson.toJson(data)
               Log.e("Model---->", json.toString())

               viewModel.getVaccinationList(data, token!!)
               viewModel.Response!!.observe(this) {
                   when (it) {
                       is Resource.Success -> {
                           if (progressDialog != null && progressDialog!!.isShowing) {
                               progressDialog!!.dismiss()
                           }
                           if (it.value.getStatusCode() == 200) {
                               try {
                                   if (it.value.getValue()!!.getData()!!.size != 0) {
                                       binding.layoutNoData.isVisible = false
                                       binding.imageViewUpload.isVisible = true
                                       binding.recyclerViewVaccinationList.isVisible = true
                                       mAdapter =
                                           VaccineReportListAdapter(
                                               this,
                                               mPrefs!!.s3BucketName,
                                               mPrefs?.w3SecretKey,
                                               mPrefs?.w3AccessKey,
                                               mPrefs?.s3BucketRegion
                                           )
                                       binding.recyclerViewVaccinationList.adapter = mAdapter
                                       mAdapter.notifyDataSetChanged()
                                   } else {
                                       binding.layoutNoData.isVisible = true
                                       binding.imageViewUpload.isVisible = false
                                       binding.recyclerViewVaccinationList.isVisible = false
                                   }
                               } catch (e: Exception) {
                                   Log.e("", e.toString())
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
                           if (progressDialog != null && progressDialog!!.isShowing) {
                               progressDialog!!.dismiss()
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
               }
           } else {
               Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
           }*/
        }
    }

    private fun checkPermissionsDocument() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        CustomBottomSheetDialogFragment(
                            this@VaccineReportActivity
                        ).apply {
                            show(
                                supportFragmentManager,
                                "CustomBottomSheetDialogFragment"
                            )
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {
                    Utils.showToast(
                        this@VaccineReportActivity,
                        resources.getString(R.string.text_error_occurred),
                        true
                    )
                }
            })
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                openSettings()
            })
        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    private fun openSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    fun fileFromContentUri(context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: UploadImageKey) {
        uploadAWSName = event.imageID
        uploadDocumentAPI(uploadAWSName, mRemarks)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: DeleteImageKey) {

        if (Utils.isNetworkAvailable(this)) {
            if (progressDialog == null) {
                progressDialog = Utils.getDialog(this)
            }
            progressDialog!!.show()
            val data = DeleteVaccinationDocumentRequest()
            data.setId(deleteAWSFileID)

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model1---->", json.toString())

            viewModel.deleteVaccinationListDocument(data, token!!)
            viewModel.response1.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            try {
                                alertDialog.dismiss()
                                loadNextPage(1)
                                mAdapter.notifyDataSetChanged()
                            } catch (e: Exception) {
                                Log.e("", e.toString())
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
                        Utils.showToast(this, it.error!!.message.toString(), true)
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
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
            }
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: DeleteDocumentKey) {
        deleteAWSFile = event.url
        deleteAWSFileID = event.id
        if (progressDialog == null) {
            progressDialog = Utils.getDialog(this)
        }
        progressDialog!!.show()
        Utils.deleteAWSObject(
            deleteAWSFile!!,
            mPrefs!!.s3BucketName,
            Utils.decryption(mPrefs!!.w3SecretKey),
            Utils.decryption(mPrefs!!.w3AccessKey),
            mPrefs!!.s3BucketRegion,
            this, ""
        )
    }

    var actionOpenDocument =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            val documentUri = uri ?: return@registerForActivityResult
        }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == RESULT_OK) {
            val currentTimestamp = System.currentTimeMillis()
            if (data?.data!!.path!!.contains(".png") || data.data!!.path!!.contains(".jpeg") || data.data!!.path!!.contains(
                    ".jpg"
                )
            ) {
                selectedFilePath = data?.data!!.path
//                selectedFilePath =
//                    data!!.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)!!
                val selectedImage = BitmapFactory.decodeFile(selectedFilePath)
                if (selectedFilePath!!.contains(".png") || selectedFilePath!!.contains("jpg")) {
                    if (selectedFilePath!!.contains(".png")) {
                        layoutDocumentView.isVisible = false
                        mDocumentImage.setImageBitmap(selectedImage)
                        mDocumentName = currentTimestamp.toString() + ".png"
                    } else {
                        layoutDocumentView.isVisible = false
                        mDocumentImage.setImageBitmap(selectedImage)
                        mDocumentName = currentTimestamp.toString() + ".jpg"
                    }
                } else {
                    Utils.showToast(this, "File not support", true)
                }
            } /*else if (data.toString().contains(".jpg")) {
                val uri: Uri = data?.data!!
                layoutDocumentView.isVisible = false
                mDocumentImage.setImageURI(uri)
//                val file: File = ImagePicker.getFile(data)!!
//                selectedFilePath = file.absolutePath.toString()
                mDocumentName = currentTimestamp.toString() + ".jpg"
            }*/ else if (REQUEST_CODE_DOC == requestCode) {
                val dataList: ArrayList<Uri>? =
                    intent.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)
                selectedFilePath = getRealPathFromURI(dataList!![0])
                if (selectedFilePath!!.contains(".pdf")) {
                    Glide
                        .with(this@VaccineReportActivity)
                        .load("")
                        .placeholder(R.drawable.ic_pdf)
                        .into(mDocumentImage)
                } else if (selectedFilePath!!.contains(".doc")) {
                    Glide
                        .with(this@VaccineReportActivity)
                        .load("")
                        .placeholder(R.drawable.ic_doc)
                        .into(mDocumentImage)
                } else if (selectedFilePath!!.contains(".ppt")) {
                    Glide
                        .with(this@VaccineReportActivity)
                        .load("")
                        .placeholder(R.drawable.ic_ppt)
                        .into(mDocumentImage)
                } else if (selectedFilePath!!.contains(".txt")) {
                    Glide
                        .with(this@VaccineReportActivity)
                        .load("")
                        .placeholder(R.drawable.ic_text)
                        .into(mDocumentImage)
                } else if (selectedFilePath!!.contains(".xls")) {
                    Glide
                        .with(this@VaccineReportActivity)
                        .load("")
                        .placeholder(R.drawable.ic_xls)
                        .into(mDocumentImage)
                }
            } else {
                val uri: Uri = data?.data!!
                lifecycleScope.launch {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    val documentStream = withContext(Dispatchers.IO) {
                        contentResolver.openInputStream(uri)
                    } ?: return@launch

                    val text = viewModel.openDocumentExample(documentStream)
                    layoutDocumentView.isVisible = false
                    //selectedFilePath = getRealPathFromURI(uri)

                    selectedFilePath = fileFromContentUri(
                        this@VaccineReportActivity,
                        uri
                    ).absolutePath

                    if (selectedFilePath!!.contains(".pdf")) {
                        val name = System.currentTimeMillis().toString() + "." + "pdf"
                        mDocumentName = name
                        Glide
                            .with(this@VaccineReportActivity)
                            .load("")
                            .placeholder(R.drawable.ic_pdf)
                            .into(mDocumentImage)
                    } else if (selectedFilePath!!.contains(".doc")) {
                        val name = System.currentTimeMillis().toString() + "." + "doc"
                        mDocumentName = name
                        Glide
                            .with(this@VaccineReportActivity)
                            .load("")
                            .placeholder(R.drawable.ic_doc)
                            .into(mDocumentImage)
                    } else if (selectedFilePath!!.contains(".ppt")) {
                        val name = System.currentTimeMillis().toString() + "." + "ppt"
                        mDocumentName = name
                        Glide
                            .with(this@VaccineReportActivity)
                            .load("")
                            .placeholder(R.drawable.ic_ppt)
                            .into(mDocumentImage)
                    } else if (selectedFilePath!!.contains(".txt")) {
                        val name = System.currentTimeMillis().toString() + "." + "txt"
                        mDocumentName = name
                        Glide
                            .with(this@VaccineReportActivity)
                            .load("")
                            .placeholder(R.drawable.ic_text)
                            .into(mDocumentImage)
                    } else if (selectedFilePath!!.contains(".xls")) {
                        val name = System.currentTimeMillis().toString() + "." + "xls"
                        mDocumentName = name
                        Glide
                            .with(this@VaccineReportActivity)
                            .load("")
                            .placeholder(R.drawable.ic_xls)
                            .into(mDocumentImage)
                    }
                }
            }
        }
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: NoDataFoundModel) {
        if (event.arraySize == 0) {
            binding.layoutNoData.isVisible = true
            binding.imageViewUpload.isVisible = false
            binding.recyclerViewVaccinationList.isVisible = false
        } else {
            mAdapter.notifyDataSetChanged()
            binding.layoutNoData.isVisible = false
            binding.imageViewUpload.isVisible = true
            binding.recyclerViewVaccinationList.isVisible = true
        }
    }

}