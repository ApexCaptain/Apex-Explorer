package com.ayteneve93.apexexplorer.prompt

import android.content.Intent
import androidx.biometric.BiometricPrompt
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt.BiometricAuthIntentPreference
import com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt.BiometricAuthManager
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthIntentPreference
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.koin.android.ext.android.inject
import java.util.concurrent.Executor

class PromptActivity : FragmentActivity() {

    val mFireBaseAuthManager : FireBaseAuthManager by inject()
    val mBiometricAuthManager : BiometricAuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(intent.action) {
            PromptIntentAction.FIRE_BASE_AUTH -> requestFireBaseAuth()
            PromptIntentAction.BIOMETRIC -> requestBiometricAuth()
            null -> finish()
        }
    }

    private fun requestFireBaseAuth() {
        when(intent.getStringExtra(FireBaseAuthIntentPreference.RequestDomain.KEY)) {
            FireBaseAuthIntentPreference.RequestDomain.Values.GOOGLE -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build()
                val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
                startActivityForResult(signInIntent, RequestCode.GOOGLE_REQUEST_CODE)
            }
            null -> finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RequestCode.GOOGLE_REQUEST_CODE -> {
                mFireBaseAuthManager.onRequestGoogleFireBaseAuthentication(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
            }
        }
        finish()
    }

    private fun requestBiometricAuth() {
        when(intent.getStringExtra(BiometricAuthIntentPreference.RequestDomain.KEY)) {
            BiometricAuthIntentPreference.RequestDomain.Values.FINGERPRINT -> {

                BiometricPrompt(this, object : Executor {
                    private val handler = Handler(Looper.getMainLooper())
                    override fun execute(runnable : Runnable) {
                        handler.post(runnable)
                    }
                }, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        mBiometricAuthManager.onRequestFingerprintAuthentication(false, errorCode, errString.toString())
                    }

                    override fun onAuthenticationSucceeded(resultInfo: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(resultInfo)
                        mBiometricAuthManager.onRequestFingerprintAuthentication(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        mBiometricAuthManager.onRequestFingerprintAuthentication(false)
                    }
                }).authenticate(
                    // BiometricPrompt 의 빌드 정보
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getString(R.string.fmt_fingerprint_title))
                        .setDescription(getString(R.string.fmt_fingerprint_desc))
                        .setNegativeButtonText(getString(R.string.fmt_fingerprint_negative_button))
                        .build()
                )
            }
            null -> finish()
        }
    }

    companion object {
        private object RequestCode {
            const val GOOGLE_REQUEST_CODE = 1000
        }
    }

}
