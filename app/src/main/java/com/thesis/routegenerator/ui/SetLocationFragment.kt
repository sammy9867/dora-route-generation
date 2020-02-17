package com.thesis.routegenerator.ui


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.routegenerator.R


import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.shashank.sony.fancytoastlib.FancyToast
import com.thesis.routegenerator.adapter.PlaceArrayAdapter
import com.thesis.routegenerator.databinding.FragmentSetLocationpBinding
import com.thesis.routegenerator.model.LocationWrapper
import com.thesis.routegenerator.util.VerticalDividerItemDecorator
import com.thesis.routegenerator.viewmodel.CommunicatorViewModel
import java.util.*

class SetLocationFragment : Fragment(){

    private lateinit var binding: FragmentSetLocationpBinding
    lateinit var placesClient: PlacesClient

    private var model: CommunicatorViewModel? = null
    private var placeAdapter: PlaceArrayAdapter? = null

    private var tempLat : Double = 0.0
    private var tempLong : Double = 0.0
    var tempAddress = ""

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_set_locationp, container, false
        )

        // to store values that can be communicated between fragments
        model = ViewModelProviders.of(activity!!).get(CommunicatorViewModel::class.java)
        model!!.setIsStartingLocationSelected(false)
        model!!.setIsEndingLocationSelected(false)

        // location permission from user
        setUpLocationPermission()

        // place search
       // setUpPlaceSearch(model!!)

        //Init Place
        Places.initialize(context!!, getString(R.string.places_api_key))
        placesClient = Places.createClient(context!!)


        model!!.startingAddress.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                binding.etStartingLocation.setText(o as String)
                model!!.setIsStartingLocationSelected(false)

            }
        })

        model!!.endingAddress.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                binding.etEndingLocation.setText(o as String)
                model!!.setIsEndingLocationSelected(false)
            }
        })

        val listener = object : PlaceArrayAdapter.OnPlaceClickListener{
            override fun click(place: Place) {
                if (model!!.isStartingLocationSelected.value!!) {
                    binding.etStartingLocation.setText(place.address.toString())
                    model!!.setStartingAddress(place.address.toString())

                    Log.i("Latlng ", "" +place.latLng!!.latitude + " " + place.latLng!!.longitude)
                    model!!.setStartingLocation(LocationWrapper(
                        com.thesis.routegenerator.model.Location(place.latLng!!.latitude, place.latLng!!.longitude)))

                    model!!.setIsStartingLocationSelected(false)

                } else if (model!!.isEndingLocationSelected.value!!) {
                    binding.etEndingLocation.setText(place.address)
                    model!!.setEndingAddress(place.address.toString())

                    Log.i("Latlng ", "" +place.latLng!!.latitude + " " + place.latLng!!.longitude)
                    model!!.setEndingLocation(LocationWrapper(
                        com.thesis.routegenerator.model.Location(place.latLng!!.latitude, place.latLng!!.longitude)))
                    model!!.setIsEndingLocationSelected(false)
                }
            }

        }


        placeAdapter = PlaceArrayAdapter(context!!, listener, placesClient)
        binding.rvPlaceNames.adapter= placeAdapter
        binding.rvPlaceNames.addItemDecoration( VerticalDividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.divider)!!))
        binding.rvPlaceNames.layoutManager = LinearLayoutManager(context)

        binding.etStartingLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.toString().equals("")) {
                    placeAdapter!!.filter.filter(p0.toString())
                    model!!.setIsStartingLocationSelected(true)
                    if (binding.rvPlaceNames.visibility == View.GONE) {binding.rvPlaceNames.visibility =  View.VISIBLE}
                } else {
                    if (binding.rvPlaceNames.visibility  == View.VISIBLE) {binding.rvPlaceNames.visibility  = View.GONE}

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


        binding.etEndingLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.toString().equals("")) {
                    placeAdapter!!.filter.filter(p0.toString())
                    model!!.setIsEndingLocationSelected(true)
                    if (binding.rvPlaceNames.visibility == View.GONE) {binding.rvPlaceNames.visibility =  View.VISIBLE}
                } else {
                    if (binding.rvPlaceNames.visibility  == View.VISIBLE) {binding.rvPlaceNames.visibility  = View.GONE}
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.ibStartingClear.setOnClickListener{
            binding.etStartingLocation.text.clear()
        }

        binding.ibEndingClear.setOnClickListener{
            binding.etEndingLocation.text.clear()
        }


        //Pinpoint
        binding.ibStartingPin.setOnClickListener{view ->
            view.findNavController().navigate(R.id.action_setLocationFragment2_to_pinPointStartLocationFragment)
        }

        binding.ibEndingPin.setOnClickListener{view ->
            view.findNavController().navigate(R.id.action_setLocationFragment2_to_pinPointEndLocationFragment)
        }


        // navigate back to the previous fragment
        binding.icLocationPrev.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_setLocationFragment2_to_priorityAndFrequencyFragment)
        )

        // navigate to the next fragment
        binding.icLocationNext.setOnClickListener { view ->

            if(model!!.startingLocation.value == null){
                FancyToast.makeText(context, "Select Starting Location", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }else if(model!!.endingLocation.value == null) {
                FancyToast.makeText(context, "Select Ending Location", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }else if(model!!.startingLocation.value != null &&  model!!.endingLocation.value != null){
                  view.findNavController().navigate(R.id.action_setLocationFragment2_to_setTimeSlotFragment)
            }else{
                FancyToast.makeText(context, "Enter Location Details", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }
        }

        return binding.root
    }


    /**
     * Checks whether location permission specified in AndroidManifest.xml file are enabled.
     * If not, the user is prompted to enable location for the app.
     */
    private fun setUpLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

   override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
        if (resultCode == RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            Log.i("LOC", "Place: " + place.name+ ", " + place.latLng)
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(data!!)
            Log.i("LOC", status.statusMessage)
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.

        }


    }
   }


}