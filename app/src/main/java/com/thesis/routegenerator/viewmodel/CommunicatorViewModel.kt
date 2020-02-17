package com.thesis.routegenerator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.thesis.routegenerator.model.AllRoutes
import com.thesis.routegenerator.model.PlaceType
import java.util.*

/**
 * A ViewModel class to store and manage UI-related data in a lifecycle conscious way
 * It handles the communication between fragments and holds/updates data automatically.
 */
class CommunicatorViewModel : ViewModel(){

    var startingLocation = MutableLiveData<com.thesis.routegenerator.model.LocationWrapper>()
    var endingLocation = MutableLiveData<com.thesis.routegenerator.model.LocationWrapper?>()
    var priorityList =  MutableLiveData<MutableList<String>>()

    var frequency = MutableLiveData<Int>()
    var dateFrom = MutableLiveData<String>()
    var week = MutableLiveData<Int>()
    var timeFrom = MutableLiveData<Int>()
    var timeTo = MutableLiveData<Int>()

    var startingAddress = MutableLiveData<String>()
    var endingAddress = MutableLiveData<String>()

    var tempStartingAddress = MutableLiveData<String?>()
    var tempEndingAddress = MutableLiveData<String?>()

    var isStartingLocationSelected = MutableLiveData<Boolean>()
    var isEndingLocationSelected = MutableLiveData<Boolean>()

    var placeTypeObject = MutableLiveData<MutableList<PlaceType>>()

    var dateFromTv = MutableLiveData<String>()
    var timeFromTv = MutableLiveData<String>()
    var timeToTv = MutableLiveData<String>()

    var dateforTimeFrom = MutableLiveData<Date>()
    var dateforTimeTo = MutableLiveData<Date>()

    var allRoutes = MutableLiveData<List<AllRoutes>?>()

    var directionChanger = MutableLiveData<Int>()
    var latlngBndsBuilder = MutableLiveData<LatLngBounds?>()

    fun setDirectionChanger(directionChanger : Int){
        this.directionChanger.value = directionChanger
    }

    fun setDateForTimeFrom(dateforTimeFrom : Date){
        this.dateforTimeFrom.value = dateforTimeFrom
    }

    fun setDateForTimeTo(dateforTimeTo : Date){
        this.dateforTimeTo.value = dateforTimeTo
    }



    fun setIsStartingLocationSelected(isStartingLocationSelected : Boolean){
        this.isStartingLocationSelected.value = isStartingLocationSelected
    }

    fun setIsEndingLocationSelected(isEndingLocationSelected : Boolean){
        this.isEndingLocationSelected.value = isEndingLocationSelected
    }

    fun setStartingAddress(startingAddress : String){
        this.startingAddress.value = startingAddress
    }

    fun setEndingAddress(endingAddress : String){
        this.endingAddress.value = endingAddress
    }

    fun setStartingLocation(location: com.thesis.routegenerator.model.LocationWrapper){
        this.startingLocation.value = location
    }

    fun setEndingLocation(location: com.thesis.routegenerator.model.LocationWrapper?){
        this.endingLocation.value = location
    }

    fun setPriorityList(priorityList :MutableList<String>){
        this.priorityList.value = priorityList
    }

    fun setFrequency(frequency : Int){
        this.frequency.value = frequency
    }


    fun setDateFrom(dateFrom : String){
        this.dateFrom.value = dateFrom
    }

    fun setWeek(week : Int){
        this.week.value = week
    }

    fun setTimeFrom(timeFrom : Int){
        this.timeFrom.value = timeFrom
    }

    fun setTimeTo(timeTo : Int){
        this.timeTo.value = timeTo
    }


}