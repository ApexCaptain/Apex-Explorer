package com.ayteneve93.apexexplorer.view.entry

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.AppTitleModel
import com.ayteneve93.apexexplorer.databinding.ItemAppTitleBinding

class AppTitleRecyclerAdapter(private val activity : AppCompatActivity, private val appTitleModelList : List<AppTitleModel>) : RecyclerView.Adapter<AppTitleRecyclerAdapter.AppTitleViewHolder>() {

    override fun getItemCount() = appTitleModelList.size

    private val itemViewList = ArrayList<View>()

    override fun onBindViewHolder(holder: AppTitleViewHolder, position: Int) {
        val eachTitleModel = appTitleModelList[position]
        holder.apply {
            bind(eachTitleModel)
            itemView.tag = eachTitleModel
            itemViewList.add(itemView)
            if(itemViewList.size == appTitleModelList.size) {
                showTitleAnimation(activity)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppTitleViewHolder {
        return AppTitleViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_app_title, parent, false
            )
        )
    }

    private val titleShowUpAnim = AnimationUtils.loadAnimation(activity, R.anim.anim_app_title_show)
    private val titleShowUpAnimDelayMills = 120L
    private fun showTitleAnimation(activity: AppCompatActivity, position : Int = 0) {
        Handler().postDelayed({
            activity.runOnUiThread{
                itemViewList[position].apply {
                    visibility = View.VISIBLE
                    startAnimation(titleShowUpAnim)
                }
                if(position == itemViewList.size - 1) {
                    mOnRecyclerStartUpAnimEndListener?.let { it() }
                    return@runOnUiThread
                }
                showTitleAnimation(activity, position + 1)
            }
        }, titleShowUpAnimDelayMills)
    }

    private var mOnRecyclerStartUpAnimEndListener : (() -> Unit)? = null
    fun onRecyclerStartUpAnimEnd(onRecyclerStartUpAnimEndListener : (() -> Unit)) : AppTitleRecyclerAdapter {
        mOnRecyclerStartUpAnimEndListener = onRecyclerStartUpAnimEndListener
        return this
    }

    class AppTitleViewHolder(
        private val binding : ItemAppTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : AppTitleModel) {
            binding.apply {
                dataModel = item
            }
        }
    }

}

