package com.undac.undaccomedor.controller.menu_week

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.android.volley.VolleyError
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.controller.reservation.CReservationFragment
import com.undac.undaccomedor.item.JustificationMenuItem
import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.item.PastMenuItem
import com.undac.undaccomedor.volley.VolleySingleton
import com.undac.undaccomedor.server.Server
import com.undaccomedor.helpers.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.android.synthetic.main.fragment_menu_week.*
import java.text.SimpleDateFormat
import java.util.*

class CMenuWeekFragment : Fragment() {

    private lateinit var mRootView: View
    private lateinit var mMenuWeekAdapter: MenuWeekAdapter
    private lateinit var mApplicationContext: Context
    private var OPTION_SELECT:Int =R.id.toggleButtonWeek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_menu_week, container, false)
        mApplicationContext = requireActivity().applicationContext
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerAndAdapter()
        menuWeekSwipeRefresh.init{ loadData(OPTION_SELECT)/*sendData(Server.GET_MENU, view.context, getParams())*/}
        initMenu()
        //loadData(OPTION_SELECT)
    }

    private fun loadData(id:Int){
        loadCantForJustify()
        when(id){
            R.id.toggleButtonWeek-> loadDataWeek()
            R.id.toggleButtonJustify->loadDataForJustification()
            R.id.toggleButtonPast->loadDataAll()
        }
    }


    private  fun loadDataWeek(){
        var volley = VolleySingleton.getInstance(mApplicationContext)
        volley.prepare(context!!, Server.MENU_GET_MENU_WEEK, result={resultWeek(it)}, error = {errorWeek(it)}, before = {before()}, after={after()} )
        volley.setUser()
        volley.post(
            Pair("typeMenu", arguments!!.get("typeMenu").toString())
        )
    }

    private  fun loadDataForJustification(){
        var volley = VolleySingleton.getInstance(mApplicationContext)
        volley.prepare(context!!, Server.HISTORY_RESERVATION_GET_LIST_FOR_JUSTIFICATION, result={resultForJustification(it)}, error = {errorJustify(it)}, before = {before()}, after={after()} )
        volley.setUser()
        volley.post(
            Pair("typeMenu", arguments!!.get("typeMenu").toString())
        )
    }

    private  fun loadDataAll(){
        var volley = VolleySingleton.getInstance(mApplicationContext)
        volley.prepare(context!!, Server.MENU_GET_LIST_ALL, result={resultPast(it)}, error = {errorPast(it)}, before = {before()}, after={after()} )
        volley.setUser()
        volley.post(
            Pair("typeMenu", arguments!!.get("typeMenu").toString())
        )
    }

 /*Cargar cantidad para justification*/

    private fun loadCantForJustify(){
        var volley = VolleySingleton(mApplicationContext)// VolleySingleton.getInstance(this)
        volley.prepare(mApplicationContext, Server.JUSTFICATION_GET_COUNT_FOR_JUSTIFICATION, result={resultCantForJustify(it)}, error = {errorCantForJustify(it)})
        volley.setUser()
        volley.post(
            Pair("typeMenu", arguments!!.get("typeMenu").toString())
        )

    }

    private fun resultCantForJustify(objJson: JSONObject?){
        if (objJson == null)
            return
        if (objJson.getInt("state") == 0) {
            Toast.makeText(view?.getContext(), objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }
        //Toast.makeText(context!!,objJson.toString(), Toast.LENGTH_LONG).show()
        val objJsonArray: JSONArray =objJson.getJSONArray("data")
        val menuList = ArrayList<Any>()
        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject = objJsonArray.getJSONObject(i);

            toggleButtonJustify.text = "PARA JUSTIFICAR(${objJsonData.getInt("count")})"
        }
    }

    private fun errorCantForJustify(e: Exception?){
        if(e is VolleyError){

            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
            //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()

        }else{

        }
    }
 /*Cargar justification para comensales*/

    private fun before(){
        enabledToggles(false)

        menuWeekSwipeRefresh.isRefreshing=true
        textViewTitleMenuWeek.visibility=View.GONE;
    }

    private fun after(){
        if(menuWeekSwipeRefresh!=null){
            menuWeekSwipeRefresh.isRefreshing=false
            enabledToggles(true)
        }
    }

    private fun resultWeek(objJson: JSONObject?){
        execMenuWeek(objJson)
        setSaveMenuWeek(objJson)
        if(menuWeekSwipeRefresh!=null){
            mRootView.snack(context, R.string.update_success, SnackType.FINISH)
        }
    }

    private fun resultForJustification(objJson: JSONObject?){
        execMenuWeek(objJson)
        setSaveMenuForJustify(objJson)
        if(menuWeekSwipeRefresh!=null){
            mRootView.snack(context, R.string.update_success, SnackType.FINISH)
        }
    }

    private fun resultPast(objJson: JSONObject?){
        execMenuWeek(objJson)
        setSaveMenuPast(objJson)
        if(menuWeekSwipeRefresh!=null){
            mRootView.snack(context, R.string.update_success, SnackType.FINISH)
        }
    }

    private fun errorWeek(e: Exception?){
        execMenuWeek(getSaveMenuWeek());

        if(e is VolleyError){
            if(menuWeekSwipeRefresh!=null){
                menuWeekSwipeRefresh.isRefreshing=false
                textViewTitleMenuWeek.visibility=View.VISIBLE
                textViewTitleMenuWeek.text=mApplicationContext.resources.getString(R.string.message_menu_week_newtwork)
                VolleySingleton.showErrorVolleySnack(
                    mRootView,
                    mApplicationContext,
                    e
                )
            }
        }else{
            Toast.makeText(mApplicationContext, e!!.message, Toast.LENGTH_LONG )
        }
    }

    private fun errorJustify(e: Exception?){
        execMenuWeek(getSaveMenuForJustify())

        if(e is VolleyError){
            if(menuWeekSwipeRefresh!=null){
                menuWeekSwipeRefresh.isRefreshing=false
                textViewTitleMenuWeek.visibility=View.VISIBLE
                textViewTitleMenuWeek.text=mApplicationContext.resources.getString(R.string.message_menu_week_newtwork)
                VolleySingleton.showErrorVolleySnack(
                    mRootView,
                    mApplicationContext,
                    e
                )
            }
        }else{
            Toast.makeText(mApplicationContext, e!!.message, Toast.LENGTH_LONG )
        }
    }

    private fun errorPast(e: Exception?){
        execMenuWeek(getSaveMenuPast());

        if(e is VolleyError){
            if(menuWeekSwipeRefresh!=null){
                menuWeekSwipeRefresh.isRefreshing=false
                textViewTitleMenuWeek.visibility=View.VISIBLE
                textViewTitleMenuWeek.text=mApplicationContext.resources.getString(R.string.message_menu_week_newtwork)
                VolleySingleton.showErrorVolleySnack(
                    mRootView,
                    mApplicationContext,
                    e
                )
            }
        }else{
            Toast.makeText(mApplicationContext, e!!.message, Toast.LENGTH_LONG )
        }
    }

    private fun execMenuWeek(objJson: JSONObject?) {
        if (objJson == null)
            return
        if (objJson.getInt("state") == 0) {
            Toast.makeText(view?.getContext(), objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }
        //Toast.makeText(context!!,objJson.toString(), Toast.LENGTH_LONG).show()
        val objJsonArray: JSONArray =objJson.getJSONArray("data")
        val menuList = ArrayList<Any>()
        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject=objJsonArray.getJSONObject(i);

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val sdfDateTime=SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val skd_date = sdf.parse(objJsonData.getString("skd_date"))
            val reser_date_start=sdfDateTime.parse(objJsonData.getString("reser_date_start"))
            val reser_date_end=sdfDateTime.parse(objJsonData.getString("reser_date_end"))
            val assist_time:Date?=if(objJsonData.isNull("assist_time")) null else sdfDateTime.parse(objJsonData.getString("assist_time"))
            val reservation_time:Date?=if(objJsonData.isNull("reservation_time")) null else sdfDateTime.parse(objJsonData.getString("reservation_time"))

            val objJsonTypeItem :Int =objJsonData.getInt("type_item")
            when (objJsonTypeItem) {
                MenuWeekAdapter.TYPE_MENU-> {
                    var menu = Menu(
                        objJsonData.getString("id").toInt(),
                        objJsonData.getInt("type"),

                        if (objJsonData.isNull("second")) null else objJsonData.getString("second"),
                        if (objJsonData.isNull("soup")) null else objJsonData.getString("soup"),
                        if (objJsonData.isNull("drink")) null else objJsonData.getString("drink"),
                        if (objJsonData.isNull("fruit")) null else objJsonData.getString("fruit"),
                        if (objJsonData.isNull("dessert")) null else objJsonData.getString("dessert"),
                        if (objJsonData.isNull("aditional")) null else objJsonData.getString("aditional"),
                        skd_date,
                        objJsonData.getInt("state_reser"),
                        reser_date_start,
                        reser_date_end,

                        /*objJsonData.getString("reser_date_start_char"),*/
                        /*objJsonData.getString("reser_date_end_char"),*/
                        objJsonData.getInt("state_menu_reservation"),
                        if (objJsonData.isNull("horary_of_reser")) null else objJsonData.getString("horary_of_reser"),
                        if (objJsonData.isNull("assist")) null else objJsonData.optBoolean("assist"),
                        assist_time,
                        if (objJsonData.isNull("id_timetable")) null else objJsonData.getInt("id_timetable"),
                        reservation_time

                    )
                    menuList.add(
                        menu
                    )
                }
                MenuWeekAdapter.TYPE_JUSTIFICATION_MENU->{
                    var justMenu = JustificationMenuItem(
                        objJsonData.getString("id").toInt(),
                        objJsonData.getInt("type"),
                        if (objJsonData.isNull("second")) null else objJsonData.getString("second"),
                        if (objJsonData.isNull("soup")) null else objJsonData.getString("soup"),
                        if (objJsonData.isNull("drink")) null else objJsonData.getString("drink"),
                        if (objJsonData.isNull("fruit")) null else objJsonData.getString("fruit"),
                        if (objJsonData.isNull("dessert")) null else objJsonData.getString("dessert"),
                        if (objJsonData.isNull("aditional")) null else objJsonData.getString("aditional"),
                        skd_date,
                        objJsonData.getInt("state_reser"),
                        reser_date_start,
                        reser_date_end,

                        objJsonData.getInt("state_menu_reservation"),
                        if (objJsonData.isNull("horary_of_reser")) null else objJsonData.getString("horary_of_reser"),
                        if (objJsonData.isNull("assist")) null else objJsonData.optBoolean("assist"),
                        assist_time,
                        if (objJsonData.isNull("id_timetable")) null else objJsonData.getInt("id_timetable"),
                        reservation_time,

                        objJsonData.getString("just_state")
                    )
                    menuList.add(
                        justMenu
                    )
                }
                MenuWeekAdapter.TYPE_PAST_MENU->{
                    var justMenu = PastMenuItem(
                        objJsonData.getString("id").toInt(),
                        objJsonData.getInt("type"),

                        if (objJsonData.isNull("second")) null else objJsonData.getString("second"),
                        if (objJsonData.isNull("soup")) null else objJsonData.getString("soup"),
                        if (objJsonData.isNull("drink")) null else objJsonData.getString("drink"),
                        if (objJsonData.isNull("fruit")) null else objJsonData.getString("fruit"),
                        if (objJsonData.isNull("dessert")) null else objJsonData.getString("dessert"),
                        if (objJsonData.isNull("aditional")) null else objJsonData.getString("aditional"),
                        skd_date,
                        objJsonData.getInt("state_reser"),
                        reser_date_start,
                        reser_date_end,


                        objJsonData.getInt("state_menu_reservation"),
                        if (objJsonData.isNull("horary_of_reser")) null else objJsonData.getString("horary_of_reser"),
                        if (objJsonData.isNull("assist")) null else objJsonData.optBoolean("assist"),
                        assist_time,
                        if (objJsonData.isNull("id_timetable")) null else objJsonData.getInt("id_timetable"),
                        reservation_time,
                        objJsonData.getString("just_state")
                    )
                    menuList.add(
                        justMenu
                    )
                }
            }

            mMenuWeekAdapter.setList(menuList)
        }
    }

    private fun initRecyclerAndAdapter() {
        menuWeekRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        menuWeekRecyclerView.layoutManager = layoutManager
    }


    private fun initMenu() {
        toggleButtonWeek.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                OPTION_SELECT=buttonView.id
                mMenuWeekAdapter.clear()
                loadData(OPTION_SELECT)

            }
        }

        toggleButtonJustify.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                OPTION_SELECT=buttonView.id
                mMenuWeekAdapter.clear()
                loadData(OPTION_SELECT)
            }
        }

       toggleButtonPast.setOnCheckedChangeListener { buttonView, isChecked ->
           if(isChecked){
               OPTION_SELECT=buttonView.id
               mMenuWeekAdapter.clear()
               loadData(OPTION_SELECT)
           }
       }

        mMenuWeekAdapter = MenuWeekAdapter(context!!/*, getlListMenu(),
            {onClick(it)}*/)
        menuWeekRecyclerView.adapter = mMenuWeekAdapter
    }

    private fun enabledToggles(state : Boolean){
        toggleButtonWeek.isEnabled=state
        toggleButtonJustify.isEnabled=state
        toggleButtonPast.isEnabled=state
    }
    private fun onClick(dayMenu: Menu) {
        try{

            var frag4 = CReservationFragment()
            val args4 = Bundle()
            args4.putInt("id",dayMenu.id);
            args4.putInt("typeMenu", dayMenu.type)
            args4.putString("day_month",dayMenu.day_of_month)
            args4.putString("name_day",dayMenu.name_day_of_week)

            val sdf = SimpleDateFormat("dd-MM-yy")
            val skd_dates : String = sdf.format(dayMenu.skd_date)

            args4.putString("skd_date",skd_dates)

            frag4.setArguments(args4)


            val fm= (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            fm.replace(R.id.framelayout, frag4, "reservation")
            fm.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
            fm.addToBackStack("reservation")
            fm.commit()
        }catch(e:Exception){
            Toast.makeText(context, e.message/*obj.getString("estado")*/, Toast.LENGTH_LONG).show()

        }
    }


    private fun getlListMenu():List<Menu>{
        val menuList = ArrayList<Menu>()
        // leer archivos temporales
        return menuList
    }

    override fun onResume() {
        super.onResume()
        //requireActivity().setTitle(R.string.navigation_menu)
        (activity as CMainActivity).supportActionBar?.title = GET_NAME_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt()
        )

        var toolbar: Toolbar = (activity as CMainActivity).toolbar
        var params : AppBarLayout.LayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags=0

        loadData(OPTION_SELECT)

    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_menu_week, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_week_update-> {
                loadData(OPTION_SELECT)
               // sendData(Server.GET_MENU, mApplicationContext, getParams())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getSaveMenuWeek():JSONObject?{
        var objJson : JSONObject?=null

        try{
            var listMneu:String?=mApplicationContext.readStringFromStorage(GET_FILE_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt()
            ))

            if(listMneu!=null && !listMneu.trim().equals("")){
                objJson = JSONObject(listMneu)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }
    private fun setSaveMenuWeek(objJson :  JSONObject?){

        var objJsonString=if(objJson==null) "" else objJson.toString()
        mApplicationContext.saveToStorage(GET_FILE_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt()
            ), objJsonString)

    }


    //justify
    private fun getSaveMenuForJustify():JSONObject?{
        var objJson : JSONObject?=null

        try{
            var listMneu:String?=mApplicationContext.readStringFromStorage(FILE_MENU_FOR_JUSTIFICATION(arguments!!.get("typeMenu").toString().toInt()
            ))

            if(listMneu!=null && !listMneu.trim().equals("")){
                objJson = JSONObject(listMneu)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }
    private fun setSaveMenuForJustify(objJson :  JSONObject?){

        var objJsonString=if(objJson==null) "" else objJson.toString()
        mApplicationContext.saveToStorage(
            FILE_MENU_FOR_JUSTIFICATION(arguments!!.get("typeMenu").toString().toInt()
        ), objJsonString)

    }

    //past
    private fun getSaveMenuPast(): JSONObject?{
        var objJson : JSONObject?=null

        try{
            var listMneu:String?=mApplicationContext.readStringFromStorage(FILE_MENU_PAST(arguments!!.get("typeMenu").toString().toInt()
            ))

            if(listMneu!=null && !listMneu.trim().equals("")){
                objJson = JSONObject(listMneu)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }
    private fun setSaveMenuPast(objJson :  JSONObject?){

        var objJsonString=if(objJson==null) "" else objJson.toString()
        mApplicationContext.saveToStorage(
            FILE_MENU_PAST(arguments!!.get("typeMenu").toString().toInt()
        ), objJsonString)

    }



    companion object {
        var DESAYUNO: Long = 1
        var ALMUERZO: Long = 2
        var CENA: Long = 3

        fun GET_NAME_TYPE_MENU(typoMenu:Int):String{
            when (typoMenu){
                1->return "Desayuno"
                2->return "Almuerzo"
                3->return "Cena"
                else->return "Otros Bocadillos"
            }
        }

        fun GET_FILE_TYPE_MENU(typoMenu:Int):String{
            when (typoMenu){
                1->return "desayuno.json"
                2->return "almuerzo.json"
                3->return "cena.json"
                else->return "otros_ocadillos.json"
            }
        }

        fun FILE_MENU_FOR_JUSTIFICATION(typeMenu: Int):String{
            when (typeMenu){
                1->return "desayuno_for_justification.json"
                2->return "almuerzo_for_justification.json"
                3->return "cena_for_justification.json"
                else->return "otros_ocadillos_for_justification.json"
            }
        }

        fun FILE_MENU_PAST(typeMenu: Int):String{
            when (typeMenu){
                1->return "desayuno_past.json"
                2->return "almuerzo_past.json"
                3->return "cena_for_past.json"
                else->return "otros_ocadillos_past.json"
            }
        }



    }





}