package com.undac.undaccomedor.controller.home

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast
import com.android.volley.VolleyError
import com.undac.undaccomedor.R
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.controller.menu_week.CMenuWeekFragment
import com.undac.undaccomedor.controller.news.CNewsFragment
import com.undac.undaccomedor.item.*
import com.undac.undaccomedor.server.Server
import com.undac.undaccomedor.view_holder.BaseViewHolder
import com.undac.undaccomedor.volley.VolleySingleton
import com.undaccomedor.helpers.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONObject




public class CHomeFragment : Fragment() {

    private lateinit var mRootView: View
    private lateinit var mApplicationContext: Context
    private lateinit var homeListAdapter :HomeAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false)
        mApplicationContext = requireActivity().applicationContext
        return mRootView
        //return LayoutInflater.from(container?.context).inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    override fun onResume() {
        super.onResume()

        (activity as CMainActivity).supportActionBar?.title = getString(R.string.inicio)


        var toolbar: Toolbar = (activity as CMainActivity).toolbar

        var params : AppBarLayout.LayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams

        params.scrollFlags=AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or  AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL

        loadData()
    }


    private fun initView() {
        linearLayoutManager = LinearLayoutManager(mApplicationContext)
        recyclerViewHome.layoutManager=linearLayoutManager
        homeListAdapter = HomeAdapter(context!!)
        recyclerViewHome.adapter = homeListAdapter
        //movieListAdapter.setList(generateDummyData())

        //loadData()

        swipeRefreshLayoutHome.init{
            loadData()
        }
    }
    private fun loadData(){
        var volleyCheckMenuEnable = VolleySingleton(context!!)// VolleySingleton.getInstance(this)
        volleyCheckMenuEnable.prepare(context!!, Server.HOME_GET_LIST_ITEMS_VIEW_, result={result(it)}, error = {error(it)}, before = {before()}, after={after()} )
        volleyCheckMenuEnable.post()
    }




    private fun before(){
        swipeRefreshLayoutHome.isRefreshing=true
        textViewTitleHome.visibility=View.GONE;
    }

    private fun after(){
        if(swipeRefreshLayoutHome!=null){
            swipeRefreshLayoutHome.isRefreshing=false
        }
    }

    private fun result(objJson: JSONObject?){
        execHome(objJson)
        setSaveAllHome(objJson)
        if(swipeRefreshLayoutHome!=null){
            mRootView.snack(context, R.string.update_success, SnackType.FINISH)
        }
    }

    private fun error(e: Exception?){
        execHome(getSaveAllHome());
        if(swipeRefreshLayoutHome!=null){
            swipeRefreshLayoutHome.isRefreshing=false
            textViewTitleHome.visibility=View.VISIBLE
            textViewTitleHome.text=mApplicationContext.resources.getString(R.string.message_home_newtwork)
            VolleySingleton.showErrorVolleySnack(
                mRootView,
                mApplicationContext,
                e as VolleyError
            )
        }
        if(e is VolleyError){

            /*VolleySingleton.showErrorVolleySnack(
                itemView.findViewById(android.R.id.content)
                ,mApplicationContext, e)*/
        }else{

        }
    }


    private fun execHome(objJson: JSONObject?) {
        if (objJson == null)
            return
        if (objJson.getInt("state") == 0) {
            Toast.makeText(view?.getContext(), objJson.getString("message"), Toast.LENGTH_SHORT).show()
            return;
        }
        val objJsonArray: JSONArray =objJson.getJSONArray("data")

        val homeList = ArrayList<Any>()

        for (i in 0..objJsonArray.length()-1) {
            val objJsonTypeItem:Int =objJsonArray.getInt(i);


            homeList.add(
                when (objJsonTypeItem) {
                    BaseViewHolder.TYPE_WELCOME-> WelcomeItem()
                    BaseViewHolder.TYPE_PENDING_RESERVATIONS ->{
                        var pendingReserItem=PendingReservationsItem(R.id.desayuno, R.id.almuerzo, R.id.cena)
                        pendingReserItem.loadFragment={loadFragment(it)}
                        pendingReserItem
                    }
                    BaseViewHolder.TYPE_LASTED_NEWS->{
                        var latestNews=LastedNewsItem(R.id.news)
                        latestNews.loadNews={loadFragment(it)}
                        latestNews
                    }
                    BaseViewHolder.TYPE_SEND_SUGGESTION -> SendSuggestionItem()
                    BaseViewHolder.TYPE_SERVICE->ServiceItem()
                    BaseViewHolder.TYPE_GUIDE->GuideItem()
                    BaseViewHolder.HORARY_ATTENTION->HoraryAttentionItem()
                    BaseViewHolder.TYPE_TODAY_RESERVATIONS->{
                        var todayReservations=TodayReservationsItem(1)
                        todayReservations.loadNextItem={loadNextItem(it)}
                        todayReservations
                    }
                    else -> throw IllegalArgumentException("Type de Item invalido")
                }
            )
            homeListAdapter.setList(homeList)
        }
    }

    private fun loadFragment(id: Int) {
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


            R.id.news->{
                fragment= CNewsFragment()
            }
           /* R.id.face->{
                openFacebook()
            }
            R.id.profile->{
                fragment= CProfileFragment();
            }

            R.id.about->{
                AboutDialog().show(supportFragmentManager, "AboutDialog")
            }
            R.id.salir->{
                session.logoutUser()
            }*/

        }



        if (fragment != null) {

            (activity as CMainActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.framelayout, fragment, "hola")
                /*.addToBackStack(null)*/
                .commit()

            /*NextFragment nextFrag= new NextFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.Layout_container, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit();*/
            (activity as CMainActivity).nav_view.setCheckedItem(id)

        }

    }

    private fun loadNextItem(next_position:Int){
        linearLayoutManager.scrollToPositionWithOffset(next_position, 0)

    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_update-> {
                loadData()
                // sendData(Server.GET_MENU, mApplicationContext, getParams())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun getSaveAllHome():JSONObject?{
        var objJson : JSONObject?=null
        try{
            var valor:String?=mApplicationContext.readStringFromStorage(
                HOME
            )
            if(valor!=null && !valor.trim().equals("")){
                objJson = JSONObject(valor)
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return objJson

    }


    private fun setSaveAllHome(objJson :  JSONObject?){
        mApplicationContext.saveToStorage(
            HOME,objJson.toString() )
    }
    companion object {
        private val  HOME:String="Home.json"
    }

}

