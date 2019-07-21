package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR

import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.databinding.FragmentFileListBinding
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class FileListFragment : BaseFragment<FragmentFileListBinding, FileListViewModel>() {

    private val mFileListViewModel : FileListViewModel by viewModel()

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
    }

    companion object {
        fun newInstance() = FileListFragment()
    }

}
