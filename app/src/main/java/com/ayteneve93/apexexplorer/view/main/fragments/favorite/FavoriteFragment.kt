package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import androidx.databinding.library.baseAdapters.BR
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.FragmentFavoriteBinding
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel


class FavoriteFragment : BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>() {

    private val mFavoriteViewModel : FavoriteViewModel by viewModel()

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

    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }

}