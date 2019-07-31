package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.content.*
import android.content.res.Configuration
import android.os.Environment
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
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
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class FileListFragment : BaseFragment<FragmentFileListBinding, FileListViewModel>() {

    private val mFileListViewModel : FileListViewModel by viewModel()
    private val mFileListRecyclerAdapter : FileListRecyclerAdapter by inject()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private val mFileModelManager : FileModelManager by inject()
    private var mCurrentPath : String? = mPreferenceUtils.getStringUserPreference(PreferenceCategory.User.LAST_VIEWED_FOLDER_NAME, null)
    private var mIsSearchMode = false

    private val mFileListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context : Context?, intent : Intent?) {
            intent?.let {


                // 메인 액티비티에서 온 명령인 경우
                (it.getStringExtra(MainBroadcastPreference.MainToFragment.Who.KEY))?.let {
                    target ->
                    if(target == MainBroadcastPreference.MainToFragment.Who.Values.FILE_LIST) {
                        when(it.action) {
                            MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED -> {
                                mViewDataBinding.fragmentFileListRefresh.isRefreshing = false
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
                            MainBroadcastPreference.MainToFragment.Action.SEARCH -> {
                                mIsSearchMode = true
                                mViewDataBinding.fragmentFileListRefresh.isRefreshing = true
                                mFileListRecyclerAdapter.searchByKeyword(mCurrentPath?:Environment.getExternalStorageDirectory().path, intent.getStringExtra(MainBroadcastPreference.MainToFragment.Keyword.KEY)) {
                                    isEmpty ->
                                    if(isEmpty) mFileListViewModel.mNoContentString.value = getString(R.string.no_search_result)
                                    mFileListViewModel.mIsEmptyDirectory.set(isEmpty)
                                    mViewDataBinding.fragmentFileListRefresh.isRefreshing = false
                                    mViewDataBinding.fragmentFileListRefresh.isEnabled = false
                                }
                            }
                            MainBroadcastPreference.MainToFragment.Action.SEARCH_FINISHED -> {
                                mIsSearchMode = false
                                mViewDataBinding.fragmentFileListRefresh.isEnabled = true
                                refreshFileListStepTwo(mCurrentPath, false)
                            }
                        }
                    }
                }


                // 다른 프래그먼트에서 온 경우
                (it.getStringExtra(MainBroadcastPreference.FragmentToFragment.Who.KEY))?.let {
                    target ->
                    if(target == MainBroadcastPreference.FragmentToFragment.Who.Values.FILE_LIST) {
                        when(it.action) {
                            MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED -> {
                                refreshFileListStepTwo(mCurrentPath, false)
                            }
                        }
                    }
                }

                // 프래그먼트에서 전체 호출
                when(it.action) {
                    MainBroadcastPreference.FragmentToAll.Action.FAVORITE_ITEM_SELECTED -> {
                        val filePath = it.getStringExtra(MainBroadcastPreference.FragmentToAll.FilePath.Key)
                        refreshFileListStepOne(filePath,false)
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
        activity?.registerReceiver(mFileListBroadcastReceiver, IntentFilter().also {
            arrayOf(

                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_SELECTED,
                MainBroadcastPreference.MainToFragment.Action.FRAGMENT_UNSELECTED,
                MainBroadcastPreference.MainToFragment.Action.BACK_BUTTON_PRESSED,
                MainBroadcastPreference.MainToFragment.Action.PATH_CLICKED,
                MainBroadcastPreference.MainToFragment.Action.SEARCH,
                MainBroadcastPreference.MainToFragment.Action.SEARCH_FINISHED,

                MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED,
                MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_EMPTY,

                MainBroadcastPreference.FragmentToAll.Action.FAVORITE_ITEM_SELECTED

            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
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
        fragmentFileListRecyclerView.addItemDecoration(DividerItemDecoration(fragmentFileListRecyclerView.context, DividerItemDecoration.VERTICAL))
    }

    private fun setFilesRefreshLayout() {
        mViewDataBinding.fragmentFileListRefresh.setOnRefreshListener {
            if(!mIsSearchMode)refreshFileListStepOne(mCurrentPath)
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
                if(isEmpty) mFileListViewModel.mNoContentString.value = getString(R.string.directory_no_content)
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
