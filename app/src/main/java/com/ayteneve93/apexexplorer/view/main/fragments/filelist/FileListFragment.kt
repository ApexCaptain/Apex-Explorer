package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.databinding.library.baseAdapters.BR

import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.FragmentFileListBinding
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference
import org.koin.android.viewmodel.ext.android.viewModel

class FileListFragment : BaseFragment<FragmentFileListBinding, FileListViewModel>() {

    private val mFileListViewModel : FileListViewModel by viewModel()

    private val mFileListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context : Context?, intent : Intent?) {
            when(intent?.action) {
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED -> {
                    if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST) {
                        Log.d("ayteneve93_test", "file list selected")
                    }
                }
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED -> {
                    if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST) {
                        Log.d("ayteneve93_test", "file list unselected")
                    }
                }
                null -> return
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_file_list
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): FileListViewModel {
        return mFileListViewModel
    }

    override fun setUp() {
        setBroadcastReceiver()
    }

    private fun setBroadcastReceiver() {
        val fileListIntentFilter = IntentFilter()
        fileListIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED)
        fileListIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED)
        activity?.registerReceiver(mFileListBroadcastReceiver, fileListIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mFileListBroadcastReceiver)
    }

    companion object {
        fun newInstance() = FileListFragment()
    }


}
