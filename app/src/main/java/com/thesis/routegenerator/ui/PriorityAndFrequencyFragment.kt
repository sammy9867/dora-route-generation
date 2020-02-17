package com.thesis.routegenerator.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.thesis.routegenerator.R
import com.thesis.routegenerator.adapter.SetPriorityAdapter
import com.thesis.routegenerator.databinding.FragmentPriorityAndFrequencyBinding
import com.thesis.routegenerator.model.PlaceType
import com.thesis.routegenerator.viewmodel.CommunicatorViewModel
import kotlinx.android.synthetic.main.fragment_priority_and_frequency.view.*
import java.util.*

class PriorityAndFrequencyFragment : Fragment() {

    var touchHelper : ItemTouchHelper? = null


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
        val binding: FragmentPriorityAndFrequencyBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_priority_and_frequency, container, false)

        val model= ViewModelProviders.of(activity!!).get(CommunicatorViewModel::class.java)

        //loading rank icons
        loadRankIcons(binding)

        // create mapping
        val placeTypeIconMap = populatePlaceType()

        //retrieve the elements from the priorityList that were set in the previous fragment
        model.priorityList.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {

                val list: MutableList<String> = o as MutableList<String>
                val placeTypeList = mutableListOf<PlaceType>()

                for (i in 0 until list.size){
                     placeTypeList.add(PlaceType(list[i],getPlaceTypeIcon(list[i], placeTypeIconMap)))
                }
                binding.rvPriority.adapter = SetPriorityAdapter(this@PriorityAndFrequencyFragment, placeTypeList, context!!)
                binding.rvPriority.layoutManager = LinearLayoutManager(context!!)

                // Handle drag and drop
                touchHelper =
                    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                        0) {
                        override fun onMove(
                            p0: RecyclerView,
                            p1: RecyclerView.ViewHolder,
                            p2: RecyclerView.ViewHolder
                        ): Boolean {
                            val sourcePosition = p1.adapterPosition
                            val targetPosition = p2.adapterPosition
                            Collections.swap(list,sourcePosition,targetPosition)
                            binding.rvPriority.adapter ?.notifyItemMoved(sourcePosition,targetPosition)
                            return true
                        }

                        override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                        }

                    })

                touchHelper?.attachToRecyclerView(binding.rvPriority)
            }
        })

       model.frequency.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                val temp = o as Int
               // Log.i("TEMP IS", temp.toString())
                when(temp){
                    0 ->  binding.radioGroup.radio0.isChecked = true
                    1 ->  binding.radioGroup.radio1.isChecked = true
                    2 -> binding.radioGroup.radio2.isChecked = true
                }
            }
        })


        //Initially, setting the choice object[low/med/more] as low. Will be changed depending on the onCheckedChangeListener
       if(model.frequency.value == null){
           model.setFrequency(0)
       }

        binding.radioGroup.setOnCheckedChangeListener{ _, checkedId ->
            val radio : RadioButton = view!!.findViewById(checkedId)
            when(radio.text.toString()){
                "LESS" ->  model.setFrequency(0)
                "MED" ->  model.setFrequency(1)
                "MORE" -> model.setFrequency(2)
            }
        }

        // navigate back to the previous fragment
        binding.icPriorityPrev.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_priorityAndFrequencyFragment_to_multiSelectionFragment))

        // navigate to the next fragment
        binding.icPriorityNext.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_priorityAndFrequencyFragment_to_setLocationFragment2))

        return binding.root
    }

    /**
     * Loading priority rank icons into their respective image view.
     */
    private fun loadRankIcons(binding : FragmentPriorityAndFrequencyBinding){

        Glide.with(context!!)
            .load(R.drawable.ic_first)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(binding.iconFirst)

        Glide.with(context!!)
            .load(R.drawable.ic_second)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(binding.iconSecond)

        Glide.with(context!!)
            .load(R.drawable.ic_third)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(binding.iconThird)
    }

    /**
     * Function that forms a mapping between place type and place type icons.
     */
    private fun populatePlaceType() : Map<String, Int>{

        val placeNames: ArrayList<String> =
            arrayListOf("Amusement Park", "Aquarium", "Art Gallery", "Bar", "Bowling Alley",  "Cafe", "Casino",
                "Church", "City Hall", "Liquor Store", "Mosque", "Movie Theater", "Museum", "Night Club", "Park", "Restaurant", "Shopping Mall",
                "Synagogue", "Tourist Attraction", "Zoo")

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
    private fun getPlaceTypeIcon(placeType : String, placeTypeIconMap : Map<String, Int>) : Int{
        for(i in placeTypeIconMap){
            if(placeType == i.key){
                return  i.value
            }
        }
        return R.drawable.marker_dest
    }


}
