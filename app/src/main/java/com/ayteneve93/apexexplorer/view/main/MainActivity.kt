package com.ayteneve93.apexexplorer.view.main

import android.app.Activity
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.managers.UserAccountInfoModelManager
import com.ayteneve93.apexexplorer.databinding.ActivityMainBinding
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.relex.circleindicator.CircleIndicator3
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel: MainViewModel by viewModel()
    private val mAlphaAnimationHandler = Handler(Looper.getMainLooper())
    private val mUserAccountInfoModelManager : UserAccountInfoModelManager by inject()
    private val mPathRecyclerAdapter : PathRecyclerAdapter by inject()
    private lateinit var mSearchView : SearchView
    private var mIsFileListSearchMode = false
    private var mIsFavoriteSearchMode = false
    private var mSearchKeyword = ""

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
        setToolBar()
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
        if(!mSearchView.isIconified) {
            mSearchView.isIconified = true
            return
        }
        if(mIsFileListSearchMode && mCurrentMainFragmentState == MainFragmentState.FILE_LIST) {
            sendBroadcast(Intent(MainBroadcastPreference.MainToFragment.Action.SEARCH_FINISHED)
                .putExtra(MainBroadcastPreference.MainToFragment.Who.KEY, MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST))
            mPathRecyclerAdapter.refresh(mCurrentPath)
            mIsFileListSearchMode = false
            return
        }
        if(mIsFavoriteSearchMode && mCurrentMainFragmentState == MainFragmentState.FAVORITE) {
            sendBroadcast(Intent(MainBroadcastPreference.MainToFragment.Action.SEARCH_FINISHED)
                .putExtra(MainBroadcastPreference.MainToFragment.Who.KEY, MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE))
            mPathRecyclerAdapter.setToFavorite()
            mIsFavoriteSearchMode = false
            return
        }

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
                if(mIsFileListSearchMode) mPathRecyclerAdapter.setToSearch(mSearchKeyword)
                else mPathRecyclerAdapter.refresh(mCurrentPath)
                selectedIntent.putExtra(MainBroadcastPreference.MainToFragment.Who.KEY,
                    MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST)
            }
            MainFragmentState.FAVORITE -> {
                if(mIsFavoriteSearchMode) mPathRecyclerAdapter.setToSearch(mSearchKeyword)
                else mPathRecyclerAdapter.setToFavorite()
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

    private fun setToolBar() {
        setSupportActionBar(mViewDataBinding.mainToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.ic_app_icon_mini)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_app_bar, menu)
        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mSearchView = menu!!.findItem(R.id.menu_main_app_bar_search).actionView as SearchView
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        mSearchView.queryHint = getString(R.string.search_in_my_files, getString(
            when(mCurrentMainFragmentState) {
                MainFragmentState.FILE_LIST -> R.string.root_directory_title
                MainFragmentState.FAVORITE -> R.string.favorites_title
        }))

        mSearchView.setOnQueryTextFocusChangeListener {
            view, isFocused ->
            mMainViewModel.mViewPagerVisibility.set(!isFocused)
        }

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow((currentFocus?:View(this@MainActivity)).windowToken, 0)
                query?.let {
                    sendBroadcast(Intent(MainBroadcastPreference.MainToFragment.Action.SEARCH)
                        .putExtra(MainBroadcastPreference.MainToFragment.Who.KEY, when(mCurrentMainFragmentState) {
                            MainFragmentState.FILE_LIST -> {
                                mIsFileListSearchMode = true
                                MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST
                            }
                            MainFragmentState.FAVORITE -> {
                                mIsFavoriteSearchMode = true
                                MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE
                            } })
                        .putExtra(MainBroadcastPreference.MainToFragment.Keyword.KEY, it))
                    mPathRecyclerAdapter.setToSearch(it)
                    mMainViewModel.mViewPagerVisibility.set(true)
                    mSearchKeyword = it
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean { return true }
        })
        mSearchView.setOnCloseListener {
            sendBroadcast(Intent(MainBroadcastPreference.MainToFragment.Action.SEARCH_FINISHED)
                .putExtra(MainBroadcastPreference.MainToFragment.Who.KEY, when(mCurrentMainFragmentState) {
                    MainFragmentState.FILE_LIST -> {
                        mIsFileListSearchMode = false
                        mPathRecyclerAdapter.refresh(mCurrentPath)
                        MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST
                    }
                    MainFragmentState.FAVORITE -> {
                        mIsFavoriteSearchMode = false
                        mPathRecyclerAdapter.setToFavorite()
                        MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE
                    }
                }))
            mMainViewModel.mViewPagerVisibility.set(true)
            false
        }
        return true
    }

}
