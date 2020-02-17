package com.thesis.routegenerator.model

/**
 * Model class PlaceSearch represents the items in the recyclerView of setLocationFragment
 * @param placeId: place id
 * @param fullText: text returned from GooglePlaces API
 **/
data class PlaceSearchModel(var placeId : String,
                            var fullText : String)