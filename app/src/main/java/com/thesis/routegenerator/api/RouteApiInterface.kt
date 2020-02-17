package com.thesis.routegenerator.api


import com.thesis.routegenerator.model.RouteResponse
import com.thesis.routegenerator.model.UserInput
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface that handles all external APIs.
 */
interface RouteApiInterface {

    @POST("v1/process_user_request")
    fun requestRoute(@Body userInput: UserInput) : Call<RouteResponse>
}

