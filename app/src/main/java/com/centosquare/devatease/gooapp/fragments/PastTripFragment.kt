package com.centosquare.devatease.gooapp.fragments

import PastTripAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.models.UserPastRides
import com.centosquare.devatease.gooapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class PastTripFragment : Fragment()  {

    private lateinit var fragemntView: View
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerview:RecyclerView
    private lateinit var pastTripAdapter:PastTripAdapter
    private var userPastRidesRef:DatabaseReference? = null
    private lateinit var userId:String
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var userPastRidesEventListner:ValueEventListener


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_past_trip, container, false)
        // initialize all view components
        viewComponents()
        firebaseObjReferences()
        initPastTripAdapter()
        // for toolbar name

        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_past_trip)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()

        }


        return fragemntView
    }

    private fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        recyclerview = fragemntView.findViewById(R.id.recyclerview_past_trip)

    }
    private fun firebaseObjReferences(){

        firebaseDatabase = FirebaseDatabase.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        userPastRidesRef = firebaseDatabase.reference.child(Constants.userPastRidesReference).child(userId)


    }
    private fun initPastTripAdapter(){

        val layoutManager = LinearLayoutManager(context)
        recyclerview.setLayoutManager(layoutManager)

        // getting past trip data from firebase
        getUserPastTrips()
        // setting listner for past rides
        userPastRidesRef!!.addListenerForSingleValueEvent(userPastRidesEventListner)


    }
    private fun getUserPastTrips(){


        userPastRidesEventListner = object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val pastTripList:ArrayList<UserPastRides> = ArrayList()
                if (dataSnapshot.hasChildren()){

                    for (i in dataSnapshot.children){

                        val userPastRides = i.getValue(UserPastRides::class.java)
                        pastTripList.add(userPastRides!!)


                    }

                    // passing list to adapter
                    pastTripAdapter = PastTripAdapter(context!!,pastTripList)
                    recyclerview.setAdapter(pastTripAdapter)

                }

            }


        }



    }


}