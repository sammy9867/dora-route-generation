package com.thesis.routegenerator.model

import com.google.gson.annotations.SerializedName

/**
 * Model class UserInput represents the userInput that will be sent to the server.
 * @param action: Action name for the JSON file
 * @param userId: Unique user-id for each android device
 * @param startLocation: Starting point for the user
 * @param endLocation: Ending point for the user
 * @param priorityType: 3 selected items based on priority
 * @param frequency: Whether user wants to visit less/medium/more places
 * @param dateFrom: Date from
 * @param week: selected day of the week [0 - Sunday, 1 - Monday, ...]
 * @param timeFrom: Time From
 * @param timeTo: Time To
 * @param history: History of the places visited
 * @param timestamp: Timestamp
 */

data class UserInput(@SerializedName("action") var action : String,
                     @SerializedName("userId") var userId : Long,
                     @SerializedName("startLocation") var startLocation : LocationWrapper,
                     @SerializedName("endLocation") var endLocation : LocationWrapper,
                     @SerializedName("priorityType")var priorityType: List<String>,
                     @SerializedName("frequency")var frequency : Int,
                     @SerializedName("dateFrom") var  dateFrom : String,
                     @SerializedName("week")var week : Int,
                     @SerializedName("timeFrom")var timeFrom : Int,
                     @SerializedName("timeTo")var timeTo : Int,
                     @SerializedName("history")var history: List<String>,
                     @SerializedName("timestamp")var timestamp: Int)