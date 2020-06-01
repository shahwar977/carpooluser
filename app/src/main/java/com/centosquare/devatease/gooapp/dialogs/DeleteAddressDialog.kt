package com.centosquare.sochlay.sochlaypartner.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.location.LocationActivity
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

internal class DeleteAddressDialog(private val ctx: Context , private val addressType:String) : Dialog(ctx) {


    private lateinit var cancelBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var homeOrOfficeTextView:TextView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var addressReference: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle())
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.delete_address_dialog_layout)



        homeOrOfficeTextView = findViewById(R.id.home_or_office_txt)
        cancelBtn = findViewById(R.id.cancel_btn)
        deleteBtn = findViewById(R.id.delete__btn)


        homeOrOfficeTextView.setText("\"$addressType\"?")

        initObjectsAndRefrences()

        cancelBtn.setOnClickListener {

            dismiss()

        }

        deleteBtn.setOnClickListener {

            deleteAddressInFirebase(addressType)

        }


    }


    private fun initObjectsAndRefrences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        addressReference = firebaseDatabase.getReference(Constants.userAddressReference)


    }

    private fun deleteAddressInFirebase(addressType: String) {



        if (addressType.equals("home")){

            addressReference.child(userId).child(Constants.homeAddressReference).removeValue().addOnSuccessListener {

                Toast.makeText(context,"address deleted", Toast.LENGTH_SHORT).show()
                cancel()
            }
                .addOnFailureListener {

                    Toast.makeText(context,it.toString(), Toast.LENGTH_SHORT).show()

                }

        }

        else if (addressType.equals("office")){
            addressReference.child(userId).child(Constants.officeAddressReference).removeValue().addOnSuccessListener {

                Toast.makeText(context,"address deleted", Toast.LENGTH_SHORT).show()
                cancel()
            }
                .addOnFailureListener {

                    Toast.makeText(context,it.toString(), Toast.LENGTH_SHORT).show()

                }

        }









    }
}
