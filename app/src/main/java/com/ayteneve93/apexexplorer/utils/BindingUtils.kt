package com.ayteneve93.apexexplorer.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter


object BindingUtils {

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageViewResource(view : ImageView, resId : Int) {
        view.setImageResource(resId)
    }

}