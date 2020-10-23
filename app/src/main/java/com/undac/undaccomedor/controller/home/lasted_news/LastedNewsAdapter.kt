package com.undac.undaccomedor.controller.home.lasted_news

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.undac.undaccomedor.R
import com.undac.undaccomedor.view_holder.BaseViewHolder
import com.undac.undaccomedor.item.*
import com.undac.undaccomedor.view_holder.EmptyItemViewHolder
import com.undac.undaccomedor.view_holder.LastNewsViewHolder

class LastedNewsAdapter(private val mApplicationContext: Context):RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var adapterDataList: List<Any> = emptyList()

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_LAST_NEWS -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_last_news, parent, false)
            LastNewsViewHolder(view, mApplicationContext)
        }
            TYPE_EMTPY_ITEM -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_empty, parent, false)
            EmptyItemViewHolder(view, mApplicationContext)
        }
        else -> throw IllegalArgumentException("Invalid view type")
    }
    }
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = adapterDataList[position]
        when (holder) {
            is LastNewsViewHolder -> holder.bind(element as LastNewsItem)
            is EmptyItemViewHolder -> holder.bind(element as EmptyItemNothingItem)
            else -> throw IllegalArgumentException()
        }
    }
    override fun getItemViewType(position: Int): Int {
        val comparable = adapterDataList[position]
        return when (comparable) {
            is LastNewsItem -> TYPE_LAST_NEWS
            is EmptyItemNothingItem -> TYPE_EMTPY_ITEM

            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }


    fun setList(list: List<Any>) {
        this.adapterDataList = list
        notifyDataSetChanged()
    }


    companion object {
        const val TYPE_EMTPY_ITEM = 0
        const val TYPE_LAST_NEWS = 1

    }

}