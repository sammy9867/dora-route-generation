package com.thesis.routegenerator.ui


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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.thesis.routegenerator.R
import com.thesis.routegenerator.adapter.PlaceDetailsAdapter
import com.thesis.routegenerator.databinding.FragmentPlaceDetailsBinding
import com.thesis.routegenerator.model.PlaceDetailsIcon
import com.thesis.routegenerator.model.PlaceDetailsModel
import com.thesis.routegenerator.viewmodel.PlaceDetailsCommunicator

class PlaceDetailsFragment : Fragment() {

    private var placeDetailsCommunicator : PlaceDetailsCommunicator?=null
    var placeDetailsIconList : MutableList<PlaceDetailsIcon> = mutableListOf()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
        val binding: FragmentPlaceDetailsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_place_details, container, false
        )

        placeDetailsCommunicator = ViewModelProviders.of(activity!!).get(PlaceDetailsCommunicator::class.java)

        setObservers(binding)

        val adapter = PlaceDetailsAdapter(placeDetailsIconList, context!!)
        binding.rvPlaceDetails.adapter = adapter
        binding.rvPlaceDetails.layoutManager = LinearLayoutManager(context)

        binding.icPlaceDetailsPrev.setOnClickListener{view->
            view.findNavController().navigate(R.id.action_placeDetailsFragment_to_mapFragment)
        }

        return binding.root
    }

    private fun setObservers(binding : FragmentPlaceDetailsBinding) {

        placeDetailsCommunicator!!.placeDetailsModel.observe(this, object : Observer<Any?> {
            override fun onChanged(o: Any?) {

                val temp = o as PlaceDetailsModel?
                if(temp != null){

                    //Photo
                    if(temp.photos != null){
                        if(temp.photos!!.size > 0){
                            val placePhotoUrl = getPlacePhoto(temp.photos!![0])
                            Glide.with(context!!).load(placePhotoUrl).into(binding.placePic)
                        }

                    }else{
                        print("No pic found")
                    }

                    //Name
                    if(temp.name != null){
                        binding.placeName.text = temp.name.toString()
                    }else{
                        binding.placeName.text = ""
                    }


                    //Rating
                    if(temp.rating != null){
                        binding.placeRating.text = temp.rating.toString()
                        binding.placeRatingBar.rating = temp.rating!!
                    }else{
                        binding.placeRating.text = ""
                    }

                    if(placeDetailsIconList.isNotEmpty()){
                        placeDetailsIconList.clear()
                    }

                    //Address
                    if(temp.formatted_address != null){
                        placeDetailsIconList.add(PlaceDetailsIcon(R.drawable.ic_location_pin_new,temp.formatted_address.toString()))
                    }

                    //Weekday
                    if(temp.opening_hours != null) {

                        var tempWeekdayText = ""
                        for(day in temp.opening_hours!!){
                            tempWeekdayText += day + "\n\n"
                        }

                        tempWeekdayText = tempWeekdayText.trimEnd()
                        tempWeekdayText = tempWeekdayText.trimEnd()

                        placeDetailsIconList.add(
                            PlaceDetailsIcon(
                                R.drawable.ic_time,
                                tempWeekdayText
                            )
                        )
                    }

                    //Phone Number
                    if(temp.formatted_phone_number != null){
                        placeDetailsIconList.add(PlaceDetailsIcon(R.drawable.ic_phone,temp.formatted_phone_number.toString()))
                    }

                    //Website
                    if(temp.website != null){
                        if(temp.website!!.contains("facebook.com")){
                            placeDetailsIconList.add(PlaceDetailsIcon(R.drawable.ic_fb_dark,temp.website.toString()))
                        }else{
                            placeDetailsIconList.add(PlaceDetailsIcon(R.drawable.ic_website,temp.website.toString()))
                        }

                    }

                    //FB link
                    if(temp.facebookLink != null){
                        placeDetailsIconList.add(PlaceDetailsIcon(R.drawable.ic_fb_dark,temp.facebookLink.toString()))
                    }

                    placeDetailsCommunicator!!.setPlaceDetailsIcon(placeDetailsIconList)

                }




            }



        })


    }

    /**
     * Function to create an API call to GooglePhotos API
     * @param photo_ref: photo reference id of a given photo
     **/
    private fun getPlacePhoto(photo_ref: String) : String{


        val dimensions = "maxwidth=400&maxheight=400"
        val photoRef = "photo_reference="  + photo_ref
        val key = "key=" + getString(R.string.places_api_key)

        val params = "$dimensions&$photoRef&$key"

        Log.i("Params:", params)

        return "https://maps.googleapis.com/maps/api/place/photo?$params"
    }


}
