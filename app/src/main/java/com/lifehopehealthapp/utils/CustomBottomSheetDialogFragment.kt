package com.lifehopehealthapp.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.with
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lifehopehealthapp.R
import com.lifehopehealthapp.vaccineReport.VaccineReportActivity


class CustomBottomSheetDialogFragment(
    val mContext: VaccineReportActivity
) :
    BottomSheetDialogFragment() {

    private var layoutCamera: LinearLayout? = null
    private var layoutGallery: LinearLayout? = null
    private var layoutDocument: LinearLayout? = null

    private var selectOption: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.layout_persistent_bottom_sheet, container, false)
        getDialog()!!.setCanceledOnTouchOutside(true)
        layoutCamera = view.findViewById(R.id.layoutCamera) as LinearLayout
        layoutGallery = view.findViewById(R.id.layoutGallery) as LinearLayout
        layoutDocument = view.findViewById(R.id.layoutDocument) as LinearLayout

        return view
    }

    @SuppressLint("QueryPermissionsNeeded", "MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutCamera!!.setOnClickListener {
            dismiss()
            selectOption = true
            dismiss()
            with(this)
                .cameraOnly()
                .crop()                    //Crop image(Optional), Check Customization for more option
                //.compress(1024)
              /*  .maxResultSize(
                    1080,
                    1080
                )  */  //Final image resolution will be less than 1080 x 1080(Optional)
                .start()

        }

        layoutGallery!!.setOnClickListener {
            dismiss()
            selectOption = true

            with(this) // Crop Image(User can choose Aspect Ratio)
                .crop() // User can only select image from Gallery
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                ) // Image resolution will be less than 1080 x 1920
              /*  .maxResultSize(1080, 1920)*/ // .saveDir(getExternalFilesDir(null))
                .start(1211)
            /*ImageSelectActivity.startImageSelectionForResult(
                mContext,
                false,
                false,
                true,
                false,
                1211
            )*/
        }

        layoutDocument!!.setOnClickListener {
            dismiss()
            /*if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                val mimeTypes = arrayOf(
                    "application/pdf"
                )
                mContext.actionOpenDocument.launch(mimeTypes)

            } else {
                FilePickerBuilder.instance
                    .setMaxCount(1)
                    .enableDocSupport(false)
                    .setActivityTheme(R.style.DocAppTheme)
                    .pickFile(mContext)
            }*/
            val mimeTypes = arrayOf(
                "application/pdf"
            )
            mContext.actionOpenDocument.launch(mimeTypes)

        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    fun TakePicture() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            )
            .start(111)
    }
}