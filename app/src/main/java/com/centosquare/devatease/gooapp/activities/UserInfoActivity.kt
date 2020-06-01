package com.centosquare.devatease.gooapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.location.LocationActivity
import com.centosquare.devatease.gooapp.models.ProfileModel
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserInfoActivity : AppCompatActivity() {



    private lateinit var btnDone: Button
    private lateinit var userNameText:TextView
    private lateinit var firebaseDatabase:FirebaseDatabase
    private lateinit var profileReference:DatabaseReference
    private  var firebaseUser:FirebaseUser? = null
    private lateinit var userId:String
    private lateinit var userPreference:SharedManager
    private lateinit var profileModel:ProfileModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        viewComponents()
        initObjectsAndRefrences()


        btnDone.setOnClickListener {

            if (!userNameText.text.toString().trim().isEmpty() && userNameText.text.toString().trim().length > 2) {


                addUserNameInFirebase(userNameText.text.toString().trim())

            }

            else {

                Toast.makeText(this,"username should be more than 3 characters",Toast.LENGTH_SHORT).show()
            }



        }
    }

    private fun viewComponents(){

        userNameText = findViewById(R.id.editText_username_login)
        btnDone = findViewById(R.id.btn_done_userinfo)


    }

    private fun initObjectsAndRefrences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        profileReference = firebaseDatabase.getReference(Constants.profileReference)

        userPreference = SharedManager(this)

    }

    private fun addUserNameInFirebase(userName: String) {

       // profileModel = ProfileModel(userName,userPreference.getStr(Constants.userNamePreference),null)

        profileReference.child(userId).child("name").setValue(userName).addOnSuccessListener {

            startActivity(Intent(applicationContext, LocationActivity::class.java))
            finish()

        }
            .addOnFailureListener {

                Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()

            }

    }
}
