package com.undac.undaccomedor.controller.news

import android.os.Bundle
import com.undac.undaccomedor.R
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.*
import android.webkit.WebViewClient
import com.undac.undaccomedor.controller.CMainActivity
import com.undac.undaccomedor.server.Server
import kotlinx.android.synthetic.main.fragment_news.*
import android.webkit.WebView
import kotlinx.android.synthetic.main.app_bar_main.*

class CNewsFragment : Fragment() {
    private var pd: ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pd=ProgressDialog(getActivity())
        pd!!.setMessage("Cargando.. Por favor espere.");
        pd!!.setCanceledOnTouchOutside(false);
        pd!!.show();
        faqContent.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (pd!!.isShowing()) {
                    pd!!.dismiss();
                }
            }
        }

        faqContent.settings.javaScriptEnabled = true
        faqContent.loadUrl(Server.GET_NEWS)

    }

    override fun onResume() {
        super.onResume()
        //requireActivity().setTitle(R.string.noticias)
        (activity as CMainActivity).supportActionBar?.title = getString(R.string.noticias)

        var toolbar: Toolbar = (activity as CMainActivity).toolbar
        var params : AppBarLayout.LayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags=0
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.faq, menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.faq_share -> requireActivity().shareText("UNCmorfi FAQ", URL)
            R.id.faq_browser -> requireActivity().startBrowser(URL)
            else -> super.onOptionsItemSelected(item)
        }
    }*/





}