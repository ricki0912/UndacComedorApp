package com.undac.undaccomedor.view_holder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.undac.undaccomedor.controller.justification.JustifyActivity
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.JustificationMenuItem
import com.undac.undaccomedor.item.Menu
import kotlinx.android.synthetic.main.li_item_menu_justify.view.*
import java.text.SimpleDateFormat
import java.util.*

internal class JustificationMenuViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<JustificationMenuItem>(itemView) {

    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val currentDate = sdf.parse(sdf.format(Date()))

    private val sdfDateTime12= SimpleDateFormat("yyyy-MM-dd hh:mm aaa")
    private val sdfTimeOfAssist= SimpleDateFormat("hh:mm aaa")
    private var reser_date_start : String=""
    private var reser_date_end: String=""

    /*variables calculados*/





    override fun bind(menuDay: JustificationMenuItem) {

        itemView.textViewSegundoLiMenuDay.text=if(menuDay.second!=null) menuDay.second else ""
        itemView.textViewSoupLiMenuDay.text=if(menuDay.soup!=null) menuDay.soup else ""
        itemView.textViewInfusionLiMenuDay.text=if(menuDay.drink!=null) menuDay.drink else ""
        itemView.textViewFroutLiMenuDay.text=if(menuDay.fruit!=null ) menuDay.fruit else ""
        itemView.textViewDessertLiMenuDay.text=if(menuDay.dessert!=null) menuDay.dessert else ""
        itemView.textViewAditionalLiMenuDay.text=if(menuDay.aditional!=null) menuDay.aditional else ""

        reser_date_start=sdfDateTime12.format(menuDay.reser_date_start)
        reser_date_end=sdfDateTime12.format(menuDay.reser_date_end)



        /*Mostrar en su lugar las fechas */
        itemView.textViewDiaSemanaLiMenuDay.text=menuDay.name_day_of_week
        itemView.textViewDiaMesLiMenuDay.text=menuDay.day_of_month
        itemView.textViewNameMonthLiMenu.text=menuDay.name_month
        itemView.textViewYearLiMenu.text=menuDay.year


        if(menuDay.state_menu_reservation==1){
            itemView.linearLayoutDialIMenuDay.setBackgroundResource(R.color.colorPrimero)
        }else{
            itemView.linearLayoutDialIMenuDay.setBackgroundResource(R.color.red)
        }


        itemView.textViewStateHoraryOfReserMenuDay.text= if(menuDay.id_timetable!=null) menuDay.horary_of_reser else if(menuDay.reservation_time!=null) "Cancelado" else "No hay reserva"


        if(menuDay.assist==true){

            itemView.textViewIconAssist.setBackgroundResource(R.drawable.icon_accepted)
            itemView.textViewIconAssist.text="A"
        }else if(menuDay.assist==false){
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.icon_missing)
            itemView.textViewIconAssist.text="F"
        }else if(menuDay.id_timetable!=null){
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.ic_checked)
            itemView.textViewIconAssist.text=""
        }else{
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.ic_unchecked)
            itemView.textViewIconAssist.text=""
        }

        if(currentDate.equals(menuDay.skd_date)){
            itemView.cardViewMenuDay.setBackgroundResource(R.color.bey)
        }else{
            itemView.cardViewMenuDay.setBackgroundResource(R.color.graylight)
        }
        //        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM 'a las' hh:mm a 'del año' yyyy");
        itemView.imageBettunNutritionalTableLiMenuDay.setOnClickListener({


            onClick(menuDay)
        })

        /*Mesaje y estado de las justificaciones*/

        when (menuDay.just_state){
            JustificationMenuItem.JUSTIFICATION_PENDING->{
                itemView.textViewStateJustificationMenuDay.text="JUSTIFICACIÓN PENDIENTE"
                itemView.textViewIconJustification.setBackgroundResource(R.drawable.icon_pending)
                itemView.textViewIconJustification.text= JustificationMenuItem.JUSTIFICATION_PENDING
                itemView.textViewIconJustification.setTextColor(Color.BLACK)
            }
            JustificationMenuItem.JUSTIFICATION_SENT->{
                itemView.textViewStateJustificationMenuDay.text="JUSTIFICACIÓN ENVIADA"
                itemView.textViewIconJustification.setBackgroundResource(R.drawable.icon_sent)
                itemView.textViewIconJustification.text= JustificationMenuItem.JUSTIFICATION_SENT
                itemView.textViewIconJustification.setTextColor(Color.BLACK)
            }
            JustificationMenuItem.JUSTIFICATION_DENIED->{
                itemView.textViewStateJustificationMenuDay.text="JUSTIFICACIÓN DENEGADA"
                itemView.textViewIconJustification.setBackgroundResource(R.drawable.icon_denied)
                itemView.textViewIconJustification.text= JustificationMenuItem.JUSTIFICATION_DENIED
                itemView.textViewIconJustification.setTextColor(Color.WHITE)
            }
            JustificationMenuItem.JUSTIFICATION_ACCEPTED->{
                itemView.textViewStateJustificationMenuDay.text="JUSTIFICACIÓN ACEPTADA"
                itemView.textViewIconJustification.setBackgroundResource(R.drawable.icon_accepted)
                itemView.textViewIconJustification.text= JustificationMenuItem.JUSTIFICATION_ACCEPTED
                itemView.textViewIconJustification.setTextColor(Color.WHITE)
            }
            JustificationMenuItem.JUSTIFICATION_OBSERVED->{
                itemView.textViewStateJustificationMenuDay.text="JUSTIFICACIÓN OBSERVADA"
                itemView.textViewIconJustification.setBackgroundResource(R.drawable.icon_observed)
                itemView.textViewIconJustification.text= JustificationMenuItem.JUSTIFICATION_OBSERVED
                itemView.textViewIconJustification.setTextColor(Color.WHITE)
            }
            else->{
                itemView.textViewStateJustificationMenuDay.text="NO REQUIERE JUSTIFICACIÓN"
                itemView.textViewIconJustification.setBackgroundResource(R.drawable.icon_not_require)
                itemView.textViewIconJustification.text= JustificationMenuItem.NO_REQUIRE_JUSTIFICATION
                itemView.textViewIconJustification.setTextColor(Color.WHITE)

            }
        }

    }


    private fun onClick(dayMenu: Menu) {
        try{
            val intent = Intent(mApplicationContext, JustifyActivity::class.java)
            intent.putExtra(Menu.NAME_SERIALIZABLE, dayMenu)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mApplicationContext.startActivity(intent)

        }catch(e:Exception){
            Toast.makeText(mApplicationContext, e.message/*obj.getString("estado")*/, Toast.LENGTH_LONG).show()

        }
    }




}