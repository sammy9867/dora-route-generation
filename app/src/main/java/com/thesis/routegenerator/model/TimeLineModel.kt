package com.thesis.routegenerator.model

/**
 * Model class TimeLineModel represents the items in the timeline View.
 * @param place_name: Given place name
 * @param place_type: Given place type
 * @param place_icon: Given place icon
 * @param expected_arrival: Expected arrival time to reach this place
 * @param expected_departure: Expected departure time to leave this place
 */
data class TimeLineModel(
    var place_name: String,
    var place_type: String,
    var place_icon: Int,
    var expected_arrival: String,
    var expected_departure: String
)