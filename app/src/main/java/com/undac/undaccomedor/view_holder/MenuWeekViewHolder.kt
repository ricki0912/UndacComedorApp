package com.undac.undaccomedor.view_holder

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.undac.undaccomedor.CReservationActivity
import com.undac.undaccomedor.R
import com.undac.undaccomedor.item.Menu
import kotlinx.android.synthetic.main.li_item_menu.view.*
import java.text.SimpleDateFormat
import java.util.*

internal class MenuWeekViewHolder(itemView: View, override val mApplicationContext: Context) : BaseViewHolder<Menu>(itemView) {

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.parse(sdf.format(Date()))
    val sdfTimeOfAssist= SimpleDateFormat("hh:mm aaa")

    override fun bind(menuDay: Menu) {
        itemView.textViewDiaSemanaLiMenuDay.text=menuDay.name_day_of_week
        itemView.textViewDiaMesLiMenuDay.text=menuDay.day_of_month
        itemView.textViewSegundoLiMenuDay.text=if(menuDay.second!=null) menuDay.second else ""
        itemView.textViewSoupLiMenuDay.text=if(menuDay.soup!=null) menuDay.soup else ""
        itemView.textViewInfusionLiMenuDay.text=if(menuDay.drink!=null) menuDay.drink else ""
        itemView.textViewFroutLiMenuDay.text=if(menuDay.fruit!=null ) menuDay.fruit else ""
        itemView.textViewDessertLiMenuDay.text=if(menuDay.dessert!=null) menuDay.dessert else ""
        itemView.textViewAditionalLiMenuDay.text=if(menuDay.aditional!=null) menuDay.aditional else ""



        itemView.textViewReserStartMenuDay.text=menuDay.reser_date_start_string
        itemView.textViewReserEndMenuDay.text=menuDay.reser_date_end_string

        if(menuDay.state_menu_reservation==1){
            itemView.linearLayoutDialIMenuDay.setBackgroundResource(R.color.colorPrimero)
        }else{
            itemView.linearLayoutDialIMenuDay.setBackgroundResource(R.color.red)
        }

        /* itemView.textViewDateReserStatelLiMenuDay.text="De "+menuDay.reser_date_start_char +" a "+
                 menuDay.reser_date_end_char*/


        itemView.textViewStateHoraryOfReserMenuDay.text= if(menuDay.horary_of_reser!=null) menuDay.horary_of_reser else "--:---- a --:----"

        itemView.textViewStateTimeOfAssistMenuDay.text=if(menuDay.assist_time!=null) sdfTimeOfAssist.format(menuDay.assist_time) else "----/--/-- --:-- -.-."

        if(menuDay.assist==true){
            itemView.imageViewStateMenuDay.setBackgroundResource(R.drawable.icon_accepted)
            itemView.imageViewStateMenuDay.text="A"
        }else if(menuDay.assist==false){
            itemView.imageViewStateMenuDay.setBackgroundResource(R.drawable.icon_missing)
            itemView.imageViewStateMenuDay.text="F"
        }else if(menuDay.id_timetable!=null){
            itemView.imageViewStateMenuDay.setBackgroundResource(R.drawable.ic_checked)
            itemView.imageViewStateMenuDay.text=""
        }else{
            itemView.imageViewStateMenuDay.setBackgroundResource(R.drawable.ic_unchecked)
            itemView.imageViewStateMenuDay.text=""
        }


        if(currentDate.equals(menuDay.skd_date)){
            itemView.cardViewMenuDay.setBackgroundResource(R.color.bey)
        }else{
            itemView.cardViewMenuDay.setBackgroundResource(R.color.graylight)
        }

        itemView.imageBettunNutritionalTableLiMenuDay.setOnClickListener({

            /*val intent = Intent(mApplicationContext, CReservationActivity::class.java)
            //intent.putExtra("keyIdentifier", value)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mApplicationContext.startActivity(intent) */
         //   mClickListener(menuDay)
            onClick(menuDay)
        })

    }


    private fun onClick(dayMenu: Menu) {
        try{
            //Toast.makeText(mApplicationContext,"hol "+dayMenu.id, Toast.LENGTH_LONG).show()

            /*startActivity(Intent(context, SecondActivity::class.java)
                .putExtra(SecondActivity.PARAM_GAME_ID, gameId))*/

            val intent = Intent(mApplicationContext, CReservationActivity::class.java)
            //intent.putExtra("keyIdentifier", value)
            intent.putExtra(Menu.NAME_SERIALIZABLE, dayMenu)
            /*
            intent.putExtra("id",dayMenu.id)
            intent.putExtra("typeMenu", dayMenu.type)
            intent.putExtra("day_month",dayMenu.day_month)
            val sdf = SimpleDateFormat("dd-MM-yy")
            val skd_dates : String = sdf.format(dayMenu.skd_date)
            intent.putExtra("skd_date",skd_dates)*/
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mApplicationContext.startActivity(intent)
    /*
            var frag4 = CReservationFragment()
            val args4 = Bundle()
            args4.putInt("id",dayMenu.id);
            args4.putInt("typeMenu", dayMenu.type)
            args4.putString("day_month",dayMenu.day_month)
            args4.putString("name_day",dayMenu.name_day)

            val sdf = SimpleDateFormat("dd-MM-yy")
            val skd_dates : String = sdf.format(dayMenu.skd_date)

            args4.putString("skd_date",skd_dates)

            frag4.setArguments(args4)


            val fm= (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            fm.replace(R.id.framelayout, frag4, "reservation")
            fm.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
            fm.addToBackStack("reservation")
            fm.commit()*/
        }catch(e:Exception){
            Toast.makeText(mApplicationContext, e.message/*obj.getString("estado")*/, Toast.LENGTH_LONG).show()

        }
    }

}