package com.lifehopehealthapp.symptomSearch.UserDetails

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.twocoffeesoneteam.glidetovectoryou.GlideApp
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lifehopehealthapp.ResponseModel.ProfileDetails
import com.lifehopehealthapp.ResponseModel.SymptomSearchQusAnsModel
import com.lifehopehealthapp.ResponseModel.UploadImageKey
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.databinding.ActivityUserDetailsBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.symptomSearch.Questions.SymptomSearchActivity
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.userAge
import com.lifehopehealthapp.utils.PreferenceHelper.userGender
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.*

class UserDetailsActivity : BaseActivity<UserDetailsViewModel, UserDetailsModel>(),
    View.OnFocusChangeListener {

    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null
    private var firstName: String? = ""
    private var lasttName: String? = ""
    private var userGender: String? = "Select Gender"
    private var token: String? = ""
    private var age: Int? = 0
    private val REQUEST_IMAGE = 100
    private var genderName: ArrayList<String>? = null
    private var destination: File? = null
    private var imageData: String? = ""

    //private var selectItems: ArrayList<UserData>? = ArrayList()
    private var json: String? = ""
    private var SymptomID: String? = ""
    private var SymptomName: String? = ""

    override fun getViewModel() = UserDetailsViewModel::class.java

    override fun getActivityRepository() =
        UserDetailsModel(
            remoteDataSource.BuildAPI(APIManager::class.java), preferences = PreferenceHelper
        )

    private lateinit var binding: ActivityUserDetailsBinding


    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        firstName = mPrefs!!.firstname
        lasttName = mPrefs!!.lastname
        userGender = mPrefs!!.userGender
        age = mPrefs!!.userAge
        token = mPrefs!!.authToken

        SymptomID = intent.getStringExtra("symptomID").toString()
        SymptomName = intent.getStringExtra("symptomName").toString()

        //ImagePickerActivity.clearCache(this)
        genderName = ArrayList<String>()
        genderName!!.add("Select Gender")
        genderName!!.add("Male")
        genderName!!.add("Female")

        val aa = ArrayAdapter(this, R.layout.simple_spinner_item, genderName!!)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.setAdapter(aa)

        binding.spinnerGender.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    if ((!binding.edittextName.text.toString()
                            .equals("") && (!binding.edittextLastname.text.toString()
                            .equals("")) && (!binding.edittextAge.text.toString()
                            .equals("")) && (!imageData.equals("")))
                    ) {
                        binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_theme_bg)
                    } else {
                        binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
                    }
                    userGender = binding.spinnerGender.selectedItem.toString()
                } else {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        })

        binding.edittextAge.setOnFocusChangeListener(this)
        binding.edittextName.setOnFocusChangeListener(this)
        binding.edittextLastname.setOnFocusChangeListener(this)

        binding.layoutTakePhoto.setOnClickListener {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }

        binding.imageButtonClose.setOnClickListener {
            finish()
        }

        binding.layoutEditImage.setOnClickListener {
            showImagePickerOptions()
        }

        binding.checkBoxProfile.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(
                buttonView: CompoundButton?,
                isChecked: Boolean
            ) {
                if (isChecked) {
                    binding.edittextName.setText(firstName)
                    binding.edittextLastname.setText(lasttName)
                    binding.edittextAge.setText(age.toString())
                    userGender= mPrefs!!.userGender
                    /*imageData = mPrefs!!.profile
                    Glide.with(this@UserDetailsActivity).load(mPrefs!!.profile)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(com.lifehope.R.drawable.ic_profile_no_image)
                            .error(com.lifehope.R.drawable.ic_profile_no_image)
                            .into(binding.imageViewUserPhoto)*/
                    for (position in 0 until genderName!!.size) {
                        if (genderName!![position] == userGender) {
                            binding.spinnerGender.setSelection(position)
                            Log.e("gender", userGender!!)
                        }
                    }
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_theme_bg)
                } else {
                    imageData = ""
                    binding.cardViewUserImage.isVisible = false
                    binding.layoutTakePhoto.isVisible = true
                    binding.edittextName.setText("")
                    binding.edittextLastname.setText("")
                    binding.edittextAge.setText("")
                    binding.spinnerGender.setSelection(0)
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
                }
            }
        })
        binding.edittextLastname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if ((!binding.edittextName.text.toString()
                        .equals("") && (!binding.edittextLastname.text.toString()
                        .equals("")) && (!binding.spinnerGender.selectedItem.toString()
                        .equals("Select Gender")) && (!binding.edittextAge.text.toString()
                        .equals("")) && (!imageData.equals("")))
                ) {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_theme_bg)
                } else {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.edittextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if ((!binding.edittextName.text.toString()
                        .equals("") && (!binding.edittextLastname.text.toString()
                        .equals("")) && (!binding.spinnerGender.selectedItem.toString()
                        .equals("Select Gender")) && (!binding.edittextAge.text.toString()
                        .equals("")) && (!imageData.equals("")))
                ) {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_theme_bg)
                } else {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.edittextAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if ((!binding.edittextName.text.toString()
                        .equals("") && (!binding.edittextLastname.text.toString()
                        .equals("")) && (!binding.spinnerGender.selectedItem.toString()
                        .equals("Select Gender")) && (!binding.edittextAge.text.toString()
                        .equals("")) && (!imageData.equals("")))
                ) {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_theme_bg)
                } else {
                    binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.buttonNext.setOnClickListener {
            checkValidation()
        }


    }

    private fun checkValidation() {
        var userAge: Int? = 0
        val stringAge = binding.edittextAge.text.toString()
        if (!stringAge.equals("")) {
            userAge = Integer.parseInt(stringAge)
        }
        userGender = binding.spinnerGender.selectedItem.toString()
        if (!binding.edittextName.text.toString().equals("")) {
            if (!binding.edittextLastname.text.toString().equals("")) {
                if (!userGender!!.equals("Select Gender")) {
                    if (userAge != 0 && userAge!! <= 120) {
                        if (!imageData.equals("")) {
                            val data =
                                SymptomSearchQusAnsModel()
                            val dynamicValue =
                                ProfileDetails()
                            dynamicValue.firstName = binding.edittextName.text.toString()
                            dynamicValue.lastName = binding.edittextLastname.text.toString()
                            dynamicValue.gender = userGender
                            dynamicValue.dob = userAge
                            dynamicValue.profilePicture = imageData
                            //selectItems!!.add(dynamicValue)
                            data.setProfileDetail(dynamicValue)
                            val gson = Gson()
                            json = gson.toJson(data)
                            Log.e("Model---->", json.toString())
                            intent =
                                Intent(this@UserDetailsActivity, SymptomSearchActivity::class.java)
                            intent.putExtra("json", json.toString())
                            intent.putExtra("symptomID", SymptomID)
                            intent.putExtra("symptomName", SymptomName)
                            intent.putExtra("Profile", Gson().toJson(data))
                            startActivity(intent)
                            finish()
                        } else {
                            Utils.showToast(
                                this,
                                getString(com.lifehopehealthapp.R.string.toast_take_photo),
                                true
                            )
                        }

                    } else {
                        if (binding.edittextAge.text!!.trim().isEmpty() ){
                            Utils.showToast(
                                this,
                                getString(com.lifehopehealthapp.R.string.toast_enter_valid_age),
                                true
                            )
                        }else{
                            Utils.showToast(
                                this,
                                getString(com.lifehopehealthapp.R.string.enter_valid_age),
                                true
                            )
                        }

                    }
                } else {
                    Utils.showToast(
                        this,
                        getString(com.lifehopehealthapp.R.string.toast_select_gender),
                        true
                    )
                }
            } else {
                Utils.showToast(
                    this,
                    getString(com.lifehopehealthapp.R.string.toast_enter_lastname),
                    true
                )
            }
        } else {
            Utils.showToast(
                this,
                getString(com.lifehopehealthapp.R.string.toast_enter_firstname),
                true
            )
        }
    }

    private fun showImagePickerOptions() {
        /*ImagePickerActivity.showImagePickerOptions(
            this,
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }
            })*/

        ImagePicker.with(this)
            .cameraOnly()
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    /*private fun launchCameraIntent() {
        val intent = Intent(
            this@UserDetailsActivity,
            ImagePickerActivity::class.java
        )
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityForResult(intent, REQUEST_IMAGE)
    }*/

    private fun showSettingsDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                openSettings()
            })
        builder.setNegativeButton(
            getString(R.string.cancel),
            { dialog, which -> dialog.cancel() })
        builder.show()
    }

    private fun openSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    fun getFilePathFromContentUri(
        contentUri: Uri?,
        contentResolver: ContentResolver
    ): String? {
        val filePath: String
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor =
            contentResolver.query(contentUri!!, filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")

            }
        }*/
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data

            //binding..setImageURI(fileUri)
            Log.e("fileUri", fileUri!!.path.toString())

            //You can get File object from intent
            //val file: File = ImagePicker.getFile(data)!!
//            val filePath =
//                getFilePathFromContentUri(fileUri!!, contentResolver).toString()
            //You can also get File Path from intent
            //val filePath: String = ImagePicker.getFilePath(data)!!
            val endPonit =
                Utils.uploadAWSimage(
                    fileUri!!.path.toString(),
                    mPrefs!!.s3BucketName,
                    mPrefs!!.userID,
                    this,
                    Utils.decryption(mPrefs!!.w3SecretKey),
                    Utils.decryption(mPrefs!!.w3AccessKey),
                    "Book_",
                    mPrefs!!.s3BucketRegion
                )
            binding.layoutTakePhoto.isVisible = false
            binding.cardViewUserImage.isVisible = true
            binding.userProfileImage.setImageURI(fileUri)
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
           // Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfile(url: String) {
        binding.layoutTakePhoto.isVisible = false
        binding.cardViewUserImage.isVisible = true

        GlideApp.with(this).load(url)
            .into(binding.userProfileImage)
        binding.userProfileImage.setColorFilter(ContextCompat.getColor(this, R.color.transparent))
    }

    private fun onCaptureImageResult(data: Bitmap) {
        val thumbnail = data as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        destination = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            destination!!.createNewFile()
            Log.d("path", destination.toString())
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {

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
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: UploadImageKey) {
        imageData = event.imageID
        if ((!binding.edittextName.text.toString()
                .equals("") && (!binding.edittextLastname.text.toString()
                .equals("")) && (!userGender!!.equals("Select Gender")) && (!binding.edittextAge.text.toString()
                .equals("")) && (!imageData.equals("")))
        ) {
            if (progressDoalog != null && progressDoalog!!.isShowing) {
                progressDoalog!!.dismiss()
            }
            binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_theme_bg)
        } else {
            binding.buttonNext.setBackgroundResource(com.lifehopehealthapp.R.drawable.button_disable_bg)
        }
        Log.e("imageData", imageData!!)
    }
}