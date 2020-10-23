package com.undac.undaccomedor.controller

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.ViewDragHelper
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.about.AboutDialog
import com.undac.undaccomedor.controller.menu_week.CMenuWeekFragment
import com.undac.undaccomedor.controller.news.CNewsFragment
import com.undac.undaccomedor.controller.profile.CProfileFragment
import com.undac.undaccomedor.controller.home.CHomeFragment
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.session.SessionManager
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.openFacebook
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

class CMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private var navigationView : NavigationView?=null
    private var headerView : View?=null
    private var textViewCodigo: TextView?=null
    private var textViewLastFirstName:TextView?=null
    private lateinit var session:SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        session= SessionManager(applicationContext)
        session.checkLogin()
        setToolbarAndNavigation()

        navigationView = findViewById(R.id.nav_view) as NavigationView
        headerView = navigationView!!.getHeaderView(0) as View
        textViewCodigo=headerView!!.findViewById(R.id.textViewCodigo)
        textViewLastFirstName=headerView!!.findViewById(R.id.textViewLastFirstName)

        var user:HashMap<String, String> =session.getUserDetails()

        textViewCodigo!!.text=user.get(SessionManager.KEY_UID)//Session.UID
        textViewLastFirstName!!.text=user.get(SessionManager.KEY_LAST_NAME_0)+" "+user.get(SessionManager.KEY_LAST_NAME_1)+", "+user.get(SessionManager.KEY_FIRST_NAME)//Session.LAST_NAME_0+" "+Session.LAST_NAME_1+", "+Session.FIRST_NAME

        //setTitle(R.string.reservacion)
        //activity_horarios.this.setTitle("Title NAme");
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        if(savedInstanceState==null){
            setMainFragment()
        }


    }

    private fun setToolbarAndNavigation() {
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_open, R.string.navigation_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        // Abrir drawerLayout desde mas o menos la mitad de la pantalla
        // https://stackoverflow.com/a/17802569
        val mDragger = drawer_layout.javaClass.getDeclaredField("mLeftDragger")
        mDragger.isAccessible = true
        val draggerObj = mDragger.get(drawer_layout) as ViewDragHelper

        val mEdgeSize = draggerObj.javaClass.getDeclaredField("mEdgeSize")
        mEdgeSize.isAccessible = true
        val edge = mEdgeSize.getInt(draggerObj)

        mEdgeSize.setInt(draggerObj, edge * 3) // any constant in dp
    }


    private fun setMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.framelayout, CHomeFragment())
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //removeAllFragments(item.itemId)
        var changed = true

        //nav_view.setCheckedItem(item.itemId)

           // item.setChecked(true)
        if (!item.isChecked)
            changed = replaceFragment(item.itemId)

        drawer_layout.closeDrawer(GravityCompat.START)
        return changed
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount>0){
            //Toast.makeText(this, supportFragmentManager.backStackEntryCount.toString(), Toast.LENGTH_LONG).show()
            supportFragmentManager.popBackStack()
            return
        }

        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            nav_view.menu.getItem(0).isChecked -> {
                super.onBackPressed()
            }
            else -> {
                replaceFragment(R.id.start)
                nav_view.setCheckedItem(R.id.start)
            }
        }
    }

    private fun replaceFragment(item: Int): Boolean {
        val fragment = getFragmentById(item)

        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit()
        }
        return fragment != null
    }

    private fun getFragmentById(id: Int): Fragment? {
        var fragment: Fragment? = null

        when (id) {
                R.id.start->{fragment = CHomeFragment()
            }
            R.id.desayuno -> {
                fragment = CMenuWeekFragment()
                val args = Bundle()
                args.putLong("typeMenu", CMenuWeekFragment.DESAYUNO)
                fragment.setArguments(args)
            }
            R.id.almuerzo -> {
                fragment = CMenuWeekFragment()
                val args = Bundle()
                args.putLong("typeMenu", CMenuWeekFragment.ALMUERZO)
                fragment.setArguments(args)
            }
            R.id.cena -> {
                fragment = CMenuWeekFragment()
                val args = Bundle()
                args.putLong("typeMenu", CMenuWeekFragment.CENA)
                fragment.setArguments(args)
            }
            R.id.faltas->{
                fragment = CNewsFragment()
            }

            R.id.news->{
                fragment= CNewsFragment()
            }
            R.id.face->{
                openFacebook()
            }
            R.id.profile->{
                fragment= CProfileFragment();
            }

            R.id.about->{
                AboutDialog().show(supportFragmentManager, "AboutDialog")
            }
            R.id.salir->{
                logout()
                session.logoutUser()

            }

        }
        return fragment
    }

    private fun removeAllFragments(itemId:Int) {
        when(itemId){
            R.id.salir->{
               return
            }
        }
       try{
           if(supportFragmentManager.backStackEntryCount>0){
              /*for(fragment in supportFragmentManager.fragments){
                  supportFragmentManager.beginTransaction().remove(fragment).commit();
              }*/
                for(fragment in supportFragmentManager.fragments){
                   supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
               }
           }
       }catch(e:Exception) {
           Toast.makeText(this, "Erorororoororo"+e.message, Toast.LENGTH_LONG).show()
       }

    }

    override fun onResume() {
        super.onResume()
        loggedInLast()
    }

    private fun loggedInLast(){
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
                    volley.prepare(baseContext, Server.FIREBASE_SAVE_LOGGED_IN_LAST, result={fireabase(it)}, error = {error(it)})
                    volley.setUser()
                    volley.post(Pair("token", token))
                }

            })
    }

    private fun logout(){
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
                    volley.prepare(baseContext, Server.FIREBASE_SAVE_LOGOUT, result={fireabase(it)}, error = {error(it)})
                    //volley.setUser()
                    volley.post(Pair("token", token))
                }

            })
    }

    private fun error(e: Exception?){
        if(e is VolleyError){
            //VolleySingleton.showErrorVolleyToast(this, e)
        }else{

        }

    }

    private fun fireabase(objJson: JSONObject?){

    }












}



