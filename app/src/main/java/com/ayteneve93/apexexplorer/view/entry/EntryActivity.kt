package com.ayteneve93.apexexplorer.view.entry

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayteneve93.apexexplorer.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.DataModelManager
import com.ayteneve93.apexexplorer.databinding.ActivityEntryBinding
import com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt.BiometricAuthManager
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.utils.ApplicationPreference
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import com.ayteneve93.apexexplorer.view.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class EntryActivity : BaseActivity<ActivityEntryBinding, EntryViewModel>() {

    private val mEntryViewModel : EntryViewModel by viewModel()
    val mFireBaseAuthManager : FireBaseAuthManager by inject()
    val mBiometricManager : BiometricAuthManager by inject()
    val mDataModelManager : DataModelManager by inject()
    val mApplicationPreference : ApplicationPreference by inject()

    override fun getLayoutId(): Int { return R.layout.activity_entry }
    override fun getViewModel(): EntryViewModel { return mEntryViewModel }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {
        mDataModelManager.getUserAccountInfoModel().isAuthenticated.let {
            if(it == null || !it) showTitleAnimation()
            else toMainActivity(false)
        }
    }

    private fun showTitleAnimation() {
        mViewDataBinding.appTitleView.adapter = AppTitleRecyclerAdapter(
            this, mDataModelManager.getAppTitleModel(this))
            .onRecyclerStartUpAnimEnd {
                mEntryViewModel.mIsOnProgress.set(true)
                runFireBaseAuthenticate()
            }
        mViewDataBinding.appTitleView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun runFireBaseAuthenticate() {
        mFireBaseAuthManager.requestGoogleFireBaseAuthentication(this) {
            isSucceed, account ->
            account?.let {
                if(isSucceed) {
                    mDataModelManager.setUserAccountInfoModel(it)
                    checkToRunBiometricAuthenticate()
                    return@requestGoogleFireBaseAuthentication
                }
            }
            Toast.makeText(this, R.string.tst_auth_failed, Toast.LENGTH_SHORT).show()
            runFireBaseAuthenticate()
        }
    }

    private fun checkToRunBiometricAuthenticate() {
        if(mBiometricManager.checkIsFingerprintAuthenticationAvailable(this)) {
            if(mApplicationPreference.getBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, false)) runBiometricAuthenticate()
            else {
                if(mApplicationPreference.getBooleanUserPreference(PreferenceCategory.User.SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION, false)) toMainActivity()
                else {
                    AskToUseBiometricAuthDialog(this){
                        useFingerprint, doNotAskToUseFingerprintAgain ->
                        mApplicationPreference.setBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, useFingerprint)
                            .setBooleanUserPreference(PreferenceCategory.User.SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION, doNotAskToUseFingerprintAgain)
                        if(useFingerprint) runBiometricAuthenticate()
                        else toMainActivity()
                    }.show()
                }
            }
            return
        }
        toMainActivity()
    }

    private fun runBiometricAuthenticate() {
        mBiometricManager.requestFingerprintAuthentication(this) {
            isSucceed: Boolean, errCode: Int?, errString: String? ->
            if(isSucceed) toMainActivity()
            else {
                if(errCode == null) {
                    Toast.makeText(this, R.string.tst_fingerprint_failed, Toast.LENGTH_LONG).show()
                    runBiometricAuthenticate()
                    return@requestFingerprintAuthentication
                }
                errCode.let {
                    val tstErrMsg : String = when(it) {
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> getString(R.string.tst_fingerprint_err_hw_unavailable)
                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> getString(R.string.tst_fingerprint_err_none_enrolled)
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> getString(R.string.tst_fingerprint_err_no_hardware)
                        else -> getString(R.string.tst_fingerprint_err_canceled)
                    }
                    mApplicationPreference.setBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, false)
                    Toast.makeText(this, tstErrMsg, Toast.LENGTH_LONG).show()
                    runBiometricAuthenticate()
                }

            }
        }
    }

    // 성공 메시지 날려야 함
    private fun toMainActivity(showWelcomeText : Boolean = true) {
        mDataModelManager.getUserAccountInfoModel().isAuthenticated = true
        if(showWelcomeText) Toast.makeText(this, getString(R.string.tst_auth_succeed, mDataModelManager.getUserAccountInfoModel().name), Toast.LENGTH_LONG).show()
        mEntryViewModel.mIsOnProgress.set(false)
        startActivity(Intent(this, MainActivity::class.java))
    }

}
