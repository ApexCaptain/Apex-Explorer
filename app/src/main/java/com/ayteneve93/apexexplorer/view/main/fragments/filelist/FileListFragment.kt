package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.data.managers.FileModelManager
import com.ayteneve93.apexexplorer.databinding.FragmentFileListBinding
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import com.ayteneve93.apexexplorer.view.main.MainActivity
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference
import com.ayteneve93.apexexplorer.view.main.MainFragmentState
import com.ayteneve93.apexexplorer.view.main.PathRecyclerAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class FileListFragment : BaseFragment<FragmentFileListBinding, FileListViewModel>() {

    private val mFileListViewModel : FileListViewModel by viewModel()
    private val mFileListRecyclerAdapter : FileListRecyclerAdapter by inject()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private val mFileModelManager : FileModelManager by inject()
    private var mCurrentPath : String? = mPreferenceUtils.getStringUserPreference(PreferenceCategory.User.LAST_VIEWED_FOLDER_NAME, null)

    private val mFileListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context : Context?, intent : Intent?) {
            intent?.let {
                (it.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY))?.let {
                    target ->
                    if(target == MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST) {
                        when(it.action) {
                            MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED -> {
                            }
                            MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED -> {

                            }
                            MainBroadcastPreference.MainToFragment.Action.BACK_BUTTON_PRESSED -> {
                                if(mCurrentPath != null && mCurrentPath != Environment.getExternalStorageDirectory().path)
                                    refreshFileListStepTwo(File(mCurrentPath).parent, false)
                                else (mActivity as MainActivity).terminateActivity()
                            }
                            MainBroadcastPreference.MainToFragment.Action.PATH_CLICKED -> {
                                val clickedPath = it.getStringExtra(MainBroadcastPreference.MainToFragment.Path.KEY)
                                refreshFileListStepTwo(clickedPath, false)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when(newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> mViewDataBinding.fragmentFileListRecyclerView.layoutManager = GridLayoutManager(mActivity, 1)
            Configuration.ORIENTATION_LANDSCAPE -> mViewDataBinding.fragmentFileListRecyclerView.layoutManager = GridLayoutManager(mActivity, 2)
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
        setFileListRecyclerAdapter()
        setFilesRefreshLayout()
        refreshFileListStepTwo(mCurrentPath)
    }


    private fun setBroadcastReceiver() {
        val fileListIntentFilter = IntentFilter()
        fileListIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED)
        fileListIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED)
        fileListIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.BACK_BUTTON_PRESSED)
        fileListIntentFilter.addAction(MainBroadcastPreference.MainToFragment.Action.PATH_CLICKED)
        activity?.registerReceiver(mFileListBroadcastReceiver, fileListIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mFileListBroadcastReceiver)
    }

    private fun setFileListRecyclerAdapter() {
        val fragmentFileListRecyclerView : RecyclerView = mViewDataBinding.fragmentFileListRecyclerView
        fragmentFileListRecyclerView.adapter = mFileListRecyclerAdapter.setFileClickedListener { onNewFileModelSelected(it) }
        when(resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> fragmentFileListRecyclerView.layoutManager = GridLayoutManager(mActivity, 1)
            Configuration.ORIENTATION_LANDSCAPE -> fragmentFileListRecyclerView.layoutManager = GridLayoutManager(mActivity, 2)
        }
        //fragmentFileListRecyclerView.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        fragmentFileListRecyclerView.addItemDecoration(DividerItemDecoration(fragmentFileListRecyclerView.context, DividerItemDecoration.VERTICAL))
    }

    private fun setFilesRefreshLayout() {
        mViewDataBinding.fragmentFileListRefresh.setOnRefreshListener {
            refreshFileListStepOne(mCurrentPath)
        }
    }

    private val alphaAnimDuration = 300L
    private fun refreshFileListStepOne(path : String?, useRefresh : Boolean = true) {
        val alphaDisappearAnim = AnimationUtils.loadAnimation(mActivity, R.anim.anim_alpha_disappear)
        alphaDisappearAnim.duration = alphaAnimDuration
        alphaDisappearAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                mFileListViewModel.mShouldRecyclerViewInvisible.set(true)
                refreshFileListStepTwo(path, useRefresh)
            }
            override fun onAnimationStart(animation: Animation?) {
                if(!mViewDataBinding.fragmentFileListRefresh.isRefreshing
                    && useRefresh)
                    mViewDataBinding.fragmentFileListRefresh.isRefreshing = true
            }
        })
        mViewDataBinding.fragmentFileListRecyclerView.startAnimation(alphaDisappearAnim)
    }

    private fun refreshFileListStepTwo(path : String?, useRefresh: Boolean = true) {
        val alphaAppearAnim = AnimationUtils.loadAnimation(mActivity, R.anim.anim_alpha_appear)
        alphaAppearAnim.duration = alphaAnimDuration
        if(!mViewDataBinding.fragmentFileListRefresh.isRefreshing && useRefresh) mViewDataBinding.fragmentFileListRefresh.isRefreshing = true
        mFileListRecyclerAdapter.refresh(path) {
            isSucceed, isEmpty ->
            mFileListViewModel.mShouldRecyclerViewInvisible.set(false)
            mViewDataBinding.fragmentFileListRecyclerView.startAnimation(alphaAppearAnim)
            mViewDataBinding.fragmentFileListRefresh.isRefreshing = false
            if(isSucceed) {
                mCurrentPath = path
                mFileListViewModel.mIsEmptyDirectory.set(isEmpty)
                (mActivity as MainActivity).refreshPathRecyclerView(mCurrentPath)
            } else {
                Toast.makeText(mActivity, R.string.unsupported_file_extension, Toast.LENGTH_LONG).show()
                refreshFileListStepTwo(mCurrentPath, false)
            }
        }
    }

    private fun onNewFileModelSelected(fileModel : FileModel) {
        if(fileModel.isDirectory) {
            refreshFileListStepOne(fileModel.canonicalPath, false)
        }
        else {
            val fileViewIntent = mFileModelManager.generateViewIntentFromModel(fileModel)
            if(fileViewIntent == null) Toast.makeText(mActivity, R.string.unsupported_file_extension, Toast.LENGTH_LONG).show()
            else try {
                startActivity(fileViewIntent)
            } catch (exception : ActivityNotFoundException) {
                Toast.makeText(mActivity, R.string.unsupported_file_extension, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance() = FileListFragment()
    }


}
