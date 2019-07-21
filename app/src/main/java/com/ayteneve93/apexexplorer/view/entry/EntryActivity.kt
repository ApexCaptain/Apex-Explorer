package com.ayteneve93.apexexplorer.view.entry

import android.animation.Animator
import android.content.Intent
import android.widget.Toast
import com.ayteneve93.apexexplorer.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.ActivityEntryBinding
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import com.ayteneve93.apexexplorer.view.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class EntryActivity : BaseActivity<ActivityEntryBinding, EntryViewModel>() {

    private val mEntryViewModel : EntryViewModel by viewModel()
    val mFireBaseAuthManager : FireBaseAuthManager by inject()

    override fun getLayoutId(): Int { return R.layout.activity_entry }
    override fun getViewModel(): EntryViewModel { return mEntryViewModel }
    override fun getBindingVariable(): Int { return BR.viewModel }

    var mIntroAnimationRepeatCount = 0
    var mIsAuthenticationStarted = false
    override fun setUp() {
        // 사설 애니메이션 라이브러리인 Lottie 는 View Model 로 제어가 불가능(미지원) 하므로 이 부분은 액티비티 단에서 직접 제어
        mViewDataBinding.entryAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
                if(mIntroAnimationRepeatCount < 2)
                    mIntroAnimationRepeatCount++
                if(mIntroAnimationRepeatCount == 2 && !mIsAuthenticationStarted) {
                    mIsAuthenticationStarted = true;
                    mEntryViewModel.mIsOnProgress.set(true)
                    runFireBaseAuthenticate()
                }
            }
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
        })
    }

    private fun runFireBaseAuthenticate() {

        mFireBaseAuthManager.requestGoogleFireBaseAuthentication(this) {
                isSucceed, account ->
            if(isSucceed) {
                mEntryViewModel.mIsOnProgress.set(false)
                Toast.makeText(this, R.string.tst_firebase_auth_succeed, Toast.LENGTH_SHORT).show()
                toMainActivity()
            } else {
                Toast.makeText(this, R.string.tst_firebase_auth_failure, Toast.LENGTH_SHORT).show()
                runFireBaseAuthenticate()
            }
        }
    }

    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }


}
