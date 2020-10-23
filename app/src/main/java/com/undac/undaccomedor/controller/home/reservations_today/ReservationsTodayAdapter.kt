package com.undac.undaccomedor.controller.home.reservations_today

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.undac.undaccomedor.R
import com.undac.undaccomedor.view_holder.BaseViewHolder
import com.undac.undaccomedor.item.*
import com.undac.undaccomedor.view_holder.EmptyItemViewHolder
import com.undac.undaccomedor.view_holder.MenuReservationTodayViewHolder
import kotlinx.android.synthetic.main.li_item_reservation_menu_today.view.*

class ReservationsTodayAdapter(private val mApplicationContext: Context):RecyclerView.Adapter<BaseViewHolder<*>>(){

    private var adapterDataList: List<Any> = emptyList()
    private var EXPANDE_POSITION : Int = -1

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_MENU_RESERVATION_TODAY -> {
            val view = LayoutInflater.from(mApplicationContext)
                .inflate(R.layout.li_item_reservation_menu_today, parent, false)
            var holder=MenuReservationTodayViewHolder(view, mApplicationContext)
                holder.itemView.setTag(holder)
                holder.expandView={expandView(it)}
                holder
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
            is MenuReservationTodayViewHolder -> {
                holder.bind(element as MenuReservationTodayItem)
                if(EXPANDE_POSITION==position){
                    holder.itemView.LinearLayoutCommentReserMenuToday.visibility=View.VISIBLE
                    holder.itemView.setBackgroundResource(R.color.celest_eclaro)
                }else{
                    holder.itemView.LinearLayoutCommentReserMenuToday.visibility=View.GONE
                    holder.itemView.setBackgroundResource(0)
                }
            }
            is EmptyItemViewHolder -> holder.bind(element as EmptyItemNothingItem)
            else -> throw IllegalArgumentException()
        }
    }
    override fun getItemViewType(position: Int): Int {
        val comparable = adapterDataList[position]
        return when (comparable) {
            is MenuReservationTodayItem -> TYPE_MENU_RESERVATION_TODAY
            is EmptyItemNothingItem -> TYPE_EMTPY_ITEM

            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }


    fun setList(list: List<Any>) {
        this.adapterDataList = list
        notifyDataSetChanged()
    }

    fun expandView(v: View?) {
        if (v!=null){
            val holder = v.getTag()// as ViewHolder
            when(holder){
                is MenuReservationTodayViewHolder->{
                    val theString = adapterDataList.get(holder.position)

                    // Check for an expanded view, collapse if you find one
                    if (EXPANDE_POSITION >= 0) {
                        val prev = EXPANDE_POSITION
                        notifyItemChanged(prev)
                    }
                    // Set the current position to "expanded"
                    EXPANDE_POSITION = holder.position
                    notifyItemChanged(EXPANDE_POSITION)
                }

            }

        }

    }

    companion object {
        const val TYPE_EMTPY_ITEM = 0
        const val TYPE_MENU_RESERVATION_TODAY = 1

    }

}