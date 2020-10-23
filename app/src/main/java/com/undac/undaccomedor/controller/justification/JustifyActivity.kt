package com.undac.undaccomedor.controller.justification

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.undac.undaccomedor.R

import com.undac.undaccomedor.item.Menu
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.volley.VolleySingleton


import kotlinx.android.synthetic.main.activity_justify.*
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.*

class JustifyActivity : AppCompatActivity() {
    private lateinit var mRootView: View
    private lateinit var dayMenu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_justify)
        initView()
    }


    private fun initView(){
        dayMenu=intent.getSerializableExtra(Menu.NAME_SERIALIZABLE) as Menu
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("${dayMenu.type_string} de ${SimpleDateFormat("dd-MM-yyyy", Locale( "es" , "PE")).format(dayMenu.skd_date)}")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        loadData()
    }

    private fun loadData(){
        var volley = VolleySingleton(this)// VolleySingleton.getInstance(this)
        volley.prepare(this, Server.JUSTFICATION_GET_STATE_MESSAGE, result={result(it)}, error = {error(it)} )
        volley.post()

    }

    private fun result(objJson: JSONObject?){
        if (objJson == null)
            return

        textViewMessage.text=objJson.getString("message")

    }

    private fun error(e: Exception?){
        if(e is VolleyError){
            VolleySingleton.showErrorVolleyToast(this,e)
            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
            //Toast.makeText(mApplicationContext, objJson.toString(), Toast.LENGTH_LONG).show()

        }else{

        }
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
