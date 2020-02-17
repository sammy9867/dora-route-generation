package com.thesis.routegenerator.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.thesis.routegenerator.R
import com.thesis.routegenerator.adapter.MultiSelectionAdapter
import com.thesis.routegenerator.databinding.FragmentMultiSelectionBinding
import com.thesis.routegenerator.model.PlaceType
import com.thesis.routegenerator.viewmodel.CommunicatorViewModel
import com.shashank.sony.fancytoastlib.FancyToast

class MultiSelectionFragment : Fragment() {

    private var model: CommunicatorViewModel?=null

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
        val binding: FragmentMultiSelectionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_multi_selection, container, false
        )

        // to store values that can be communicated between fragments
        model = ViewModelProviders.of(activity!!).get(CommunicatorViewModel::class.java)

        val adapter : MultiSelectionAdapter
        if(model!!.priorityList.value == null){ //Setting up the recyclerView adapter
            model!!.priorityList.value = mutableListOf()
            model!!.placeTypeObject.value = mutableListOf()
            populatePlaceType(model!!.placeTypeObject.value!!)
            adapter = MultiSelectionAdapter(model!!.priorityList.value!!, model!!.placeTypeObject.value!!, context!!)
        }else{
            adapter = MultiSelectionAdapter(model!!.priorityList.value!!, model!!.placeTypeObject.value!!, context!!)
        }

        binding.rvPlace.adapter = adapter
        binding.rvPlace.layoutManager = LinearLayoutManager(context)

        // navigate back to the previous fragment
        binding.icMultiNext.setOnClickListener {view ->
            if(model!!.priorityList.value!!.size == 3) {  // Only when 3 items are selected, navigate to the next fragment
                view.findNavController().navigate(R.id.action_multiSelectionFragment_to_priorityAndFrequencyFragment)
                model!!.setPriorityList(model!!.priorityList.value!!)
            }else{
                FancyToast.makeText(context, "Select 3 Items only", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            }
        }

        return binding.root
    }

    /**
     * Populating the recyclerView with default items that are supported by Google Places.
     */
    private fun populatePlaceType(placeList : MutableList<PlaceType>){
        val placeNames: ArrayList<String> =
            arrayListOf("Tourist Attraction", "Amusement Park",  "Art Gallery", "Museum","Cafe" ,"Bar", "Night Club","Restaurant", "Bowling Alley",
                "Casino", "Park",  "Shopping Mall", "Church", "Mosque","Synagogue","City Hall",  "Movie Theater", "Liquor Store",
                "Aquarium", "Zoo")

        val placeIcons :  ArrayList<Int> = arrayListOf(R.drawable.ic_tourist_attraction, R.drawable.ic_amusement_park, R.drawable.ic_art_gallery,R.drawable.ic_museum,
            R.drawable.ic_cafe,R.drawable.ic_bar, R.drawable.ic_night_club, R.drawable.ic_restaurant,  R.drawable.ic_bowling_alley,   R.drawable.ic_casino,
            R.drawable.ic_park,R.drawable.ic_shopping_mall, R.drawable.ic_church, R.drawable.ic_mosque, R.drawable.ic_synagogue, R.drawable.ic_city_hall,  R.drawable.ic_movie_theater, R.drawable.ic_liquor_store,
            R.drawable.ic_aquarium , R.drawable.ic_zoo)

        val placeMap: Map<String, Int> = placeNames.zip(placeIcons).toMap()
        for(i in placeMap){
            placeList.add(PlaceType(i.key, i.value))
        }

    }



}
