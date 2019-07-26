package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.managers.FileModelManager
import com.ayteneve93.apexexplorer.databinding.ItemFavoriteBinding
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.ayteneve93.apexexplorer.view.main.MainBroadcastPreference


class FavoriteListRecyclerAdapter(
    private val mPreferenceUtils: PreferenceUtils,
    private val mFileModelManager: FileModelManager,
    private val application: Application) : RecyclerView.Adapter<FavoriteListRecyclerAdapter.FavoriteListViewHolder>(){

    private val mFavoriteViewModelList : ArrayList<FavoriteFileViewModel> = ArrayList()

    override fun getItemCount() : Int = mFavoriteViewModelList.size

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        val eachFavoriteFileViewModel = mFavoriteViewModelList[position]
        holder.apply {
            bind(eachFavoriteFileViewModel)
            itemView.tag = eachFavoriteFileViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        return FavoriteListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_favorite, parent, false
            )
        )
    }

    private val sizeCalculate = Handler()
    private val sizeCalculateDelay = 1000L
    fun refresh(activity : AppCompatActivity, onRefreshFinished : (isEmpty : Boolean) -> Unit) {
        mFavoriteViewModelList.clear()
        sizeCalculate.removeCallbacksAndMessages(null)
        val favoriteFilePaths = mPreferenceUtils.getStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES)
        if(favoriteFilePaths.isEmpty()) {
            notifyDataSetChanged()
            onRefreshFinished(true)
            return
        }
        val fileCount = favoriteFilePaths.size
        var scanCompletedCount = 0

        favoriteFilePaths.forEach {
            eachPath ->
            mFileModelManager.getFileInfoFrom(eachPath) {
                isSucceed, fileModel ->
                scanCompletedCount++
                if(isSucceed) {

                    mFavoriteViewModelList.add(FavoriteFileViewModel(application).apply {
                        mFileModel = fileModel!!
                        mThumbnail.set(mFileModelManager.getThumbnailUriFromModel(fileModel))
                        mPath = application.getString(R.string.item_favorite_path, mFileModel.canonicalPath.replace(Environment.getExternalStorageDirectory().path, application.getString(R.string.item_path_home_directory)))
                        mExtension = if(mFileModel.isDirectory) application.getString(R.string.file_extensions_case_directory)
                        else application.getString(R.string.item_favorite_extension, mFileModel.extension)
                        mSizeAndUnit.set(
                            if(!mFileModel.isDirectory) application.getString(R.string.item_favorite_size,"${kotlin.math.round(mFileModel.size!! * 100) / 100} ${mFileModel.sizeUnit}")
                            else application.getString(R.string.item_favorite_size, application.getString(R.string.item_favorite_calculating))
                        )
                        onItemClickListener = {
                            if(mFileModel.isDirectory) {
                                application.sendBroadcast(Intent(MainBroadcastPreference.FragmentToAll.Action.FAVORITE_ITEM_SELECTED)
                                    .putExtra(MainBroadcastPreference.FragmentToAll.FilePath.Key, mFileModel.canonicalPath))
                            } else {
                                val fileViewIntent = mFileModelManager.generateViewIntentFromModel(mFileModel)
                                if(fileViewIntent == null) Toast.makeText(activity, R.string.unsupported_file_extension, Toast.LENGTH_LONG).show()
                                else try {
                                    activity.startActivity(fileViewIntent)
                                } catch (exception : ActivityNotFoundException) {
                                    Toast.makeText(activity, R.string.unsupported_file_extension, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        onDeleteButtonClickListener = {
                            mFavoriteViewModelList.remove(this)
                            mPreferenceUtils.removeStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES, mFileModel.canonicalPath)
                            application.sendBroadcast(Intent(MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_CHANGED)
                                .putExtra(MainBroadcastPreference.FragmentToFragment.Who.KEY, MainBroadcastPreference.FragmentToFragment.Who.Values.FILE_LIST))
                            notifyDataSetChanged()
                            if(mFavoriteViewModelList.size == 0)
                                application.sendBroadcast(Intent(MainBroadcastPreference.FragmentToFragment.Action.FAVORITE_LIST_EMPTY)
                                    .putExtra(MainBroadcastPreference.FragmentToFragment.Who.KEY, MainBroadcastPreference.FragmentToFragment.Who.Values.FAVORITE))

                        }
                    })
                }

                if(scanCompletedCount == fileCount) {
                    notifyDataSetChanged()
                    onRefreshFinished(mFavoriteViewModelList.isEmpty())
                    sizeCalculate.postDelayed({
                        mFavoriteViewModelList.forEach {
                            eachFavoriteFileViewModel ->
                            if(eachFavoriteFileViewModel.mFileModel.isDirectory) {
                                mFileModelManager.setDeepFileSizeAndUnit(eachFavoriteFileViewModel.mFileModel)
                                eachFavoriteFileViewModel.mSizeAndUnit.set(
                                    application.getString(
                                        R.string.item_favorite_size,
                                        "${kotlin.math.round((eachFavoriteFileViewModel.mFileModel.size!! * 100) / 100)} ${eachFavoriteFileViewModel.mFileModel.sizeUnit}"
                                    )
                                )
                            }
                        }
                    }, sizeCalculateDelay)
                }
            }
        }
    }

    fun searchByKeyword(keyword : String, onSearchResult : (isEmpty : Boolean) -> Unit) {
        val filteredFavoriteViewModelList = mFavoriteViewModelList.filter {
            it.mFileModel.title.contains(keyword)
        }
        mFavoriteViewModelList.clear()
        mFavoriteViewModelList.addAll(filteredFavoriteViewModelList)
        notifyDataSetChanged()
        onSearchResult(mFavoriteViewModelList.isEmpty())
    }

    class FavoriteListViewHolder(
        private val binding : ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : FavoriteFileViewModel) {
            binding.apply {
                viewModel = item
            }
        }
    }


}
