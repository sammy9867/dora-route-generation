package com.thesis.routegenerator.model

import com.google.gson.annotations.SerializedName

/**
 * Model class Location represents the location
 * @param lat: Latitude of a given place.
 * @param lng: Longitude of a given place.
 **/
data class Location(@SerializedName("lat") var lat : Double,
                    @SerializedName("lng") var lng : Double)

data class LocationWrapper(@SerializedName("location") var location : Location)