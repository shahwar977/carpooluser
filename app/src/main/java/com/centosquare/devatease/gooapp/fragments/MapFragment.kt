package com.centosquare.devatease.gooapp.fragments

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.activities.LoginActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.centosquare.devatease.gooapp.interfaces.TaskLoadedCallback
import com.centosquare.devatease.gooapp.interfaces.TaskLoadedDurationCallback
import com.centosquare.devatease.gooapp.models.*
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.FetchURL
import com.firebase.geofire.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class MapFragment:Fragment(),NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,View.OnClickListener,TaskLoadedCallback,TaskLoadedDurationCallback {

    private lateinit var fragemntView:View
    private lateinit var mMap: GoogleMap
    private lateinit var txtvChooseDestination: TextView
    private lateinit var imageButton: ImageButton
    private lateinit var  drawerLayout: DrawerLayout
    private lateinit var layoutConfirmPickup:ConstraintLayout
    private lateinit var layoutWhereTo:ConstraintLayout
    private lateinit var layoutRideAccepted:ConstraintLayout
    private lateinit var cardviewConfirmPickup:CardView
    private lateinit var cardviewWhereTo:CardView
    private lateinit var imgvProfile:ImageView
    private lateinit var btnSearchConfirm:Button
    private lateinit var btnConfirmPickup:Button
    private lateinit var btnLegal:TextView
    private lateinit var userNameTextView:TextView
    private lateinit var userRatingTextView:TextView
    private lateinit var profileInfoReference: DatabaseReference
    private lateinit var profileListener: ValueEventListener
    private lateinit var sharedPreferences:SharedPreferences
    private var driversAvailableRef: DatabaseReference? = null
    private var driversWorkingRef: DatabaseReference? = null
    private var requestsRef: DatabaseReference? = null
    private var driverResponse:DatabaseReference? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var gfDriversAvailable:GeoFire
    private lateinit var gfDriversWorking:GeoFire
    private lateinit var gfRequests:GeoFire
    private lateinit var  markerOptions:MarkerOptions
    private lateinit var rideType:String
    private lateinit var welcomeTxtLbl:TextView
    private lateinit var  vAnimator:ValueAnimator
    private lateinit var circle:Circle
    private  var radiusForSearching:Double = 10.0
    private var geoQuery: GeoQuery? = null
    private var driverfoundid: String? = null
    private lateinit var rideCurrentLocEventListner: ValueEventListener
    private lateinit var rideDestinationLocEventListner: ValueEventListener
    private lateinit var pickupLocationRef: DatabaseReference
    private lateinit var currentRidePickupDatabaseRef: DatabaseReference
    private lateinit var currentRideCurrentLocEventListner: ValueEventListener
    private lateinit var destinationLocationRef: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private var pickupLat:Double = 0.0
    private var pickupLng:Double = 0.0
    private var destinationLat:Double = 0.0
    private var destinationLng:Double = 0.0
    private lateinit var acceptResponselistener: ValueEventListener
    private lateinit var arriveResponselistener: ValueEventListener
    private  var destinationLocationMarker:MarkerOptions? = null
    private lateinit var pickupLocationMarker:MarkerOptions
    private lateinit var driverFoundLocationMarker:MarkerOptions
    private lateinit var pickupLatLng:LatLng
    private lateinit var destinationLatLng:LatLng
    private lateinit var driverFoundLatLng:LatLng
    private var currentPolyline:Polyline? = null
    private var driverLat:Double = 0.0
    private var driverLng:Double = 0.0
    private lateinit var  timeDriverAway:TextView
    private lateinit var imgviewPhone:ImageView
    private lateinit var pickupAddress:String
    private lateinit var destinationAddress:String
    private lateinit var ridePrice:String
    private lateinit var ridelistener: ValueEventListener
    private var rideDatabaseRef:DatabaseReference? = null
    private lateinit var driverDetailsEventListner:ValueEventListener
    private lateinit var driverDetailsRef:DatabaseReference
    private  var driverName:String? = null
    private lateinit var driverNo:String
    private lateinit var driverVehicleNo:String
    private lateinit var fireBaseAuth:FirebaseAuth
    private var userPastRidesRef:DatabaseReference? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_drawer_main, container, false)

        viewComponents()
        clickListners()

       val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync(this)

        // initialize shared preferences
        sharedPreferences = context!!.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)

        initObjectsAndReferencesProfile()
        firebaseListenersProfile()

        profileInfoReference.addListenerForSingleValueEvent(profileListener)


        firebaseDatabase = FirebaseDatabase.getInstance()
        // getting curent user id
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid

        // setting geofire for working drivers
        driversWorkingRef =   firebaseDatabase.getReference(Constants.driversWorkingReference)
        gfDriversWorking = GeoFire(driversWorkingRef)

        return fragemntView
    }


    fun viewComponents(){
        imageButton = fragemntView.findViewById(R.id.drawerbtn)
        txtvChooseDestination = fragemntView.findViewById(R.id.tv_map_where_to);
        drawerLayout = fragemntView.findViewById(R.id.drawer_layout)
        cardviewWhereTo = fragemntView.findViewById(R.id.cardview_where_to)
        cardviewConfirmPickup = fragemntView.findViewById(R.id.cardview_confirm_pickup)
        layoutWhereTo = fragemntView.findViewById(R.id.layout_where_to)
        layoutConfirmPickup = fragemntView.findViewById(R.id.layout_confirm_pickup)
        layoutRideAccepted = fragemntView.findViewById(R.id.layout_ride_accepted)
        btnLegal = fragemntView.findViewById(R.id.nav_legal)
        btnSearchConfirm = fragemntView.findViewById(R.id.btn_search_confirm)
        btnConfirmPickup=fragemntView.findViewById(R.id.btn_confirm_pickup)
        welcomeTxtLbl = fragemntView.findViewById(R.id.tv_map_welcome_text)
        timeDriverAway = fragemntView.findViewById(R.id.tv_time_driver_away)
        imgviewPhone = fragemntView.findViewById(R.id.imgv_phone_driver)


        val navView: NavigationView =  fragemntView.findViewById(R.id.nav_view)

        var header:View = navView.getHeaderView(0)
        imgvProfile =  header.findViewById(R.id.nav_profile_img)
        userNameTextView = header.findViewById(R.id.nav_tv_profile_name)
        userRatingTextView = header.findViewById(R.id.nav_tv_rating)


        navView.setNavigationItemSelectedListener(this)

    }

    fun clickListners(){

        btnSearchConfirm.setOnClickListener(this)
        btnConfirmPickup.setOnClickListener(this)
        layoutRideAccepted.setOnClickListener(this)
        txtvChooseDestination.setOnClickListener(this)
        imageButton.setOnClickListener(this)
        btnLegal.setOnClickListener(this)
        imgvProfile.setOnClickListener(this)
        welcomeTxtLbl.setOnClickListener(this)
        imgviewPhone.setOnClickListener(this)

    }
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        val drawerLayout: DrawerLayout = fragemntView.findViewById(R.id.drawer_layout)

        when (p0.itemId) {
            R.id.nav_your_trip -> {
                // Handle the camera action

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,PastTripFragment()).addToBackStack(null).commit()


            }


            R.id.nav_logout -> {

                signOut()
                startActivity(Intent(context,LoginActivity::class.java))
                (context as AppCompatActivity).finish()

            }
            R.id.nav_settings -> {

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,AccountSettingsFragment()).addToBackStack(null).commit()


            }



        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!

        // when user comes from ride selecteion fragment
        if (arguments != null){

            // ride type  eg car,bike and auto
            rideType = this.arguments!!.getString("rideType").toString()
            pickupAddress = this.arguments!!.getString("pickupAddress").toString()
            destinationAddress = this.arguments!!.getString("destinationAddress").toString()
            ridePrice = this.arguments!!.getString("price").toString()


            // if ride is empty
            if (!rideType.equals("")){


                // if user selected ride type then it initialize the driversavailable on geofire
                driversAvailableRef = firebaseDatabase.getReference(Constants.driversAvailableReference).child(rideType)
                gfDriversAvailable = GeoFire(driversAvailableRef)


                layoutWhereTo.visibility = View.GONE
                cardviewWhereTo.visibility = View.GONE
                layoutRideAccepted.visibility = View.VISIBLE

                initObjectsAndRefrences()
                firebaseListeners()

                // setting location listner for getting current location
                pickupLocationRef.addListenerForSingleValueEvent(rideCurrentLocEventListner)
                // setting location listner for getting destination location
                destinationLocationRef.addListenerForSingleValueEvent(rideDestinationLocEventListner)






            }
        }else{


            // if ride is in progress, and user goes to background and come back to app it will check ride node for current user and getting ride data
            currentRidePickupDatabaseRef = firebaseDatabase.getReference(Constants.ride).child(userId)
            currentRideFirebaseListeners()
            currentRidePickupDatabaseRef.addListenerForSingleValueEvent(currentRideCurrentLocEventListner)

            imgviewPhone.visibility = View.VISIBLE
        }

    }
    override fun onClick(p0: View?) {

        when (p0!!.id) {
            R.id.drawerbtn -> {

                drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.tv_map_where_to -> {

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,ChooseDestinationFragment()).addToBackStack(null).commit()
            }
            R.id.btn_search_confirm -> {

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,ChooseDestinationFragment()).addToBackStack(null).commit()
            }
           /* R.id.btn_confirm_pickup -> {

                cardviewConfirmPickup.visibility = View.GONE
                layoutConfirmPickup.visibility = View.GONE
                layoutRideAccepted.visibility = View.GONE

                val ratingDialog = RatingDialog(activity!!)
                ratingDialog.setCancelable(true)
                ratingDialog.show()

                val back = ColorDrawable(Color.WHITE)
                val inset = InsetDrawable(back, 90)
                val window = ratingDialog.getWindow()!!
                window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                window.setBackgroundDrawable(inset)


                //  activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,ChooseDestinationFragment()).commit()
            }
*/
            R.id.layout_ride_accepted -> {

               /* if (driverName != null)
                {*/
                    transactionToRideDetailsFragment()
             //   }


            }
            R.id.nav_profile_img -> {

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,EditAccountFragment()).addToBackStack(null).commit()
            }

            R.id.nav_legal -> {

                activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,LegalFragment()).addToBackStack(null).commit()


            }
            R.id.tv_map_welcome_text -> {

            drawRouteFromPickupToDestination()

        }
            R.id.imgv_phone_driver -> {

                // open caller intent with given driver's number
                val intentDialer = Intent(Intent.ACTION_DIAL)
                intentDialer.setData(Uri.parse("tel:"+driverNo))
                startActivity(intentDialer)

            }

        }
    }
    private fun getDriversOnMap(){

        val driverAvailableList = ArrayList<GeoLocation>()

        // setting geofire for available drivers with user
        val gq = gfDriversAvailable.queryAtLocation(GeoLocation(pickupLat,pickupLng),radiusForSearching)

        gq.addGeoQueryEventListener(object : GeoQueryEventListener{

            override fun onGeoQueryReady() {

                // when all drivers found, all availabe driver's marker on map
                drawMarker(driverAvailableList)

            }
            override fun onKeyEntered(key: String, location: GeoLocation) {

                // adding available drivers in list
                driverAvailableList.add(location)


            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {

              //  Toast.makeText(context, "KEy Moved", Toast.LENGTH_SHORT).show()

            }

            override fun onKeyExited(key: String?) {

             //   Toast.makeText(context, "KEy Exited", Toast.LENGTH_SHORT).show()

            }

            override fun onGeoQueryError(error: DatabaseError?) {

            }


        })

    }
    private fun drawMarker(driversAvailableList:ArrayList<GeoLocation>){

        var icon:BitmapDescriptor? = null
        // if ride type car icon will be car
        if (rideType.equals("Car")){

            icon = BitmapDescriptorFactory.fromResource(R.drawable.car)
        }

        // setting marker on map for all available drivers
        for (i in driversAvailableList){

            markerOptions =  MarkerOptions()
            // setting marker on availabe drivers with their ridetype icon
            markerOptions.position(LatLng(i.latitude,i.longitude)).title("Available driver").icon(icon)
            mMap.addMarker(markerOptions)
        }

    }
    private fun drawMarkerPickupLocation(){

        var icon:BitmapDescriptor? = null
        // if ride type car icon will be car
        if (rideType.equals("Car")){

            icon = BitmapDescriptorFactory.fromResource(R.drawable.car)
        }

        // setting marker on map for all available drivers
            markerOptions =  MarkerOptions()
        //
            markerOptions.position(LatLng(pickupLat,pickupLng)).title("Driver").icon(icon)

            mMap.addMarker(markerOptions)


    }
    private fun searchingRider(){

        // setting requester's location to geofire for finding the nearest driver
        requestToDriver()
        // making circle for searching nearest driver with 500 radius
        circle = mMap.addCircle(CircleOptions().center(LatLng(pickupLat,pickupLng)).strokeColor(Color.YELLOW).radius(500.0))

        vAnimator = ValueAnimator()
        vAnimator.setRepeatCount(ValueAnimator.INFINITE)
        vAnimator.setRepeatMode(ValueAnimator.RESTART)  /* PULSE */
        vAnimator.setIntValues(0, 100)
        vAnimator.setDuration(2000)
        vAnimator.setEvaluator(IntEvaluator())
        vAnimator.setInterpolator(AccelerateDecelerateInterpolator())

        vAnimator.addUpdateListener(object :ValueAnimator.AnimatorUpdateListener{
            override fun onAnimationUpdate(p0: ValueAnimator?) {
                val animatedFraction = p0!!.getAnimatedFraction()

                circle.setRadius((animatedFraction * 500).toDouble())

            }


        })

        vAnimator.start()

    }
    private fun requestToDriver(){

        val uid = FirebaseAuth.getInstance().uid
        // setting requester id
        requestsRef = firebaseDatabase.getReference(Constants.requestReference)
        gfRequests = GeoFire(requestsRef)
        gfRequests.setLocation(uid,GeoLocation(pickupLat,pickupLng))
        // finding nearest driver
        getClosestDriver()

    }
    private fun getClosestDriver(){

        val driversList = ArrayList<NearestDriverModel>()

        //Geoquery search for available drivers with in given radius
        geoQuery = gfDriversAvailable.queryAtLocation(GeoLocation(pickupLat,pickupLng), 5.0)

        // removing all geofire listners
        geoQuery!!.removeAllListeners()
        geoQuery!!.addGeoQueryEventListener(object :GeoQueryEventListener{
            override fun onGeoQueryReady() {

                // assinging driver to requester
                assigndriver(driversList)



            }

            override fun onKeyEntered(key: String?, location: GeoLocation?) {

                // finding nearest driver for requester's location
                val nearestDriver = NearestDriverModel(key,location!!.latitude,location.longitude)
                driversList.add(nearestDriver)


            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {

            }

            override fun onKeyExited(key: String?) {

            }

            override fun onGeoQueryError(error: DatabaseError?) {



            }


        })

    }
    private fun assigndriver(list: ArrayList<NearestDriverModel>){


       // if list is empty so no driver found
       if(list.isEmpty()){
           Toast.makeText(context, "No Driver Found", Toast.LENGTH_LONG).show()
           removeCircle()

           layoutWhereTo.visibility = View.VISIBLE
           cardviewWhereTo.visibility = View.VISIBLE
           layoutRideAccepted.visibility = View.GONE
           firebaseDatabase.getReference(Constants.ride).child(userId).removeValue()

       }else{

           // getting top item from list for getting nearest driver
           driverfoundid = list.get(0).key
           driverLat = list.get(0).lat!!
           driverLng = list.get(0).lng!!

           FirebaseDatabase.getInstance().reference.child("Users").child("drivers")
               .child(driverfoundid!!).setValue("request")
           // if driver accept request then requester assign to driver.
           acceptResponselistener = driverResponse!!.child(driverfoundid!!).addValueEventListener(object : ValueEventListener {
               override fun onDataChange(dataSnapshot: DataSnapshot) {

                   if (dataSnapshot.exists()) {

                       val data = dataSnapshot.value!!.toString()

                       if (data == "accept") {

                           // adding accepted driver location updated on ride node
                           updateDriverLocationToRide(list)
                           imgviewPhone.visibility = View.VISIBLE

                           try {
                               Toast.makeText(context, "Driver has been found", Toast.LENGTH_SHORT).show()

                           }catch (e:Exception){

                              Log.i("exception","exception in context")
                           }

                           // driver found it will remove searching circle
                           removeCircle()
                           // making user assign to driver in firebase with their id
                           val assignDriverRef = FirebaseDatabase.getInstance().reference.child("Users").child("drivers")
                               .child(driverfoundid!!)
                           val map = HashMap<String,String>()
                           map.put("customerid", userId)
                           map.put("request", "request")
                           assignDriverRef.setValue(map as Map<String, Any>)

                           // available driver move to working driver
                             moveDriverfromAvailabletoWorking()
                           //  remove from available driver
                             driverResponse!!.removeEventListener(acceptResponselistener)


                       }else if(data == "cancel"){

                           // if cancel driver so it remove item from list and reassign list
                           driverResponse!!.removeEventListener(acceptResponselistener)

                               list.removeAt(0)

                           if (list.isEmpty()){

                               try {
                                   Toast.makeText(context, "No Driver Found", Toast.LENGTH_LONG).show()

                               }catch (e:java.lang.Exception){

                                   Log.i("exception","Toast context")
                               }

                               removeCircle()
                               layoutWhereTo.visibility = View.VISIBLE
                               cardviewWhereTo.visibility = View.VISIBLE
                               layoutRideAccepted.visibility = View.GONE
                               firebaseDatabase.getReference(Constants.ride).child(userId).removeValue()

                           }else{

                               assigndriver(list)

                           }




                       }
                   }


               }

               override fun onCancelled(databaseError: DatabaseError) {

                   Log.i("assingDriver",databaseError.message)

               }
           })


       }



    }
    private fun userPastRides(){

        // getting user past rides
        userPastRidesRef = firebaseDatabase.reference.child(Constants.userPastRidesReference).child(userId)
        val userPastRides = UserPastRides(driverName,ridePrice,rideType,driverVehicleNo)
        // each past rides has unique id
        userPastRidesRef!!.child(UUID.randomUUID().toString()).setValue(userPastRides)

    }
    private fun getCheckingArriveStatus(){

        // checking driver response
        driverResponse = firebaseDatabase.reference.child("response")
        arriveResponselistener = driverResponse!!.child(driverfoundid!!).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.i("driverResponse",p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    val data = dataSnapshot.value!!.toString()

                    if (data == "arrived") {
                        // it will amke polyline from pickup to dropoff
                        drawRouteFromPickupToDestination()

                    }else if(data == "complete"){

                        // current ride move to past ride
                        userPastRides()
                        // remove driver's response listner
                        driverResponse!!.removeEventListener(arriveResponselistener)

                        // remove all current database references
                        removeCurrentRideReference()
                        mMap.clear()

                        layoutWhereTo.visibility = View.VISIBLE
                        cardviewWhereTo.visibility = View.VISIBLE
                        layoutRideAccepted.visibility = View.GONE


                    }
                }

            }


        })

    }
    private fun moveDriverfromAvailabletoWorking(){

        // availabe driver move to working
        gfDriversAvailable.getLocation(driverfoundid,object : LocationCallback{
            override fun onLocationResult(key: String?, location: GeoLocation?) {

                // setting available driver to working
                gfDriversWorking.setLocation(key, location)
                // remove from available driver
                gfDriversAvailable.removeLocation(driverfoundid)

                // draw rpolyline from pickup to driver's location
                drawRouteFromPickupToDriverLocation()

                // getting driver's details
                getDriverDetails()
                driverDetailsRef = firebaseDatabase.reference.child(Constants.driverDetailsReference)
                driverDetailsRef.addListenerForSingleValueEvent(driverDetailsEventListner)




            }

            override fun onCancelled(databaseError: DatabaseError?) {

                Toast.makeText(context,"Error for removing accpeted driver location",Toast.LENGTH_SHORT).show()
            }


        })

    }
    private fun getDriverDetails(){

        driverDetailsEventListner = object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
                Log.i("driverDetails",p0.message)
            }
            override fun onDataChange(dataSnapshot:DataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    val driverDetailsModel = dataSnapshot.child(driverfoundid!!).getValue(DriverDetailsModel::class.java)
                    driverName = driverDetailsModel!!.name.toString()

                    driverNo = driverDetailsModel.number!!
                    driverVehicleNo = driverDetailsModel.vehicleNo!!

                }

            }

        }


    }
    private fun removeCircle(){

        // removing searching ride circle
        vAnimator.cancel()
        vAnimator.removeAllUpdateListeners()
        vAnimator.removeAllListeners()
        circle.remove()
    }
    private fun firebaseListeners() {

        rideCurrentLocEventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    // getting current lat and lng
                     pickupLat = dataSnapshot.child("latitude").getValue(Double::class.java)!!
                     pickupLng = dataSnapshot.child("longitude").getValue(Double::class.java)!!
                     mMap.clear()
                     getDriversOnMap()
                     val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(pickupLat,pickupLng),15f)
                     mMap.animateCamera(cameraUpdate)
                    //searching driver for requester
                    searchingRider()

                }else{

                    Log.i("novalue","novalue")
                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

                Toast.makeText(context,"pickup listner cancelled",Toast.LENGTH_SHORT).show()
            }
        }

        rideDestinationLocEventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    // getting destination lat and lng
                    destinationLat = dataSnapshot.child("latitude").getValue(Double::class.java)!!
                    destinationLng = dataSnapshot.child("longitude").getValue(Double::class.java)!!



                }else{

                    Log.i("novalue","novalue")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Toast.makeText(context,"detinationLoc listner cancelled",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun initObjectsAndRefrences(){

        // setting ref of firebase
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        driverResponse = firebaseDatabase.reference.child(Constants.driverResponseReference)
      //  rideRef = firebaseDatabase.getReference(Constants.ride)
        pickupLocationRef = firebaseDatabase.getReference(Constants.rideLocation).child(userId).child(Constants.current)
        destinationLocationRef = firebaseDatabase.getReference(Constants.rideLocation).child(userId).child(Constants.destination)
        //  destinationLocationRef = firebaseDatabase.getReference(Constants.rideLocation)


    }
    private fun drawRouteFromPickupToDestination(){

        pickupLocationMarker = MarkerOptions()
        destinationLocationMarker = MarkerOptions()
        pickupLatLng = LatLng(pickupLat,pickupLng)
        destinationLatLng = LatLng(destinationLat,destinationLng)
        pickupLocationMarker.position(pickupLatLng)
        destinationLocationMarker!!.position(destinationLatLng)


        FetchURL(this,this
        ).execute(
            getUrl(
                pickupLocationMarker.getPosition(),
                destinationLocationMarker!!.getPosition(),
                "driving"
            ), "driving"
        )


    }
    private fun drawRouteFromPickupToDriverLocation(){

        pickupLocationMarker = MarkerOptions()
        driverFoundLocationMarker = MarkerOptions()
        pickupLatLng = LatLng(pickupLat,pickupLng)
        driverFoundLatLng = LatLng(driverLat,driverLng)
        pickupLocationMarker.position(pickupLatLng)
        driverFoundLocationMarker.position(driverFoundLatLng)


        FetchURL(this
        ,this).execute(
            getUrl(
                pickupLocationMarker.getPosition(),
                driverFoundLocationMarker.getPosition(),
                "driving"
            ), "driving"
        )

        getCheckingArriveStatus()

    }
    //setting url for polyline direction
    private fun getUrl(origin: LatLng, dest: LatLng, directionMode: String): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Mode
        val mode = "mode=$directionMode"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_mapsdirection_key)
    }
    private fun transactionToRideDetailsFragment(){

            // passing data to ride details fragment
            val transaction =  activity!!.supportFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putString("pickupAddress",pickupAddress)
            bundle.putString("destinationAddress",destinationAddress)
            bundle.putString("price",ridePrice)
            bundle.putString("timeAway",timeDriverAway.text.toString())
            bundle.putString("driverfoundid",driverfoundid)
            bundle.putString("driverName",driverName)
            bundle.putString("driveNo",driverNo)
            bundle.putString("driverVehicleNo",driverVehicleNo)
            val fragInfo = RideDetailsFragment()
            fragInfo.setArguments(bundle)
            transaction.add(R.id.frame_layout, fragInfo)
            transaction.commit()






    }
    private fun currentRideFirebaseListeners() {

        currentRideCurrentLocEventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    val locationLatLngPickupModel = dataSnapshot.child(Constants.current).getValue(LocationLatLng::class.java)
                    val locationLatLngDestinationModel = dataSnapshot.child(Constants.destination).getValue(LocationLatLng::class.java)
                    val  rideTypeModel = dataSnapshot.getValue(RideTypeModel::class.java)
                    rideType = rideTypeModel!!.rideName!!

                    pickupAddress = locationLatLngPickupModel!!.address!!
                    destinationAddress = locationLatLngDestinationModel!!.address!!
                    ridePrice = rideTypeModel.ridePrice.toString()

                    pickupLat = locationLatLngPickupModel.latitude!!
                    pickupLng = locationLatLngPickupModel.longitude!!

                    destinationLat = locationLatLngDestinationModel.latitude!!
                    destinationLng = locationLatLngDestinationModel.longitude!!

                    driverLat = dataSnapshot.child(Constants.driversLocationReference).child("latitude").getValue(String::class.java)!!.toDouble()
                    driverLng = dataSnapshot.child(Constants.driversLocationReference).child("longitude").getValue(String::class.java)!!.toDouble()
                    driverfoundid = dataSnapshot.child(Constants.driversLocationReference).child("driverfoundid").getValue(String::class.java)!!


                    layoutWhereTo.visibility = View.GONE
                    cardviewWhereTo.visibility = View.GONE
                    layoutRideAccepted.visibility = View.VISIBLE

                    // getting driver details when user ride in progress
                    getDriverDetails()
                    driverDetailsRef = firebaseDatabase.reference.child(Constants.driverDetailsReference)
                    driverDetailsRef.addListenerForSingleValueEvent(driverDetailsEventListner)


                    // setting marker on pickup location
                    drawMarkerPickupLocation()
                   /// making polyline from pickup to driver location
                    drawRouteFromPickupToDriverLocation()


                }else{



                    // getting current lat and lng from sharedpreference
                    val latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
                    val longitude =  sharedPreferences.getFloat("longitude", 0.0f).toDouble()

                    // when map ready it moves to current location
                    val latLng = LatLng(latitude, longitude)
                    // zoom camera on current location
                    mMap.addMarker(MarkerOptions().position(latLng).title("Current location"))
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,20f)
                    mMap.animateCamera(cameraUpdate)
                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

                Toast.makeText(context,"pickup listner cancelled",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun updateDriverLocationToRide(list:ArrayList<NearestDriverModel>){

        rideDatabaseRef = firebaseDatabase.reference
        ridelistener = rideDatabaseRef!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(context,"cancel update children",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {


                        // making user assign to driver in firebase with their id
                        val driverLocationRef = FirebaseDatabase.getInstance().reference.child(Constants.ride).child(userId).child(Constants.driversLocationReference)
                        val map = HashMap<String,String>()
                        // setting driver lat, lng and driver id to driverslocation for making polyline
                        map.put("latitude",list.get(0).lat.toString())
                        map.put("longitude", list.get(0).lng.toString())
                        map.put("driverfoundid",list.get(0).key!!)

                       driverLocationRef.updateChildren(map as Map<String, Any>)
                       rideDatabaseRef!!.removeEventListener(ridelistener)





                    }
                }


            })




    }
    private fun removeCurrentRideReference(){

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference(Constants.rideLocation).child(userId).removeValue()
        firebaseDatabase.getReference(Constants.requestReference).child(userId).removeValue()
        firebaseDatabase.getReference(Constants.assignUsersReference).child(Constants.assignDriversReference).child(driverfoundid!!).removeValue()


    }
    private fun signOut(){

        fireBaseAuth = FirebaseAuth.getInstance()
        fireBaseAuth.signOut()
    }
    // implemented interfaces
    override fun onDuration(value: String) {

        // setting time away from pickup point
        timeDriverAway.setText(value+" away")
    }
    override fun onTaskDone(vararg values: Any) {

        if (currentPolyline != null)
            currentPolyline!!.remove()
        // making polyline on map
        currentPolyline = mMap.addPolyline(values[0] as PolylineOptions)
        // setting bound from pickup to dropoff
        val builderBounds = LatLngBounds.Builder()
        // zoomout polyline
        val  padding = 50;

        // if destination marker is null it will make polyline pickup location to driver location
        if (destinationLocationMarker == null){

            builderBounds.include(pickupLatLng)
            builderBounds.include(driverFoundLatLng)
            val bounds = builderBounds.build()
            // zoom camera on polyline
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,padding)
            mMap.animateCamera(cameraUpdate)

        }else{

            // if destination marker is null it will make polyline pickup location to dropoff location
            builderBounds.include(pickupLatLng)
            builderBounds.include(destinationLatLng)
            val bounds = builderBounds.build()
            // zoom camera on polyline
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,padding)
            mMap.animateCamera(cameraUpdate)
        }



    }
    override fun onTimeDone(vararg value: String) {


    }











    private fun firebaseListenersProfile() {

        profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                val name:String = dataSnapshot.child(Constants.userNameReference).value.toString()

                userNameTextView.setText(name)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
    }

    private fun initObjectsAndReferencesProfile() {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        profileInfoReference = firebaseDatabase.getReference(Constants.profileReference).child(userId)
    }





}