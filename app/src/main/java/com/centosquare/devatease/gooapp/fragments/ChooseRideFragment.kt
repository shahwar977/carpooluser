package com.centosquare.devatease.gooapp.fragments

import ChooseRideAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ButtonBarLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.models.LocationLatLng
import com.centosquare.devatease.gooapp.utils.Constants
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.centosquare.devatease.gooapp.interfaces.RecyclerViewClickListener


class ChooseRideFragment:Fragment(),View.OnClickListener,RecyclerViewClickListener {

    private lateinit var fragemntView:View
    private lateinit var toolbar: Toolbar
    private lateinit var btnConfirm:Button
    private lateinit var recyclerviewRide:RecyclerView
    private lateinit var chooseRideAdapter:ChooseRideAdapter
    private lateinit var rideList:ArrayList<String>
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var rideRef: DatabaseReference
    private lateinit var locationCurrenRef: DatabaseReference
    private lateinit var locationDestinationRef: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private lateinit var locationLatLng: LocationLatLng
    private lateinit var rideCurrentLocEventListner: ValueEventListener
    private lateinit var rideDestinationLocEventListner: ValueEventListener
    private lateinit var rateList:ArrayList<Int>
    private lateinit var selectedRideType:String
    private lateinit var pickupAddress:String
    private lateinit var destinationAddress:String
    private  var ridePrice:Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        fragemntView = inflater.inflate(R.layout.fragment_choose_ride, container, false)


        // getting rate list of vehicle
        rateList = this.arguments!!.getIntegerArrayList("rateList") as ArrayList<Int>
        // initialize all view components
        viewComponents()
        viewClickListners()
        initObjectsAndRefrences()


        // for toolbar name
        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_choose_ride)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

        }



        return fragemntView
    }
    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        btnConfirm = fragemntView.findViewById(R.id.btn_confirm_ride)
        recyclerviewRide = fragemntView.findViewById(R.id.recyclerview_choose_ride)
        val layoutManager = LinearLayoutManager(context)
        recyclerviewRide.setLayoutManager(layoutManager)
        //insertingListData()

        chooseRideAdapter = ChooseRideAdapter(context!!,rateList,this)
        recyclerviewRide.setAdapter(chooseRideAdapter)




    }

    fun viewClickListners(){

        btnConfirm.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

            R.id.btn_confirm_ride -> {



                firebaseListeners()
                locationCurrenRef.addListenerForSingleValueEvent(rideCurrentLocEventListner)
                locationDestinationRef.addListenerForSingleValueEvent(rideDestinationLocEventListner)



              //  activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

            }


        }

    }

    private fun initObjectsAndRefrences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        rideRef = firebaseDatabase.getReference(Constants.ride)
        locationCurrenRef = firebaseDatabase.getReference(Constants.rideLocation).child(userId).child(Constants.current)
        locationDestinationRef = firebaseDatabase.getReference(Constants.rideLocation).child(userId).child(Constants.destination)
        //  destinationLocationRef = firebaseDatabase.getReference(Constants.rideLocation)


    }

    fun firebaseListeners() {

        rideCurrentLocEventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    val latitude:Double? = dataSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude:Double? = dataSnapshot.child("longitude").getValue(Double::class.java)
                    pickupAddress = dataSnapshot.child("address").getValue(String::class.java)!!

                    locationLatLng = LocationLatLng(latitude,longitude,pickupAddress)
                    rideRef.child(userId).child(Constants.current).setValue(locationLatLng)



                }else{

                    Log.i("novalue","novalue")
                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

                Toast.makeText(context,"currentLoc listner cancelled",Toast.LENGTH_SHORT).show()
            }
        }

        rideDestinationLocEventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    val latitude:Double? = dataSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude:Double? = dataSnapshot.child("longitude").getValue(Double::class.java)
                    destinationAddress = dataSnapshot.child("address").getValue(String::class.java)!!

                    locationLatLng = LocationLatLng(latitude,longitude,destinationAddress)
                    rideRef.child(userId).child(Constants.destination).setValue(locationLatLng)

                    transactionToMapFragment()



                }else{

                    Log.i("novalue","novalue")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Toast.makeText(context,"detinationLoc listner cancelled",Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun recyclerViewListClicked(rideType: String,price:Int) {

        selectedRideType = rideType
        ridePrice = price


    }

    fun transactionToMapFragment(){

        val transaction =  activity!!.supportFragmentManager.beginTransaction()
        val bundle = Bundle()

        bundle.putString("rideType",selectedRideType)
        bundle.putString("pickupAddress",pickupAddress)
        bundle.putString("destinationAddress",destinationAddress)
        bundle.putString("price",ridePrice.toString())
        val fragInfo = MapFragment()
        fragInfo.setArguments(bundle)
        transaction.replace(R.id.frame_layout, fragInfo)
        transaction.commit()

    }





}