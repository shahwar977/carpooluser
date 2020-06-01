package com.centosquare.devatease.gooapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.centosquare.devatease.gooapp.R

class LegalFragment:Fragment(), View.OnClickListener {

    private lateinit var fragemntView: View
    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_legal, container, false)

        // initialize all view components
        viewComponents()
        viewClickListners()
        // for toolbar name
        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_legal)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        // for back button transaction
        toolbar.setNavigationOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

        }

        return fragemntView


    }
    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)

    }
    fun viewClickListners(){


    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

            /* R.id.btn_confirm_destination -> {

                 activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,ChooseRideFragment()).commit()

             }*/


        }

    }


}