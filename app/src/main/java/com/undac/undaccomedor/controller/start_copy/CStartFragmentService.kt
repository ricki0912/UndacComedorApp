package com.undac.undaccomedor.controller.start_copy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.*
import android.view.ViewGroup
import com.undac.undaccomedor.R

class CStartFragmentService : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start_service, container, false)
    }
}