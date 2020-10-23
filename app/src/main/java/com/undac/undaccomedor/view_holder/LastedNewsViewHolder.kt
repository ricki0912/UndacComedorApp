package com.undac.undaccomedor.view_holder

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.home.lasted_news.LastedNewsAdapter
import com.undac.undaccomedor.controller.home.reservations_today.ReservationsTodayAdapter
import com.undac.undaccomedor.item.EmptyItemNothingItem
import com.undac.undaccomedor.item.LastNewsItem
import com.undac.undaccomedor.item.LastedNewsItem
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.session.SessionManager
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.readStringFromStorage
import com.undaccomedor.helpers.saveToStorage
import kotlinx.android.synthetic.main.li_item_latest_news.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

internal class LastedNewsViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<LastedNewsItem>(itemView) {


    private  val adapter = LastedNewsAdapter(mApplicationContext)


    override fun bind(item: LastedNewsItem) {
        initView()
        itemView.buttonSeeMoreNews.setOnClickListener({
            item.loadNews(item.id)
        })
    }

    private fun initView() {
        itemView.recyclerLatestNews.layoutManager = LinearLayoutManager(mApplicationContext)
        //recyclerViewMovies.addItemDecoration(VerticalSpaceItemDecoration(48))

        //This will for default android divider
        //recyclerViewMovies.addItemDecoration(DividerItemDecoration(this))
        /*val mDividerItemDecoration = DividerItemDecoration(
            itemView.recyclerLatestNews.getContext(),
            (itemView.recyclerLatestNews.layoutManager as LinearLayoutManager).getOrientation()
        )

        */
        var dividirDecoration=DividerItemDecoration(
            itemView.recyclerLatestNews.getContext(),
            DividerItemDecoration.VERTICAL
        )
        dividirDecoration.setDrawable(mApplicationContext.resources.getDrawable(R.drawable.dividir_decoration))
        itemView.recyclerLatestNews.addItemDecoration(dividirDecoration)

        //This will for custom divider
        //recyclerViewMovies.addItemDecoration(DividerItemDecoration(this, R.drawable.drawable_divider_view))

        itemView.recyclerLatestNews.adapter = adapter
        //adapter.setList(generateDummyData())

        var volley = VolleySingleton(mApplicationContext)//VolleySingleton.getInstance(mApplicationContext)
        volley.prepare(mApplicationContext, Server.NEWS_GET_LATEST_NEWS_, result={result(it)}, error = {error(it)}, before = {before()}, after={after()} )
        volley.setUser()
        volley.post(
        )
    }


    private fun before(){
        itemView.progressBarLoadingLatestNews.visibility=View.VISIBLE
    }
    private fun after(){
        itemView.progressBarLoadingLatestNews.visibility=View.GONE
    }

    private fun result(objJson: JSONObject?){
        exec(objJson)
        setSaveLatestNews(objJson)
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
                LastedNewsAdapter.TYPE_LAST_NEWS-> {
                    val sdfDateTime= SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val date_pub=sdfDateTime.parse(objJsonData.getString("date_pub"))

                    list.add(
                        LastNewsItem(
                            objJsonData.getString("title"),
                            date_pub,
                            objJsonData.getString("description")
                        )
                    )
                }
                LastedNewsAdapter.TYPE_EMTPY_ITEM -> {
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
        exec(getSaveSaveLatestNews())
        if(e is VolleyError){
            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
            //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()

        }else{

        }

    }

    private fun getSaveSaveLatestNews():JSONObject?{
        var objJson : JSONObject?=null
        try{
            var valor:String?=mApplicationContext.readStringFromStorage(
                LATEST_NEWS
            )
            if(valor!=null && !valor.trim().equals("")){
                objJson = JSONObject(valor)
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }


    private fun setSaveLatestNews(objJson :  JSONObject?){
        mApplicationContext.saveToStorage(
            LATEST_NEWS,objJson.toString() )
    }
    companion object {
        private val  LATEST_NEWS:String="Latest_news.json"
    }




}