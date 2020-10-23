package com.undac.undaccomedor.session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.app.ActivityCompat
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.controller.login.CLoginActivity

class SessionManager {

    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mApplicationContext : Context
    var PRIVATE_MODE: Int=0

    constructor(mApplicationContext: Context){
        this.mApplicationContext=mApplicationContext
        pref=mApplicationContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor=pref.edit()
    }

    companion object {
        val PREF_NAME:String="UNDAC Comedor"
        val IS_LOGIN:String="isLoggedIn"

        var KEY_UID: String="uid"
        var KEY_EID:String="eid"
        var KEY_OID:String="oid"
        var KEY_ESCID:String="escid"
        var KEY_SUBID:String="subid"
        var KEY_PID:String="pid"

        var KEY_FIRST_NAME: String="first_name"
        var KEY_LAST_NAME_0: String="last_name0"
        var KEY_LAST_NAME_1:String="last_name1"
        var KEY_MAIL_PERSON: String="mail_person"

        var KEY_TOKEN:String="token"

    }



    fun createLogginSession(
                            key_uid: String,
                            key_eid:String,
                            key_oid:String,
                            key_escid:String,
                            key_subid:String,
                            key_pid: String,

                            key_first_name:String,
                            key_last_name_0:String,
                            key_last_name_1:String,
                            key_mail_person: String,

                            key_token:String
                        ){
        editor.putBoolean(IS_LOGIN,true)

        editor.putString(KEY_UID, key_uid)
        editor.putString(KEY_EID,key_eid )
        editor.putString(KEY_OID, key_oid)
        editor.putString(KEY_ESCID , key_escid)
        editor.putString(KEY_SUBID,key_subid)
        editor.putString(KEY_PID , key_pid)
        editor.putString(KEY_FIRST_NAME,key_first_name )
        editor.putString(KEY_LAST_NAME_0 ,key_last_name_0 )
        editor.putString(KEY_LAST_NAME_1 , key_last_name_1)
        editor.putString(KEY_MAIL_PERSON,key_mail_person )
        editor.putString(KEY_TOKEN, key_token)

        editor.commit()
    }
    fun checkLogin(){
        if(!this.isLoggedIn()){
            var i: Intent = Intent(mApplicationContext, CLoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //mApplicationContext.finish()
            mApplicationContext.startActivity(i)
        }
    }

    fun getUserDetails():HashMap<String, String>{
        var user: Map<String, String> =HashMap<String, String>()
        (user as HashMap).put(KEY_UID, pref.getString(KEY_UID, null))
        user.put(KEY_EID, pref.getString(KEY_EID, null))
        user.put(KEY_OID, pref.getString(KEY_OID, null))
        user.put(KEY_ESCID, pref.getString(KEY_ESCID, null))
        user.put(KEY_SUBID, pref.getString(KEY_SUBID, null))
        user.put(KEY_PID, pref.getString(KEY_PID, null))
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null))
        user.put(KEY_LAST_NAME_0, pref.getString(KEY_LAST_NAME_0, null))
        user.put(KEY_LAST_NAME_1, pref.getString(KEY_LAST_NAME_1, null))
        user.put(KEY_MAIL_PERSON, pref.getString(KEY_MAIL_PERSON, null))
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null))
        return user
    }
    fun getUserDetailsByPar(): Array<Pair<String, Any>> {

        return arrayOf(
            Pair(KEY_UID, pref.getString(KEY_UID, null)),
            Pair(KEY_EID, pref.getString(KEY_EID, null)),
            Pair(KEY_OID, pref.getString(KEY_OID, null)),
            Pair(KEY_ESCID, pref.getString(KEY_ESCID, null)),
            Pair(KEY_SUBID, pref.getString(KEY_SUBID, null)),
            Pair(KEY_PID, pref.getString(KEY_PID, null)),
            Pair(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null)),
            Pair(KEY_LAST_NAME_0, pref.getString(KEY_LAST_NAME_0, null)),
            Pair(KEY_LAST_NAME_1, pref.getString(KEY_LAST_NAME_1, null)),
            Pair(KEY_MAIL_PERSON, pref.getString(KEY_MAIL_PERSON, null)),
            Pair(KEY_TOKEN, pref.getString(KEY_TOKEN, null))
        )
    }


    fun logoutUser(){
        editor.clear()
        editor.commit()
        var i: Intent = Intent(mApplicationContext, CLoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mApplicationContext.startActivity(i)
    }

    fun isLoggedIn():Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }


}