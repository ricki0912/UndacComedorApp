package com.undac.undaccomedor.controller.menu_week

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.EmptyItemNothingItem
import com.undac.undaccomedor.item.JustificationMenuItem
import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.item.PastMenuItem
import com.undac.undaccomedor.view_holder.*
import kotlinx.android.synthetic.main.li_item_menu.view.*


class MenuWeekAdapter(private val mApplicationContext: Context):RecyclerView.Adapter<BaseViewHolder<*>>(), View.OnClickListener {

    private var adapterDataList: List<Any> = emptyList()
    private var EXPANDE_POSITION : Int = -1

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_EMTPY_ITEM->{
                val view=LayoutInflater.from(mApplicationContext)
                    .inflate(R.layout.li_item_empty, parent, false)
                var holder =EmptyItemViewHolder(view, mApplicationContext)
                holder
            }
            TYPE_MENU -> {
                val view = LayoutInflater.from(mApplicationContext)
                    .inflate(R.layout.li_item_menu, parent, false)
                var holder= MenuWeekViewHolder(view, mApplicationContext)

                holder.itemView.setTag(holder)
                holder.itemView.setOnClickListener(this)

                holder
            }
            TYPE_PAST_MENU->{
                val view=LayoutInflater.from(mApplicationContext)
                    .inflate(R.layout.li_item_menu_past, parent, false)
                var holder= PastMenuViewHolder(view, mApplicationContext)

                holder.itemView.setTag(holder)
                holder.itemView.setOnClickListener(this)

                holder
            }
            TYPE_JUSTIFICATION_MENU->{
                var view=LayoutInflater.from(mApplicationContext)
                    .inflate(R.layout.li_item_menu_justify,parent, false)
                var holder= JustificationMenuViewHolder(view, mApplicationContext)

                holder.itemView.setTag(holder)
                holder.itemView.setOnClickListener(this)

                holder
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = adapterDataList[position]
        when (holder) {
            is EmptyItemViewHolder->{
                holder.bind(element as EmptyItemNothingItem)
            }
            is MenuWeekViewHolder ->{
                holder.bind(element as Menu)
                if(EXPANDE_POSITION==position){
                    holder.itemView.linearLayoutExpandArea.visibility=View.VISIBLE
                }else{
                    holder.itemView.linearLayoutExpandArea.visibility=View.GONE
                }
            }
            is PastMenuViewHolder ->{
               holder.bind(element as PastMenuItem)
                if(EXPANDE_POSITION==position){
                    holder.itemView.linearLayoutExpandArea.visibility=View.VISIBLE
                }else{
                    holder.itemView.linearLayoutExpandArea.visibility=View.GONE
                }
            }
            is JustificationMenuViewHolder ->{
                holder.bind(element as JustificationMenuItem)
                if(EXPANDE_POSITION==position){
                    holder.itemView.linearLayoutExpandArea.visibility=View.VISIBLE
                }else{
                    holder.itemView.linearLayoutExpandArea.visibility=View.GONE
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onClick(v: View?) {
        if (v!=null){
            val holder = v.getTag()// as ViewHolder
            when(holder){
                is MenuWeekViewHolder ->{
                    val theString = adapterDataList.get(holder.position)
                    if (EXPANDE_POSITION >= 0) {
                        val prev = EXPANDE_POSITION
                        notifyItemChanged(prev)
                    }
                    EXPANDE_POSITION = holder.position
                    notifyItemChanged(EXPANDE_POSITION)
                }
                is JustificationMenuViewHolder ->{
                    val theString = adapterDataList.get(holder.position)
                    if (EXPANDE_POSITION >= 0) {
                        val prev = EXPANDE_POSITION
                        notifyItemChanged(prev)
                    }
                    EXPANDE_POSITION = holder.position
                    notifyItemChanged(EXPANDE_POSITION)
                }
                is PastMenuViewHolder ->{
                    val theString = adapterDataList.get(holder.position)
                    if (EXPANDE_POSITION >= 0) {
                        val prev = EXPANDE_POSITION
                        notifyItemChanged(prev)
                    }
                    EXPANDE_POSITION = holder.position
                    notifyItemChanged(EXPANDE_POSITION)
                }

            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        val comparable = adapterDataList[position]
        return when (comparable) {
            is EmptyItemNothingItem-> TYPE_EMTPY_ITEM
            is JustificationMenuItem -> TYPE_JUSTIFICATION_MENU
            is PastMenuItem -> TYPE_PAST_MENU
            is Menu -> TYPE_MENU

            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }


    fun setList(list: List<Any>) {
        this.adapterDataList = list
        notifyDataSetChanged()
    }

    fun clear(){
        EXPANDE_POSITION=-1
        val size:Int =this.adapterDataList.size
      this.adapterDataList= emptyList()
        notifyItemRangeRemoved(0,size)
    }

    companion object {
        const val TYPE_EMTPY_ITEM=0
        const val TYPE_MENU = 1
        const val TYPE_JUSTIFICATION_MENU=2
        const val TYPE_PAST_MENU=3

    }

}