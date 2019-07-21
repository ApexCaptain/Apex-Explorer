package com.ayteneve93.apexexplorer.view.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.library.baseAdapters.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.ActivityMainBinding
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel: MainViewModel by viewModel()
    private val mAlphaAnimationHandler = Handler(Looper.getMainLooper())

    private val mMainBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
        }
    }

    override fun getLayoutId(): Int { return R.layout.activity_main }
    override fun getViewModel(): MainViewModel {
        mMainViewModel.setNavigator(this)
        return mMainViewModel
    }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {
        setBroadcastReceiver()
        setViewPagerProperties()
    }

    private fun setBroadcastReceiver() {
        val mainIntentFilter = IntentFilter()
            // addAction
        registerReceiver(mMainBroadcastReceiver, mainIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
    }

    private fun setViewPagerProperties() {
        val alphaAnimDelayMills = 1500L
        val alphaDisappearAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_disappear)
        alphaDisappearAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                mViewDataBinding.viewPagerRubberIndicatorContainer.visibility = View.INVISIBLE
            }
            override fun onAnimationStart(p0: Animation?) {}
        })
        mViewDataBinding.viewPagerRubberIndicator.setCount(MainFragmentState.getCount())
        mViewDataBinding.mainViewPager.adapter = MainFragmentStateAdapter(this) {
            prevFragmentState, newFragmentState, isRight ->
            onFragmentChanged(prevFragmentState, newFragmentState)
            mViewDataBinding.viewPagerRubberIndicatorContainer.visibility = View.VISIBLE
            mViewDataBinding.viewPagerRubberIndicatorContainer.animation = null
            mAlphaAnimationHandler.removeCallbacksAndMessages(null)
            if(isRight) mViewDataBinding.viewPagerRubberIndicator.moveToRight()
            else mViewDataBinding.viewPagerRubberIndicator.moveToLeft()
            mAlphaAnimationHandler.postDelayed({
                mViewDataBinding.viewPagerRubberIndicatorContainer.startAnimation(alphaDisappearAnim)
            }, alphaAnimDelayMills)
        }
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
