package com.undac.undaccomedor.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Vibrator
import android.support.v4.app.NotificationCompat

import com.undac.undaccomedor.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.undac.undaccomedor.controller.login.CLoginActivity
import com.undac.undaccomedor.session.SessionManager

class MessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        if(p0?.notification!=null){
            showNotification(p0!!.notification!!.title!!, p0!!.notification!!.body!!)
        }

        if(p0?.data!=null){
            val code : Int=p0?.data.get("code")!!.toInt()
            when(code){
                code->logoutUser(code)
            }
        }

    }

    private fun logoutUser(code:Int){
        if(code==CODE_LOGOUT_USER){
            var session: SessionManager
            session= SessionManager(applicationContext)
            session.logoutUser()

        }
    }

    private fun showNotification(title:String , body:String){

       // val intent:Intent?= Intent(this, CMainActivity.)
         //intent.setFlags()

        val intent = Intent(this, CLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)


        val soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder= NotificationCompat.Builder(this,"M_CH_ID")
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            //.setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound( soundUri)
            .setContentIntent(pendingIntent)


        val v=this.getSystemService(Context.VIBRATOR_SERVICE)
        if(v is Vibrator){
            v.vibrate(1000)
        }

        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE)

        if(notificationManager is NotificationManager){
            notificationManager.notify(0,notificationBuilder.build())
        }

    }

    companion object {
        val CODE_LOGOUT_USER:Int=2545
    }
}