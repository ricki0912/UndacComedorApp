package com.undac.undaccomedor.view_holder

import android.content.Context
import android.view.View
import com.undac.undaccomedor.item.LastNewsItem
import kotlinx.android.synthetic.main.li_item_last_news.view.*
import java.text.SimpleDateFormat

internal class LastNewsViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<LastNewsItem>(itemView) {

    private val sdfDateTime12= SimpleDateFormat("yyyy-MM-dd hh:mm aaa")
    override fun bind(item: LastNewsItem) {
        itemView.textViewTitleLastNews.text=item.title
        itemView.textViewDatePubLastNews.text=sdfDateTime12.format(item.date_pub)
        itemView.justifiedTextViewDescriptionLastNews.text=item.description

    }

}