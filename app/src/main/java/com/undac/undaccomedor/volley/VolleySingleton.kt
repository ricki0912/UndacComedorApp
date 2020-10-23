package com.undac.undaccomedor.volley
import android.content.Context
import android.view.View
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.undac.undaccomedor.R
import com.undac.undaccomedor.session.SessionManager
import com.undaccomedor.helpers.SnackType
import com.undaccomedor.helpers.snack
import org.json.JSONObject

class VolleySingleton constructor(var context: Context) {

    private lateinit var url: String
    private lateinit var result : (jsonObject: JSONObject?) -> Unit
    private lateinit var error : (e: Exception?) -> Unit
    private lateinit var before : ()->Unit
    private lateinit var after : ()->Unit
    private lateinit var hashMap: HashMap<String,String>
    private lateinit var session: SessionManager

    fun prepare (context: Context, url: String,
                 result: (JSONObject?) -> Unit={},
                 error: (Exception?) -> Unit={},
                 before :()->Unit={},
                 after : ()->Unit={}
                  ) {
        this.hashMap=HashMap()
        this.before=before
        this.after=after
        this.context=context
        this.url=url
        this.result=result
        this.error=error
    }

    fun setUser(){
        session= SessionManager(context.applicationContext)
        var user:HashMap<String, String> =session.getUserDetails()
        hashMap.putAll(user)
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }


    fun post(vararg params: Pair<String, Any>) {
       // val hashMap = HashMap<String, String>()
        params.forEach {
            hashMap[it.first] = it.second.toString()
        }

        before()
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    result(JSONObject(response))

                }catch(e: Exception){
                    Toast.makeText(context, e.message+"<--->"+response, Toast.LENGTH_LONG).show()
                    error(e)
                }
                after()
            }, object : Response.ErrorListener {
                override fun onErrorResponse(p0: VolleyError?) {
                    error(p0)
                    after()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMap
            }
        }

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)

        //addToRequestQueue(stringRequest)
    }

    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: VolleySingleton(context).also {
                    INSTANCE = it
                }
            }

        fun showErrorVolleyToast(context: Context, p0: VolleyError?){
            if(p0 is TimeoutError || p0 is NoConnectionError){
                Toast.makeText(context, R.string.TIMEOUT_ERROR, Toast.LENGTH_LONG).show()
            }else if(p0 is NoConnectionError){
                Toast.makeText(context, R.string.NO_CONNECTION_ERROR, Toast.LENGTH_LONG).show()
            }else if(p0 is AuthFailureError){
                Toast.makeText(context, R.string.AUTH_FAILURE_ERROR, Toast.LENGTH_LONG).show()
            }else if(p0 is ServerError){
                Toast.makeText(context, R.string.SERVER_ERROR, Toast.LENGTH_LONG).show()
            }else if(p0 is NetworkError){
                Toast.makeText(context, R.string.NETWORK_ERROR, Toast.LENGTH_LONG).show()
            }else if(p0 is ParseError){
                Toast.makeText(context, R.string.PARSE_ERROR, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, R.string.UNKNOWN_ERROR, Toast.LENGTH_LONG).show()
            }
        }

        fun showErrorVolleySnack(mRootView: View, context: Context, p0: VolleyError?){
            if(p0 is TimeoutError){
                mRootView.snack(context, R.string.TIMEOUT_ERROR, SnackType.ERROR)
            }else if(p0 is NoConnectionError){
                mRootView.snack(context, R.string.NO_CONNECTION_ERROR, SnackType.ERROR)
            }else if(p0 is AuthFailureError){
                mRootView.snack(context, R.string.AUTH_FAILURE_ERROR, SnackType.ERROR)
            }else if(p0 is ServerError){
                mRootView.snack(context, R.string.SERVER_ERROR, SnackType.ERROR)
            }else if(p0 is NetworkError){
                mRootView.snack(context, R.string.NETWORK_ERROR, SnackType.ERROR)
            }else if(p0 is ParseError){
                mRootView.snack(context, R.string.PARSE_ERROR, SnackType.ERROR)
            }else{
                mRootView.snack(context, R.string.UNKNOWN_ERROR, SnackType.ERROR)
            }
        }

        fun showErrorVolleyDialog(dialog:(state:Int, message:String) -> Unit, context: Context,p0: VolleyError?){
            var message:String=""
            try {
                if(p0 is TimeoutError){
                    message=context.resources.getString(R.string.TIMEOUT_ERROR)
                    dialog(0,  message)
                }else if(p0 is NoConnectionError){
                    message=context.resources.getString(R.string.NO_CONNECTION_ERROR)
                    dialog(0,  message)
                }else if(p0 is AuthFailureError){
                    message=context.resources.getString(R.string.AUTH_FAILURE_ERROR)
                    dialog(0,  message)
                }else if(p0 is ServerError){
                    message=context.resources.getString(R.string.SERVER_ERROR)
                    dialog(0,  message)
                }else if(p0 is NetworkError){
                    message=context.resources.getString(R.string.NETWORK_ERROR)
                    dialog(0,  message)
                }else if(p0 is ParseError){
                    message=context.resources.getString(R.string.PARSE_ERROR)
                    dialog(0,  message)
                }else{
                    message=context.resources.getString(R.string.UNKNOWN_ERROR)
                    dialog(0,  message)
                }
            }catch (e:Exception){
                dialog(0, e.message!!)
            }

        }
    }





}