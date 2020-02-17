package com.thesis.routegenerator.model

import com.google.gson.annotations.SerializedName

/**
 * Model classes to deserialize routes.json received from the server.
 */
data class Event(var facebook : String?, var event_name : String?)

data class Route(var placeId : String, var name: String, var type: String, var timeArrive : String, var timeDeparture : String, var timeSpent : String,
                 var location: Location, var event: Event?,
                 var formatted_address : String?,
                 var formatted_phone_number : String?,
                 var rating: Float?,
                 var website : String?,
                 var opening_hours : MutableList<String>?,
                 var photos : MutableList<String>?)

data class AllRoutes(var algorithm: String, var comp_time: Double, var route : List<Route>)

class RouteResponse(@SerializedName("action")var action: String,
                    @SerializedName("allRoutes")var allRoutes: List<AllRoutes>,
                    @SerializedName("timestamp") var timestamp: Double)