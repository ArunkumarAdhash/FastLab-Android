package com.lifehopehealthapp.utils


import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText


class CustomEditText : TextInputEditText {
    var listeners: ArrayList<GoEditTextListener>? = null

    constructor(context: Context) : super(context) { listeners = ArrayList()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        listeners = ArrayList()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        listeners = ArrayList()
    }

    fun addListener(listener: GoEditTextListener) {
        try {
            listeners!!.add(listener)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    /**
     * Here you can catch paste, copy and cut events
     */
    override fun onTextContextMenuItem(id: Int): Boolean {
        val consumed = super.onTextContextMenuItem(id)
        when (id) {
            android.R.id.cut -> onTextCut()
            android.R.id.paste -> onTextPaste()
            android.R.id.copy -> onTextCopy()
        }
        return consumed
    }

    fun onTextCut() {}

    fun onTextCopy() {}

    /**
     * adding listener for Paste for example
     */
    open fun onTextPaste() {
        for (listener in listeners!!) {
            listener.onUpdate()
        }
    }

    interface GoEditTextListener {
        fun onUpdate()
    }

}