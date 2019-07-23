package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.databinding.library.baseAdapters.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.FragmentFavoriteBinding
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference
import org.koin.android.viewmodel.ext.android.viewModel


class FavoriteFragment : BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>() {

    private val mFavoriteViewModel : FavoriteViewModel by viewModel()

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

    companion object {
        fun newInstance() = FavoriteFragment()
    }

}