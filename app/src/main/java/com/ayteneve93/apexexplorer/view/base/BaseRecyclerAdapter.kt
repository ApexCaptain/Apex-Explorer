package com.ayteneve93.apexexplorer.view.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class BaseRecyclerAdapter<T> : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder<T>>() {

    @FunctionalInterface
    interface OnItemClickListener<T> { fun onItemClick(view : View, item : T, position : Int) }
    private var mOnItemClickListener : OnItemClickListener<T>? = null

    companion object {
        const val VIEW_TYPE_EMPTY = 0
        const val VIEW_TYPE_NORMAL = 1
    }

    fun setOnItemClickListener(listener : (View, T, Int) -> Unit) {
        mOnItemClickListener = object : OnItemClickListener<T> {
            override fun onItemClick(view: View, item: T, position: Int) {
                listener(view, item, position)
            }
        }
    }

    fun getOnItemClickListener() : OnItemClickListener<T>? {
        return mOnItemClickListener
    }

    private var items = Collections.emptyList<T>()

    override fun getItemCount(): Int {
        return if(items.isEmpty()) 1 else items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_NORMAL)
            holder.populate(items[position], position)
    }

    fun updateList(itemList: List<T>) {
        items = itemList
        notifyDataSetChanged()
    }

    fun getItems() : List<T> {
        return items
    }

    open class ViewHolder<T>(
        itemView: View,
        private var baseRecyclerAdapter: BaseRecyclerAdapter<T>
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        open fun populate(item: T, position: Int) {}

        override fun onClick(v: View) {
            baseRecyclerAdapter.mOnItemClickListener?.onItemClick(v, baseRecyclerAdapter.items[adapterPosition], adapterPosition)
        }

    }

}