package com.centosquare.devatease.gooapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.models.HomeAddressModel
import com.centosquare.devatease.gooapp.models.OfficeAddressModel
import com.centosquare.devatease.gooapp.models.LocationLatLng
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_trip_details.*
import java.util.*


class EditAddressFragment : Fragment(),View.OnClickListener {


    private lateinit var fragemntView:View
    private lateinit var toolbar: Toolbar
    private lateinit var addressTypeEditText:EditText
    private lateinit var btnAdd:Button
    private lateinit var addressType:String
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var addressReference: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private lateinit var userPreference: SharedManager
    private  var addressFromBundle:String? = null
    private lateinit var  etPlace:EditText
    private var addressLat:Double = 0.0
    private var addressLng:Double = 0.0
    private  var addressString:String? = null
    private lateinit var homeAddressModel: HomeAddressModel
    private lateinit var officeAddressModel: OfficeAddressModel
    private  var latitudeFromBundle:Double? = null
    private  var longitudeFromBundle:Double? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        fragemntView = inflater.inflate(R.layout.fragment_edit_address, container, false)

        addressType = this.arguments!!.getString("addressType")!!
        addressFromBundle = this.arguments?.getString("address")
        addressLat = this.arguments!!.getDouble("addressLat")
        addressLng = this.arguments!!.getDouble("addressLng")


        viewComponents()
        viewClickListners()
        initObjectsAndRefrences()

        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)

        toolbar.title  = "Edit Address"
        // for back button
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,AccountSettingsFragment()).commit()

        }

        addressTypeEditText.setText(addressType)
        addressTypeEditText.isEnabled = false





        //selection for current loc
        selectAddressFromFragment()



        return fragemntView
    }


    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        addressTypeEditText = fragemntView.findViewById(R.id.adress_type_editText)
        btnAdd = fragemntView.findViewById(R.id.btn_add_edit_address)

    }

    fun viewClickListners(){

        btnAdd.setOnClickListener(this)

    }


    override fun onClick(p0: View?) {

        when(p0!!.id){
            R.id.btn_add_edit_address ->{


                if (etPlace.text.toString().trim().isNotEmpty()){


                    val addressString:String = etPlace.text.toString().trim()

                    addAddressInFirebase(addressString)

                }

                else{


                    Toast.makeText(activity,"address cannot be empty", Toast.LENGTH_SHORT).show()
                }


              //  activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,AccountSettingsFragment()).commit()

            }

        }
    }

    private fun initObjectsAndRefrences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        addressReference = firebaseDatabase.getReference(Constants.userAddressReference)


    }

    private fun addAddressInFirebase(address: String) {

        if (addressType.equals("home"))
        {

            homeAddressModel = HomeAddressModel(address,addressLat,addressLng)

            addressReference.child(userId).child(Constants.homeAddressReference).setValue(homeAddressModel).addOnSuccessListener {

                Toast.makeText(activity,"address added", Toast.LENGTH_SHORT).show()

            }
                .addOnFailureListener {

                    Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()

                }


        }
        else if (addressType.equals("office")){

            officeAddressModel = OfficeAddressModel(address,addressLat,addressLng)

            addressReference.child(userId).child(Constants.officeAddressReference).setValue(officeAddressModel).addOnSuccessListener {



                Toast.makeText(activity,"address added", Toast.LENGTH_SHORT).show()

            }
                .addOnFailureListener {

                    Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()

                }



        }




    }

    private fun selectAddressFromFragment() {

        Places.initialize(context!!, "AIzaSyAJM1g2XzSmJn3zVbuSABmsDZG91-UL-HQ")
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_address) as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            Arrays.asList<Place.Field>(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        etPlace = autocompleteFragment.view?.findViewById(R.id.places_autocomplete_search_input) as EditText

        if (addressFromBundle != null){

            etPlace.setText(addressFromBundle)
        }

        etPlace.setHint("Enter Address")
        etPlace.setHintTextColor(resources.getColor(android.R.color.black))
        autocompleteFragment.view!!.setBackgroundColor(resources.getColor(R.color.background_curren_loc))
        // autocompleteFragment.setHint("Current LocationLatLng")
        autocompleteFragment.setCountry("PK")


        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener,
            com.google.android.gms.location.places.ui.PlaceSelectionListener {
            override fun onPlaceSelected(p0: com.google.android.gms.location.places.Place?) {
                //  Toast.makeText(applicationContext,p0!!.name,Toast.LENGTH_SHORT).show()

            }

            override fun onPlaceSelected(p0: Place) {

                val latLng = p0.latLng
                addressLat = latLng!!.latitude
                addressLng= latLng.longitude
                addressString = p0.address
                etPlace.setText(addressString)
            }



            override fun onError(status: Status) {
                //  txtVw.text = status.toString()
            }
        })



    }




}
