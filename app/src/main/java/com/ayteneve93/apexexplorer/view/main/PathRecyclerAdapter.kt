package com.ayteneve93.apexexplorer.view.main

import android.app.Application
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.databinding.ItemPathBinding
import com.ayteneve93.apexexplorer.view.main.fragments.filelist.FileListRecyclerAdapter
import java.io.File


class PathRecyclerAdapter(private val application : Application) : RecyclerView.Adapter<PathRecyclerAdapter.FilePathViewHolder>(){

    private val mPathViewModelList : ArrayList<PathViewModel> = ArrayList()

    override fun getItemCount(): Int = mPathViewModelList.size

    override fun onBindViewHolder(holder: FilePathViewHolder, position: Int) {
        val eachFilePathViewModel = mPathViewModelList[position]
        holder.apply {
            bind(eachFilePathViewModel)
            itemView.tag = eachFilePathViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilePathViewHolder {
        return FilePathViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_path, parent, false
            )
        )
    }

    private var pathClickedListener : ((path : String) -> Unit)? = null
    fun setPathClickedListener(pathClickedListener : (path : String) -> Unit) : PathRecyclerAdapter {
        this.pathClickedListener = pathClickedListener
        return this
    }

    fun refresh(path : String?) {
        mPathViewModelList.clear()
        path?.let {
            var file = File(path)
            while(file.canonicalPath != Environment.getExternalStorageDirectory().path) {
                mPathViewModelList.add(0, PathViewModel(application).apply {
                    mDirectoryPath = file.path
                    mDirectoryTitle = file.name.trim()
                    onItemClickListener = {
                        pathClickedListener?.let {
                            pathClickedListener ->
                            pathClickedListener(mDirectoryPath)
                        }
                    }
                })
                file = file.parentFile
            }
        }
        mPathViewModelList.add(0, PathViewModel(application).apply {
            mDirectoryPath = Environment.getExternalStorageDirectory().path
            mDirectoryTitle = application.getString(R.string.root_directory_title)
            onItemClickListener = {
                pathClickedListener?.let {
                    pathClickedListener ->
                    pathClickedListener(mDirectoryPath)
                }
            }
        })
        mPathViewModelList.last().apply {
            mNextIndicatorString = ""
            mIsSelected = true
        }
        notifyDataSetChanged()
    }

    fun setToFavorite() {
        mPathViewModelList.clear()
        mPathViewModelList.add(PathViewModel(application).apply {
            mDirectoryTitle = application.getString(R.string.favorites_title)
            onItemClickListener = {}
            mNextIndicatorString = ""
            mIsSelected = true
        })
        notifyDataSetChanged()
    }

    fun setToSearch(keyword : String) {
        mPathViewModelList.clear()
        mPathViewModelList.add(PathViewModel(application).apply {
            mDirectoryTitle = application.getString(R.string.search_by, keyword)
            onItemClickListener = {}
            mNextIndicatorString = ""
            mIsSelected = true
        })
        notifyDataSetChanged()
    }

    class FilePathViewHolder(
        private val binding : ItemPathBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : PathViewModel) {
            binding.apply {
                viewModel = item
            }
        }
    }

}
