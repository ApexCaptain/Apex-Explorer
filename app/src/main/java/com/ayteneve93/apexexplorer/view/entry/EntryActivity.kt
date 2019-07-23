package com.ayteneve93.apexexplorer.view.entry

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricConstants
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
        mEntryViewModel.mIsOnProgress.set(true)
        mDataModelManager.getUserAccountInfoModel().isAuthenticated.let {
            if(it == null || !it) showTitleAnimation()
            else toMainActivity(false)
        }
    }

    private fun showTitleAnimation() {
        mViewDataBinding.appTitleView.adapter = AppTitleRecyclerAdapter(
            this, mDataModelManager.getAppTitleModel(this))
            .onRecyclerStartUpAnimEnd {
                runFireBaseAuthenticate()
            }
        mViewDataBinding.appTitleView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun runFireBaseAuthenticate() {
        mEntryViewModel.mIsOnProgress.set(true)
        mFireBaseAuthManager.requestGoogleFireBaseAuthentication(this) {
            isSucceed, account ->
            mEntryViewModel.mIsOnProgress.set(false)
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

    private val biometricRetryHandler = Handler()
    private val biometricRetryDelayMills = 3500L
    private fun runBiometricAuthenticate() {
        mBiometricManager.requestFingerprintAuthentication(this) {
            isSucceed: Boolean, errCode: Int? ->
            if(isSucceed) toMainActivity()
            else if(errCode != null) {
                when(errCode) {
                    BiometricConstants.ERROR_HW_UNAVAILABLE,
                    BiometricConstants.ERROR_UNABLE_TO_PROCESS,
                    BiometricConstants.ERROR_CANCELED,
                    BiometricConstants.ERROR_NO_SPACE -> {
                        // 일시적인 에러 혹은 잘못된 지문으로 Scanner 가 지문을 인식하지 못한 경우
                        Toast.makeText(this, R.string.tst_fingerprint_temporal_error, Toast.LENGTH_LONG).show()
                        biometricRetryHandler.removeCallbacksAndMessages(null)
                        biometricRetryHandler.postDelayed({
                            runBiometricAuthenticate()
                        }, biometricRetryDelayMills)
                    }
                    BiometricConstants.ERROR_TIMEOUT,
                    BiometricConstants.ERROR_LOCKOUT,
                    BiometricConstants.ERROR_VENDOR,
                    BiometricConstants.ERROR_NEGATIVE_BUTTON,
                    BiometricConstants.ERROR_USER_CANCELED-> {
                        // 뒤로가기, 취소 버튼, 시간 초과 등 지문 인식 프로세스가 취소 된 경우
                        Toast.makeText(this, R.string.tst_fingerprint_canceled, Toast.LENGTH_LONG).show()
                        finish()
                    }
                    BiometricConstants.ERROR_NO_BIOMETRICS,
                    BiometricConstants.ERROR_LOCKOUT_PERMANENT,
                    BiometricConstants.ERROR_HW_NOT_PRESENT -> {
                        // H/W 혹은 S/W 적인 이유로 지문인식 실행 자체가 불가능한 경우. 이 때는 지문인식 기능을 비활성화 하고
                        // 바로 다음 액티비티로 넘어감. 단 로그인 성공시 메시지는 출력하지 않음
                        Toast.makeText(this, R.string.tst_fingerprint_unavailable, Toast.LENGTH_LONG).show()
                        mApplicationPreference.setBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, false)
                            .setBooleanUserPreference(PreferenceCategory.User.SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION, true)
                        toMainActivity(false)
                    }
                }
            }
        }
    }

    // 성공 메시지 날려야 함
    private fun toMainActivity(showWelcomeText : Boolean = true) {
        mDataModelManager.getUserAccountInfoModel().isAuthenticated = true
        if(showWelcomeText) Toast.makeText(this, getString(R.string.tst_auth_succeed, mDataModelManager.getUserAccountInfoModel().name), Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
    }

}
