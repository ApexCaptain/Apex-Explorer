package com.ayteneve93.apexexplorer.utils

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter


object BindingUtils {

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageViewResource(view : ImageView, resId : Int) {
        view.setImageResource(resId)
    }


    @JvmStatic
    @BindingAdapter("android:textStyle")
    fun setTypeface(textView : TextView, style : String) {
        when(style) {
            "bold" -> textView.setTypeface(null, Typeface.BOLD)
            else -> textView.setTypeface(null, Typeface.NORMAL)
        }
    }

}