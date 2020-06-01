package com.centosquare.devatease.gooapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.centosquare.devatease.gooapp.R

internal class RatingDialog(private val ctx: Context) : Dialog(ctx) {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle())
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.rider_rating_dialog)




    }
}
