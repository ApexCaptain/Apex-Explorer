package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.content.*
import android.net.Uri
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.data.managers.FileModelManager
import com.ayteneve93.apexexplorer.databinding.FragmentFileListBinding
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.ayteneve93.apexexplorer.view.base.BaseFragment
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class FileListFragment : BaseFragment<FragmentFileListBinding, FileListViewModel>() {

    private val mFileListViewModel : FileListViewModel by viewModel()
    val mFileListRecyclerAdapter : FileListRecyclerAdapter by inject()
    val mPreferenceUtils : PreferenceUtils by inject()
    val mFileModelManager : FileModelManager by inject()
    private var mCurrentPath : String? = mPreferenceUtils.getStringUserPreference(PreferenceCategory.User.LAST_VIEWED_FOLDER_NAME, null)


    private val mFileListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context : Context?, intent : Intent?) {
            when(intent?.action) {
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED -> {
                    if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST) {
                    }
                }
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED -> {
                    if(intent.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY) == MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST) {
                        mViewDataBinding.fragmentFileListRefresh.isRefreshing = false
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
        setFilesRecyclerAdapter()
        setFilesRefreshLayout()
        refreshFileListStepTwo(mCurrentPath)
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

    private fun setFilesRecyclerAdapter() {
        val recyclerView : RecyclerView = mViewDataBinding.fragmentFileListRecyclerView
        recyclerView.adapter = mFileListRecyclerAdapter.setFileClickedListener { onNewFileModelSelected(it) }
        recyclerView.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
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
        mCurrentPath = path
        if(!mViewDataBinding.fragmentFileListRefresh.isRefreshing && useRefresh) mViewDataBinding.fragmentFileListRefresh.isRefreshing = true
        mFileListRecyclerAdapter.refresh(mCurrentPath) {
            isSucceed, isEmpty ->
            mFileListViewModel.mShouldRecyclerViewInvisible.set(false)
            mViewDataBinding.fragmentFileListRecyclerView.startAnimation(alphaAppearAnim)
            mViewDataBinding.fragmentFileListRefresh.isRefreshing = false
            mFileListViewModel.mIsEmptyDirectory.set(isEmpty)
        }
    }

    private fun onNewFileModelSelected(fileModel : FileModel) {
        if(fileModel.isDirectory)
            refreshFileListStepOne(fileModel.canonicalPath, false)
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
