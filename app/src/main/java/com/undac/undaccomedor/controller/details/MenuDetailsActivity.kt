package com.undac.undaccomedor.controller.details

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import com.android.volley.*
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.JustificationMenuItem
import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.readStringFromStorage
import com.undaccomedor.helpers.saveToStorage

import kotlinx.android.synthetic.main.activity_menu_details.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MenuDetailsActivity : AppCompatActivity() {
    private lateinit var mRootView: View
    private lateinit var dayMenu: Menu
    private var score:Int =0
    private var comment:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_details)
        initView()
    }

    private fun initView(){
        dayMenu=intent.getSerializableExtra(Menu.NAME_SERIALIZABLE) as Menu
        setSupportActionBar(toolbar)

        supportActionBar!!.setTitle("${dayMenu.type_string} de ${SimpleDateFormat("dd-MM-yyyy", Locale( "es" , "PE")).format(dayMenu.skd_date)}")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        loadData()
    }


    private fun loadData() {

        var volleyMenu = VolleySingleton(this)// VolleySingleton.getInstance(this)
        volleyMenu.prepare(
            this,
            Server.MENU_GET_MENU,
            result = { loadDataMenu(it) },
            error = { errorMenu(it) },
            before = { beforeMenu() },
            after = { afterMenu() })
        volleyMenu.post(Pair("id_menu", dayMenu.id))

        var volleyReservation = VolleySingleton(this)// VolleySingleton.getInstance(this)
        volleyReservation.prepare(
            this,
            Server.HISTORY_RESERVATION_GET_RESERVATION,
            result = { loadDataReservation(it) },
            error = { errorReservation(it) },
            before = { beforeReservation() },
            after = { afterReservation() })
        volleyReservation.setUser()
        volleyReservation.post(Pair("id_menu", dayMenu.id))
    }

    private fun beforeMenu(){

    }
    private fun afterMenu(){

        //onBackPressed()
    }

    private fun loadDataMenu(objJson: JSONObject?){
        resultMenu(objJson)
        setSaveDetailsMenu(objJson)
    }

    private fun resultMenu(objJson: JSONObject?){
        if (objJson == null)
            return
        if (objJson.getInt("state") == 0) {
            Toast.makeText(this, objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }
        //Toast.makeText(context!!,objJson.toString(), Toast.LENGTH_LONG).show()
        val objJsonArray: JSONArray =objJson.getJSONArray("data")
        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject=objJsonArray.getJSONObject(i);

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val sdfDateTime=SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val skd_date = sdf.parse(objJsonData.getString("skd_date"))
            val reser_date_start=sdfDateTime.parse(objJsonData.getString("reser_date_start"))
            val reser_date_end=sdfDateTime.parse(objJsonData.getString("reser_date_end"))
            val assist_time: Date?=if(objJsonData.isNull("assist_time")) null else sdfDateTime.parse(objJsonData.getString("assist_time"))
            val reservation_time:Date?=if(objJsonData.isNull("reservation_time")) null else sdfDateTime.parse(objJsonData.getString("reservation_time"))

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
                null,
                if (objJsonData.isNull("assist")) null else objJsonData.optBoolean("assist"),
                assist_time,
                if (objJsonData.isNull("id_timetable")) null else objJsonData.getInt("id_timetable"),
                reservation_time

            )

            if(!menu.second.isNullOrEmpty()){
                linearLayoutSecond.visibility=View.VISIBLE
                textViewSecondDescription.text=menu.second
            }
            if(!menu.soup.isNullOrEmpty()){
                linearLayoutSoup.visibility=View.VISIBLE
                textViewSoupDescription.text=menu.soup
            }
            if(!menu.dessert.isNullOrEmpty()){
                linearLayoutDessert.visibility=View.VISIBLE
                textViewDessertDescription.text=menu.dessert
            }
            if(!menu.fruit.isNullOrEmpty()){
                linearLayoutFruit.visibility=View.VISIBLE
                textViewFruitDescription.text=menu.fruit
            }
            if(!menu.second.isNullOrEmpty()){
                linearLayoutSecond.visibility=View.VISIBLE
                textViewSecondDescription.text=menu.second
            }
            if(!menu.drink.isNullOrEmpty()){
                linearLayoutDrink.visibility=View.VISIBLE
                textViewDrinkDescription.text=menu.drink
            }
            if(!menu.aditional.isNullOrEmpty()){
                linearLayoutAditional.visibility=View.VISIBLE
                textViewAditionalDescription.text=menu.aditional
            }

            textViewDateAttention.text="${menu.name_day_of_week}, ${menu.day_of_month} de ${menu.name_month.toLowerCase()} de ${menu.year}"
            textViewStartReservation.text=menu.reser_date_start_string
            textViewEndReservation.text=menu.reser_date_end_string

            if(menu.state_menu_reservation== Menu.MENU_ACTIVE){
                textViewStateReservation.text="Activo"
            }else if(menu.state_menu_reservation== Menu.MENU_INACTIVE){
                textViewStateReservation.text="Inactivo"
            }else if(menu.state_menu_reservation== Menu.MENU_OUTSIDE_TIME){
                textViewStateReservation.text="Fuera de tiempo de reservación"
            }


        }
    }
    //activity_send_suggestion val rootView = window.decorView.rootView    findViewById(android.R.id.content)
    private fun errorMenu(e: Exception?){
        if(e is VolleyError){
            VolleySingleton.showErrorVolleySnack(
                findViewById(android.R.id.content)

                ,this, e)
            resultMenu(getSaveDetailsMenu())

        }else{

        }

    }


    private fun beforeReservation(){

    }
    private fun afterReservation(){

        //onBackPressed()
    }
    private fun loadDataReservation(objJson: JSONObject?){
        resultReservation(objJson)
        setSaveDetailsReservation(objJson)
    }
    private fun resultReservation(objJson: JSONObject?){
        if (objJson == null)
            return
        if (objJson.getInt("state") == 0) {
            Toast.makeText(this, objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }
       // Toast.makeText(this,objJson.toString(), Toast.LENGTH_LONG).show()
        val objJsonArray: JSONArray =objJson.getJSONArray("data")

        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject = objJsonArray.getJSONObject(i);


            if(objJsonData.isNull("id_timetable")){
                textViewHoraryOfReser.text="Cancelado"
            }else {
                textViewHoraryOfReser.text=objJsonData.getString("horary_of_reser")
            }

            if(!objJsonData.isNull("assist_time")){
                textViewTimeAssist.text=SimpleDateFormat("hh:mm aaa",  Locale( "es" , "PE")).format(
                    SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                        .parse(objJsonData.getString("assist_time"))
                )
                textViewTimeAssist.visibility=View.VISIBLE
            }

            var assist:Boolean?=if(objJsonData.isNull("assist")) null else objJsonData.getBoolean("assist")
            if(assist==true){
                textViewIconAssist.setBackgroundResource(R.drawable.icon_assist)
                textViewIconAssist.text="A"
                liniarLayoutScoreComment.visibility=View.VISIBLE

            }else if(assist==false){
                textViewIconAssist.setBackgroundResource(R.drawable.icon_missing)
                textViewIconAssist.text="F"
            }else if(!objJsonData.isNull("id_timetable")){
                textViewIconAssist.setBackgroundResource(R.drawable.ic_checked)
                textViewIconAssist.text=""
            }else{
                textViewIconAssist.setBackgroundResource(R.drawable.ic_unchecked)
                textViewIconAssist.text=""
            }


            score=objJsonData.optInt("score",0)
            comment=if(objJsonData.isNull("comment")) null else objJsonData.getString("comment")

            showComment()
            hideComment()

            buttonAddComment.setOnClickListener{
                LinearLayoutAddComment.visibility=View.GONE
                LinearLayouSeetComment.visibility=View.GONE
                LinearLayoutCommentReserMenuToday.visibility=View.VISIBLE
            }

            buttonUpdComment.setOnClickListener{
                LinearLayoutAddComment.visibility=View.GONE
                LinearLayouSeetComment.visibility=View.GONE
                LinearLayoutCommentReserMenuToday.visibility=View.VISIBLE
            }



            ratingBarScore.rating=score.toFloat()

            ratingBarScore.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->


                    var volleySetScore = VolleySingleton(this)// VolleySingleton.getInstance(this)
                    volleySetScore.prepare(
                        this,
                        Server.RESERVATION_SET_SCORE,
                        result = { resultSetScore(it) },
                        error = { errorScore(it) },
                        before = { beforeSetScore() },
                        after = { afterSetScore() })
                    volleySetScore.setUser()

                    volleySetScore.post(
                        Pair("score", rating.toInt()),
                        Pair("id_menu", dayMenu.id)
                    )

                }


            buttonCancelComment.setOnClickListener{
                editTextCommentReserMenuToday.setText(if(comment.isNullOrEmpty())"" else comment)
                hideComment()
                showComment()
            }
            buttonSendComment.setOnClickListener{
                if(checkEmptyFiedlsComment()){
                    var volleySetComment = VolleySingleton(this)// VolleySingleton.getInstance(this)
                    volleySetComment.prepare(this, Server.RESERVATION_SET_COMMENT, result={resultSetComment(it)}, error = {errorComment(it)}, before = {beforeSetComment()}, after={afterSetComment()} )
                    volleySetComment.setUser()
                    volleySetComment.post(
                        Pair("comment",editTextCommentReserMenuToday.text),
                        Pair("id_menu",dayMenu.id)
                    )
                }
                //hideComment()
            }


            var just_state:String? = if(objJsonData.isNull("just_state")) null else objJsonData.getString("just_state")
            when (just_state) {
                JustificationMenuItem.JUSTIFICATION_PENDING -> {
                    textViewStateJustify.text = "JUSTIFICACIÓN PENDIENTE"
                    textViewIconJustify.setBackgroundResource(R.drawable.icon_pending)
                    textViewIconJustify.text = JustificationMenuItem.JUSTIFICATION_PENDING
                    textViewIconJustify.setTextColor(Color.BLACK)
                }
                JustificationMenuItem.JUSTIFICATION_SENT->{
                    textViewStateJustify.text="JUSTIFICACIÓN ENVIADA"
                    textViewIconJustify.setBackgroundResource(R.drawable.icon_sent)
                    textViewIconJustify.text= JustificationMenuItem.JUSTIFICATION_SENT
                    textViewIconJustify.setTextColor(Color.BLACK)
                }
                JustificationMenuItem.JUSTIFICATION_DENIED -> {
                    textViewStateJustify.text = "JUSTIFICACIÓN DENEGADA"
                    textViewIconJustify.setBackgroundResource(R.drawable.icon_denied)
                    textViewIconJustify.text = JustificationMenuItem.JUSTIFICATION_DENIED
                    textViewIconJustify.setTextColor(Color.WHITE)
                }
                JustificationMenuItem.JUSTIFICATION_ACCEPTED -> {
                    textViewStateJustify.text = "JUSTIFICACIÓN ACEPTADA"
                    textViewIconJustify.setBackgroundResource(R.drawable.icon_accepted)
                    textViewIconJustify.text = JustificationMenuItem.JUSTIFICATION_ACCEPTED
                    textViewIconJustify.setTextColor(Color.WHITE)
                }
                JustificationMenuItem.JUSTIFICATION_OBSERVED -> {
                    textViewStateJustify.text = "JUSTIFICACIÓN OBSERVADA"
                    textViewIconJustify.setBackgroundResource(R.drawable.icon_observed)
                    textViewIconJustify.text = JustificationMenuItem.JUSTIFICATION_OBSERVED
                    textViewIconJustify.setTextColor(Color.WHITE)
                }
                else -> {
                    textViewStateJustify.text = "NO REQUIERE JUSTIFICACIÓN"
                    textViewIconJustify.setBackgroundResource(R.drawable.icon_not_require)
                    textViewIconJustify.text = JustificationMenuItem.NO_REQUIRE_JUSTIFICATION
                    textViewIconJustify.setTextColor(Color.WHITE)

                }

            }


            /*
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfDateTime=SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val skd_date = sdf.parse(objJsonData.getString("skd_date"))
    val reser_date_start=sdfDateTime.parse(objJsonData.getString("reser_date_start"))
    val reser_date_end=sdfDateTime.parse(objJsonData.getString("reser_date_end"))
    val assist_time: Date?=if(objJsonData.isNull("assist_time")) null else sdfDateTime.parse(objJsonData.getString("assist_time"))
    val reservation_time:Date?=if(objJsonData.isNull("reservation_time")) null else sdfDateTime.parse(objJsonData.getString("reservation_time"))
    */
        }
    }
    //activity_send_suggestion val rootView = window.decorView.rootView    findViewById(android.R.id.content)
    private fun errorReservation(e: Exception?){
        if(e is VolleyError){
            VolleySingleton.showErrorVolleySnack(
                findViewById(android.R.id.content)
                ,this, e)
            resultReservation(getSaveDetailsReservation())

        }else{

        }

    }


    private fun beforeSetScore(){


    }
    private fun afterSetScore(){

    }
    private fun resultSetScore(objJson: JSONObject?){
        if (objJson == null)
            return
        Toast.makeText(this, objJson.getString("message"), Toast.LENGTH_LONG).show()
        if (objJson.getInt("state") == 0) {
            return;
        }
        score=ratingBarScore.rating.toInt()

    }

    private fun errorScore(e: Exception?){
        ratingBarScore.rating=score.toFloat()

        if(e is VolleyError){
            VolleySingleton.showErrorVolleyToast(this,e)

            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
            //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()

        }else{

        }

    }


    //guardar comen tario
    private fun beforeSetComment(){
        //
    }
    private fun afterSetComment(){

    }
    private fun resultSetComment(objJson: JSONObject?){
        if (objJson == null)
            return
        Toast.makeText(this, objJson.getString("message"), Toast.LENGTH_LONG).show()
        if (objJson.getInt("state") == 0) {
            return;
        }
        comment= editTextCommentReserMenuToday.text.toString()
        //LinearLayoutCommentReserMenuToday.visibility=View.GONE
        showComment()
        hideComment()

    }

    private fun errorComment(e: Exception?){
        if(e is VolleyError){
            VolleySingleton.showErrorVolleyToast(this,e)

            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
            //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()

        }else{

        }

    }

    private fun checkEmptyFiedlsComment(): Boolean {
        if (editTextCommentReserMenuToday.text.isNullOrEmpty()) {

            Toast.makeText(this, "Ingresé comentario", Toast.LENGTH_LONG).show()
            editTextCommentReserMenuToday.requestFocus()
            return false
        }


        return true
    }


    override fun  onSupportNavigateUp() :Boolean {
        onBackPressed()
        return false
    }

    override  fun onBackPressed(){
        super.onBackPressed()
        finish()
    }


    private fun hideComment(){

        if(comment.isNullOrEmpty()){
            LinearLayoutAddComment.visibility=View.VISIBLE
            LinearLayouSeetComment.visibility=View.GONE
            LinearLayoutCommentReserMenuToday.visibility=View.GONE
        }else{
            LinearLayoutAddComment.visibility=View.GONE
            LinearLayouSeetComment.visibility=View.VISIBLE
            LinearLayoutCommentReserMenuToday.visibility=View.GONE
        }

    }

    private fun showComment(){
        if(!comment.isNullOrEmpty()){
            justifiedTextViewDescriptionLastNews2.text=comment
            editTextCommentReserMenuToday.setText(comment)
            editTextCommentReserMenuToday.setSelection(editTextCommentReserMenuToday.text.length)
        }

    }



    //
    private fun getSaveDetailsMenu(): JSONObject?{
        var objJson : JSONObject?=null

        try{
            var listMneu:String?=this.readStringFromStorage(
                FILE_DETAILS_MENU(
                    dayMenu.id
                )
            )

            if(listMneu!=null && !listMneu.trim().equals("")){
                objJson = JSONObject(listMneu)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }

    private fun setSaveDetailsMenu(objJson :  JSONObject?){

        var objJsonString=if(objJson==null) "" else objJson.toString()
        this.saveToStorage(
            FILE_DETAILS_MENU(
                dayMenu.id
            ), objJsonString)

    }
    //

    private fun getSaveDetailsReservation(): JSONObject?{
        var objJson : JSONObject?=null

        try{
            var listMneu:String?=this.readStringFromStorage(
                FILE_DETAILS_RESERVATION(
                    dayMenu.id
                )
            )

            if(listMneu!=null && !listMneu.trim().equals("")){
                objJson = JSONObject(listMneu)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }

    private fun setSaveDetailsReservation(objJson :  JSONObject?){

        var objJsonString=if(objJson==null) "" else objJson.toString()
        this.saveToStorage(
            FILE_DETAILS_RESERVATION(
                dayMenu.id
            ), objJsonString)

    }


    companion object {
        fun FILE_DETAILS_MENU(idMenu: Int):String{
            return "Detalle_Menu_${idMenu}"
        }

        fun FILE_DETAILS_RESERVATION(idMenu: Int):String{
            return "Detalle_Reser_${idMenu}"
        }

    }
}
