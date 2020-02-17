package com.thesis.routegenerator

import com.thesis.routegenerator.api.ApiService
import com.thesis.routegenerator.model.*

import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Before
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class UnitTest {

    private lateinit var userInput: UserInput
    private lateinit var allRoutes: AllRoutes

    @Before
    fun setup(){
        this.userInput = UserInput("requestRoute", 1000, LocationWrapper(Location(52.2162238, 21.0139582)), LocationWrapper(Location(52.2162248, 21.0239502)),
           Arrays.asList("Bar", "Cafe", "Casino"), 0, "20191119", 4, 1330, 1730,
            arrayListOf<String>(),2323)
        this.allRoutes = AllRoutes("BFS", 0.0, arrayListOf(Route("ChIJX-IJVe_MHkcR914dYc5r0Do", "Bar-I","bar","14:38","16:40",
            "01:58", Location(52.219649,  21.017303), Event("link", "event name"), "ADDRESS", "343434343",
                    4.5f, null, null, null)))

    }


    @Test fun userInputCheck(){
        assertEquals(userInput.action, "requestRoute")
        assertEquals(userInput.frequency, 0)
        assertEquals(userInput.dateFrom, "20191119")
        assertEquals(userInput.week, 4)
        assertEquals(userInput.timeFrom, 1330)
        assertEquals(userInput.timeTo, 1730)
    }

    @Test fun allRoutesCheck(){
        assertEquals(allRoutes.algorithm, "BFS")
        assertEquals(allRoutes.route[0].timeArrive, "14:38")
        assertEquals(allRoutes.route[0].timeDeparture,"16:40" )
        assertEquals(allRoutes.route[0].timeSpent, "01:58")
    }

    @Test fun serverRequest(){
        val call = ApiService.routeApi.requestRoute(userInput)
        call.enqueue(object : Callback<RouteResponse> {
            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                assertEquals("Error","Error")
            }

            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if(response.isSuccessful) {
                    assertEquals(response.body()!!.allRoutes[0].algorithm, allRoutes.algorithm)
                }

            }
        })
    }

   @Test fun priorityCheck(){
       assertEquals(userInput.priorityType, Arrays.asList("Bar", "Cafe", "Casino"))
   }

    @Test fun locationCheck(){
        assertEquals(userInput.startLocation,  LocationWrapper(Location(52.2162238, 21.0139582)))
        assertEquals(userInput.endLocation, LocationWrapper(Location(52.2162248, 21.0239502)))
    }


}
