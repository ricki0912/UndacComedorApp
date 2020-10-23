package com.undac.undaccomedor.controller.login

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.undac.undaccomedor.R
import org.json.JSONObject

import android.view.View
import com.android.volley.*

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.volley.VolleySingleton

import com.undac.undaccomedor.volley.VolleySingleton.Companion.showErrorVolleyToast
import com.undac.undaccomedor.server.Server
import com.google.firebase.messaging.FirebaseMessaging
import com.undac.undaccomedor.session.SessionManager
import com.undaccomedor.helpers.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray

public class CLoginActivity : AppCompatActivity() {

    private lateinit  var pd:ProgressDialog

    private lateinit var session:SessionManager
    private lateinit var mRootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootView = layoutInflater.inflate(R.layout.activity_login, null)
        setContentView(mRootView)

        //suscribTopic()
        session= SessionManager(applicationContext)

        if(session.isLoggedIn()){
            startActivity(Intent(this, CMainActivity::class.java))
        }



        pd=ProgressDialog(this@CLoginActivity)

        buttonlogin.setOnClickListener{
            if(checkEmptyFiedls()){
                var volley = VolleySingleton.getInstance(this)
                volley.prepare(this, Server.VALIDATE_LOGIN, result={execLogin(it)}, error = {error(it)}, before = {before()}, after={after()} )
                volley.post(
                    Pair("user",editTextUser.text.toString()),
                    Pair("password", editTextPasword.text.toString())
                )
            }
        }

        if(!applicationContext.isOnline()){
            mRootView.snack(applicationContext, R.string.no_connection, SnackType.ERROR)
        }

    }

    private fun checkEmptyFiedls():Boolean{
        if(editTextUser.text.isNullOrEmpty()){
            editTextUser.requestFocus()
            Toast.makeText(applicationContext, "Ingrese su código de alumno.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(editTextPasword.text.isNullOrEmpty()){
            Toast.makeText(applicationContext, "Ingrese su password.", Toast.LENGTH_SHORT).show()
            editTextPasword.requestFocus()
            return false
        }
        return true
    }

    private fun execLogin(objJson: JSONObject?){
        if(objJson==null)
            return

        if(objJson.getInt("state")==1){
            val objJsonArray: JSONArray =objJson.getJSONArray("data")
            val objJsonData: JSONObject=objJsonArray.getJSONObject(0);
            session.createLogginSession(
                key_uid=objJsonData.getString("code"),
                key_eid =  objJsonData.getString("eid"),
                key_oid = objJsonData.getString("oid"),
                key_escid = objJsonData.getString("escid"),
                key_subid = objJsonData.getString("subid"),
                key_pid=objJsonData.getString("pid"),

                key_first_name = objJsonData.getString("first_name"),
                key_last_name_0 = objJsonData.getString("last_name0"),
                key_last_name_1 = objJsonData.getString("last_name1"),
                key_mail_person = objJsonData.getString("mail_person"),
                key_token = objJsonData.getString("token")
            )
            saveNewInstanceIDToken()
            startActivity(Intent(this, CMainActivity::class.java))
        }else{
            Toast.makeText(applicationContext, objJson.getString("message"), Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        //const val TOPIC_ALL="all_prueba"
    }

    /*private fun suscribTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(CLoginActivity.TOPIC_ALL)
            .addOnCompleteListener { task ->
                var msg = "Correcto"
                if (!task.isSuccessful) {
                    msg = "No conecto"
                }
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }*/

    private fun saveNewInstanceIDToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("UNDAC Comedor", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token


                //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

                if(token!=null){
                    var volley = VolleySingleton(baseContext)// VolleySingleton.getInstance(this)
                    volley.prepare(baseContext, Server.LOGIN_SAVE_NEW_INSTANCE_ID_TOKEN_, result={fireabase(it)}, error = {error(it)}, before = {before()}, after={after()} )
                    volley.setUser()
                    volley.post(Pair("token", token))
                }

            })
    }

    private fun before(){
        pd.setMessage("Validando credenciales, espere por favor.")
        pd.show()
        buttonlogin.isEnabled=false
    }

    private fun after(){
        buttonlogin.isEnabled=true
        pd.dismiss()
    }

    private fun error(e: Exception?){
        if(e is VolleyError){
            showErrorVolleyToast(this, e)
        }else{

        }

    }

    private fun fireabase(objJson: JSONObject?){

    }

}

