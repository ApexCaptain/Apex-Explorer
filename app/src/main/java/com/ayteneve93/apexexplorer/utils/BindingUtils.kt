package com.ayteneve93.apexexplorer.utils

import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter


object BindingUtils {

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageViewFromResId(view : ImageView, resId : Int) {
        view.setImageResource(resId)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageViewFromUri(view : ImageView, uri : Uri) {
        view.setImageURI(uri)
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