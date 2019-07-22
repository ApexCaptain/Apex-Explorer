package com.ayteneve93.apexexplorer.view.entry

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.ayteneve93.apexexplorer.R
import kotlinx.android.synthetic.main.dialog_ask_to_user_biometric_auth.*

class AskToUseBiometricAuthDialog(context : Context, private val onResult : (useFingerprint : Boolean, doNotAskToUseFingerprintAgain : Boolean) -> Unit) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_ask_to_user_biometric_auth)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable())
        window?.setDimAmount(0.0f)
        setCancelable(false)
        ask_to_use_biometric_negative_btn.setOnClickListener {
            onResult(false, ask_to_use_biometric_checkbox.isChecked)
            dismiss()
        }
        ask_to_use_biometric_positive_btn.setOnClickListener {
            onResult(true, ask_to_use_biometric_checkbox.isChecked)
            dismiss()
        }
    }

}