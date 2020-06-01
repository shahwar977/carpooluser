package com.centosquare.devatease.gooapp.fragments

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.location.LocationActivity
import com.centosquare.devatease.gooapp.models.ProfileModel
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class EditAccountFragment :Fragment(), View.OnClickListener {

    private lateinit var fragemntView: View
    private lateinit var toolbar: Toolbar
    private lateinit var btnUpdate:Button
    private lateinit var userNameEditText:EditText
    private lateinit var mobileNumberEditText:EditText
    private lateinit var emailEditText:EditText
    private lateinit var userImageView:ImageView
    private lateinit var chooseImageBtn:ImageView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var profileInfoReference: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var profileReference:DatabaseReference
    private lateinit var userId:String
    private lateinit var userPreference: SharedManager
    private lateinit var profileModel: ProfileModel
    private lateinit var profileListener:ValueEventListener
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var firebaseStorage:FirebaseStorage
    private lateinit var storageReference:StorageReference
    private lateinit var progressBar:ProgressBar
    private var isImageSet:Boolean = false
    private lateinit var imageUrl:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_edit_account, container, false)

        // initialize all view components
        viewComponents()
        viewClickListners()
        initObjectsAndReferences()
        firebaseListeners()


        // for toolbar name
        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_edit_fragment)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

        }


        profileInfoReference.addListenerForSingleValueEvent(profileListener)


        return fragemntView


    }
    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        userNameEditText = fragemntView.findViewById(R.id.edtxt_name_edit_profile)
        mobileNumberEditText = fragemntView.findViewById(R.id.edtxt_phone_no_edit_profile)
        emailEditText = fragemntView.findViewById(R.id.edtxt_email_add_edit_profile)
        userImageView = fragemntView.findViewById(R.id.edit_profile_img)
        chooseImageBtn = fragemntView.findViewById(R.id.edit_profile_img_btn)
        btnUpdate = fragemntView.findViewById(R.id.btn_update_edit_address)
        progressBar = fragemntView.findViewById(R.id.progress_bar)

    }

    fun viewClickListners(){

        btnUpdate.setOnClickListener(this)
        chooseImageBtn.setOnClickListener(this)


    }

    fun firebaseListeners() {

         profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                val name:String = dataSnapshot.child(Constants.userNameReference).value.toString()
                val phone:String = dataSnapshot.child(Constants.userPhoneReference).value.toString()



                userNameEditText.setText(name)
                mobileNumberEditText.setText(phone)

                if (dataSnapshot.child(Constants.userEmailReference).getValue() !=null){

                    val email:String = dataSnapshot.child(Constants.userEmailReference).value.toString()
                    emailEditText.setText(email)

                }

                if (dataSnapshot.child(Constants.userImageReference).getValue() !=null){

                  imageUrl = dataSnapshot.child(Constants.userImageReference).value.toString()
                    Glide.with(activity!!).load(imageUrl).into(userImageView)
                    isImageSet = true

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
    }

    fun initObjectsAndReferences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        profileInfoReference = firebaseDatabase.getReference(Constants.profileReference).child(userId)
        profileReference = firebaseDatabase.getReference(Constants.profileReference)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReferenceFromUrl("gs://gooapp-73976.appspot.com/uploads")

        userPreference = SharedManager(activity!!)



    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

             R.id.btn_update_edit_address -> {




                 updateProfileInfoInFiebase(userNameEditText.text.toString().trim(),mobileNumberEditText.text.toString().trim(),emailEditText.text.toString().trim())

             }

            R.id.edit_profile_img_btn->{


                launchGallery()

            }


        }





    }

    private fun updateProfileInfoInFiebase(userName: String, userMobile: String, userEmail: String) {


        progressBar.visibility = View.VISIBLE



        if (filePath!=null && isImageSet == false){

            val childRefrence:StorageReference = storageReference.child("${System.currentTimeMillis()}.jpg")
            childRefrence.putFile(filePath!!).addOnSuccessListener {

                childRefrence.downloadUrl.addOnSuccessListener {

                   /* profileReference.child(userId).child("profileImageUrl").setValue(it.toString()).addOnSuccessListener {

                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()

                    }*/
                    profileModel = ProfileModel(userName,userMobile,userEmail,it.toString())
                    profileReference.child(userId).setValue(profileModel).addOnSuccessListener {

                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()

                    }
                        .addOnFailureListener {

                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()

                        }
                }



            }
        }

        else if (filePath == null && isImageSet == true){


            profileModel = ProfileModel(userName,userMobile,userEmail,imageUrl)
            profileReference.child(userId).setValue(profileModel).addOnSuccessListener {

                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()

            }
                .addOnFailureListener {

                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()

                }

        }

        else if (isImageSet == true && filePath !=null){

            val childRefrence:StorageReference = storageReference.child("${System.currentTimeMillis()}.jpg")
            childRefrence.putFile(filePath!!).addOnSuccessListener {

                childRefrence.downloadUrl.addOnSuccessListener {

                    /* profileReference.child(userId).child("profileImageUrl").setValue(it.toString()).addOnSuccessListener {

                         progressBar.visibility = View.INVISIBLE
                         Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()

                     }*/
                    profileModel = ProfileModel(userName,userMobile,userEmail,it.toString())
                    profileReference.child(userId).setValue(profileModel).addOnSuccessListener {

                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()

                    }
                        .addOnFailureListener {

                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()

                        }
                }



            }
        }

        else if(isImageSet == false && filePath == null){

            profileModel = ProfileModel(userName,userMobile,userEmail,null)
            profileReference.child(userId).setValue(profileModel).addOnSuccessListener {

                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()

            }
                .addOnFailureListener {

                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()

                }
        }


      /*  profileReference.child(userId).child("name").setValue(userName)
        profileReference.child(userId).child("mobile").setValue(userMobile)
        profileReference.child(userId).child("email").setValue(userEmail)*/





    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, filePath)
                userImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    }
