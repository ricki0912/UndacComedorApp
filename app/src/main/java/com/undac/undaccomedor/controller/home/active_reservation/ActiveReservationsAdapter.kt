package com.undac.undaccomedor.controller.home.active_reservation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.undac.undaccomedor.R
import com.undac.undaccomedor.view_holder.BaseViewHolder
import com.undac.undaccomedor.view_holder.PendingMenuViewHolder
import com.undac.undaccomedor.item.*
import com.undac.undaccomedor.view_holder.EmptyItemViewHolder

class ActiveReservationsAdapter(private val mApplicationContext: Context):RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var adapterDataList: List<Any> = emptyList()

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
        TYPE_PENDING_MENU -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_pending_menu, parent, false)
            PendingMenuViewHolder(view, mApplicationContext)
        }
        TYPE_PENDING_MENU_NOTHING -> {
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
            is PendingMenuViewHolder -> holder.bind(element as PendingMenuItem)
            is EmptyItemViewHolder -> holder.bind(element as EmptyItemNothingItem)
            else -> throw IllegalArgumentException()
        }
    }
    override fun getItemViewType(position: Int): Int {
        val comparable = adapterDataList[position]
        return when (comparable) {
            is PendingMenuItem -> TYPE_PENDING_MENU
            is EmptyItemNothingItem -> TYPE_PENDING_MENU_NOTHING

            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }


    fun setList(list: List<Any>) {
        this.adapterDataList = list
        notifyDataSetChanged()
    }


    companion object {
        const val TYPE_PENDING_MENU = 0
        const val TYPE_PENDING_MENU_NOTHING = 1

    }

}