package com.centosquare.devatease.gooapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.fragments.MapFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentmanger = supportFragmentManager
        fragmentmanger.beginTransaction().replace(R.id.frame_layout,MapFragment()).addToBackStack(null) .commit()


    }



   /* override fun onBackPressed() {
        val fragment  = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (!(fragment is OnBackPressed) || !((fragment as OnBackPressed).onBackPressed())){

            super.onBackPressed()
        }
    }*/


    }

