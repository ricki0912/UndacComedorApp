package com.undac.undaccomedor.controller.about

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.undac.undaccomedor.BuildConfig
import com.undac.undaccomedor.R
import kotlinx.android.synthetic.main.dialog_about.view.*

class AboutDialog : DialogFragment(){
    private val version: String = BuildConfig.VERSION_NAME

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        val v = View.inflate(context, R.layout.dialog_about, null)
        v.versionName.text = version

        builder.setView(v)
        return builder.create()
    }
}