package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.FragmentFavoriteBinding
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class FavoriteFragment : BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>() {

    private val mFavoriteViewModel : FavoriteViewModel by viewModel()
    private val mFavoriteListRecyclerAdapter : FavoriteListRecyclerAdapter by inject()

    private val mFavoriteBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context : Context?, intent : Intent?) {
            intent?.let {


                // 메인 액티비티에서 온 명령인 경우
                (it.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY))?.let {
                    target ->
                    if(target == MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE) {
                        when(it.action) {
                            MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED -> {
                                if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE) {
                                    mViewDataBinding.fragmentFavoriteRefresh.isRefreshing = false
                                }
                            }
                            MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED -> {
                                if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE) {
                                }
                            }
                        }
                    }
                }


                // 그 외 다른 프래그먼트에서 온 명령인 경우
                (it.getStringExtra(MainBroadcastPreference.FragmentToFragment.Who.KEY))?.let {
                    target ->
                    if(target == MainBroadcastPreference.FragmentToFragment.Who.Values.FAVORITE) {
                        when(it.action) {
                            MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED -> {
                                refreshFavoriteListStepTwo(false)
                            }
                            MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_EMPTY -> {
                                mFavoriteViewModel.mIsEmptyDirectory.set(true)
                            }
                        }
                    }
                }


            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_favorite
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): FavoriteViewModel {
        return mFavoriteViewModel
    }

    override fun setUp() {
        setBroadcastReceiver()
        setFavoriteListRecyclerAdapter()
        setFavoriteRefreshLayout()
        refreshFavoriteListStepTwo()
    }

    private fun setBroadcastReceiver() {
        activity?.registerReceiver(mFavoriteBroadcastReceiver, IntentFilter().also {
            arrayOf(

                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED,
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED,

                MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED,
                MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_EMPTY

            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mFavoriteBroadcastReceiver)
    }

    private fun setFavoriteListRecyclerAdapter() {
        val fragmentFavoriteRecyclerView = mViewDataBinding.fragmentFavoriteRecyclerView
        fragmentFavoriteRecyclerView.adapter = mFavoriteListRecyclerAdapter
        when(resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> fragmentFavoriteRecyclerView.layoutManager = GridLayoutManager(mActivity, 1)
            Configuration.ORIENTATION_LANDSCAPE -> fragmentFavoriteRecyclerView.layoutManager = GridLayoutManager(mActivity, 2)
        }
        fragmentFavoriteRecyclerView.addItemDecoration(DividerItemDecoration(fragmentFavoriteRecyclerView.context, DividerItemDecoration.VERTICAL))
    }

    private fun setFavoriteRefreshLayout() {
        mViewDataBinding.fragmentFavoriteRefresh.setOnRefreshListener {
            refreshFavoriteListStepOne()
        }
    }

    private val alphaAnimDuration = 300L
    private fun refreshFavoriteListStepOne(useRefresh : Boolean = true) {
        val alphaDisappearAnim = AnimationUtils.loadAnimation(mActivity, R.anim.anim_alpha_disappear)
        alphaDisappearAnim.duration = alphaAnimDuration
        alphaDisappearAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                mFavoriteViewModel.mShouldRecyclerViewInvisible.set(true)
                refreshFavoriteListStepTwo()
            }
            override fun onAnimationStart(p0: Animation?) {
                if(!mViewDataBinding.fragmentFavoriteRefresh.isRefreshing
                    && useRefresh)
                    mViewDataBinding.fragmentFavoriteRefresh.isRefreshing = true
            }
        })
        mViewDataBinding.fragmentFavoriteRecyclerView.startAnimation(alphaDisappearAnim)
    }

    private fun refreshFavoriteListStepTwo(useRefresh : Boolean = true) {
        val alphaAppearAnim = AnimationUtils.loadAnimation(mActivity, R.anim.anim_alpha_appear)
        alphaAppearAnim.duration = alphaAnimDuration
        if(!mViewDataBinding.fragmentFavoriteRefresh.isRefreshing && useRefresh) mViewDataBinding.fragmentFavoriteRefresh.isRefreshing = true
        mFavoriteListRecyclerAdapter.refresh(mActivity!!) {
            isEmpty ->
            mFavoriteViewModel.mShouldRecyclerViewInvisible.set(false)
            mViewDataBinding.fragmentFavoriteRecyclerView.startAnimation(alphaAppearAnim)
            mViewDataBinding.fragmentFavoriteRefresh.isRefreshing = false
            mFavoriteViewModel.mIsEmptyDirectory.set(isEmpty)
        }

    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }

}