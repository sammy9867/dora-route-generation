package com.thesis.routegenerator.ui


import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

import com.thesis.routegenerator.R
import com.thesis.routegenerator.databinding.FragmentPinPointEndLocationBinding
import com.thesis.routegenerator.model.LocationWrapper
import com.thesis.routegenerator.viewmodel.CommunicatorViewModel
import java.util.*
import androidx.core.os.HandlerCompat.postDelayed
import android.os.SystemClock
import android.view.animation.BounceInterpolator
import android.os.Handler
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


class PinPointEndLocationFragment : Fragment(), OnMapReadyCallback,  GoogleMap.OnMarkerDragListener{

    //GoogleMap objects
    private lateinit var binding: FragmentPinPointEndLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tempLat : Double = 0.0
    private var tempLong : Double = 0.0
    private var tempAddress = ""

    private var model: CommunicatorViewModel?=null

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pin_point_end_location, container, false
        )

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_pinpoint_end) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        // to store values that can be communicated between fragments
        model = ViewModelProviders.of(activity!!).get(CommunicatorViewModel::class.java)

        binding.icPinpointPrev.setOnClickListener{view ->
                view.findNavController().navigate(R.id.action_pinPointEndLocationFragment_to_setLocationFragment2)

        }


        binding.pinAddressEnd.text = "Loading..."

        //Confirm
        binding.pinConfirmEnd.setOnClickListener{view->
            model!!.setEndingLocation(LocationWrapper(
                com.thesis.routegenerator.model.Location(tempLat, tempLong)))
            getAddress(tempLat, tempLong, true)
            view.findNavController().navigate(R.id.action_pinPointEndLocationFragment_to_setLocationFragment2)
        }

        return binding.root
    }

    /**
     * Function that loads google map in the fragment
     * @param googleMap: Google Map object
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success: Boolean = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.style_uber))

            if (!success) {
                Log.e("Error", "Style parsing failed.");
            }
        } catch (e : Resources.NotFoundException ) {
            Log.e("Error", "Can't find style. Error:")
        }

        try {
            map.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->

                        if (location != null) {
                            lastLocation = location

                        if(model!!.endingLocation.value == null){
                            tempLat = location.latitude
                            tempLong = location.longitude
                        }else{
                            tempLat = model!!.endingLocation.value!!.location!!.lat
                            tempLong =model!!.endingLocation.value!!.location!!.lng
                        }

                        getAddress(tempLat, tempLong, false)
                            val markerOptions = MarkerOptions()
                                .position(LatLng(tempLat, tempLong))
                                .icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_marker_black, 120, 120))
                                .draggable(true)

                            val marker = map.addMarker(markerOptions)
                            map.setOnMarkerDragListener(this@PinPointEndLocationFragment)

                            map.setOnCameraMoveListener {
                                marker.setAnchor(0.5f, 0.8f)

                                marker.position = map.projection.visibleRegion.latLngBounds.center
                            }

                            map.setOnCameraIdleListener {
                                marker.setAnchor(0.5f, 0.5f)
                                tempLat = map.cameraPosition.target.latitude
                                tempLong = map.cameraPosition.target.longitude
                                getAddress(tempLat, tempLong, false)
                            }

                        map.uiSettings.isZoomControlsEnabled = true
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(tempLat, tempLong),17f))
                   }
            }
        }catch (ex : SecurityException){
            ex.printStackTrace()
        }
    }



    override fun onMarkerDragStart(marker : Marker) {
        marker.setAnchor(0.5f, 0.8f)
    }

    override fun onMarkerDragEnd(marker : Marker) {

        marker.setAnchor(0.5f, 0.5f)
        tempLat = marker.position.latitude
        tempLong = marker.position.longitude
        Log.i("Marker: ", "" + tempLat + " " +  tempLong)
        getAddress(tempLat, tempLong, false)

    }

    override fun onMarkerDrag(marker : Marker) {

    }

    private fun getAddress(latitude : Double, longitude : Double, isConfirmed : Boolean){
        try{
            val geo = Geocoder(context, Locale.getDefault())
            val addressList = geo.getFromLocation(latitude, longitude, 1)
            if(addressList.isEmpty()){
                tempAddress = "Waiting for location"
                binding.pinAddressEnd.text = "Loading..."
                if(!isConfirmed) {
                    model!!.tempEndingAddress.value = tempAddress
              //      Log.i("Temp address", tempAddress)
                }
                else
                     model!!.setEndingAddress(tempAddress)
            }else{
                if(addressList.size > 0){
                    val temp = addressList[0]
                    val addressFragments = with(temp) {
                        (0..maxAddressLineIndex).map { getAddressLine(it) }
                    }
                    tempAddress = addressFragments.joinToString(separator = ", ")
                    if(!isConfirmed) {
                        model!!.tempEndingAddress.value = tempAddress
                        binding.pinAddressEnd.text = tempAddress
                   //     Log.i("Temp address", model!!.tempEndingAddress.value)
                        binding.pinAddressEnd.text = model!!.tempEndingAddress.value
                    }
                    else
                        model!!.setEndingAddress(tempAddress)
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


    /**
     * Function to return bitmap descriptor from bitmap
     * @param context: Application's current context
     * @param vectorDrawableResourceId: Drawable resource ID
     */
    private fun  bitmapDescriptorFromVector(context : Context, @DrawableRes vectorDrawableResourceId : Int, width : Int, height : Int) :BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(0, 0, width, height)

        val bitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}
