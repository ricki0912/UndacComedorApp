package com.undac.undaccomedor.controller.start_copy

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.undac.undaccomedor.controller.CMainActivity
import kotlinx.android.synthetic.main.fragment_start_copy.*

import android.content.res.Configuration
import android.widget.LinearLayout
import com.undac.undaccomedor.R


public class CStartFragment : Fragment() {

    private lateinit var mRootView: View
    private lateinit var mApplicationContext: Context
   // var tabIcons: IntArray= intArrayOf (R.drawable.description, R.drawable.instrucciones, R.drawable.horary)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_start_copy, container, false)
        mApplicationContext = requireActivity().applicationContext
        return mRootView

        //return LayoutInflater.from(container?.context).inflate(R.layout.fragment_start, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        establishMeasurement()

        setupViewPager(viewpager)
        tabs.setupWithViewPager(viewpager)
        setUpTabIcons()
        viewpager.adapter!!.notifyDataSetChanged()
    }


    override fun onResume() {
        super.onResume()
        //requireActivity().setTitle(R.string.noticias)
        (activity as CMainActivity).supportActionBar?.title = getString(R.string.inicio)

    }
    private fun setUpTabIcons() {
        //tabs.getTabAt(0)!!.setIcon(tabIcons[0])
        //tabs.getTabAt(1)!!.setIcon(tabIcons[1])
        //tabs.getTabAt(2)!!.setIcon(tabIcons[2])
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter((activity as AppCompatActivity).supportFragmentManager)
        adapter.addFragment(CStartFragmentService(), "Servicio")
        adapter.addFragment(CStartFragmentGuide(), "Guía")
        adapter.addFragment(CStartFragmentHorary(), "Horarios")
        viewPager.adapter = adapter

    }

    private fun establishMeasurement(){

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayoutContainer.orientation= LinearLayout.HORIZONTAL
        } else {
            linearLayoutContainer.orientation= LinearLayout.VERTICAL
        }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }


        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }
}