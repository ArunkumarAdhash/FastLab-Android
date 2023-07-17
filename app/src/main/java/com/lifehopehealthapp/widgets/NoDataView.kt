package com.lifehopehealthapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatImageView
import com.lifehopehealthapp.R

class NoDataView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    @StyleableRes
    var index0 = 0

    @StyleableRes
    var index1 = 1

    @StyleableRes
    var index2 = 2

    private var dataImageView: AppCompatImageView? = null
    private var dataTextView: LifeHopenTextView? = null
    private var dataButtonView: LifeHopeButton? = null
    private var transitionsContainer: ViewGroup? = null

    /**
     * Create constructor
     *
     * @param context The activity object inherits the Context object
     * @param attrs   AttributeSet
     */
    fun NoDataView(
        context: Context,
        attrs: AttributeSet
    ) {
        init(context, attrs)
    }

    /**
     * @param context The activity object inherits the Context object
     * @param attrs   AttributeSet
     */
    private fun init(
        context: Context,
        attrs: AttributeSet
    ) {
        View.inflate(context, R.layout.no_data_view, this)
        val sets = intArrayOf(R.attr.DataText, R.attr.ButtonText, R.attr.srcCompat)
        transitionsContainer = findViewById(R.id.rootLay)
        val typedArray = context.obtainStyledAttributes(attrs, sets)
        val dataText = typedArray.getText(index0)
        val buttonText = typedArray.getText(index1)
        val image = typedArray.getResourceId(index2, 0)
        typedArray.recycle()
        initComponents()
        setDataText(dataText)
        setButtonText(buttonText)
        setDataImageView(image)
    }

    /**
     * Binding Widgets Id's
     */
    private fun initComponents() {
        dataImageView = findViewById(R.id.img_data)
        dataTextView = findViewById(R.id.txt_data)
        dataButtonView = findViewById(R.id.btn_data)
    }

    /**
     * Set imageview data
     *
     * @param imageView
     */
    fun setDataImageView(imageView: Int) {
        dataImageView!!.setImageResource(imageView)
        if (imageView == R.drawable.ic_no_internet) dataButtonView!!.setVisibility(View.VISIBLE) else dataButtonView!!.setVisibility(
            View.GONE
        )
    }

    fun getTrackText(): CharSequence? {
        return dataTextView!!.getText()
    }

    fun setDataText(value: CharSequence?) {
        dataTextView!!.setText(value)
    }

    fun getButton(): LifeHopeButton? {
        return dataButtonView
    }

    fun setButtonText(value: CharSequence?) {
        dataButtonView!!.setText(value)
    }

    /**
     * Show default NoInternet Setting
     */
    fun defaultNoInternetSetting() {
        setDataImageView(R.drawable.ic_no_internet)
        setDataText(context.resources.getString(R.string.no_internet_msg))
        setButtonText(context.resources.getString(R.string.try_again))
        visibility = View.VISIBLE
//        ViewUtils.animateView(transitionsContainer,this);
    }
}