package com.thesis.routegenerator.model

/**
 * Model class PlaceType represents each item in the multi-selection list fragment.
 * @param placeName: Name of the PlaceType.
 * @param placeIcon: Icon of the PlaceType [for now, default for all].
 * @param isSelected: Checks whether an item is selected in the list or not.
 */
data class PlaceType(var placeName : String, var placeIcon : Int, var isSelected : Boolean = false)