package com.undac.undaccomedor.controller.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.undac.undaccomedor.BuildConfig
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.session.SessionManager
import com.undac.undaccomedor.volley.VolleySingleton
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import org.json.JSONObject

class CChangePasswordDialog : DialogFragment() {
    private val version: String = BuildConfig.VERSION_NAME
    private lateinit var mRootView: View
    private lateinit var mApplicationContext: Context
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        mRootView = View.inflate(context, R.layout.dialog_change_password, null)
        mApplicationContext=(activity as CMainActivity).applicationContext
        //v.versionName.text = version
        mRootView.buttonOK.setOnClickListener({
            changePassword()
        })

        mRootView.buttonCancel.setOnClickListener({
            dismiss()
        })
        builder.setView(mRootView)
        return builder.create()
    }


    private fun changePassword(){
        if(checkEmptyFiedls()){
            sendData(Server.CHANGE_PASSWORD,mApplicationContext, getParams() )
        }
    }



    fun sendData(URL: String, context: Context, params: MutableMap<String, String>) {
        var objJson: JSONObject? = null

        val stringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener<String> { response ->
                try {
                    objJson = JSONObject(response)

                    //inicio de acciones
                    execChangePassword(objJson);
                    //find dea acciones
                } catch (e: Exception) {
                    mRootView.textViewInformation.visibility=View.VISIBLE
                    mRootView.textViewInformation.text="Surgio un error."
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(p0: VolleyError?) {
                    VolleySingleton.showErrorVolleyToast(context, p0)
                    //VolleySingleton.showErrorVolleySnack(mRootView,mApplicationContext, p0 )
                    mRootView.textViewInformation.visibility=View.VISIBLE
                    mRootView.textViewInformation.text="No se pudo concretar la operación."
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }


    fun getParams(): MutableMap<String, String> {

        val params = HashMap<String, String>()

        var session= SessionManager(mApplicationContext)
        var user: java.util.HashMap<String, String> =session.getUserDetails()
        /*
        params.put("uid", Session.UID!!)
        params.put("eid", Session.EID!!)
        params.put("oid", Session.OID!!)
        params.put("escid", Session.ESCID!!)
        params.put("subid", Session.SUBID!!)
        params.put("pid", Session.PID!!)
        */
        params.putAll(user)
        params.put("password_old", mRootView.editTextPasswordOld.text.toString())
        params.put("password_new", mRootView.editTextPasswordNew.text.toString())
        params.put("repit_password_new", mRootView.editTextRepitPasswordNew.text.toString())



        return params
    }

    private fun checkEmptyFiedls(): Boolean {
        if (mRootView.editTextPasswordOld.text.isNullOrEmpty()) {

            mRootView.textViewInformation.visibility=View.VISIBLE
            mRootView.textViewInformation.text = "Ingrese contraseña actual."
            mRootView.editTextPasswordOld.requestFocus()
            return false
        }

        if (mRootView.editTextPasswordNew.text.isNullOrEmpty()) {
            mRootView.textViewInformation.visibility=View.VISIBLE
            mRootView.textViewInformation.text = "Ingrese nueva contraseña."
            mRootView.editTextPasswordNew.requestFocus()
            return false
        }

        if (mRootView.editTextRepitPasswordNew.text.isNullOrEmpty()) {
            mRootView.textViewInformation.visibility=View.VISIBLE
            mRootView.textViewInformation.text = "Repita nueva contraseña."
            mRootView.editTextRepitPasswordNew.requestFocus()
            return false
        }

        return true
    }


    private fun execChangePassword(objJson: JSONObject?) {
        if (objJson == null)
            return

        if (objJson.getInt("state") == 1) {
           // mRootView.textViewInformation.visibility=View.VISIBLE
            //mRootView.textViewInformation.text=objJson.getString("message")
            Toast.makeText(mApplicationContext, objJson.getString("message"), Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            mRootView.textViewInformation.visibility=View.VISIBLE
            mRootView.textViewInformation.text=objJson.getString("message")

        }

    }
}