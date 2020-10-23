package com.undac.undaccomedor.view_holder

import android.content.Context
import android.content.Intent
import android.view.View
import com.undac.undaccomedor.controller.suggestion.SendSuggestionActivity
import com.undac.undaccomedor.item.SendSuggestionItem
import kotlinx.android.synthetic.main.li_item_send_suggestion.view.*

internal class SendSuggestionViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<SendSuggestionItem>(itemView) {

    override fun bind(item: SendSuggestionItem) {
        itemView.buttonSendSuggestion.setOnClickListener({
            val intent = Intent(mApplicationContext, SendSuggestionActivity::class.java)
            //intent.putExtra("keyIdentifier", value)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mApplicationContext.startActivity(intent)
        })

    }

}