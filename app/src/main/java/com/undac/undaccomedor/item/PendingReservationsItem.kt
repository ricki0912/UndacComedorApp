package com.undac.undaccomedor.item

class PendingReservationsItem(var id_f_desayuno: Int, var id_f_almuerzo:Int, var id_f_cena:Int) {

    public var loadFragment : (id:Int) -> Unit={}

}