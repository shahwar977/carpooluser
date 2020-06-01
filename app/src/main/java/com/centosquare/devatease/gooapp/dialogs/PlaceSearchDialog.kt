package com.centosquare.devatease.gooapp.dialogs

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.centosquare.devatease.gooapp.R
import com.codemybrainsout.placesearch.PlaceAutocompleteAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Rahul Juneja on 30-10-2015.
 */
class PlaceSearchDialog(context: Context, private val builder: Builder) :
    AppCompatDialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth),
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    private val TAG = "PlaceSearchDialog"

    private var locationET: AppCompatAutoCompleteTextView? = null
    private var locationTIL: TextInputLayout? = null
    private var cancelTV: AppCompatTextView? = null
    private var okTV: AppCompatTextView? = null
    private var headerImageIV: ImageView? = null

    var mGoogleApiClient: GoogleApiClient? = null
    private var mAdapter: PlaceAutocompleteAdapter? = null

    private lateinit var mcontext: Context

    private val mAutocompleteClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val item = mAdapter!!.getItem(position)
        val placeId = item!!.placeId
        val primaryText = item.getPrimaryText(null)

        //Hide Keyboard
        val `in` = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(view.windowToken, 0)

        Log.i(TAG, "Autocomplete item selected: $primaryText")
    }

    interface LocationNameListener {
        fun locationName(locationName: String)
    }

    init {
        setContentView(R.layout.place_search_dialog)
        this.mcontext = builder.context
    }

    fun init() {

        locationET = findViewById<View>(R.id.place_search_dialog_location_ET) as AppCompatAutoCompleteTextView?
        locationTIL = findViewById<View>(R.id.place_search_dialog_location_TIL) as TextInputLayout?
        cancelTV = findViewById<View>(R.id.place_search_dialog_cancel_TV) as AppCompatTextView?
        okTV = findViewById<View>(R.id.place_search_dialog_ok_TV) as AppCompatTextView?
        headerImageIV = findViewById<View>(R.id.place_search_dialog_header_image_IV) as ImageView?

        okTV!!.setOnClickListener(this)
        cancelTV!!.setOnClickListener(this)

        buildDialog()

        locationET!!.onItemClickListener = mAutocompleteClickListener
        mAdapter = PlaceAutocompleteAdapter(context, mGoogleApiClient, BOUNDS_WORLD, null)
        locationET!!.threshold = 3
        locationET!!.setAdapter<PlaceAutocompleteAdapter>(mAdapter)
    }

    private fun buildDialog() {

        okTV!!.text =
            if (!TextUtils.isEmpty(builder.positiveText)) builder.positiveText else context.resources.getString(R.string.ok)
        cancelTV!!.text =
            if (!TextUtils.isEmpty(builder.negativeText)) builder.negativeText else context.resources.getString(R.string.cancel)
        locationET!!.hint =
            if (!TextUtils.isEmpty(builder.hintText)) builder.hintText else context.resources.getString(com.codemybrainsout.placesearch.R.string.enter_location_hint)

        okTV!!.setTextColor(
            if (builder.positiveTextColor != 0) ContextCompat.getColor(
                context,
                builder.positiveTextColor
            ) else ContextCompat.getColor(context, R.color.mt_red)
        )
        cancelTV!!.setTextColor(
            if (builder.negativeTextColor != 0) ContextCompat.getColor(
                context,
                builder.negativeTextColor
            ) else ContextCompat.getColor(context, R.color.mt_gray4)
        )
        locationET!!.setHintTextColor(
            if (builder.hintTextColor != 0) ContextCompat.getColor(
                context,
                builder.hintTextColor
            ) else ContextCompat.getColor(context, R.color.mt_gray3)
        )

        if (builder.latLngBounds != null) {
            BOUNDS_WORLD = builder.latLngBounds
        }

        if (builder.headerImageResource != 0) {
            headerImageIV!!.setImageResource(builder.headerImageResource)
        } else {
            headerImageIV!!.setImageResource(R.drawable.place_picker_dialog)
        }

    }


    private fun ok() {
        if (builder.locationNameListener != null) {
            builder.locationNameListener!!.locationName(locationET!!.text.toString().trim { it <= ' ' })
        }
        dismiss()
    }


    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        mGoogleApiClient = GoogleApiClient.Builder(context)
            //.enableAutoManage(activity, 0 /* clientId */, this)
            .addApi(Places.GEO_DATA_API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
    }

    override fun onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
        super.onStop()
    }

    override fun onConnected(bundle: Bundle?) {
        Log.e(TAG, "Google API client connected")
        init()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.e(TAG, "Google API client suspended")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.errorCode)

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(
            context,
            "Could not connect to Google API Client: Error " + connectionResult.errorCode,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onClick(view: View) {

        if (view.id == R.id.place_search_dialog_ok_TV) {
            ok()
        } else if (view.id == R.id.place_search_dialog_cancel_TV) {
            dismiss()
        }
    }

    class Builder(val context: Context) {
        var locationNameListener: PlaceSearchDialog.LocationNameListener? = null
        var latLngBounds: LatLngBounds? = null
        var positiveText: String? = null
        var negativeText: String? = null
        var hintText: String? = null
        var positiveTextColor: Int = 0
        var negativeTextColor: Int = 0
        var hintTextColor: Int = 0
        var headerImageResource: Int = 0

        fun setLocationNameListener(locationNameListener: PlaceSearchDialog.LocationNameListener): Builder {
            this.locationNameListener = locationNameListener
            return this
        }

        fun setLatLngBounds(latLngBounds: LatLngBounds): Builder {
            this.latLngBounds = latLngBounds
            return this
        }

        fun setPositiveText(positiveText: String): Builder {
            this.positiveText = positiveText
            return this
        }

        fun setNegativeText(negativeText: String): Builder {
            this.negativeText = negativeText
            return this
        }

        fun setHintText(hintText: String): Builder {
            this.hintText = hintText
            return this
        }

        fun setPositiveTextColor(positiveTextColor: Int): Builder {
            this.positiveTextColor = positiveTextColor
            return this
        }

        fun setNegativeTextColor(negativeTextColor: Int): Builder {
            this.negativeTextColor = negativeTextColor
            return this
        }

        fun setHintTextColor(hintTextColor: Int): Builder {
            this.hintTextColor = hintTextColor
            return this
        }

        fun setHeaderImage(headerImageResource: Int): Builder {
            this.headerImageResource = headerImageResource
            return this
        }

        fun build(): PlaceSearchDialog {
            return PlaceSearchDialog(context, this)
        }

    }

    companion object {
        private var BOUNDS_WORLD: LatLngBounds? = LatLngBounds(
            LatLng(-85.0, 180.0), LatLng(85.0, -180.0)
        )
    }

}
