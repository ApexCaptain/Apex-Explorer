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
            when(intent?.action) {
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED -> {
                    if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE) {
                    }
                }
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED -> {
                    if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FAVORITE) {
                    }
                }
                null -> return
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
    }

    private fun setBroadcastReceiver() {
        val favoriteIntentFilter = IntentFilter()
        favoriteIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED)
        favoriteIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED)
        activity?.registerReceiver(mFavoriteBroadcastReceiver, favoriteIntentFilter)
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
        mFavoriteListRecyclerAdapter.refresh()
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }

}