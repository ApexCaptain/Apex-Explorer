package com.ayteneve93.apexexplorer.view.main

import android.util.Log
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.ActivityMainBinding
import com.ayteneve93.apexexplorer.view.base.BaseActivity
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteFragment
import com.ayteneve93.apexexplorer.view.main.fragments.filelist.FileListFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel: MainViewModel by viewModel()


    override fun getLayoutId(): Int { return R.layout.activity_main }
    override fun getViewModel(): MainViewModel {
        mMainViewModel.setNavigator(this)
        return mMainViewModel
    }
    override fun getBindingVariable(): Int { return BR.viewModel }

    override fun setUp() {

        mViewDataBinding.mainViewPager.adapter = MainFragmentStateAdapter(this)

        /*
        mViewDataBinding.mainViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> FileListFragment.newInstance()
                    else -> FavoriteFragment.newInstance()
                }
            }

            override fun getItemCount(): Int {
                return 2
            }
        }
        */





    }


}
