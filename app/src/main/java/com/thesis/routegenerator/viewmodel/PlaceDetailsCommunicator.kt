package com.thesis.routegenerator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.routegenerator.model.PlaceDetailsIcon
import com.thesis.routegenerator.model.PlaceDetailsModel

/**
 * A ViewModel class to store and manage UI-related data in a lifecycle conscious way
 * It handles the communication between map and placeDetails fragment and holds/updates data automatically.
 */
class PlaceDetailsCommunicator : ViewModel() {

    var placeDetailsModel = MutableLiveData<PlaceDetailsModel>()
    var placeDetailsIcon = MutableLiveData<MutableList<PlaceDetailsIcon>>()

    fun setPlaceDetailsModel(placeDetailsModel: PlaceDetailsModel) {
        this.placeDetailsModel.value = placeDetailsModel
    }

    fun setPlaceDetailsIcon(placeDetailsIcon: MutableList<PlaceDetailsIcon>) {
        this.placeDetailsIcon.value = placeDetailsIcon
    }


}