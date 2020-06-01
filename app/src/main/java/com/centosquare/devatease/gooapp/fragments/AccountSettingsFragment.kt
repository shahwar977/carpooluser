package com.centosquare.devatease.gooapp.fragments


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout

import com.centosquare.devatease.gooapp.R
import com.centosquare.sochlay.sochlaypartner.activities.DeleteAddressDialog
import android.view.MenuItem
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.centosquare.devatease.gooapp.models.ProfileModel
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.*


class AccountSettingsFragment : Fragment(),View.OnClickListener {

    private lateinit var fragemntView:View
    private lateinit var toolbar: Toolbar
    private lateinit var homeOptionBtn:ImageView
    private lateinit var officeOptionBtn:ImageView
    private lateinit var homeAdressTextView:TextView
    private lateinit var officeAddressTextView:TextView
    private lateinit var safetyLayout:ConstraintLayout
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var profileInfoReference: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var profileReference: DatabaseReference
    private lateinit var userId:String
    private lateinit var userPreference: SharedManager
    private lateinit var profileModel: ProfileModel
    private lateinit var profileListener: ValueEventListener
    private lateinit var userNameTextView:TextView
    private lateinit var userMobileTextView:TextView
    private lateinit var userEmailTextView:TextView
    private lateinit var userAddressReference : DatabaseReference
    private lateinit var addressListener: ValueEventListener
    private lateinit var userNumberTextView:TextView
    private lateinit var userImageView:ImageView
    private  var homeAddressString:String? = null
    private  var officeAddressString:String? = null
    private var homeLatitude:Double? = null
    private var homeLongitude:Double? = null
    private var officeLatitude:Double? = null
    private var officeLongitude:Double? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        fragemntView =  inflater.inflate(R.layout.fragment_account_settings, container, false)

        viewComponents()
        viewClickListners()
        initObjectsAndReferences()
        firebaseListeners()

        var activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)

        toolbar.title  = "Account Setting"

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)


        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

        }



        profileInfoReference.addListenerForSingleValueEvent(profileListener)
        userAddressReference.addValueEventListener(addressListener)


      /*  homeOptionBtn.setOnClickListener {


            val deleteDialog = RatingDialog(activity!!)
            deleteDialog.setCancelable(false)
            deleteDialog.show()

            val back = ColorDrawable(Color.WHITE)
            val inset = InsetDrawable(back, 90)
            val window = deleteDialog.getWindow()!!
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(inset)


        }
*/


      /*  homeOptionBtn.setOnClickListener{

            openHomeMenu(it)
        }

        officeOptionBtn.setOnClickListener {

            openHomeMenu(it)
        }*/

        return fragemntView
    }


    private fun openHomeMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun  onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.edit -> {


                        val transaction =  activity!!.supportFragmentManager.beginTransaction()
                        val bundle = Bundle()
                        val myMessage = "home"
                        if (homeAdressTextView.text.toString().isNotEmpty() && !homeAdressTextView.text.toString().trim().equals("No Address added.")){



                             homeAddressString = homeAdressTextView.text.toString().trim()

                        }

                        bundle.putString("addressType", myMessage)
                        bundle.putString("address",homeAddressString)
                        bundle.putDouble("addressLat",homeLatitude!!)
                        bundle.putDouble("addressLng",homeLongitude!!)
                        val fragInfo = EditAddressFragment()
                        fragInfo.arguments = bundle
                        transaction.replace(R.id.frame_layout, fragInfo)
                        transaction.commit()

                       // activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,EditAddressFragment()).commit()
                        return true
                    }
                    R.id.delete -> {

                        if (homeAdressTextView.text.toString().isNotEmpty() && !homeAdressTextView.text.toString().trim().equals("No Address added.")){


                            val deleteDialog = DeleteAddressDialog(activity!!,"home")
                            deleteDialog.setCancelable(false)
                            deleteDialog.show()

                            val back = ColorDrawable(Color.WHITE)
                            val inset = InsetDrawable(back, 50)
                            val window = deleteDialog.getWindow()!!
                            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                            window.setBackgroundDrawable(inset)

                        }
                        return true

                    }

                    else -> return false
                }
            }
        })
        popupMenu.inflate(R.menu.default_menu)
        popupMenu.show()
    }

    private fun openOfficeMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun  onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.edit -> {


                        val transaction =  activity!!.supportFragmentManager.beginTransaction()
                        val bundle = Bundle()

                        if (officeAddressTextView.text.toString().isNotEmpty() && !officeAddressTextView.text.toString().trim().equals("No Address added.")){



                            officeAddressString = officeAddressTextView.text.toString().trim()

                        }

                        val addressType = "office"
                        bundle.putString("addressType", addressType)
                        bundle.putString("address",officeAddressString)
                        officeLatitude?.let { bundle.putDouble("addressLat", it) }
                        officeLongitude?.let { bundle.putDouble("addressLng", it) }
                        val fragInfo = EditAddressFragment()
                        fragInfo.setArguments(bundle)
                        transaction.replace(R.id.frame_layout, fragInfo)
                        transaction.commit()

                        // activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,EditAddressFragment()).commit()
                        return true
                    }
                    R.id.delete -> {

                        if (officeAddressTextView.text.toString().isNotEmpty() && !officeAddressTextView.text.toString().trim().equals("No Address added.")){



                            val deleteDialog = DeleteAddressDialog(activity!!,"office")
                            deleteDialog.setCancelable(false)
                            deleteDialog.show()

                            val back = ColorDrawable(Color.WHITE)
                            val inset = InsetDrawable(back, 50)
                            val window = deleteDialog.getWindow()!!
                            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                            window.setBackgroundDrawable(inset)

                        }





                        return true
                    }

                    else -> return false
                }
            }
        })
        popupMenu.inflate(R.menu.default_menu)
        popupMenu.show()
    }

    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        homeOptionBtn = fragemntView.findViewById(R.id.home_options)
        officeOptionBtn = fragemntView.findViewById(R.id.office_options)
        safetyLayout = fragemntView.findViewById(R.id.layout_safety)
        userNameTextView = fragemntView.findViewById(R.id.user_name)
        userMobileTextView = fragemntView.findViewById(R.id.user_number)
        userEmailTextView = fragemntView.findViewById(R.id.user_email)
        userImageView = fragemntView.findViewById(R.id.user_profile_img)


    }

    fun initObjectsAndReferences(){

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        profileInfoReference = firebaseDatabase.getReference(Constants.profileReference).child(userId)
        userAddressReference = firebaseDatabase.getReference(Constants.userAddressReference).child(userId)

        userPreference = SharedManager(activity!!)
        userNameTextView = fragemntView.findViewById(R.id.user_name)
        userNumberTextView = fragemntView.findViewById(R.id.user_number)
        userEmailTextView = fragemntView.findViewById(R.id.user_email)
        homeAdressTextView = fragemntView.findViewById(R.id.home_details_txt)
        officeAddressTextView = fragemntView.findViewById(R.id.office_details_txt)




    }


    fun viewClickListners(){


        safetyLayout.setOnClickListener(this)
        homeOptionBtn.setOnClickListener(this)
        officeOptionBtn.setOnClickListener(this)



    }




    override fun onClick(p0: View?) {

        when(p0!!.id){


            R.id.layout_safety -> {


                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,TrustedContactsFragment()).commit()
            }

            R.id.home_options -> {

                openHomeMenu(p0)


            }

            R.id.office_options ->{

                openOfficeMenu(p0)
            }






        }
    }



    fun firebaseListeners() {

        profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {



                val name:String = dataSnapshot.child(Constants.userNameReference).value.toString()
                val phone:String = dataSnapshot.child(Constants.userPhoneReference).value.toString()


                userNameTextView.setText(name)
                userNumberTextView.setText(phone)

                if (dataSnapshot.child(Constants.userEmailReference).getValue() !=null){

                    val email:String = dataSnapshot.child(Constants.userEmailReference).value.toString()
                    userEmailTextView.setText(email)

                }

                if (dataSnapshot.child(Constants.userImageReference).getValue() !=null){

                    val imageUrl:String = dataSnapshot.child(Constants.userImageReference).value.toString()

                    Glide.with(activity!!).load(imageUrl).into(userImageView)

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }


        addressListener = object :  ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(Constants.homeAddressReference).exists()) {
                    if (dataSnapshot.child(Constants.homeAddressReference).getValue() != null) {

                        val homeAddress: String = dataSnapshot.child(Constants.homeAddressReference).child("homeAddressString").value.toString()
                        val homeAddressLat:String =  dataSnapshot.child(Constants.homeAddressReference).child("homeLatitude").value.toString()
                        val homeAddressLng:String =  dataSnapshot.child(Constants.homeAddressReference).child("homeLongitude").value.toString()
                        homeAdressTextView.setText(homeAddress)
                        homeLatitude = homeAddressLat.toDouble()
                        homeLongitude = homeAddressLng.toDouble()

                        Log.d("home",homeAddressLat+" "+homeAddressLng)

                    }
                }
                else{

                    homeAdressTextView.setText("No Address added.")

                }


                if(dataSnapshot.child(Constants.officeAddressReference).exists()) {
                    if (dataSnapshot.child(Constants.officeAddressReference).getValue() != null) {

                        val officeAddress: String = dataSnapshot.child(Constants.officeAddressReference).child("officeAddressString").value.toString()
                        val officeAddressLat:String =  dataSnapshot.child(Constants.officeAddressReference).child("officeLatitude").value.toString()
                        val officeAddressLng:String =  dataSnapshot.child(Constants.officeAddressReference).child("officeLongitude").value.toString()
                        officeAddressTextView.setText(officeAddress)
                        officeLatitude = officeAddressLat.toDouble()
                        officeLongitude = officeAddressLng.toDouble()

                        Log.d("office",officeAddressLat+" "+officeAddressLng)

                    }
                }
                else{

                    officeAddressTextView.setText("No Address added.")
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }



        }




    }




}
