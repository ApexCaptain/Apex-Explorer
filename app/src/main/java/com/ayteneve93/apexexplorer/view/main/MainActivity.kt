package com.ayteneve93.apexexplorer.view.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.databinding.library.baseAdapters.BR
import androidx.viewpager2.widget.ViewPager2
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.ActivityMainBinding
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import me.liangfei.indicator.RubberIndicator
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel: MainViewModel by viewModel()
    private val mAlphaAnimationHandler = Handler(Looper.getMainLooper())

    private val mMainBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {

            }
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
        //mainIntentFilter.addAction("stop")
        //mainIntentFilter.addAction("continue")
            // 인텐트 필터에 액션 추가해야 함 (현재 정해진 사항 없음)
        registerReceiver(mMainBroadcastReceiver, mainIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
    }

    private var currentPageState : MainFragmentState = MainFragmentState.FILE_LIST
    private fun setViewPagerProperties() {

        val viewPager : ViewPager2 = mViewDataBinding.mainViewPager
        val viewPagerIndicatorContainer : LinearLayout = mViewDataBinding.mainViewPagerRubberIndicatorContainer
        val viewPagerRubberIndicator : RubberIndicator = mViewDataBinding.mainViewPagerRubberIndicator

        val alphaAnimDelayMills = 1500L
        val alphaDisappearAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_disappear)
        alphaDisappearAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                viewPagerIndicatorContainer.visibility = View.INVISIBLE
            }
            override fun onAnimationStart(p0: Animation?) {}
        })
        viewPagerRubberIndicator.setCount(MainFragmentState.getCount())
        viewPager.adapter = MainFragmentStateAdapter(this)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if(currentPageState.ordinal != position) {
                    viewPagerIndicatorContainer.visibility = View.VISIBLE
                    viewPagerIndicatorContainer.animation = null
                    mAlphaAnimationHandler.removeCallbacksAndMessages(null)

                    if(currentPageState.ordinal > position) viewPagerRubberIndicator.moveToLeft()
                    else viewPagerRubberIndicator.moveToRight()

                    onFragmentChanged(currentPageState, MainFragmentState.getState(position))

                    currentPageState = MainFragmentState.getState(position)

                    mAlphaAnimationHandler.postDelayed({
                        viewPagerIndicatorContainer.startAnimation(alphaDisappearAnim)
                    }, alphaAnimDelayMills)
                }
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
