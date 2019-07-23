package com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ayteneve93.apexexplorer.prompt.PromptActivity
import com.ayteneve93.apexexplorer.prompt.PromptIntentAction
import java.lang.Exception

class BiometricAuthManager {

    private var onAuthenticationResult : ((Boolean, Int?) -> Unit)? = null
    fun requestFingerprintAuthentication(sourceActivity : AppCompatActivity, onAuthenticationResult : (isSucceed : Boolean, errCode : Int?) -> Unit) {
        val requestFingerprintAuthenticationIntent = Intent(sourceActivity, PromptActivity::class.java)
            .setAction(PromptIntentAction.BIOMETRIC)
            .putExtra(BiometricAuthIntentPreference.RequestDomain.KEY, BiometricAuthIntentPreference.RequestDomain.Values.FINGERPRINT)
        sourceActivity.startActivity(requestFingerprintAuthenticationIntent)
        this.onAuthenticationResult = onAuthenticationResult
    }

    fun checkIsFingerprintAuthenticationAvailable(context: Context) : Boolean {
        try {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val packageManager = context.packageManager
            if (
                !packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                || !keyguardManager.isKeyguardSecure
                || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.USE_BIOMETRIC
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
            return true
        } catch (anyException : Exception) {
            return false
        }
    }

    fun onRequestFingerprintAuthentication(isSucceed : Boolean, errCode : Int? = null) {
        onAuthenticationResult?.let {
            it(isSucceed, errCode)
        }
    }


}