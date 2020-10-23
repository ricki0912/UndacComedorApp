package com.undac.undaccomedor.item;

import com.undaccomedor.helpers.toUpperFirt
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

public open class Menu (id:Int, type:Int,  second: String?, soup: String?,
                        drink: String?, fruit: String?, dessert: String?, aditional: String?, skd_date: Date,
                        state_reser:Int, reser_date_start:Date, reser_date_end:Date, state_menu_reservation:Int,
                        horary_of_reser: String?,
                        assist :Boolean?, assist_time:Date?,
                        id_timetable:Int?, reservation_time:Date?
                 ):Serializable{
    val id:Int=id
    val type:Int=type
    val second:String?=second
    val soup:String?=soup
    val drink:String?=drink
    val fruit:String?=fruit
    val dessert:String?=dessert
    val aditional:String?=aditional
    val skd_date:Date=skd_date
    val state_reser:Int=state_reser
    val reser_date_start: Date=reser_date_start
    val reser_date_end: Date=reser_date_end

    //campos calculados

    val day_of_month:String= SimpleDateFormat("dd ", Locale( "es" , "PE")).format(skd_date).toUpperFirt()
    val name_day_of_week:String=SimpleDateFormat("EEEE", Locale( "es" , "PE")).format(skd_date).toUpperFirt()
    val year=SimpleDateFormat("yyyy", Locale( "es" , "PE")).format(skd_date).toUpperFirt()
    val name_month=SimpleDateFormat("MMMM",  Locale( "es" , "PE")).format(skd_date).toUpperFirt()

    val reser_date_start_string:String= SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale( "es" , "PE")).format(reser_date_start)
    val reser_date_end_string:String=SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale( "es" , "PE")).format(reser_date_end)

    val assist_time_string:String?=if(assist_time!=null)SimpleDateFormat("hh:mm aaa",  Locale( "es" , "PE")).format(assist_time) else null

    /*campos calculados de base de datos por cuestion de optimizar y más precisión*/
    val state_menu_reservation:Int=state_menu_reservation
    val horary_of_reser: String?=horary_of_reser



    /*assist_time */
    val assist:Boolean?=assist
    val assist_time:Date?=assist_time

    /*id de horario*/
    val id_timetable:Int?=id_timetable
    val reservation_time:Date?=reservation_time

    val type_string=if(type== TYPE_DESAYUNO) "Desayuno" else if(type== TYPE_ALMUERZO) "Almuerzo" else if(type== TYPE_CENA) "Cena" else "Cena"

    companion object {
        val NAME_SERIALIZABLE : String="item_menu"
        val TYPE_DESAYUNO :Int=1
        val TYPE_ALMUERZO :Int=2
        val TYPE_CENA :Int=3

        val MENU_INACTIVE=2
        val MENU_OUTSIDE_TIME=3
        val MENU_ACTIVE=1


    }




}


