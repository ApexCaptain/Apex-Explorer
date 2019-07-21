package com.ayteneve93.apexexplorer.view.entry

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayteneve93.apexexplorer.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.DataModelManager
import com.ayteneve93.apexexplorer.databinding.ActivityEntryBinding
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import com.ayteneve93.apexexplorer.view.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class EntryActivity : BaseActivity<ActivityEntryBinding, EntryViewModel>() {

    private val mEntryViewModel : EntryViewModel by viewModel()
    val mFireBaseAuthManager : FireBaseAuthManager by inject()
    val mDataModelManager : DataModelManager by inject()

    override fun getLayoutId(): Int { return R.layout.activity_entry }
    override fun getViewModel(): EntryViewModel { return mEntryViewModel }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {
        showTitleAnimation()
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
