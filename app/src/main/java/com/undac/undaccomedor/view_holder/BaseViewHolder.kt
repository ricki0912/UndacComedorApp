package com.undac.undaccomedor.view_holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract val mApplicationContext: Context

    abstract fun bind(item: T)
    //abstract fun findViewById(content: Int): View
    //abstract fun startActivity(intent: Intent)


    companion object {
        const val TYPE_LASTED_NEWS = 0
        const val TYPE_GUIDE = 1
        const val TYPE_SERVICE = 2
        const val TYPE_WELCOME=3
        const val TYPE_PENDING_RESERVATIONS=4
        const val TYPE_SEND_SUGGESTION=5
        const val HORARY_ATTENTION=6
        const val TYPE_TODAY_RESERVATIONS=7
    }

}

