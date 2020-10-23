package com.undac.undaccomedor.view_holder

import android.content.Context

import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.item.MenuReservationTodayItem
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.volley.VolleySingleton
import kotlinx.android.synthetic.main.li_item_reservation_menu_today.view.*
import org.json.JSONObject
import android.widget.RatingBar.OnRatingBarChangeListener

internal class MenuReservationTodayViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<MenuReservationTodayItem>(itemView) {


    var expandView:(v: View?)-> Unit ={}
    private lateinit var  item: MenuReservationTodayItem

    override fun bind(item: MenuReservationTodayItem) {
        this.item=item
        initView()
    }

    private fun initView(){
        itemView.textViewTitleReserMenuToday.setText(
            if(item.type== Menu.TYPE_DESAYUNO) "Desayuno" else if(item.type== Menu.TYPE_ALMUERZO)  "Almuerzo" else if(item.type== Menu.TYPE_CENA) "Cena" else "Menú"
        )
        itemView.textViewHoraryReserMenuToday.text=if(item.id_timetable!=null) item.horary_of_reser else if(item.reservation_time!=null) "Cancelado" else "Desliza y haz tu reserva"

        if(item.assist==true){
            itemView.ratingBarScoreReserMenuToday.visibility=View.VISIBLE
        }else{
            itemView.ratingBarScoreReserMenuToday.visibility=View.INVISIBLE
        }

        if(item.assist==true){
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.icon_accepted)
            itemView.textViewIconAssist.text="A"
            itemView.textViewDateReserPendingMenu.text=item.assist_time_string
        }else if(item.assist==false){
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.icon_missing)
            itemView.textViewIconAssist.text="F"
            itemView.textViewDateReserPendingMenu.text="No asistió"
        }else if(item.id_timetable!=null){
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.ic_checked)
            itemView.textViewIconAssist.text=""
            itemView.textViewDateReserPendingMenu.text="Pendiente"
        }else{
            itemView.textViewIconAssist.setBackgroundResource(R.drawable.ic_unchecked)
            itemView.textViewIconAssist.text=""
            itemView.textViewDateReserPendingMenu.text="Cancelado"
        }

        itemView.ratingBarScoreReserMenuToday.rating=item.score.toFloat()

        itemView.ratingBarScoreReserMenuToday.onRatingBarChangeListener =
            OnRatingBarChangeListener {
                    ratingBar, rating, fromUser ->
                //Toast.makeText(mApplicationContext, rating.toInt().toString()+"---"+rating.toString(),Toast.LENGTH_SHORT).show()

                expandView(ratingBar)
                var volleySetScore = VolleySingleton(mApplicationContext)// VolleySingleton.getInstance(this)
                volleySetScore.prepare(
                    mApplicationContext,
                    Server.RESERVATION_SET_SCORE,
                    result = { resultSetScore(it) },
                    error = { errorScore(it) },
                    before = { beforeSetScore() },
                    after = { afterSetScore() })
                volleySetScore.setUser()

                volleySetScore.post(
                    Pair("score", rating.toInt()),
                    Pair("id_menu", item.id)
                )
                itemView.editTextCommentReserMenuToday.setSelection(itemView.editTextCommentReserMenuToday.text.length)
            };

        itemView.editTextCommentReserMenuToday.setText(if(item.comment.isNullOrEmpty())"" else item.comment)
        itemView.buttonCancelReserMenuToday.setOnClickListener{
            itemView.editTextCommentReserMenuToday.setText(if(item.comment.isNullOrEmpty())"" else item.comment)
            itemView.LinearLayoutCommentReserMenuToday.visibility=View.GONE
            itemView.setBackgroundResource(0)

        }
        itemView.buttonOKReserMenuToday.setOnClickListener{

            if(checkEmptyFiedlsComment()){
                var volleySetComment = VolleySingleton(mApplicationContext)// VolleySingleton.getInstance(this)
                volleySetComment.prepare(mApplicationContext, Server.RESERVATION_SET_COMMENT, result={resultSetComment(it)}, error = {errorComment(it)}, before = {beforeSetComment()}, after={afterSetComment()} )
                volleySetComment.setUser()
                volleySetComment.post(
                    Pair("comment",itemView.editTextCommentReserMenuToday.text),
                    Pair("id_menu",item.id)
                )
            }


        }

    }


    private fun beforeSetScore(){
        itemView.LinearLayoutCommentReserMenuToday.visibility = View.VISIBLE
        itemView.setBackgroundResource(R.color.celest_eclaro)

    }
    private fun afterSetScore(){

    }
    private fun resultSetScore(objJson: JSONObject?){
        if (objJson == null)
            return
        Toast.makeText(mApplicationContext, objJson.getString("message"), Toast.LENGTH_LONG).show()
        if (objJson.getInt("state") == 0) {
            return;
        }
        item.score=itemView.ratingBarScoreReserMenuToday.rating.toInt()

    }

    private fun errorScore(e: Exception?){
        itemView.ratingBarScoreReserMenuToday.rating=item.score.toFloat()

        if(e is VolleyError){
            VolleySingleton.showErrorVolleyToast(mApplicationContext,e)
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
        Toast.makeText(mApplicationContext, objJson.getString("message"), Toast.LENGTH_LONG).show()
        if (objJson.getInt("state") == 0) {
            return;
        }
        itemView.LinearLayoutCommentReserMenuToday.visibility=View.GONE
        itemView.setBackgroundResource(0)

    }

    private fun errorComment(e: Exception?){
        if(e is VolleyError){
            VolleySingleton.showErrorVolleyToast(mApplicationContext,e)
            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
            //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()

        }else{

        }

    }

    private fun checkEmptyFiedlsComment(): Boolean {
        if (itemView.editTextCommentReserMenuToday.text.isNullOrEmpty()) {

           Toast.makeText(mApplicationContext, "Ingresé comentario", Toast.LENGTH_LONG).show()
            itemView.editTextCommentReserMenuToday.requestFocus()
            return false
        }


        return true
    }
}