package com.undac.undaccomedor.view_holder

import android.content.Context
import android.content.Intent

import android.view.View
import com.undac.undaccomedor.CReservationActivity
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.item.PendingMenuItem
import kotlinx.android.synthetic.main.li_item_pending_menu.view.*
import java.text.SimpleDateFormat

internal class PendingMenuViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<PendingMenuItem>(itemView) {
    private val sdf = SimpleDateFormat("dd-MM-yyyy")
    //private val sdfDateTime12= SimpleDateFormat("dd-MM-yyyy hh:mm aaa")
    val sdfTimeOfAssist= SimpleDateFormat("hh:mm aaa")
    private lateinit var item:PendingMenuItem

    override fun bind(item: PendingMenuItem) {
        this.item=item


        initView()
}

    private fun initView(){

        var title=""
        title=title+if(item.type== Menu.TYPE_DESAYUNO) "Desayuno" else if(item.type== Menu.TYPE_ALMUERZO)  "Almuerzo" else if(item.type== Menu.TYPE_CENA) "Cena" else "Menú"
        title=title+" de "
        title=title+item.name_day_of_week
        title=title+", "
        title=title+sdf.format(item.skd_date).toString()
        itemView.textViewTitlePendingMenu.setText(title)
        var dateReser="Activo hasta "
        dateReser=dateReser+item.reser_date_end_string
        itemView.textViewDateReserPendingMenu.setText(dateReser)




        var state=""
        if(item.assist_time!=null){
            state="Has asistido a las "+sdfTimeOfAssist.format(item.assist_time)

            itemView.textViewStatePendingMenu.setTextColor(mApplicationContext.applicationContext.resources.getColor(R.color.verde_d))

        }else if(item.horary_of_reser!=null){

            state="Has reservado de: "+item.horary_of_reser
            itemView.textViewStatePendingMenu.setTextColor(mApplicationContext.applicationContext.resources.getColor(R.color.verde_d))

        }else{
            itemView.textViewStatePendingMenu.setTextColor(mApplicationContext.applicationContext.resources.getColor(R.color.red))
            state="Haz tu reserva ahora mismo."
        }
        itemView.textViewStatePendingMenu.setText(state)


        itemView.setOnClickListener({
            val intent = Intent(mApplicationContext, CReservationActivity::class.java)
            intent.putExtra(Menu.NAME_SERIALIZABLE, item as Menu)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mApplicationContext.startActivity(intent)

        })

        itemView.imageViewEmpty.setImageResource(
            if(item.type== Menu.TYPE_DESAYUNO)
                R.drawable.desayuno
            else if(item.type== Menu.TYPE_ALMUERZO)
                R.drawable.almuerzo
            else if(item.type== Menu.TYPE_CENA)
                R.drawable.cena
            else
                R.drawable.almuerzo
        )

        itemView.imageViewEmpty.setOnClickListener({
            if(item.type== Menu.TYPE_DESAYUNO)
                item.loadFragment(item.id__f_desayuno)
            else if(item.type== Menu.TYPE_ALMUERZO)
                item.loadFragment(item.id__f_almuerzo)
            else if(item.type== Menu.TYPE_CENA)
                item.loadFragment(item.id__f_cena)

        })
    }

}