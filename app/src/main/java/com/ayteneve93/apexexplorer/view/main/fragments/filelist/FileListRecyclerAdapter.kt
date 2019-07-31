package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.data.managers.FileModelManager
import com.ayteneve93.apexexplorer.databinding.ItemFileListBinding
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference

class FileListRecyclerAdapter(private val mPreferenceUtils: PreferenceUtils, private val mFileModelManager: FileModelManager, private val application: Application) : RecyclerView.Adapter<FileListRecyclerAdapter.FileListViewHolder>() {

    private val mFileViewModelList : ArrayList<FileViewModel> = ArrayList()

    override fun getItemCount(): Int = mFileViewModelList.size

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val eachFileViewModel = mFileViewModelList[position]
        holder.apply {
            bind(eachFileViewModel)
            itemView.tag = eachFileViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        return FileListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_file_list, parent, false
            )
        )
    }

    private var fileClickedListener : ((fileModel : FileModel) -> Unit)? = null
    fun setFileClickedListener(fileClickedListener : (fileModel : FileModel) -> Unit) : FileListRecyclerAdapter {
        this.fileClickedListener = fileClickedListener
        return this
    }

    fun refresh(path : String?, onRefreshFinished : (isSucceed : Boolean, isEmpty : Boolean) -> Unit) {
        mFileModelManager.scanFileListFrom(path) {
                isSucceed, fileModelList ->
            if(isSucceed) {
                mFileViewModelList.clear()

                fileModelList?.sortWith(compareBy ({ it.iconResId }, {it.title}) )
                // Todo : 파일 정렬은 기본적으로 아이콘 별(확장자별) -> 이름 순으로 정함. 추후 사용자 옵션에 따라 변결될 수 있게끔 바꿔야함

                fileModelList?.forEach {
                    eachFileModel ->
                    mFileViewModelList.add(FileViewModel(application).apply {
                        mFileModel = eachFileModel
                        mThumbnail.set(mFileModelManager.getThumbnailUriFromModel(eachFileModel))
                        mSubTitle = if(eachFileModel.isDirectory) application.resources.getString(R.string.file_extensions_case_directory)
                        else "${kotlin.math.round(eachFileModel.size!! * 100) / 100} ${eachFileModel.sizeUnit}"
                        onFavoriteButtonClickListener = {
                            val currentFavoriteState = mFileModel.isFavorite.get()
                            if(currentFavoriteState!!) {
                                mPreferenceUtils.removeStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES, mFileModel.canonicalPath)
                                mFileModel.isFavorite.set(false)
                            } else {
                                mPreferenceUtils.addStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES, mFileModel.canonicalPath)
                                mFileModel.isFavorite.set(true)
                            }
                            application.sendBroadcast(Intent(MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED)
                                .putExtra(MainBroadcastPreference.FragmentToFragment.Who.KEY, MainBroadcastPreference.FragmentToFragment.Who.Values.FAVORITE))
                        }
                        onItemClickListener = {
                            fileClickedListener?.let {
                                it(mFileModel)
                            }
                        }
                        onItemLongClickListener = {
                            view ->
                            false
                        }
                    })
                }
                notifyDataSetChanged()
                onRefreshFinished(true, fileModelList?.size == 0)
            } else onRefreshFinished(false, true)
        }
    }

    fun searchByKeyword(rootPath : String, keyword : String, onSearchResult : (isEmpty : Boolean) -> Unit) {

        mFileViewModelList.clear()
        notifyDataSetChanged()
        mFileModelManager.rxSearchByKeyword(rootPath, keyword) {
                it.subscribe ({
                    mFileViewModelList.add(FileViewModel(application).apply {
                        mFileModel = it
                        mThumbnail.set(mFileModelManager.getThumbnailUriFromModel(it))
                        mSubTitle = if(it.isDirectory) application.resources.getString(R.string.file_extensions_case_directory)
                        else "${kotlin.math.round(it.size!! * 100) / 100} ${it.sizeUnit}"
                        onFavoriteButtonClickListener = {
                            val currentFavoriteState = mFileModel.isFavorite.get()
                            if(currentFavoriteState!!) {
                                mPreferenceUtils.removeStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES, mFileModel.canonicalPath)
                                mFileModel.isFavorite.set(false)
                            } else {
                                mPreferenceUtils.addStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES, mFileModel.canonicalPath)
                                mFileModel.isFavorite.set(true)
                            }
                            application.sendBroadcast(Intent(MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED)
                                .putExtra(MainBroadcastPreference.FragmentToFragment.Who.KEY, MainBroadcastPreference.FragmentToFragment.Who.Values.FAVORITE))
                        }
                        onItemClickListener = {
                            fileClickedListener?.let {
                                it(mFileModel)
                            }
                        }
                        onItemLongClickListener = {
                            view ->
                            false
                        }
                    })
                    notifyDataSetChanged()
                },
                {

                },
                {
                    onSearchResult(mFileViewModelList.isEmpty())
                }
            )
        }

    }

    class FileListViewHolder(
        private val binding : ItemFileListBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : FileViewModel) {
            binding.apply {
                viewModel = item
            }
        }
    }

}