package com.centosquare.devatease.gooapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.centosquare.devatease.gooapp.R

class SavePlacesFragment : Fragment(), View.OnClickListener {

    private lateinit var fragemntView:View
    private lateinit var toolbar: Toolbar
    private lateinit var homeOptionBtn: ImageView
    private lateinit var officeOptionBtn: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        fragemntView =  inflater.inflate(R.layout.fragment_save_places, container, false)

        viewComponents()
        viewClickListners()

        var activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)

        toolbar.title  = "Save Places"

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)


         // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,AccountSettingsFragment()).commit()

        }



        return fragemntView
    }

    fun viewComponents(){
        toolbar = fragemntView.findViewById(R.id.toolbar)
        homeOptionBtn = fragemntView.findViewById(R.id.home_options)
        officeOptionBtn = fragemntView.findViewById(R.id.office_options)

    }

    fun viewClickListners(){

        homeOptionBtn.setOnClickListener(this)
        officeOptionBtn.setOnClickListener(this)


    }


    private fun openMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun  onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.edit -> {
                        return true
                    }
                    R.id.delete -> {
                        return true
                    }

                    else -> return false
                }
            }
        })
        popupMenu.inflate(R.menu.default_menu)
        popupMenu.show()
    }
    override fun onClick(p0: View?) {

        when(p0!!.id){

            R.id.home_options -> {

                openMenu(p0)
            }

            R.id.office_options -> {
                openMenu(p0)

            }

        }

    }



}
