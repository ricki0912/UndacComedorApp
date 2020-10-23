package com.undac.undaccomedor.controller.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.undac.undaccomedor.R
import com.undac.undaccomedor.view_holder.*
import com.undac.undaccomedor.item.*

class HomeAdapter(private val mApplicationContext: Context):RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var adapterDataList: List<Any> = emptyList()

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            BaseViewHolder.TYPE_LASTED_NEWS -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_latest_news, parent, false)
            LastedNewsViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.TYPE_GUIDE -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_guide, parent, false)
            GuideViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.TYPE_SERVICE -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_service, parent, false)
            ServiceViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.TYPE_WELCOME->{
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_welcome, parent, false)
            WelcomeViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.TYPE_PENDING_RESERVATIONS->{
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_pending_reservations, parent, false)
            PendingReservationsViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.TYPE_SEND_SUGGESTION->{
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_send_suggestion, parent, false)
            SendSuggestionViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.HORARY_ATTENTION->{
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_horary_attention, parent, false)
            HoraryAttentionViewHolder(view, mApplicationContext)
        }
            BaseViewHolder.TYPE_TODAY_RESERVATIONS->{
                val view = LayoutInflater.from(mApplicationContext)
                    .inflate(R.layout.li_item_reservations_today, parent, false)
                TodayReservationsViewHolder(view, mApplicationContext)
            }

        else -> throw IllegalArgumentException("Invalid view type")
    }
    }
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = adapterDataList[position]
        when (holder) {
            is LastedNewsViewHolder -> holder.bind(element as LastedNewsItem)
            is GuideViewHolder -> holder.bind(element as GuideItem)
            is ServiceViewHolder -> holder.bind(element as ServiceItem)
            is WelcomeViewHolder -> holder.bind(element as WelcomeItem)
            is PendingReservationsViewHolder ->holder.bind(element as  PendingReservationsItem)
            is SendSuggestionViewHolder ->holder.bind(element as SendSuggestionItem)
            is HoraryAttentionViewHolder ->holder.bind(element as HoraryAttentionItem)
            is TodayReservationsViewHolder->holder.bind(element as TodayReservationsItem)

            else -> throw IllegalArgumentException()
        }
    }
    override fun getItemViewType(position: Int): Int {
        val comparable = adapterDataList[position]
        return when (comparable) {
            is LastedNewsItem -> BaseViewHolder.TYPE_LASTED_NEWS
            is GuideItem -> BaseViewHolder.TYPE_GUIDE
            is ServiceItem -> BaseViewHolder.TYPE_SERVICE
            is WelcomeItem -> BaseViewHolder.TYPE_WELCOME
            is PendingReservationsItem->BaseViewHolder.TYPE_PENDING_RESERVATIONS
            is SendSuggestionItem-> BaseViewHolder.TYPE_SEND_SUGGESTION
            is HoraryAttentionItem->BaseViewHolder.HORARY_ATTENTION
            is TodayReservationsItem->BaseViewHolder.TYPE_TODAY_RESERVATIONS
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }


    fun setList(list: List<Any>) {
        this.adapterDataList = list
        notifyDataSetChanged()
    }

}