package com.centosquare.devatease.gooapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.centosquare.devatease.gooapp.R
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import java.lang.reflect.Field
import java.util.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions


class SearchActivity : AppCompatActivity() {

     lateinit var location: TextInputEditText
     lateinit var txtVw:TextView
     private lateinit var markerOptionsonAutoComplete: MarkerOptions


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_current_loc)
        txtVw = findViewById(R.id.placeName)

        Places.initialize(applicationContext, "AIzaSyAJM1g2XzSmJn3zVbuSABmsDZG91-UL-HQ")


        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            Arrays.asList<Place.Field>(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setCountry("PK")
     //   autocompleteFragment.setLocationBias(BOUNDS_MOUNTAIN_VIEW)


       /* val filter = AutocompleteFilter.Builder()
            .setCountry("PK")
            .build()

        autocompleteFragment.setFilter(filter)
*/
        // Specify the types of place data to return.

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener,
            com.google.android.gms.location.places.ui.PlaceSelectionListener {
            override fun onPlaceSelected(p0: com.google.android.gms.location.places.Place?) {
              //  Toast.makeText(applicationContext,p0!!.name,Toast.LENGTH_SHORT).show()

            }



            override fun onPlaceSelected(p0: Place) {

                //add marker to select place
              //  addMarker(place)

              /*  if (markerOptionsonAutoComplete.position != null) {
                    FetchURL(this@MapsActivity).execute(
                        getUrl(
                            currentLocationMarker.getPosition(),
                            markerOptionsonAutoComplete.position,
                            "driving"
                        ), "driving"
                    )
                }*/


                Toast.makeText(applicationContext,p0.name,Toast.LENGTH_SHORT).show()


            }


            override fun onError(status: Status) {
                txtVw.text = status.toString()
            }
        })


    }

    /*//method for add marker to selected place
    private fun addMarker(p: Place) {

        if (p.latLng != null) {
            markerOptionsonAutoComplete.position(p.latLng!!)
            markerOptionsonAutoComplete.title(p.name!! + "")
        }
        markerOptionsonAutoComplete.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        mMap.clear()
        mMap.addMarker(markerOptionsonAutoComplete)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
    }
*/
}