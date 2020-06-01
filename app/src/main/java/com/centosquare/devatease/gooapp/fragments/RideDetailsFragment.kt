package com.centosquare.devatease.gooapp.fragments
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class RideDetailsFragment : Fragment(),View.OnClickListener {


    private lateinit var v:View
    private lateinit var fragemntView:View
    private lateinit var toolbar: Toolbar
    private lateinit var btnCancel:Button
    private lateinit var callToRiderImgview:ImageView
    private lateinit var timeRiderAwayTxtview: TextView
    private lateinit var pickupAddressTxtview:TextView
    private lateinit var dropoffAddressTxtview:TextView
    private lateinit var tripAmountTxtview:TextView
    private lateinit var driverFoundId:String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userId:String
    internal lateinit var firebaseDatabase: FirebaseDatabase
    internal lateinit var riderNameTxtview:TextView
    internal lateinit var riderNoTxtview:TextView
    internal lateinit var riderVehicleNoTxtview:TextView
    internal lateinit var driverNo:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        fragemntView = inflater.inflate(R.layout.fragment_ride_details, container, false)

        viewComponents()
        viewClickListners()
        getRidersDetails()



        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)

        toolbar.title  = "Ride Details"

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)


        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()

        }


        return fragemntView
    }

    fun viewComponents(){

        btnCancel = fragemntView.findViewById(R.id.cancel_ride_btn)
        toolbar = fragemntView.findViewById(R.id.toolbar)
        callToRiderImgview = fragemntView.findViewById(R.id.contact_rider_img)
        pickupAddressTxtview = fragemntView.findViewById(R.id.pickup_location_address)
        dropoffAddressTxtview = fragemntView.findViewById(R.id.destination_address)
        tripAmountTxtview = fragemntView.findViewById(R.id.trip_amount_txt)
        timeRiderAwayTxtview = fragemntView.findViewById(R.id.min_away_txt)
        riderNameTxtview = fragemntView.findViewById(R.id.rider_name)
        riderVehicleNoTxtview = fragemntView.findViewById(R.id.vehicle_details_txt)




    }

    fun viewClickListners(){

        callToRiderImgview.setOnClickListener(this)
        btnCancel.setOnClickListener(this)


    }
    override fun onClick(p0: View?) {

        when(p0!!.id){

            R.id.cancel_ride_btn -> {

                initObjectsAndRefrences()
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,MapFragment()).commit()
            }

            R.id.contact_rider_img ->{

                val intentDialer = Intent(Intent.ACTION_DIAL)
                intentDialer.setData(Uri.parse("tel:"+driverNo))
                startActivity(intentDialer)

            }

        }
    }

    fun getRidersDetails(){

        if (arguments != null){

            pickupAddressTxtview.text  = this.arguments!!.getString("pickupAddress").toString()
            dropoffAddressTxtview.text = this.arguments!!.getString("destinationAddress").toString()
            tripAmountTxtview.text = this.arguments!!.getString("price").toString()
            timeRiderAwayTxtview.text = this.arguments!!.getString("timeAway")
            riderNameTxtview.text = this.arguments!!.getString("driverName").toString()
            riderVehicleNoTxtview.text = this.arguments!!.getString("driverVehicleNo").toString()
            driverFoundId = this.arguments!!.getString("driverfoundid").toString()
            driverNo = this.arguments!!.getString("driveNo")!!

        }

    }
    private fun initObjectsAndRefrences(){


        // setting ref of firebase
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        userId = firebaseUser.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference(Constants.rideLocation).child(userId).removeValue()
        firebaseDatabase.getReference(Constants.ride).child(userId).removeValue()
        firebaseDatabase.getReference(Constants.requestReference).child(userId).removeValue()
        firebaseDatabase.getReference(Constants.assignUsersReference).child(Constants.assignDriversReference).child(driverFoundId).removeValue()



    }




}
