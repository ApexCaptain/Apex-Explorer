package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.DataModelManager
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.databinding.ItemFileListBinding

class FileListRecyclerAdapter(private val mDataModelManager: DataModelManager) : RecyclerView.Adapter<FileListRecyclerAdapter.FileListViewHolder>() {

    private val mFileList : ArrayList<FileModel> = ArrayList()

    override fun getItemCount(): Int = mFileList.size

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val eachFileModel = mFileList[position]
        holder.apply {
            bind(eachFileModel)
            itemView.tag = eachFileModel
            /*
            itemView.setOnClickListener {

            }
            */
        }
    }

    fun test() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        return FileListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_file_list, parent, false
            )
        )
    }

    class FileListViewHolder(
        private val binding : ItemFileListBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : FileModel) {
            binding.apply {
                dataModel = item
            }
        }
    }

}