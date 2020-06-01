package com.centosquare.devatease.gooapp.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.models.LocationLatLng
import com.centosquare.devatease.gooapp.utils.Constants
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList


class ChooseDestinationFragment:Fragment(),View.OnClickListener {
   /* override fun onBackPressed():Boolean {



        return false
    }*/


    private lateinit var fragemntView: View
    private lateinit var toolbar:Toolbar
    private lateinit var btnConfirm:Button
    private lateinit var btnCancel:Button
    private lateinit var  etPlace:EditText
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var locationRef: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private lateinit var locationLatLng:LocationLatLng
    private var currentLat:Double = 0.0
    private var currentLng:Double = 0.0
    private var destLat:Double = 0.0
    private var destLng:Double = 0.0
    private var bikeRatePerKm:Int = 10
    private var autoRatePerKm:Int = 15
    private var carRatePerKm:Int = 20
    private  var rateList:ArrayList<Int> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_choose_destination, container, false)

        viewComponents()
        viewClickListeners()
        initObjectsAndRefrences()

        // for toolbar name
        var activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_where_to)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)


        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

        }

        //selection for current loc
        selectCurrentLoc()
        // selection for destination loc
        selectDestinationLoc()


       /*  fragemntView.setFocusableInTouchMode(true);
        fragemntView.requestFocus();
        fragemntView.setOnKeyListener( object : View.OnKeyListener{
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {

                if(p1 == KeyEvent.KEYCODE_BACK )
                {
""
                    Toast.makeText(context,"onBack",Toast.LENGTH_SHORT).show()

                    activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()


                }
                return false;
            }


        })
*/


        return fragemntView
    }

    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        btnConfirm = fragemntView.findViewById(R.id.btn_confirm_destination)
        btnCancel = fragemntView.findViewById(R.id.btn_cancel_destination)

    }

    fun viewClickListeners(){

        btnConfirm.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

            R.id.btn_confirm_destination -> {

                if (currentLat == 0.0 || currentLng == 0.0){

                    Toast.makeText(context,"Please select current location",Toast.LENGTH_SHORT).show()
                }else if(destLat == 0.0 || destLng == 0.0){

                    Toast.makeText(context,"Please select destination location",Toast.LENGTH_SHORT).show()
                }else {

                    val distance = getKmFromLatLong(currentLat,currentLng,destLat,destLng)
                    for (i in 0..2){


                        when(i){
                            0->{
                                bikeRatePerKm = distance.toInt() * bikeRatePerKm
                                rateList.add(bikeRatePerKm)
                            }
                            1-> {

                                autoRatePerKm = distance.toInt() * autoRatePerKm
                                rateList.add(autoRatePerKm)

                            }
                            2-> {

                                carRatePerKm = distance.toInt() * carRatePerKm
                                rateList.add(carRatePerKm)
                            }

                        }


                    }

                    val transaction =  activity!!.supportFragmentManager.beginTransaction()
                    val bundle = Bundle()


                    bundle.putIntegerArrayList("rateList",rateList)
                    val fragInfo = ChooseRideFragment()
                    fragInfo.setArguments(bundle)
                    transaction.replace(R.id.frame_layout, fragInfo)
                    transaction.commit()

//                         activity!!.supportFragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, ChooseRideFragment()).addToBackStack(null).commit()

                }

            }
            R.id.btn_cancel_destination -> {

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

            }


        }

    }

    fun selectCurrentLoc(){

        Places.initialize(context!!, "AIzaSyAJM1g2XzSmJn3zVbuSABmsDZG91-UL-HQ")
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_current) as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            Arrays.asList<Place.Field>(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        etPlace = autocompleteFragment.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
        etPlace.setHint("Current Location")
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
                currentLat = latLng!!.latitude
                currentLng = latLng.longitude
                locationLatLng = LocationLatLng(currentLat,currentLng,p0.name!!)
                locationRef.child(userId).child(Constants.current).setValue(locationLatLng)

            }






            override fun onError(status: Status) {
                //  txtVw.text = status.toString()
            }
        })

    }


    fun selectDestinationLoc(){

        Places.initialize(context!!, "AIzaSyAJM1g2XzSmJn3zVbuSABmsDZG91-UL-HQ")
        // val fragmentmanger = activity!!.supportFragmentManager
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_destination) as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            Arrays.asList<Place.Field>(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        etPlace = autocompleteFragment.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
        etPlace.setHint("Destination Location")
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

                destLat = latLng!!.latitude
                destLng = latLng.longitude
                locationLatLng = LocationLatLng(destLat,destLng,p0.name!!)
                locationRef.child(userId).child(Constants.destination).setValue(locationLatLng)


            }


            override fun onError(status: Status) {
                //  txtVw.text = status.toString()
            }
        })


    }

    private fun initObjectsAndRefrences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        locationRef = firebaseDatabase.getReference(Constants.rideLocation)
      //  destinationLocationRef = firebaseDatabase.getReference(Constants.rideLocation)


    }

    fun getKmFromLatLong(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val loc1 = Location("")
        loc1.setLatitude(lat1)
        loc1.setLongitude(lng1)
        val loc2 = Location("")
        loc2.setLatitude(lat2)
        loc2.setLongitude(lng2)
        val distanceInMeters = loc1.distanceTo(loc2)
        val distanceInKm  = distanceInMeters / 1000

        return distanceInKm.toDouble()
    }


}

