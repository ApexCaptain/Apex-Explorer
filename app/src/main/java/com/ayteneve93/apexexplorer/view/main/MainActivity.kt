package com.ayteneve93.apexexplorer.view.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.library.baseAdapters.BR
import androidx.viewpager2.widget.ViewPager2
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.managers.UserAccountInfoModelManager
import com.ayteneve93.apexexplorer.databinding.ActivityMainBinding
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import me.relex.circleindicator.CircleIndicator3
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel: MainViewModel by viewModel()
    private val mAlphaAnimationHandler = Handler(Looper.getMainLooper())
    private val mUserAccountInfoModelManager : UserAccountInfoModelManager by inject()

    private val mMainBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {

            }
        }
    }

    override fun getLayoutId(): Int { return R.layout.activity_main }
    override fun getViewModel(): MainViewModel {
        return mMainViewModel
    }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {
        setBroadcastReceiver()
        setViewPagerProperties()
    }


    private fun setBroadcastReceiver() {
        val mainIntentFilter = IntentFilter()
        registerReceiver(mMainBroadcastReceiver, mainIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
        mUserAccountInfoModelManager.getUserAccountInfoModel().isAuthenticated = false
    }

    private var currentPageState : MainFragmentState = MainFragmentState.FILE_LIST
    private fun setViewPagerProperties() {
        val viewPager : ViewPager2 = mViewDataBinding.mainViewPager
        viewPager.adapter = MainFragmentStateAdapter(this)
        val indicator : CircleIndicator3 = mViewDataBinding.mainViewPagerIndicator
        indicator.setViewPager(viewPager)

        val alphaAnimDelayMills = 1500L
        val alphaDisappearAnim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_alpha_disappear)
        alphaDisappearAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                mMainViewModel.mIsViewChanging.set(false)
            }
            override fun onAnimationStart(p0: Animation?) {}
        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                mMainViewModel.mIsViewChanging.set(true)
                indicator.animation = null
                mAlphaAnimationHandler.removeCallbacksAndMessages(null)
                mAlphaAnimationHandler.postDelayed({
                    indicator.startAnimation(alphaDisappearAnim)
                }, alphaAnimDelayMills)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val newFragmentState = MainFragmentState.getState(position)
                onFragmentChanged(currentPageState, newFragmentState)
                currentPageState = newFragmentState
            }
        })

    }

    private fun onFragmentChanged(prevFragmentState: MainFragmentState, newFragmentState: MainFragmentState) {
        val selectedIntent = Intent().setAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED)
        val unSelectedIntent = Intent().setAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED)

        when(newFragmentState) {
            MainFragmentState.FILE_LIST -> {
                selectedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST)
            }
            MainFragmentState.FAVORITE -> {
                selectedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE)
            }
        }
        sendBroadcast(selectedIntent)

        when(prevFragmentState) {
            MainFragmentState.FILE_LIST -> {
                unSelectedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST)
            }
            MainFragmentState.FAVORITE -> {
                unSelectedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE)
            }
        }
        sendBroadcast(unSelectedIntent)

    }



}
