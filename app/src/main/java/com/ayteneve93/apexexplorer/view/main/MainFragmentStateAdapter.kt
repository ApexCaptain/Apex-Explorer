package com.ayteneve93.apexexplorer.view.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteFragment
import com.ayteneve93.apexexplorer.view.main.fragments.filelist.FileListFragment

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

class MainFragmentStateAdapter(private val activity : AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return when(MainFragmentState.getState(position)) {
            MainFragmentState.FILE_LIST -> FileListFragment.newInstance()
            MainFragmentState.FAVORITE -> FavoriteFragment.newInstance()
        }
    }

    override fun getItemCount() : Int = MainFragmentState.getCount()
}