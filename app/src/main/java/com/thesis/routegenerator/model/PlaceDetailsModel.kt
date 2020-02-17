package com.thesis.routegenerator.model

/**
 * Model class PlaceDetails represents the items in the placeDetailsFragment
 * @param name: place name
 * @param formatted_address: place address
 * @param formatted_phone_number: place phone number
 * @param rating: place rating
 * @param website: place website
 * @param opening_hours: list of place opening hours
 * @param photos: list of place photos
 **/
data class PlaceDetailsModel(var name: String?,
                             var formatted_address : String?,
                             var formatted_phone_number : String?,
                             var rating: Float?,
                             var website : String?,
                             var opening_hours : MutableList<String>?,
                             var photos : MutableList<String>?,
                             var facebookLink : String?)