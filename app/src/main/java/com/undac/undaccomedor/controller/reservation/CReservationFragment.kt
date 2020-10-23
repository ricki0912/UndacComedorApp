package com.undac.undaccomedor.controller.reservation
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.controller.login.CLoginActivity
import com.undac.undaccomedor.volley.VolleySingleton
import com.undac.undaccomedor.server.Server
import com.undaccomedor.helpers.*
import kotlinx.android.synthetic.main.fragment_reservation.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class CReservationFragment : Fragment(){

    private lateinit var mRootView: View
    private lateinit var mHoraryAdapter: HoraryAdapter
    private lateinit var mApplicationContext: Context


    private var linearLayoutReservacion: LinearLayout?=null
    private var textViewMensaje:TextView?=null
    private var textViewReserFecha:TextView?    =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mRootView = inflater.inflate(R.layout.fragment_reservation, container, false)

        mApplicationContext = requireActivity().applicationContext
        return mRootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutReservacion = view!!.findViewById(R.id.linearLayoutReservacion)

        textViewMensaje=view!!.findViewById(R.id.textViewMensaje)
        textViewReserFecha=view!!.findViewById(R.id.textViewReserFecha)

        textViewReserFecha!!.text=arguments!!.get("name_day").toString()+", "+arguments!!.get("skd_date").toString()


        initRecyclerAndAdapter()
        horarySwipeRefresh.init {
            loadData()
        }

        initHorary()
        loadData()

    }

    private fun loadData(){
        getHorary(Server.GET_HORARY_CANT_RESERVATION, mApplicationContext, getParamsHorary())
        checkMenuEnable(Server.CHECK_MEMU_ENABLE,mApplicationContext, getParamsMenu())
    }

    private fun loadHorayReservation(objJson: JSONObject?) {
        if (objJson == null)
            return

        if (objJson.getInt("state") == 0) {
            Toast.makeText(mApplicationContext, objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }

        var idTimiTableReservation:Int=-1;

        val objJsonArrayReservation: JSONArray=objJson.getJSONArray("data_reservation")

        val objJsonArrayHorary: JSONArray =objJson.getJSONArray("data_horary")


        if(objJsonArrayReservation.length()!=0){
            val objJsonDataReservation: JSONObject=objJsonArrayReservation.getJSONObject(0);
            idTimiTableReservation=objJsonDataReservation.optInt("id_timetable",-1);
        }

        val horaryList = ArrayList<Horary>()
        for (i in 0..objJsonArrayHorary.length()-1) {
            val objJsonDataHorary: JSONObject=objJsonArrayHorary.getJSONObject(i);
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

    private fun onClick(horary: Horary, toggleButtonReser:ToggleButton, i:Int){
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
            textViewMensaje!!.setBackgroundResource(R.color.accent)
            //linearLayoutReservacion!!.isEnabled=false;
        }else if(objJson.getInt("state") == 1){
            textViewMensaje!!.setBackgroundResource(R.color.colorSegundo)
            //linearLayoutReservacion!!.isEnabled=true
        }
        textViewMensaje!!.text=objJson.getString("message").toString()

        val objJsonArray: JSONArray =objJson.getJSONArray("data")

        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject=objJsonArray.getJSONObject(i);
            val _idMenu=objJsonData.getInt("id")
        }
    }


/* Get data hoarario por tipo y fecha*/
    private fun getHorary(URL: String, context: Context, params: MutableMap<String, String>) {
        var objJson: JSONObject? = null
        horarySwipeRefresh.isRefreshing=true
    textViewReserTitle.visibility=View.GONE
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
                        textViewReserTitle.visibility=View.VISIBLE
                        textViewReserTitle.text=mApplicationContext.resources.getString(R.string.message_reservation_newtwork)
                        VolleySingleton.showErrorVolleySnack(
                            mRootView,
                            mApplicationContext,
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


    fun getParamsHorary( ): MutableMap<String, String> {
        val params = HashMap<String, String>()
        params.put("typeMenu",arguments!!.get("typeMenu").toString())
        params.put("id_menu",arguments!!.get("id").toString())
        /*params.put("uid", Session.UID!!)
        params.put("eid", Session.EID!!)
        params.put("oid", Session.OID!!)
        params.put("escid", Session.ESCID!!)
        params.put("subid",Session.SUBID!!)
        params.put("pid", Session.PID!!)*/
        return params
    }



    /* Get data hoarario por tipo y fecha*/
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
        params.put("id_menu",arguments!!.get("id").toString())
        return params
    }



    /* send enviar reservatión*/
    private fun sendReservation(URL: String, context: Context, params: MutableMap<String, String>, i:Int, toggleButtonReser:ToggleButton, dialog: Dialog) {
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
                        VolleySingleton.showErrorVolleyDialog( { state, message -> showDialogResult(state, message) }, mApplicationContext, p0)
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

            /*params.put("uid", Session.UID!!)
            params.put("eid", Session.EID!!)
            params.put("oid", Session.OID!!)
            params.put("escid", Session.ESCID!!)
            params.put("subid",Session.SUBID!!)
            params.put("pid", Session.PID!!)*/

            //params.put("id_user",idUser)
            params.put("id_timetable",idHoraryS)
        }catch (e:Exception){
            var intent= Intent(mApplicationContext, CLoginActivity::class.java)
            (activity as CMainActivity).finish()
            startActivity(intent)
        }





        return params
    }

 override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       inflater.inflate(R.menu.activity_reservation, menu)
     super.onCreateOptionsMenu(menu, inflater)
    }

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
        val layoutManager = LinearLayoutManager(context)
        horaryRecyclerView.layoutManager = layoutManager
    }

    private fun initHorary() {
        mHoraryAdapter= HoraryAdapter(
            mApplicationContext,
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
        (activity as CMainActivity).supportActionBar?.title = GET_NAME_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt()
        )
    }
    private fun showDialogDelete(title: String,i:Int, idHorary:Int,  toggleButtonReser:ToggleButton)  {
        var dialogs = Dialog(activity)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(false)
        dialogs.setContentView(R.layout.dialog_delete_reservation)

        val buttonConfirmar :Button = dialogs.findViewById(R.id.buttonConfirmar) as Button
        val buttonCancel:Button = dialogs.findViewById(R.id.buttonCancel) as Button


        buttonConfirmar.setOnClickListener {

            buttonConfirmar.text="Eliminando.."
            buttonConfirmar.isEnabled=false
            buttonCancel.isEnabled=false
            sendReservation(Server.SET_RESERVATION, mApplicationContext, getParamsReservation(-1, arguments!!.get("id").toString().toInt()
            ), i,toggleButtonReser, dialogs)


        }

        buttonCancel.setOnClickListener {
            toggleButtonReser.isChecked=!toggleButtonReser.isChecked
            dialogs.dismiss()
        }
        dialogs.show()
    }
    private fun showDialogConfirm(title: String, i:Int, idHorary:Int, toggleButtonReser:ToggleButton) {
        var dialogs = Dialog(activity)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(false)
        dialogs.setContentView(R.layout.dialog_confirm_reservation)

        val buttonConfirmar :Button = dialogs.findViewById(R.id.buttonConfirmar) as Button
        val buttonCancel:Button = dialogs.findViewById(R.id.buttonCancel) as Button

        buttonConfirmar.setOnClickListener {
            buttonConfirmar.text="Confirmando.."
            buttonConfirmar.isEnabled=false
            buttonCancel.isEnabled=false
            sendReservation(Server.SET_RESERVATION, mApplicationContext, getParamsReservation(idHorary, arguments!!.get("id").toString().toInt()
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
        var dialogs = Dialog(activity)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(false)
        dialogs.setContentView(R.layout.dialog_result_reservation)

        val buttonSalir :Button = dialogs.findViewById(R.id.buttonSalir) as Button
        val textViewMessage:TextView=dialogs.findViewById(R.id.textViewMessage) as TextView
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
                requireActivity().onBackPressed()
            }else if(state==0){
                dialogs.dismiss()
            }
        }
        dialogs.show()
    }

    private fun getSaveReservation():JSONObject?{
        try{
            return JSONObject(mApplicationContext.readStringFromStorage(GET_FILE_RESERVATION_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt(), arguments!!.get("id").toString().toInt()
            )))
        }catch(e:Exception){
            return null
        }

    }
    private fun setSaveReservation(objJson :  JSONObject){
        mApplicationContext.saveToStorage(GET_FILE_RESERVATION_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt(), arguments!!.get("id").toString().toInt()
        ), objJson.toString())
    }

    private fun getSaveCheckMenu():JSONObject?{
        try{
            return JSONObject(mApplicationContext.readStringFromStorage(GET_FILE_CHECK_MENU_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt(), arguments!!.get("id").toString().toInt()
            )))
        }catch(e:Exception){
            return null
        }

    }
    private fun setSaveCheckMenu(objJson :  JSONObject){
        mApplicationContext.saveToStorage(GET_FILE_CHECK_MENU_TYPE_MENU(arguments!!.get("typeMenu").toString().toInt(), arguments!!.get("id").toString().toInt()
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


