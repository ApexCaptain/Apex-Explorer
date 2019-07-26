package com.ayteneve93.apexexplorer.view.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val mPathRecyclerAdapter : PathRecyclerAdapter by inject()

    private var mCurrentMainFragmentState : MainFragmentState = MainFragmentState.FILE_LIST

    private val mMainBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {

                // 프래그먼트로부터 전체 호출
                when(it.action) {
                    MainBroadcastPreference.FragmentToAll.Action.FAVORITE_ITEM_SELECTED -> {
                        mViewDataBinding.mainViewPager.currentItem = MainFragmentState.FILE_LIST.ordinal
                    }
                }

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
        setPathRecyclerView()
    }


    private fun setBroadcastReceiver() {
        val mainIntentFilter = IntentFilter()
        registerReceiver(mMainBroadcastReceiver, mainIntentFilter)
        registerReceiver(mMainBroadcastReceiver, IntentFilter().also {
            arrayOf(

                MainBroadcastPreference.FragmentToAll.Action.FAVORITE_ITEM_SELECTED

            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
        mUserAccountInfoModelManager.getUserAccountInfoModel().isAuthenticated = false
    }

    override fun onBackPressed() {
        when(mCurrentMainFragmentState) {
            MainFragmentState.FILE_LIST -> {
                val backButtonPressedIntent = Intent().setAction(MainBroadcastPreference.MainToFragment.Action.BACK_BUTTON_PRESSED)
                backButtonPressedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST)
                sendBroadcast(backButtonPressedIntent)
            }
            MainFragmentState.FAVORITE -> {
                mViewDataBinding.mainViewPager.currentItem = MainFragmentState.FILE_LIST.ordinal
            }
        }
    }

    private var applicationTerminatingFlag = false
    private val applicationTerminatingHandler = Handler()
    fun terminateActivity() {
        if(!applicationTerminatingFlag) {
            Toast.makeText(this, R.string.press_again_to_exist, Toast.LENGTH_LONG).show()
            applicationTerminatingFlag = true
            applicationTerminatingHandler.removeCallbacksAndMessages(null)
            applicationTerminatingHandler.postDelayed({
                applicationTerminatingFlag = false
            }, 3000)
            return
        }
        finish()
    }

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
                onFragmentChanged(mCurrentMainFragmentState, newFragmentState)
                mCurrentMainFragmentState = newFragmentState
            }
        })

    }

    private fun onFragmentChanged(prevFragmentState: MainFragmentState, newFragmentState: MainFragmentState) {
        val selectedIntent = Intent().setAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED)
        val unSelectedIntent = Intent().setAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED)

        when(newFragmentState) {
            MainFragmentState.FILE_LIST -> {
                mPathRecyclerAdapter.refresh(mCurrentPath)
                selectedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST)
            }
            MainFragmentState.FAVORITE -> {
                mPathRecyclerAdapter.setToFavorite()
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

    private fun setPathRecyclerView() {
        val pathRecyclerView : RecyclerView = mViewDataBinding.mainPathRecyclerView
        pathRecyclerView.adapter = mPathRecyclerAdapter.setPathClickedListener {
            sendBroadcast(
            Intent().setAction(MainBroadcastPreference.MainToFragment.Action.PATH_CLICKED)
                .putExtra(MainBroadcastPreference.MainToFragment.Who.KEY, MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST)
                .putExtra(MainBroadcastPreference.MainToFragment.Path.KEY, it)
            )
        }
        pathRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }

    private var mCurrentPath : String? = null
    fun refreshPathRecyclerView(path : String?) {
        mCurrentPath = path
        when(mCurrentMainFragmentState) {
            MainFragmentState.FILE_LIST -> mPathRecyclerAdapter.refresh(path)
            MainFragmentState.FAVORITE -> mPathRecyclerAdapter.setToFavorite()
        }
    }



}
