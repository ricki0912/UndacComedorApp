package com.undac.undaccomedor.view_holder

import android.content.Context
import android.view.View
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.EmptyItemNothingItem
import kotlinx.android.synthetic.main.li_item_empty.view.*

internal class EmptyItemViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<EmptyItemNothingItem>(itemView) {

    override fun bind(item: EmptyItemNothingItem) {
        itemView.textViewTitleEmpty.text=item.title
        itemView.textViewMessageEmpty.text=item.message
        if(item.icon==EmptyItemNothingItem.ICON_FACE_SAD){
            itemView.imageViewEmpty.setImageResource(R.drawable.icon_sad)
        }else if(item.icon==EmptyItemNothingItem.ICON_NEWS){
            itemView.imageViewEmpty.setImageResource(R.drawable.icon_news)

        }




    }

}