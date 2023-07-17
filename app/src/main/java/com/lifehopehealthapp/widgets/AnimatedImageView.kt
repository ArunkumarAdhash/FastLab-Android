package com.lifehopehealthapp.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.lifehopehealthapp.R

class AnimatedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    @StyleableRes
    var index0 = 0

    /**
     * Create constructor
     * @param context The activity object inherits the Context object
     * @param attrs AttributeSet
     */
    fun AnimatedImageView(
        context: Context,
        attrs: AttributeSet
    ) {
        init(context, attrs)
    }

    /**
     * Animated Vector Drawable Compat function
     * @param context The activity object inherits the Context object
     * @param attrs AttributeSet
     */
    private fun init(
        context: Context,
        attrs: AttributeSet
    ) {
        val sets = intArrayOf(R.attr.srcCompat)
        val typedArray = context.obtainStyledAttributes(attrs, sets)
        val image = typedArray.getResourceId(index0, 0)
        typedArray.recycle()
        val animatedVectorDrawableCompat =
            AnimatedVectorDrawableCompat.create(
                context,
                image
            )
        setImageDrawable(animatedVectorDrawableCompat)
        if (animatedVectorDrawableCompat != null) {
            animatedVectorDrawableCompat.registerAnimationCallback(object :
                Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    super.onAnimationEnd(drawable)
                    animatedVectorDrawableCompat.start()
                }
            })
            animatedVectorDrawableCompat.start()
        }
    }
}