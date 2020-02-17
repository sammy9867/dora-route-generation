package com.thesis.routegenerator.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

import com.thesis.routegenerator.R
import com.thesis.routegenerator.databinding.FragmentMapBinding

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.thesis.routegenerator.adapter.MapTimeLineAdapter
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

import com.google.gson.Gson
import com.thesis.routegenerator.api.ApiService
import com.thesis.routegenerator.model.*
import com.thesis.routegenerator.viewmodel.*
import kotlinx.coroutines.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.HashMap

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    // binding object for mapFragment
    private lateinit var binding: FragmentMapBinding

    // ViewModel
    private var placeDetailsCommunicator : PlaceDetailsCommunicator?=null
    private var model : CommunicatorViewModel?=null

    // User Input object
    private lateinit var userInput : UserInput

    // GoogleMap objects
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentPolyLine: Polyline? = null

    // Bottom Sheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val mDataList = ArrayList<TimeLineModel>()
    private lateinit var  placeTypeIconBottomSheet: Map<String, Int>
    private var placeNameBottomSheet : ArrayList<ArrayList<String>> = arrayListOf()
    private var departureTimeBottomSheet : ArrayList<ArrayList<String>> = arrayListOf()
    private var arrivalTimeBottomSheet : ArrayList<ArrayList<String>> = arrayListOf()
    private var placeTypeBottomSheet : ArrayList<ArrayList<String>> = arrayListOf()

    private var algorithmBottomSheet : ArrayList<String> = arrayListOf()
    private var compTimeBottomSheet : ArrayList<Double> = arrayListOf()


    // Place Details
    private var placeDetailsMutableMap: MutableMap<String, PlaceDetailsModel> = mutableMapOf<String, PlaceDetailsModel>()

    // Google Directions and Markers
    private lateinit var  placeTypeIconMap: Map<String, Int>
    private var wayPointList : ArrayList<ArrayList<LatLng>> = arrayListOf()
    private var placeIdList : ArrayList<ArrayList<String>> = arrayListOf()
    private var markerMap : HashMap<String, String> = hashMapOf()


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
         binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_map, container, false
        )

        placeTypeIconBottomSheet = populatePlaceType()
        placeTypeIconMap = populateCustomMapIcons()

        // to store and retrieve value that can be communicated between fragments
        model = ViewModelProviders.of(activity!!).get(CommunicatorViewModel::class.java)
        placeDetailsCommunicator = ViewModelProviders.of(activity!!).get(PlaceDetailsCommunicator::class.java)


        // User Input
        val userId = 1234567851L
        val history = listOf<String>()

        val tempPriorityTypes =  filterPriorityList(model!!.priorityList.value!!)
        userInput = UserInput("requestRoute", userId, model!!.startingLocation.value!!,
            model!!.endingLocation.value!!,
            tempPriorityTypes, model!!.frequency.value!!,
            model!!.dateFrom.value!!,  model!!.week.value!!,  model!!.timeFrom.value!!,  model!!.timeTo.value!!,
            history,1)


        // Google Maps
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)


        // Bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        val adapter = MapTimeLineAdapter(mDataList, context!!)
        binding.rvMap.adapter = adapter
        binding.rvMap.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // Switch Route Algorithms
        binding.ibSwitchDirection.setOnClickListener{

            if(mDataList.isNotEmpty()){
                when(model!!.directionChanger.value){
                    0 -> {
                        map.clear()
                        markerMap.clear()
                        displayRoute(0, Color.BLACK)
                        populateTimeLine(0)
                        binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[0]
                        binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[0] +"ms"
                        model!!.setDirectionChanger(1)

                        // Log.i("Changer:", model!!.directionChanger.value!!.toString())
                    }
                    1->{
                        map.clear()
                        markerMap.clear()
                        displayRoute(1, Color.BLUE)
                        populateTimeLine(1)
                        binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[1]
                        binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[1] +"ms"
                        model!!.setDirectionChanger(2)

                        //  Log.i("Changer:", model!!.directionChanger.value!!.toString())
                    }
                    2->{
                        map.clear()
                        markerMap.clear()
                        displayRoute(2, Color.rgb(165, 42, 42))
                        populateTimeLine(2)
                        binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[2]
                        binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[2] +"ms"
                        model!!.setDirectionChanger(0)

                        //   Log.i("Changer:", model!!.directionChanger.value!!.toString())
                    }
                }
            }

        }

        binding.icMapFilter.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_mapFragment_to_multiSelectionFragment))

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

        // A Security Exception will be thrown only if location permissions are not enabled.
        try {
            map.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->

             if(model!!.latlngBndsBuilder.value != null){
                 map.moveCamera(CameraUpdateFactory.newLatLngBounds(model!!.latlngBndsBuilder.value, 100))
             }
                if (location != null) {
                    lastLocation = location

                    map.setOnMarkerClickListener(this)

                    Log.i("User Input ", Gson().toJson(userInput))

                    // Call API if null
                    if(model!!.allRoutes.value == null){
                        val call = ApiService.routeApi.requestRoute(userInput)

                        GlobalScope.launch{

                            call.enqueue(object : Callback<RouteResponse> {
                                override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                                    Log.i("Fail to get response: ", t.message)
                                }

                                override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {

                                    //add Waypoint locations to wayPointList from server response.
                                    if(response.isSuccessful) {

                                        val rsp = response.body()
                                        model!!.allRoutes.value = rsp!!.allRoutes // Saving data
                                        for(it in rsp.allRoutes){

                                            val tempList = arrayListOf<LatLng>()
                                            val tempPlaceNameList = arrayListOf<String>()
                                            val tempPlaceIdList = arrayListOf<String>()
                                            val tempDepartList = arrayListOf<String>()
                                            val tempArrivalList = arrayListOf<String>()
                                            val tempPlaceTypeList = arrayListOf<String>()
                                            for (it2 in it.route) {
                                                tempList.add(LatLng(it2.location.lat, it2.location.lng))
                                                tempPlaceIdList.add(it2.placeId)

                                                tempPlaceNameList.add(it2.name)
                                                tempPlaceTypeList.add(it2.type)
                                                val paddedTimeDeparture = editTimeSlot(it2.timeDeparture)
                                                val paddedTimeArrive = editTimeSlot(it2.timeArrive)
                                                tempDepartList.add(paddedTimeDeparture)
                                                tempArrivalList.add(paddedTimeArrive)

                                                if(it2.event != null){
                                                    placeDetailsMutableMap.put(
                                                        it2.placeId,
                                                        PlaceDetailsModel(
                                                            it2.name, it2.formatted_address, it2.formatted_phone_number,
                                                            it2.rating, it2.website, it2.opening_hours, it2.photos, it2.event!!.facebook
                                                        )
                                                    )

                                                }else{
                                                    placeDetailsMutableMap.put(
                                                        it2.placeId,
                                                        PlaceDetailsModel(
                                                            it2.name, it2.formatted_address, it2.formatted_phone_number,
                                                            it2.rating, it2.website, it2.opening_hours, it2.photos, null
                                                        )
                                                    )
                                                }

                                            }

                                            algorithmBottomSheet.add(it.algorithm)
                                            compTimeBottomSheet.add(it.comp_time)

                                            wayPointList.add(tempList)
                                            placeIdList.add(tempPlaceIdList)

                                            placeNameBottomSheet.add(tempPlaceNameList)
                                            placeTypeBottomSheet.add(tempPlaceTypeList)
                                            departureTimeBottomSheet.add(tempDepartList)
                                            arrivalTimeBottomSheet.add(tempArrivalList)
                                        }
                                        populateTimeLine(0)
                                        binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[0]
                                        binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[0] + "ms"
                                        Log.i("Direction Changer:", model!!.directionChanger.value!!.toString())
                                        displayRoute(model!!.directionChanger.value!!, Color.BLACK)
                                        model!!.setDirectionChanger(1)
                                    }else{
                                        Log.i("Response from server: ", response.message())
                                    }

                                }
                            })

                        }

                    }else{ // Load saved allRoute data
                        Log.i("Loading..", "Saved data")
                        for(it in model!!.allRoutes.value!!){
                            val tempList = arrayListOf<LatLng>()
                            val tempPlaceNameList = arrayListOf<String>()
                            val tempPlaceIdList = arrayListOf<String>()
                            val tempDepartList = arrayListOf<String>()
                            val tempArrivalList = arrayListOf<String>()
                            val tempPlaceTypeList = arrayListOf<String>()
                            for (it2 in it.route) {
                                tempList.add(LatLng(it2.location.lat, it2.location.lng))
                                tempPlaceIdList.add(it2.placeId)

                                tempPlaceNameList.add(it2.name)
                                tempPlaceTypeList.add(it2.type)
                                val paddedTimeDeparture = editTimeSlot(it2.timeDeparture)
                                val paddedTimeArrive = editTimeSlot(it2.timeArrive)
                                tempDepartList.add(paddedTimeDeparture)
                                tempArrivalList.add(paddedTimeArrive)

                                if(it2.event != null){
                                    placeDetailsMutableMap.put(
                                        it2.placeId,
                                        PlaceDetailsModel(
                                            it2.name, it2.formatted_address, it2.formatted_phone_number,
                                            it2.rating, it2.website, it2.opening_hours, it2.photos, it2.event!!.facebook
                                        )
                                    )

                                }else{
                                    placeDetailsMutableMap.put(
                                        it2.placeId,
                                        PlaceDetailsModel(
                                            it2.name, it2.formatted_address, it2.formatted_phone_number,
                                            it2.rating, it2.website, it2.opening_hours, it2.photos, null
                                        )
                                    )
                                }
                            }

                            algorithmBottomSheet.add(it.algorithm)
                            compTimeBottomSheet.add(it.comp_time)

                            wayPointList.add(tempList)
                            placeIdList.add(tempPlaceIdList)

                            placeNameBottomSheet.add(tempPlaceNameList)
                            placeTypeBottomSheet.add(tempPlaceTypeList)
                            departureTimeBottomSheet.add(tempDepartList)
                            arrivalTimeBottomSheet.add(tempArrivalList)
                        }

                        Log.i("Direction Changer:", model!!.directionChanger.value!!.toString())
                        //Whilst coming back from place details, use previous direction
                        when(model!!.directionChanger.value!!){
                            0-> {
                                displayRoute(2, Color.rgb(165, 42, 42))
                                populateTimeLine(2)
                                binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[2]
                                binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[2] + "ms"
                                model!!.setDirectionChanger(0)
                            }
                            1-> {
                                displayRoute(0, Color.BLACK)
                                populateTimeLine(0)
                                binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[0]
                                binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[0] + "ms"
                                model!!.setDirectionChanger(1)

                            }
                            2-> {
                                displayRoute(1, Color.BLUE)
                                populateTimeLine(1)
                                binding.algorithmName.text = "Algorithm Name: " + algorithmBottomSheet[1]
                                binding.algorithmCompTime.text = "Time Taken: " + compTimeBottomSheet[1] +"ms"
                                model!!.setDirectionChanger(2)
                            }
                        }

                    }

                }
            }

        }catch (ex : SecurityException){
             Log.i("Exception occured", ex.printStackTrace().toString())
        }
    }


    /**
     * Function to display route on the map
     * @param tempCounter: change algorithm based on counter
     * @param color: color of the polyline
     */
    private fun displayRoute(tempCounter : Int, color: Int){


            val directionURL = getDirections(
                        LatLng(model!!.startingLocation.value!!.location!!.lat, model!!.startingLocation.value!!.location!!.lng),
                        LatLng(model!!.endingLocation.value!!.location!!.lat, model!!.endingLocation.value!!.location!!.lng),
                        "walking", wayPointList[tempCounter],  placeIdList[tempCounter], tempCounter)


            async {
                // Response from direction API.
                val result = URL(directionURL).readText()

                uiThread {
                    if (result.isNotEmpty()) {
                        // Draw polyline based on direction API response
                        drawPolyLines(result, wayPointList[tempCounter], color)
                    }
                }
            }
    }


    /**
     * Function to populate bottomSheetTimeLine
     * @param tempChanger: change algorithm based on changer
     */
    private fun populateTimeLine(tempChanger : Int){
        mDataList.clear()

        //Log.i("PlaceType:", placeTypeBottomSheet.toString())
        if(placeNameBottomSheet.isNotEmpty()){
            mDataList.add((TimeLineModel("Start Location", "", R.drawable.ic_marker_active,"", "")))
            for(i in 0 until placeNameBottomSheet[tempChanger].size){
                val replace = replaceUnderscoreWithSpace(placeTypeBottomSheet[tempChanger][i])
                mDataList.add(TimeLineModel(placeNameBottomSheet[tempChanger][i], replace, getPlaceTypeIcon(placeTypeBottomSheet[tempChanger][i], placeTypeIconBottomSheet)
                    ,arrivalTimeBottomSheet[tempChanger][i], departureTimeBottomSheet[tempChanger][i]))

            }

            if(model!!.endingLocation.value != null){
                mDataList.add(TimeLineModel("End Location",
                    "", R.drawable.ic_marker_black,"", ""))
            }

            binding.rvMap.adapter!!.notifyDataSetChanged()
         }
    }


    /**
     * Function that draws polyline on the google map
     * @param result: String response from Google Direction API
     * @param tempList: List of placeTypes
     * @param color: color of the polyline
     */
    private fun drawPolyLines(result: String, tempList : ArrayList<LatLng>, color : Int){


        val path : ArrayList<LatLng> = arrayListOf()

        // De-serializing JSON
        val parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(result)
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
        val routes = json.array<JsonObject>("routes")


        // Setting Bounds for the map to focus on.
        val LatLongB = LatLngBounds.Builder()
        LatLongB.include(LatLng(model!!.startingLocation.value!!.location!!.lat, model!!.startingLocation.value!!.location!!.lng))

        if(routes!!.isNotEmpty()){
            if(model!!.endingLocation.value != null){
                for(i in  0 until tempList.size + 1){
                    val points = routes!!["legs"]["steps"][i] as JsonArray<JsonObject>
                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }

                    for (point in polypts) {
                        path.add(LatLng(point.latitude, point.longitude))
                        LatLongB.include(LatLng(point.latitude, point.longitude))
                    }
                }
            }else{
                for(i in  0 until tempList.size){
                    val points = routes!!["legs"]["steps"][i] as JsonArray<JsonObject>
                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }

                    for (point in polypts) {
                        path.add(LatLng(point.latitude, point.longitude))
                        LatLongB.include(LatLng(point.latitude, point.longitude))
                    }
                }

            }


            //Setting Polyline pattern
            val dot : PatternItem=  Dot()
            val gap : PatternItem =  Gap(20f)

            if(path.size > 0){
                val polyLineOptions = PolylineOptions().addAll(path).width(20f).color(color).geodesic(true)
                if(currentPolyLine == null){
                    currentPolyLine = map.addPolyline(polyLineOptions)
                    currentPolyLine!!.startCap =
                        CustomCap(bitmapDescriptorFromVector(context!!, R.drawable.ic_marker_active, 120, 120), 20f)
                    currentPolyLine!!.pattern = Arrays.asList(gap, dot)
                    currentPolyLine!!.jointType = JointType.ROUND
                    currentPolyLine!!.zIndex = 1f
                }else{
                    currentPolyLine!!.remove()
                    currentPolyLine = map.addPolyline(polyLineOptions)
                    currentPolyLine!!.startCap =
                        CustomCap(bitmapDescriptorFromVector(context!!, R.drawable.ic_marker_active, 120, 120), 20f)
                    currentPolyLine!!.pattern = Arrays.asList(gap, dot)
                    currentPolyLine!!.jointType = JointType.ROUND
                    currentPolyLine!!.zIndex = 1f
                }

            }
        }
        placeMarkerOnMap(LatLng(model!!.endingLocation.value!!.location!!.lat, model!!.endingLocation.value!!.location!!.lng))
        LatLongB.include( LatLng(model!!.endingLocation.value!!.location!!.lat, model!!.endingLocation.value!!.location!!.lng))
        val bounds = LatLongB.build()
        map.uiSettings.isZoomControlsEnabled = true

         model!!.latlngBndsBuilder.value = bounds
         map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))




    }


    /**
    * Function that handles events when a marker is clicked
    * @param marker: marker object
    */
    override fun onMarkerClick(marker: Marker?): Boolean {

        for(i in markerMap){
            if(i.value == marker!!.id){
                Log.i(i.key , i.value)
                val placeDetails = placeDetailsMutableMap.get(i.key)
                placeDetailsCommunicator!!.setPlaceDetailsModel(placeDetails!!)
                view!!.findNavController().navigate(R.id.action_mapFragment_to_placeDetailsFragment)
            }
        }

        return true
    }


    /**
     * Function to place marker for suggested place types from server on Google Maps.
     * @param place_id: place id of the place type
     * @param location: latlng
     */
    private fun placeCustomMarkerOnMap(place_id: String, placeType: String, location: LatLng) {
        val height = 90
        val width = 90

        if(placeType.isNotEmpty()){
            Log.i("PlaceType" , placeType)
            val bitmap = bitmapDescriptorFromVector(context!!, getPlaceTypeIconOnMap(placeType, placeTypeIconMap), width, height)
            val markerOptions = MarkerOptions().position(location).icon(bitmap)
            if(!markerMap.containsKey(place_id)){
                val marker  = map.addMarker(markerOptions)
                markerMap.put(place_id, marker!!.id)
            }
        }

    }

    /**
     * Function that forms a mapping between place type and place type icons.
     */
    private fun populateCustomMapIcons() : Map<String, Int>{

        val placeNames: ArrayList<String> =
            arrayListOf("amusement_park", "aquarium", "art_gallery", "bar", "bowling_alley",  "cafe", "casino",
                "church", "city_hall", "liquor_store", "mosque", "movie_theater", "museum", "night_club", "park", "restaurant", "shopping_mall",
                "synagogue", "tourist_attraction", "zoo")

        val placeIcons :  ArrayList<Int> = arrayListOf(R.drawable.ic_amusement_park_map, R.drawable.ic_aquarium_map, R.drawable.ic_art_gallery_map,
            R.drawable.ic_bar_map, R.drawable.ic_bowling_alley_map, R.drawable.ic_cafe_map, R.drawable.ic_casino_map,
            R.drawable.ic_church_map, R.drawable.ic_city_hall_map, R.drawable.ic_liquor_store_map, R.drawable.ic_mosque_map, R.drawable.ic_movie_theater_map,
            R.drawable.ic_museum_map, R.drawable.ic_night_club_map, R.drawable.ic_park_map, R.drawable.ic_restaurant_map, R.drawable.ic_shopping_mall_map,
            R.drawable.ic_synagogue_map, R.drawable.ic_tourist_attraction_map, R.drawable.ic_zoo_map)

        val playeTypeIcon = placeNames.zip(placeIcons).toMap()
        return playeTypeIcon

    }


    /**
     * Function to place marker on Google Maps.
     * @param location: latlng
     */
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_marker_black, 140, 140)).position(location)
        map.addMarker(markerOptions)
    }


    /**
     * Function to create a call to Google Directions API.
     * @param from: starting location
     * @param to: ending location
     * @param tempWaypointList: suggested place types from server
     * @param tempPlaceIdList: place id for each place type
     */
    private fun getDirections(from: LatLng, to: LatLng, directionMode : String, tempWaypointList : ArrayList<LatLng>, tempPlaceIdList : ArrayList<String>, changer : Int) : String{
        val origin = "origin=" + from.latitude + "," + from.longitude


        var waypoints = "waypoints="
        var counter = 0
        if(placeTypeBottomSheet[0].isNotEmpty()){
            for(points in tempWaypointList) {
                waypoints += "" + points.latitude + "," + points.longitude + "|"
               // Log.i("PlaceType:", "[" +model!!.directionChanger.value!! + "," + counter + "]")
                placeCustomMarkerOnMap(tempPlaceIdList[counter], placeTypeBottomSheet[changer][counter], points)
                counter += 1
            }
        }

        Log.i("PlaceType:", "[" +changer)
        Log.i("PlaceType", "----------------")



        if(waypoints.isNotEmpty()){
            waypoints = waypoints.substring(0, waypoints.length - 1)
        }

        val dest = "destination=" + to.latitude + "," + to.longitude
        val mode = "mode=" + directionMode
        val key = "key=" + getString(R.string.places_api_key)

        val params = "$origin&$dest&$waypoints&$mode&$key"

        return "https://maps.googleapis.com/maps/api/directions/json?$params"

    }


    /**
     * Function to replace space with underscore for placeTypes in priorityList
     * @param priorityList: priority List from UserInput
     */
    private fun filterPriorityList(priorityList: MutableList<String>) : MutableList<String>{
        val resultList : MutableList<String> = mutableListOf()
        for(item in priorityList){
            val temp = item.replace("\\s".toRegex(), "_")
            resultList.add(temp.toLowerCase())
        }
        return resultList
    }


    /**
     * Function to replace underscore with space for placeTypes in bottomSheet
     * @param placeType: place type
     */
    private fun replaceUnderscoreWithSpace(placeType: String): String{
        var replace = placeType.replace("_", " ")
        if(replace == "meal takeaway") replace = "restaurant"
        return replace
    }


    /**
     * Function that forms a mapping between place type and place type icons.
     */
    private fun populatePlaceType() : Map<String, Int>{

        val placeNames: ArrayList<String> =
            arrayListOf("amusement_park", "aquarium", "art_gallery", "bar", "bowling_alley",  "cafe", "casino",
                "church", "city_hall", "liquor_store", "mosque", "movie_theater", "museum", "night_club", "park", "restaurant", "shopping_mall",
                "synagogue", "tourist_attraction", "zoo")

        val placeIcons :  ArrayList<Int> = arrayListOf(R.drawable.ic_amusement_park, R.drawable.ic_aquarium, R.drawable.ic_art_gallery,
            R.drawable.ic_bar, R.drawable.ic_bowling_alley, R.drawable.ic_cafe, R.drawable.ic_casino,
            R.drawable.ic_church, R.drawable.ic_city_hall, R.drawable.ic_liquor_store, R.drawable.ic_mosque, R.drawable.ic_movie_theater,
            R.drawable.ic_museum, R.drawable.ic_night_club, R.drawable.ic_park, R.drawable.ic_restaurant, R.drawable.ic_shopping_mall,
            R.drawable.ic_synagogue, R.drawable.ic_tourist_attraction, R.drawable.ic_zoo)

        val playeTypeIcon = placeNames.zip(placeIcons).toMap()
        return playeTypeIcon

    }


    /**
     * Function to get placeTypeIcon
     * @param placeType: place type
     */
    private fun getPlaceTypeIcon(placeType : String, placeMapping : Map<String, Int>) : Int{
        for(i in placeMapping){
           if(placeType == i.key){
               return  i.value
           }
        }
        return R.drawable.ic_restaurant
    }

    /**
    * Function to get placeTypeIcon
    * @param placeType: place type
    */
    private fun getPlaceTypeIconOnMap(placeType : String, placeMapping : Map<String, Int>) : Int{
        for(i in placeMapping){
            if(placeType == i.key){
                return  i.value
            }
        }
        return R.drawable.ic_restaurant_map
    }


    /**
     * Function to return bitmap descriptor from bitmap
     * @param context: Application's current context
     * @param vectorDrawableResourceId: Drawable resource ID
     */
    private fun  bitmapDescriptorFromVector(context : Context, @DrawableRes  vectorDrawableResourceId : Int, width : Int, height : Int) :BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(0, 0, width, height)

        val bitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    /**
     * Function to decode polyline from Google directions API to display on google map
     * @param encoded: Given encoded polyline
     */
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }


    private fun editTimeSlot(timeSlot: String) : String{

        var timeSlotPadded = ""
        if(timeSlot.length == 1){
            timeSlotPadded =  "000" + timeSlot
        }else if(timeSlot.length == 2){
            timeSlotPadded =  "00" + timeSlot
        }else if(timeSlot.length == 3) {
            timeSlotPadded = "0" + timeSlot
        }else{
            timeSlotPadded = timeSlot
        }

        timeSlotPadded = timeSlotPadded.substring(0, 2) + ":" + timeSlotPadded.substring(2, timeSlotPadded.length)

        return timeSlotPadded
    }


}


