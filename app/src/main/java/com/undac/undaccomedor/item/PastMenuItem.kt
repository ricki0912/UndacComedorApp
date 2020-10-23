package com.undac.undaccomedor.item

import com.undac.undaccomedor.item.Menu
import java.util.*

class PastMenuItem(id:Int, type:Int, second: String?, soup: String?,
                      drink: String?, fruit: String?, dessert: String?, aditional: String?, skd_date: Date,
                      state_reser:Int, reser_date_start:Date, reser_date_end: Date,  state_menu_reservation:Int,
                      horary_of_reser: String?, assist:Boolean?, assist_time:Date?,  id_timetable:Int?, reservation_time:Date?, /*Id de fragments*/ val just_state:String)
    : Menu(
    id, type,  second, soup,
    drink, fruit, dessert, aditional, skd_date,
    state_reser, reser_date_start, reser_date_end, state_menu_reservation,
    horary_of_reser,
    assist , assist_time,
    id_timetable, reservation_time

){




}