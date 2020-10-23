package com.undac.undaccomedor.view_holder

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.TodayReservationsItem

import com.undac.undaccomedor.controller.home.reservations_today.ReservationsTodayAdapter
import com.undac.undaccomedor.item.EmptyItemNothingItem
import com.undac.undaccomedor.item.MenuReservationTodayItem
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.readStringFromStorage
import com.undaccomedor.helpers.saveToStorage
import kotlinx.android.synthetic.main.li_item_reservations_today.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


internal class TodayReservationsViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<TodayReservationsItem>(itemView) {

    private  val adapter = ReservationsTodayAdapter(mApplicationContext)
    private lateinit  var item:TodayReservationsItem

    override fun bind(item: TodayReservationsItem) {
        this.item=item
        initView()

    }


    private fun initView() {
        itemView.recyclerReservationsToday.layoutManager = LinearLayoutManager(mApplicationContext)
        //recyclerViewMovies.addItemDecoration(VerticalSpaceItemDecoration(48))

        //This will for default android divider
        //recyclerViewMovies.addItemDecoration(DividerItemDecoration(this))
        /*val mDividerItemDecoration = DividerItemDecoration(
            itemView.recyclerReservationsToday.getContext(),
            (itemView.recyclerReservationsToday.layoutManager as LinearLayoutManager).getOrientation()
        ) */
        var dividirDecoration=DividerItemDecoration(
            itemView.recyclerReservationsToday.getContext(),
            DividerItemDecoration.VERTICAL
        )
        dividirDecoration.setDrawable(mApplicationContext.resources.getDrawable(R.drawable.dividir_decoration))
        itemView.recyclerReservationsToday.addItemDecoration(dividirDecoration)

        //This will for custom divider
//        recyclerViewMovies.addItemDecoration(DividerItemDecoration(this, R.drawable.drawable_divider_view))


        itemView.recyclerReservationsToday.adapter = adapter
        //adapter.setList(generateDummyData())

        itemView.cardViewNextPosition.setOnClickListener{
            item.loadNextItem(item.next_position)
        }

        itemView.buttonNextPosition.setOnClickListener{
            item.loadNextItem(item.next_position)
        }

        var volley = VolleySingleton(mApplicationContext)//VolleySingleton.getInstance(mApplicationContext)
        volley.prepare(mApplicationContext, Server.RESERVATION_GET_RESERVATIONS_TODAY_, result={result(it)}, error = {error(it)}, before = {before()}, after={after()} )
        volley.setUser()
        volley.post(
        )
    }


    private fun before(){
        itemView.progressBarLoadingReservationsToday.visibility=View.VISIBLE
    }

    private fun after(){
        itemView.progressBarLoadingReservationsToday.visibility=View.GONE
    }

    private fun result(objJson: JSONObject?){
        exec(objJson)
        setSaveTodayReservations(objJson)
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
                ReservationsTodayAdapter.TYPE_MENU_RESERVATION_TODAY-> {
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val sdfDateTime=SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val skd_date = sdf.parse(objJsonData.getString("skd_date"))
                    val reser_date_start=sdfDateTime.parse(objJsonData.getString("reser_date_start"))
                    val reser_date_end=sdfDateTime.parse(objJsonData.getString("reser_date_end"))
                    val assist_time: Date?=if(objJsonData.isNull("assist_time")) null else sdfDateTime.parse(objJsonData.getString("assist_time"))
                   val reservation_time:Date?=if(objJsonData.isNull("reservation_time")) null else sdfDateTime.parse(objJsonData.getString("reservation_time"))

                    list.add(
                        MenuReservationTodayItem(
                            objJsonData.getString("id_menu").toInt(),
                            objJsonData.getInt("type"),
                            /*objJsonData.getString("second")*/"",
                            /*objJsonData.getString("soup")*/"",
                            /*objJsonData.getString("drink")*/"",
                            /*objJsonData.getString("fruit")*/"",
                            /*objJsonData.getString("dessert")*/"",
                            /*objJsonData.getString("aditional")*/"",
                            skd_date,
                            objJsonData.getInt("state_reser"),
                            reser_date_start,
                            reser_date_end,

                            /*objJsonData.getInt("state_menu_reservation")*/1,
                            if(objJsonData.isNull("horary_of_reser")) null else objJsonData.getString("horary_of_reser"),
                            if(objJsonData.isNull("assist")) null else objJsonData.getBoolean("assist")   ,
                            assist_time,
                            if(objJsonData.isNull("id_timetable")) null else objJsonData.getInt("id_timetable"),
                            reservation_time,

                            objJsonData.optInt("score",0),
                            if(objJsonData.isNull("comment")) "" else objJsonData.getString("comment")

                            )
                    )
                }
                ReservationsTodayAdapter.TYPE_EMTPY_ITEM -> {
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
        exec(getSaveTodayReservations())
        if(e is VolleyError){
            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
        }else{

        }

    }

    private fun getSaveTodayReservations():JSONObject?{
        var objJson : JSONObject?=null
        try{
            var valor:String?=mApplicationContext.readStringFromStorage(
                TODAY_RESERVATIONS
            )
            if(valor!=null && !valor.trim().equals("")){
                objJson = JSONObject(valor)
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }


    private fun setSaveTodayReservations(objJson :  JSONObject?){
        mApplicationContext.saveToStorage(
            TODAY_RESERVATIONS,objJson.toString() )
    }
    companion object {
        private val  TODAY_RESERVATIONS:String="Today_reservation.json"
    }

}

