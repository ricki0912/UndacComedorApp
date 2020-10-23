package com.undac.undaccomedor.service

import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

class InstanceIdService:FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
       // val token:String?= FirebaseInstanceId.getInstance().getToken()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL)
            .addOnCompleteListener { task ->
                var msg = "Suscrito "
                if (!task.isSuccessful) {
                    msg = "No se suscribio"
                }

                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }


    }

    companion object {
        private val TOPIC_ALL:String="all"
    }
}