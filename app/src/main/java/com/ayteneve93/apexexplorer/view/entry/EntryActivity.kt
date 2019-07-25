package com.ayteneve93.apexexplorer.view.entry

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricConstants
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayteneve93.apexexplorer.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.managers.AppTitleModelManager
import com.ayteneve93.apexexplorer.data.managers.UserAccountInfoModelManager
import com.ayteneve93.apexexplorer.databinding.ActivityEntryBinding
import com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt.BiometricAuthManager
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import com.ayteneve93.apexexplorer.view.main.MainActivity
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class EntryActivity : BaseActivity<ActivityEntryBinding, EntryViewModel>() {

    private val mEntryViewModel : EntryViewModel by viewModel()
    private val mFireBaseAuthManager : FireBaseAuthManager by inject()
    private val mBiometricManager : BiometricAuthManager by inject()
    private val mAppTitleModelManager : AppTitleModelManager by inject()
    private val mUserAccountInfoModelManager : UserAccountInfoModelManager by inject()
    private val mPreferenceUtils : PreferenceUtils by inject()

    override fun getLayoutId(): Int { return R.layout.activity_entry }
    override fun getViewModel(): EntryViewModel { return mEntryViewModel }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {
        mUserAccountInfoModelManager.getUserAccountInfoModel().isAuthenticated?.let {
            if(it) {
                toMainActivity(false)
                return@setUp
            }
        }
        mEntryViewModel.mIsAppLoading.set(true)
        showTitleAnimation()
    }

    private fun showTitleAnimation() {
        mViewDataBinding.appTitleView.adapter = AppTitleRecyclerAdapter(
            this, mAppTitleModelManager.getAppTitleModel())
            .onRecyclerStartUpAnimEnd {
                runFireBaseAuthenticate()
            }
        mViewDataBinding.appTitleView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun runFireBaseAuthenticate() {
        mEntryViewModel.mIsAppLoading.set(true)
        mFireBaseAuthManager.requestGoogleFireBaseAuthentication(this) {
            isSucceed, account ->
            mEntryViewModel.mIsAppLoading.set(false)
            account?.let {
                if(isSucceed) {
                    mUserAccountInfoModelManager.setUserAccountInfoModel(it)
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
            if(mPreferenceUtils.getBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, false)) runBiometricAuthenticate()
            else {
                if(mPreferenceUtils.getBooleanUserPreference(PreferenceCategory.User.SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION, false)) toMainActivity()
                else {
                    AskToUseBiometricAuthDialog(this){
                        useFingerprint, doNotAskToUseFingerprintAgain ->
                        mPreferenceUtils.setBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, useFingerprint)
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
                        mPreferenceUtils.setBooleanUserPreference(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION, false)
                            .setBooleanUserPreference(PreferenceCategory.User.SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION, true)
                        toMainActivity(false)
                    }
                }
            }
        }
    }

    private fun toMainActivity(showWelcomeText : Boolean = true) {
        // 유저 정보는 앱 전체에서 공용으로 사용하니 Intent 대신 Data Model 로 관리
        mUserAccountInfoModelManager.getUserAccountInfoModel().isAuthenticated = true
        if(showWelcomeText) Toast.makeText(this, getString(R.string.tst_auth_succeed, mUserAccountInfoModelManager.getUserAccountInfoModel().name), Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        Handler().postDelayed({ finish() }, 3000)
    }

}
