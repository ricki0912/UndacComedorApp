package com.undac.undaccomedor.controller.profile

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.CMainActivity

import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.session.SessionManager
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.SnackType
import com.undaccomedor.helpers.readStringFromStorage
import com.undaccomedor.helpers.saveToStorage
import com.undaccomedor.helpers.snack
import kotlinx.android.synthetic.main.app_bar_main.*

import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class CProfileFragment : Fragment() {
    private lateinit var mRootView: View
    private lateinit var mApplicationContext: Context
    private lateinit var session: SessionManager
    private lateinit var user:HashMap<String, String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //return LayoutInflater.from(container?.context).inflate(R.layout.fragment_profile, container, false)
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false)
        mApplicationContext = requireActivity().applicationContext
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session= SessionManager(mApplicationContext)
        user =session.getUserDetails()
        textViewLastFirstName.text= user.get(SessionManager.KEY_LAST_NAME_0)+" "+ user.get(SessionManager.KEY_LAST_NAME_1)+", "+ user.get(SessionManager.KEY_FIRST_NAME)
        textViewUID.text=user.get(SessionManager.KEY_UID)
        textViewPID.text=user.get(SessionManager.KEY_PID)


        //loadProfile()
        loadAllReservation()
        buttonChangePassword.setOnClickListener({
            CChangePasswordDialog().show((activity as CMainActivity).supportFragmentManager, "ChangePasswordDialog")
        })

    }

    override fun onResume() {
        super.onResume()
        //requireActivity().setTitle(R.string.perfil)
        (activity as CMainActivity).supportActionBar?.title = getString(R.string.perfil)

        var toolbar: Toolbar = (activity as CMainActivity).toolbar
        var params : AppBarLayout.LayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags=0
    }

    /*Metodod para obtener datos de perfil*/

    private fun loadAllReservation(){
        sendDataAllReservation(Server.GET_ALL_RESERVATION, mApplicationContext, getParams())
    }
    private fun sendDataAllReservation(URL: String, context: Context, params: MutableMap<String, String>) {

        var objJson: JSONObject? = null

        val stringRequest = object : StringRequest(
            Request.Method.POST, URL,
            Response.Listener<String> { response ->

                //Toast.makeText(context, response, Toast.LENGTH_SHORT).show()

                try {
                    objJson = JSONObject(response)
                    //inicio de acciones
                    setSaveAllReservation(objJson)
                    execAllReservation(objJson!!);

                    mRootView.snack(context, R.string.update_success, SnackType.FINISH)
                    //find dea acciones
                } catch (e: Exception) {
                    e.printStackTrace()
                    mRootView.snack(context, R.string.update_fail, SnackType.ERROR)

                }

            }, object : Response.ErrorListener {

                override fun onErrorResponse(p0: VolleyError?) {

                    //execMenuWeek1()

                    execAllReservation(getSaveAllReservation());
                    VolleySingleton.showErrorVolleySnack(
                        mRootView,
                        mApplicationContext,
                        p0
                    )
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


        params.putAll(user)

        return params
    }



    private fun execAllReservation(objJson: JSONObject?) {
        if (objJson == null)
            return

        if (objJson.getInt("state") == 0) {
            Toast.makeText(view?.getContext(), objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }

        val objJsonArray: JSONArray =objJson.getJSONArray("data")


        for (i in 0..objJsonArray.length()-1) {
            val objJsonData: JSONObject=objJsonArray.getJSONObject(i);

            textViewProfileAssist.text=objJsonData.getInt("asssit_true").toString()
            textViewProfileMissing.text=objJsonData.getInt("asssit_false").toString()
            textViewProfileCancel.text=objJsonData.getInt("asssit_cancel").toString()
            textViewProfileNotAttended.text=objJsonData.getInt("asssit_not_attended").toString()


        }
    }

    /**/
    private fun getSaveAllReservation():JSONObject?{
        var objJson : JSONObject?=null
        try{
            var valor:String?=mApplicationContext.readStringFromStorage(
                RESERVATION
            )
            if(valor!=null && !valor.trim().equals("")){
                objJson = JSONObject(valor)
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }


    private fun setSaveAllReservation(objJson :  JSONObject?){
        mApplicationContext.saveToStorage(
            RESERVATION,objJson.toString() )
    }

    companion object {
        private val  PROFILE:String="Profile.json"
        private val RESERVATION:String="CantAllReservation.json"
    }


}