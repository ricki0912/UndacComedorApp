package com.undac.undaccomedor.controller.reservation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import com.undac.undaccomedor.R
import kotlinx.android.synthetic.main.li_item_horary.view.*

internal class HoraryAdapter (private val mContext: Context,
                              private var mHoraryList: List<Horary>,
                              private val mClickListener: (Horary, ToggleButton, Int) -> Unit
    /*private val mLongClickListener: (Menu) -> Unit*/) :
    RecyclerView.Adapter<HoraryAdapter.MenuItemViewHolder>() {
    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val horary = mHoraryList[position]
        holder.bind(horary)    }


    internal inner class MenuItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        fun bind(horary: Horary) {

            itemView.textViewHoraOpcReser.text = horary.food_start_char+" a "+horary.food_end_char
            itemView.textViewCantDeTotalReser.text= horary.cantreser.toString()+" de ("+horary.cant+")"
            itemView.textViewIdHorary.text=horary.id.toString()

            itemView.toggleButtonReser.isChecked=horary.checked

            itemView.toggleButtonReser.setOnClickListener({
                    mClickListener (horary, itemView.toggleButtonReser, -1)

            })
            //itemView.toggleButtonReser.text.
            //linearLayoutReservacion!!.addView(viewaux)

            //itemView.imageBettunNutritionalTableLiMenuDay.setOnClickListener({
                // mClickListener(menuDay)
            //})

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.li_item_horary, parent, false)
        return MenuItemViewHolder(v)
    }


    override fun getItemCount(): Int {
        return mHoraryList.size
    }

    fun updateHorary(menuList: List<Horary>) {
        mHoraryList = menuList
        notifyDataSetChanged()
    }
}