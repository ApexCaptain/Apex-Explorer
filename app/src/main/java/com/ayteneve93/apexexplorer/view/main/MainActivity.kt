package com.ayteneve93.apexexplorer.view.main

import android.util.Log
import androidx.databinding.library.baseAdapters.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.ActivityMainBinding
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator {

    private val mMainViewModel: MainViewModel by viewModel()

    override fun getLayoutId(): Int { return R.layout.activity_main }
    override fun getViewModel(): MainViewModel {
        mMainViewModel.setNavigator(this)
        return mMainViewModel
    }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {
        /*
        val host = supportFragmentManager.findFragmentById(R.id.main_navigation_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(main_bottom_navigation_view, host.navController)
        */
    }

    override fun test() {
        Log.d("ayteneve93_test", "test")
    }

}
