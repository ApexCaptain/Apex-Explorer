package com.ayteneve93.apexexplorer.view.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteFragment
import com.ayteneve93.apexexplorer.view.main.fragments.filelist.FileListFragment

class MainFragmentStateAdapter(private val activity : AppCompatActivity, private val onFragmentChanged : (prevFragmentState : MainFragmentState, newFragmentState : MainFragmentState, isRight : Boolean) -> Unit) : FragmentStateAdapter(activity) {

    private var mCurrentPosition : Int = MainFragmentState.FILE_LIST.ordinal

    override fun createFragment(position: Int): Fragment {
        return when(MainFragmentState.getState(position)) {
            MainFragmentState.FILE_LIST -> FileListFragment.newInstance()
            MainFragmentState.FAVORITE -> FavoriteFragment.newInstance()
        }
    }

    override fun getItemId(position: Int): Long {
        if(position != mCurrentPosition) {
            onFragmentChanged(
                MainFragmentState.getState(mCurrentPosition),
                MainFragmentState.getState(position),
                mCurrentPosition < position
            )
            mCurrentPosition = position
        }
        return super.getItemId(position)
    }



    override fun getItemCount() : Int = MainFragmentState.getCount()


}