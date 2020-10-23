package com.undac.undaccomedor

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.controller.reservation.Horary
import com.undac.undaccomedor.controller.reservation.HoraryAdapter
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.session.SessionManager
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.*
import kotlinx.android.synthetic.main.activity_reservation.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap


class CReservationActivity : AppCompatActivity() {

    private lateinit var mHoraryAdapter: HoraryAdapter
    private lateinit var mRootView: View
    private lateinit var dayMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        mRootView=(findViewById(android.R.id.content) as View)
        initView()
    }



    private fun initView(){
        dayMenu=intent.getSerializableExtra(Menu.NAME_SERIALIZABLE) as Menu

        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(GET_NAME_TYPE_MENU(dayMenu.type))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        textViewReserFecha.text="${dayMenu.name_day_of_week}, ${dayMenu.day_of_month} de ${dayMenu.name_month.toLowerCase()} de ${dayMenu.year}"//arguments!!.get("name_day").toString()+", "+arguments!!.get("skd_date").toString()



        initRecyclerAndAdapter()
        horarySwipeRefresh.init {
            loadData()
        }

        initHorary()
        loadData()
    }





    override fun  onSupportNavigateUp() :Boolean {
        onBackPressed()
        return false
    }

    override  fun onBackPressed(){
        super.onBackPressed()
        finish()
    }



    private fun loadData(){

        var volleyCheckMenuEnable = VolleySingleton(this)// VolleySingleton.getInstance(this)
        volleyCheckMenuEnable.prepare(this, Server.CHECK_MEMU_ENABLE, result={resultCheckMenuEnable(it)}, error = {errorCheckMenuEnable(it)}, before = {beforeCheckMenuEnable()}, after={afterCheckMenuEnable()} )
        volleyCheckMenuEnable.post(Pair("id_menu", dayMenu.id))

        //getHorary(Server.GET_HORARY_CANT_RESERVATION, applicationContext, getParamsHorary())

        var volleyListMenu = VolleySingleton(this)//VolleySingleton.getInstance(this)
        volleyListMenu.prepare(this, Server.GET_HORARY_CANT_RESERVATION, result={resultListHorary(it)}, error = {errorListHorary(it)}, before = {beforeListHorary()}, after={afterListHorary()} )
        volleyListMenu.setUser()
        volleyListMenu.post(
            Pair("typeMenu", dayMenu.type),
            Pair("id_menu", dayMenu.id)

        )
    }

    /* Get data hoarario por tipo y fecha*/
    private fun beforeListHorary(){
        horarySwipeRefresh.isRefreshing=true
        textViewReserTitle.visibility= View.GONE
    }

    private fun afterListHorary(){
        horarySwipeRefresh.isRefreshing=false

    }

    private fun resultListHorary(objJson: JSONObject?){
        try {
            //inicio de acciones
            loadHorayReservation(objJson);
            setSaveReservation(objJson!!)
            mRootView.snack(this, R.string.update_success, SnackType.FINISH)

            //find dea acciones
        } catch (e: Exception) {
            e.printStackTrace()
            mRootView.snack(this,R.string.update_fail, SnackType.ERROR)

        }
    }

    private fun errorListHorary(e: Exception?){

        loadHorayReservation(getSaveReservation())
        if(e is VolleyError){
            if(horarySwipeRefresh!=null){
                textViewReserTitle.visibility= View.VISIBLE
                textViewReserTitle.text=this.resources.getString(R.string.message_reservation_newtwork)
                VolleySingleton.showErrorVolleySnack(
                    mRootView,
                    this,
                    e
                )
            }
        }

    }

    private fun loadHorayReservation(objJson: JSONObject?) {
        if (objJson == null)
            return

        if (objJson.getInt("state") == 0) {
            Toast.makeText(this, objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }

        var idTimiTableReservation:Int=-1;

        val objJsonArrayReservation: JSONArray =objJson.getJSONArray("data_reservation")

        val objJsonArrayHorary: JSONArray =objJson.getJSONArray("data_horary")


        if(objJsonArrayReservation.length()!=0){
            val objJsonDataReservation: JSONObject =objJsonArrayReservation.getJSONObject(0);
            idTimiTableReservation=objJsonDataReservation.optInt("id_timetable",-1);
        }

        val horaryList = ArrayList<Horary>()
        for (i in 0..objJsonArrayHorary.length()-1) {
            val objJsonDataHorary: JSONObject =objJsonArrayHorary.getJSONObject(i);
            var checked:Boolean=false;
            if(idTimiTableReservation==objJsonDataHorary.getInt("id")){
                checked=true
            }

            horaryList.add(
                Horary(
                    objJsonDataHorary.getInt("id"),
                    objJsonDataHorary.getInt("type"),
                    objJsonDataHorary.getString("food_start"),
                    objJsonDataHorary.getString("food_end"),
                    objJsonDataHorary.getInt("cant"),
                    objJsonDataHorary.getInt("cantreser"),
                    objJsonDataHorary.getString("food_start_char"),
                    objJsonDataHorary.getString("food_end_char"),
                    checked
                )
            )
        }
        mHoraryAdapter.updateHorary(horaryList)

    }

    private fun onClick(horary: Horary, toggleButtonReser: ToggleButton, i:Int){
        if(toggleButtonReser.isChecked){
            showDialogConfirm("", i, horary.id,toggleButtonReser)

        }else{
            showDialogDelete("", i, horary.id, toggleButtonReser)
        }
    }

    private  fun execReservation(objJson: JSONObject?, i:Int){
        if (objJson == null)
            return

        loadData()

        showDialogResult(objJson.getInt("state"),objJson.getString("message") )
        if(objJson.getInt("state")==1){

        }
    }

    private fun execCheckMenuEnable(objJson: JSONObject?){
        if (objJson == null)
            return

        if (objJson.getInt("state") == 0) {
            textViewMensaje.setBackgroundResource(R.color.accent)
            //linearLayoutReservacion!!.isEnabled=false;
        }else if(objJson.getInt("state") == 1){
            textViewMensaje.setBackgroundResource(R.color.colorSegundo)
            //linearLayoutReservacion!!.isEnabled=true
        }
        textViewMensaje.text=objJson.getString("message").toString()

        val objJsonArray: JSONArray =objJson.getJSONArray("data")

        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject =objJsonArray.getJSONObject(i);
            val _idMenu=objJsonData.getInt("id")
        }
    }


    /* Get data hoarario por tipo y fecha*/
    private fun getHorary(URL: String, context: Context, params: MutableMap<String, String>) {
        var objJson: JSONObject? = null
        horarySwipeRefresh.isRefreshing=true
        textViewReserTitle.visibility= View.GONE
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener<String> { response ->
                try {
                    objJson = JSONObject(response)
                    //inicio de acciones
                    loadHorayReservation(objJson!!);
                    setSaveReservation(objJson!!)
                    mRootView.snack(context, R.string.update_success, SnackType.FINISH)

                    //find dea acciones
                } catch (e: Exception) {
                    mRootView.snack(context, R.string.update_fail, SnackType.ERROR)

                }
                horarySwipeRefresh.isRefreshing=false
            }, object : Response.ErrorListener {
                override fun onErrorResponse(p0: VolleyError?) {
                    loadHorayReservation(getSaveReservation())

                    if(horarySwipeRefresh!=null){
                        horarySwipeRefresh.isRefreshing=false
                        textViewReserTitle.visibility= View.VISIBLE
                        textViewReserTitle.text=applicationContext.resources.getString(R.string.message_reservation_newtwork)
                        VolleySingleton.showErrorVolleySnack(
                            mRootView,
                            applicationContext,
                            p0
                        )
                    }



                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    /* Get data hoarario por tipo y fecha*/
    private fun beforeCheckMenuEnable(){
        horarySwipeRefresh.isRefreshing=true

    }

    private fun afterCheckMenuEnable(){

    }

    private fun resultCheckMenuEnable(objJson: JSONObject?){
        execCheckMenuEnable(objJson);
        setSaveCheckMenu(objJson)
    }

    private fun errorCheckMenuEnable(e: Exception?){
        execCheckMenuEnable(getSaveCheckMenu())
    }

    private fun checkMenuEnable(URL: String, context: Context, params: MutableMap<String, String>) {
        var objJson: JSONObject? = null
        horarySwipeRefresh.isRefreshing=true

        val stringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener<String> { response ->

                try {
                    objJson = JSONObject(response)
                    //inicio de acciones
                    execCheckMenuEnable(objJson!!);
                    setSaveCheckMenu(objJson!!)
                    //find dea acciones
                } catch (e: Exception) {
                    //Toast.makeText(context, "$response"/*obj.getString("estado")*/, Toast.LENGTH_LONG).show()
                    //Toast.makeText(context, e.message/*obj.getString("estado")*/, Toast.LENGTH_LONG).show()
                }
                //horarySwipeRefresh.isRefreshing=false

            }, object : Response.ErrorListener {
                override fun onErrorResponse(p0: VolleyError?) {
                    execCheckMenuEnable(getSaveCheckMenu())
                    // horarySwipeRefresh.isRefreshing=false
                    //Toast.makeText(context, "Error $p0?.message", Toast.LENGTH_LONG).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun getParamsMenu(): MutableMap<String, String> {
        val params = HashMap<String, String>()
        params.put("id_menu",intent.getStringExtra("id").toString())
        return params
    }

    /* send enviar reservatión*/
    private fun sendReservation(URL: String, context: Context, params: MutableMap<String, String>, i:Int, toggleButtonReser: ToggleButton, dialog: Dialog) {
        var objJson: JSONObject? = null
        horarySwipeRefresh.isRefreshing=true
        val stringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener<String> { response ->
                try {
                    objJson = JSONObject(response)
                    //inicio de acciones


                    execReservation(objJson,i)
                    dialog.dismiss()
                    //find dea acciones
                } catch (e: Exception) {
                    Toast.makeText(context, "Surgio error: $e"/*obj.getString("estado")*/, Toast.LENGTH_LONG).show()
                }
                horarySwipeRefresh.isRefreshing=false
            }, object : Response.ErrorListener {
                override fun onErrorResponse(p0: VolleyError?) {
                    if(horarySwipeRefresh!=null){
                        horarySwipeRefresh.isRefreshing=false
                        toggleButtonReser.isChecked=!toggleButtonReser.isChecked
                        dialog.dismiss()
                        VolleySingleton.showErrorVolleyDialog( { state, message -> showDialogResult(state, message) }, applicationContext, p0)
                    }
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }


    fun getParamsReservation(idHorary: Int, idMenu: Int ): MutableMap<String, String> {
        val params = HashMap<String, String>()

        try{
            var idHoraryS="";
            if(idHorary!=1)
                idHoraryS=idHorary.toString()

            params.put("id_menu",idMenu.toString() )
            var session= SessionManager(applicationContext)
            var user:HashMap<String, String> =session.getUserDetails()
            params.putAll(user)

            //params.put("id_user",idUser)
            params.put("id_timetable",idHoraryS)
        }catch (e:Exception){
           // var intent= Intent(applicationContext, CLoginActivity::class.java)
            finish()
            startActivity(intent)
        }





        return params
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_reservation, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }*/


    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
       // return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_reservation, menu)
        return true
    }
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.fragment_reservation, menu)
        return true
    }*/




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.horary_update -> {
                loadData()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun initRecyclerAndAdapter() {
        horaryRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        horaryRecyclerView.layoutManager = layoutManager

    }

    private fun initHorary() {
        mHoraryAdapter= HoraryAdapter(
            applicationContext,
            getlListHorary(),
            { horary, toggleButtonReser, i -> onClick(horary, toggleButtonReser, i) })

        horaryRecyclerView.adapter = mHoraryAdapter
    }

    private fun getlListHorary():List<Horary>{
        val horaryList = ArrayList<Horary>()
        return horaryList
    }

    override fun onResume() {
        super.onResume()
        //requireActivity().setTitle(R.string.navigation_menu)
        //supportActionBar?.title = GET_NAME_TYPE_MENU(intent.getStringExtra("typeMenu").toString().toInt()
       // )
    }
    private fun showDialogDelete(title: String,i:Int, idHorary:Int,  toggleButtonReser: ToggleButton)  {
        var dialogs = Dialog(this)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(false)
        dialogs.setContentView(R.layout.dialog_delete_reservation)

        val buttonConfirmar : Button = dialogs.findViewById(R.id.buttonConfirmar) as Button
        val buttonCancel: Button = dialogs.findViewById(R.id.buttonCancel) as Button


        buttonConfirmar.setOnClickListener {

            buttonConfirmar.text="Eliminando.."
            buttonConfirmar.isEnabled=false
            buttonCancel.isEnabled=false
            sendReservation(
                Server.SET_RESERVATION, applicationContext, getParamsReservation(-1, dayMenu.id/*intent.getStringExtra("id").toString().toInt()*/
                ), i,toggleButtonReser, dialogs)


        }

        buttonCancel.setOnClickListener {
            toggleButtonReser.isChecked=!toggleButtonReser.isChecked
            dialogs.dismiss()
        }
        dialogs.show()
    }
    private fun showDialogConfirm(title: String, i:Int, idHorary:Int, toggleButtonReser: ToggleButton) {
        var dialogs = Dialog(this)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(false)
        dialogs.setContentView(R.layout.dialog_confirm_reservation)

        val buttonConfirmar : Button = dialogs.findViewById(R.id.buttonConfirmar) as Button
        val buttonCancel: Button = dialogs.findViewById(R.id.buttonCancel) as Button

        buttonConfirmar.setOnClickListener {
            buttonConfirmar.text="Confirmando.."
            buttonConfirmar.isEnabled=false
            buttonCancel.isEnabled=false
            sendReservation(
                Server.SET_RESERVATION, applicationContext, getParamsReservation(idHorary, dayMenu.id/*intent.getStringExtra("id").toString().toInt()*/
                ), i, toggleButtonReser, dialogs)
            //dialogs.dismiss()
        }

        buttonCancel.setOnClickListener {
            toggleButtonReser.isChecked=!toggleButtonReser.isChecked
            dialogs.dismiss()
        }
        dialogs.show()
    }

    private fun showDialogResult(state:Int, message:String) {
        var dialogs = Dialog(this)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(false)
        dialogs.setContentView(R.layout.dialog_result_reservation)

        val buttonSalir : Button = dialogs.findViewById(R.id.buttonSalir) as Button
        val textViewMessage: TextView =dialogs.findViewById(R.id.textViewMessage) as TextView
        val imageViewState: ImageView =dialogs.findViewById(R.id.imageViewState) as ImageView

        if(state==1){
            imageViewState.setImageResource(R.drawable.check)
            buttonSalir.text = "SALIR"
        }else if(state==0){
            imageViewState.setImageResource(R.drawable.icons8_cancelar_2_48)
            buttonSalir.text = "VOLVER"
        }
        textViewMessage.text=message
        buttonSalir.setOnClickListener {

            if(state==1){
                dialogs.dismiss()
                onBackPressed()
            }else if(state==0){
                dialogs.dismiss()
            }
        }
        dialogs.show()
    }

    private fun getSaveReservation(): JSONObject?{
        try{
            return JSONObject(this.readStringFromStorage(GET_FILE_RESERVATION_TYPE_MENU(dayMenu.type/*intent.getStringExtra("typeMenu").toString().toInt()*/, /*intent.getStringExtra("id").toString().toInt()*/dayMenu.id
            )))
        }catch(e:Exception){
            return null
        }

    }
    private fun setSaveReservation(objJson : JSONObject){
        this.saveToStorage(GET_FILE_RESERVATION_TYPE_MENU(/*intent!!.getStringExtra("typeMenu").toString().toInt()*/dayMenu.type, /*intent!!.getStringExtra("id").toString().toInt()*/dayMenu.id
        ), objJson.toString())
    }

    private fun getSaveCheckMenu(): JSONObject?{
        try{
            return JSONObject(
                this.readStringFromStorage(GET_FILE_CHECK_MENU_TYPE_MENU(/*intent!!.getStringExtra("typeMenu").toString().toInt()*/dayMenu.type, dayMenu.id/*intent!!.getStringExtra("id").toString().toInt()*/
            )))
        }catch(e:Exception){
            return null
        }

    }
    private fun setSaveCheckMenu(objJson : JSONObject?){
        this.saveToStorage(GET_FILE_CHECK_MENU_TYPE_MENU(/*intent!!.getStringExtra("typeMenu").toString().toInt()*/dayMenu.type, /*intent!!.getStringExtra("id").toString().toInt()*/dayMenu.id
        ), objJson.toString())
    }

    companion object {
        private var DESAYUNO: Long = 1
        private var ALMUERZO: Long = 2
        private var CENA: Long = 3

        private fun GET_NAME_TYPE_MENU(typoMenu:Int):String{
            when (typoMenu){
                1->return "Desayuno > Reservación"
                2->return "Almuerzo > Reservación"
                3->return "Cena > Reservación"
                else->return "Otros Bocadillos > Reservación"
            }
        }

        private fun GET_FILE_RESERVATION_TYPE_MENU(typoMenu:Int, id:Int):String{
            when (typoMenu){
                1->return "desayuno_reser_$id.json"
                2->return "almuerzo_reser_$id.json"
                3->return "cena_reser_$id.json"
                else->return "otros_bocadillos_reser_$id.json"
            }
        }

        private fun GET_FILE_CHECK_MENU_TYPE_MENU(typoMenu:Int, id:Int):String{
            when (typoMenu){
                1->return "m_desayuno_reser_$id.json"
                2->return "m_almuerzo_reser_$id.json"
                3->return "m_cena_reser_$id.json"
                else->return "m_otros_bocadillos_reser_$id.json"
            }
        }
    }
}
