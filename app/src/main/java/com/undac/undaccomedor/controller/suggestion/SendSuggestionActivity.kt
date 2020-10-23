package com.undac.undaccomedor.controller.suggestion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.*
import com.undac.undaccomedor.R
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.SnackType
import com.undaccomedor.helpers.snack

import kotlinx.android.synthetic.main.activity_send_suggestion.*
import org.json.JSONObject

class SendSuggestionActivity : AppCompatActivity() {
    private var all_suggestion:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_suggestion)
        initView()
    }


    private fun initView(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Sugerencia o Reclamo")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        buttonSendSuggestion.setOnClickListener({
                if(!isEmptyFields())
                {
                    var volley = VolleySingleton.getInstance(this)
                    volley.prepare(this, Server.SUGERENCIA_CREATE, result={result(it)}, error = {error(it)}, before = {before()}, after={after()} )

                    volley.setUser()
                    volley.post(

                        Pair("details", editTextSuggestion.text)
                    )
                }


        })
    }

    private fun before(){
        buttonSendSuggestion.isEnabled=false
        buttonSendSuggestion.text="ENVIANDO SUGERENCIA"
        progressbar.visibility= View.VISIBLE
    }
    private fun after(){
        buttonSendSuggestion.text="ENVIAR SUGERENCIA"
        buttonSendSuggestion.isEnabled=true
        progressbar.visibility= View.GONE
        //onBackPressed()
    }
    private fun result(objJson: JSONObject?){
        if (objJson == null)
            return
        if (objJson.getInt("state") == 1) {
            editTextSuggestion.setText("")
            all_suggestion++
            if(all_suggestion>1){
                textViewAllSuggestion.setText("${all_suggestion} nueva sugerencia.")
            }else {
                textViewAllSuggestion.setText("${all_suggestion} nuevas sugerencias.")
            }
            //textViewAllSuggestion.visibility=View.VISIBLE

            //Toast.makeText(this, objJson.getString("message"), Toast.LENGTH_LONG).show()
            (findViewById(android.R.id.content) as View).snack(this, objJson.getString("message") , SnackType.FINISH)
            return;
        }
        //findViewById(android.R.id.content).snack()
    }
    //activity_send_suggestion val rootView = window.decorView.rootView    findViewById(android.R.id.content)
    private fun error(e: Exception?){
        if(e is VolleyError){
            VolleySingleton.showErrorVolleySnack(
                findViewById(android.R.id.content)
                ,this, e)
        }else{

        }

    }

    private fun isEmptyFields(): Boolean {
        if(editTextSuggestion.text.isNullOrEmpty()){
            Toast.makeText(this, "Sugerencia vacío.", Toast.LENGTH_LONG).show()
            return true
        }

        return false;
    }

    override fun  onSupportNavigateUp() :Boolean {
        onBackPressed()
        return false
    }

    override  fun onBackPressed(){
        super.onBackPressed()
        finish()
    }
}
