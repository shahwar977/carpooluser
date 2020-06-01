package com.centosquare.devatease.gooapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.models.TrustedContactModel
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

internal class AddContactDialog(private val ctx: Context) : Dialog(ctx),View.OnClickListener {



    private lateinit var userNameEditText: EditText
    private lateinit var userNumberEditText:EditText
    private lateinit var addContactBtn: Button
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var trustedContactReference: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle())
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.add_contact_dialog)

        initViewComponents()
        viewClickListeners()
        initObjectsAndReferences()




    }

    private fun initViewComponents(){

        userNameEditText = findViewById(R.id.contact_name_edittext)
        userNumberEditText = findViewById(R.id.contact_number_edittext)
        addContactBtn = findViewById(R.id.add_contact_btn)


    }

    private fun viewClickListeners(){

        addContactBtn.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

            R.id.add_contact_btn -> {


                if (userNameEditText.text.toString().trim().isNotEmpty() && userNumberEditText.text.toString().trim().isNotEmpty()) {

                    addContactInfoInFirebase(userNameEditText.text.toString().trim(),userNumberEditText.text.toString().trim())
                }

                else{

                    Toast.makeText(ctx,"contact name & number is required",Toast.LENGTH_SHORT).show()


                }

            }
        }

    }

    private fun addContactInfoInFirebase(contactName: String, contactNumber: String) {


        val trustedContactModel = TrustedContactModel(contactName,contactNumber)

        trustedContactReference.setValue(trustedContactModel).addOnSuccessListener {



            Toast.makeText(ctx,"successfully added contact",Toast.LENGTH_SHORT).show()
            cancel()



        }
            .addOnFailureListener{

                Toast.makeText(ctx,"unable to add contact",Toast.LENGTH_SHORT).show()


            }



    }






  private  fun initObjectsAndReferences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        trustedContactReference = firebaseDatabase.getReference(Constants.trustedContactReference).child(userId).child(
            UUID.randomUUID().toString())





    }



}
