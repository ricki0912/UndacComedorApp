package com.undac.undaccomedor.view_holder

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.volley.VolleyError
import com.undac.undaccomedor.item.PendingMenuItem
import com.undac.undaccomedor.item.PendingReservationsItem
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.session.SessionManager
import com.undac.undaccomedor.volley.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject
import android.support.v7.widget.DividerItemDecoration
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.home.active_reservation.ActiveReservationsAdapter
import com.undac.undaccomedor.item.EmptyItemNothingItem
import com.undaccomedor.helpers.readStringFromStorage
import com.undaccomedor.helpers.saveToStorage
import kotlinx.android.synthetic.main.li_item_menu_past.*
import kotlinx.android.synthetic.main.li_item_pending_reservations.view.*
import java.text.SimpleDateFormat
import java.util.*


internal class PendingReservationsViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<PendingReservationsItem>(itemView) {

    private  val adapter = ActiveReservationsAdapter(mApplicationContext)
    private lateinit var item:PendingReservationsItem

    override fun bind(item: PendingReservationsItem) {
        this.item=item
        initView()
    }


    private fun initView() {
        itemView.recyclerPendingReservations.layoutManager = LinearLayoutManager(mApplicationContext)
        //recyclerViewMovies.addItemDecoration(VerticalSpaceItemDecoration(48))

        //This will for default android divider
        //recyclerViewMovies.addItemDecoration(DividerItemDecoration(this))
       /* val mDividerItemDecoration = DividerItemDecoration(
            itemView.recyclerPendingReservations.getContext(),
            (itemView.recyclerPendingReservations.layoutManager as LinearLayoutManager).getOrientation()
        )
        itemView.recyclerPendingReservations.addItemDecoration(mDividerItemDecoration)*/
        var dividirDecoration=DividerItemDecoration(
            itemView.recyclerPendingReservations.getContext(),
            DividerItemDecoration.VERTICAL
        )
        dividirDecoration.setDrawable(mApplicationContext.resources.getDrawable(R.drawable.dividir_decoration))
        itemView.recyclerPendingReservations.addItemDecoration(
            dividirDecoration
        )

        //This will for custom divider
//        recyclerViewMovies.addItemDecoration(DividerItemDecoration(this, R.drawable.drawable_divider_view))

        itemView.recyclerPendingReservations.adapter = adapter
        //adapter.setList(generateDummyData())

        var volley = VolleySingleton(mApplicationContext)//VolleySingleton.getInstance(mApplicationContext)
        volley.prepare(mApplicationContext, Server.GET_MENU_ACTIVE, result={result(it)}, error = {error(it)}, before = {before()}, after={after()} )
        volley.setUser()
        volley.post()

    }

    private fun before(){
        itemView.progressBarLoadingPendingReser.visibility=View.VISIBLE
    }

    private fun after(){
        itemView.progressBarLoadingPendingReser.visibility=View.GONE
    }

    private fun result(objJson: JSONObject?){
        exec(objJson)
        setSavePendingReservations(objJson)
    }
    private fun exec(objJson: JSONObject?){
        if (objJson == null)
            return
        //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()
        if (objJson.getInt("state") == 0) {
            return;
        }
            val objJsonArray: JSONArray =objJson.getJSONArray("data")

            var list :MutableList<Any> = mutableListOf() // =listOf( PendingMenuItem(),PendingMenuItem(), PendingMenuItem(), PendingMenuItem(), PendingMenuItem())
            //val menuList = ArrayList<Menu>()
            for (i in 0..objJsonArray.length()-1) {
                val objJsonData: JSONObject=objJsonArray.getJSONObject(i);
                //list.add(PendingMenuItem())
                val objJsonTypeItem :Int =objJsonData.getInt("type_item")

                when (objJsonTypeItem) {
                    ActiveReservationsAdapter.TYPE_PENDING_MENU-> {

                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val sdfDateTime=SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                        val skd_date = sdf.parse(objJsonData.getString("skd_date"))
                        val reser_date_start=sdfDateTime.parse(objJsonData.getString("reser_date_start"))
                        val reser_date_end=sdfDateTime.parse(objJsonData.getString("reser_date_end"))
                        val assist_time: Date?=if(objJsonData.isNull("assist_time")) null else sdfDateTime.parse(objJsonData.getString("assist_time"))


                        var pendingReser=PendingMenuItem(
                            objJsonData.getString("id").toInt(),
                            objJsonData.getInt("type"),

                            objJsonData.getString("second"),
                            objJsonData.getString("soup"),
                            objJsonData.getString("drink"),
                            objJsonData.getString("fruit"),
                            objJsonData.getString("dessert"),
                            objJsonData.getString("aditional"),
                            skd_date,
                            objJsonData.getInt("state_reser"),
                            reser_date_start,
                            reser_date_end,

                            objJsonData.getInt("state_menu_reservation"),
                            if(objJsonData.isNull("horary_of_reser")) null else objJsonData.getString("horary_of_reser"),
                            if(objJsonData.isNull("assist")) null else objJsonData.getBoolean("assist"),
                            assist_time,
                            null,
                            null,
                            item.id_f_desayuno,
                            item.id_f_almuerzo,
                            item.id_f_cena

                        )
                        pendingReser.loadFragment={item.loadFragment(it)}
                        list.add(
                            pendingReser
                        )
                    }
                    ActiveReservationsAdapter.TYPE_PENDING_MENU_NOTHING -> {
                        list.add(
                            EmptyItemNothingItem(

                                objJsonData.getString("title"),
                                objJsonData.getString("message"),
                                objJsonData.getInt("icon")
                            )
                        )
                    }
                    else -> throw IllegalArgumentException("Type de Item invalido")
                }




            }
        adapter.setList(list)

    }

    private fun error(e: Exception?){
        exec(getSavePendingReservations())
        if(e is VolleyError){
            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
        }else{

        }

    }

    private fun getSavePendingReservations():JSONObject?{
        var objJson : JSONObject?=null
        try{
            var valor:String?=mApplicationContext.readStringFromStorage(
                PENDING_RESERVATIONS
            )
            if(valor!=null && !valor.trim().equals("")){
                objJson = JSONObject(valor)
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }


    private fun setSavePendingReservations(objJson :  JSONObject?){
        mApplicationContext.saveToStorage(
            PENDING_RESERVATIONS,objJson.toString() )
    }
    companion object {
        private val  PENDING_RESERVATIONS:String="Pending_Reservation.json"
    }


}

