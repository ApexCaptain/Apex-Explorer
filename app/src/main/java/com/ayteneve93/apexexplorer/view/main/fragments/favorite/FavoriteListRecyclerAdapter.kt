package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.managers.FileModelManager
import com.ayteneve93.apexexplorer.databinding.ItemFavoriteBinding
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.utils.PreferenceUtils


class FavoriteListRecyclerAdapter(private val mPreferenceUtils: PreferenceUtils, private val mFileModelManager: FileModelManager, private val application: Application) : RecyclerView.Adapter<FavoriteListRecyclerAdapter.FavoriteListViewHolder>(){

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

    fun refresh() {
        /*
        for(x in 1..100) {
            mFavoriteViewModelList.add(FavoriteFileViewModel(application).apply {

            })
        }
        */
        mPreferenceUtils.getStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES).forEach {
            mFileModelManager.getFileInfoFrom(it) {
                isSucceed, fileModel ->
                if(isSucceed) {
                    mFavoriteViewModelList.add(FavoriteFileViewModel(application).apply {
                        mFileModel = fileModel!!
                    })
                }
            }
        }

        notifyDataSetChanged()
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
