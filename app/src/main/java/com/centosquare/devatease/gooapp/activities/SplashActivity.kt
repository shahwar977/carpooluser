package com.centosquare.devatease.gooapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.location.LocationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity: AppCompatActivity() {

    private lateinit var splashImgv:ImageView
    internal lateinit var firebaseAuth: FirebaseAuth
    internal var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        splashImgv = findViewById(R.id.imgv_splash)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        Glide.with(this).load(R.drawable.ic_splash_logo).centerInside().into(splashImgv)

        val secondsDelayed = 1

        Handler().postDelayed({


            if (firebaseUser == null){

                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()

            }else{

                startActivity(Intent(this@SplashActivity, LocationActivity::class.java))
                finish()
            }




        }, (secondsDelayed * 4000).toLong())


    }
}