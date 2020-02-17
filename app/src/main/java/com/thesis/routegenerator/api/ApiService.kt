package com.thesis.routegenerator.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit


/**
 * Service class for route API.
 */
object ApiService {
    //creating a Network Interceptor to add api_key in all the request as authInterceptor
    private val interceptor = Interceptor { chain ->
        val url = chain.request().url().newBuilder().build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        chain.proceed(request)
    }
    // we are creating a networking client using OkHttp and add our authInterceptor.
    private val apiClient = OkHttpClient().newBuilder().addInterceptor(interceptor).build()

    private fun getRetrofit(URL : String): Retrofit {
        return Retrofit.Builder().client(apiClient)
            .baseUrl(URL)
            .addConverterFactory(ApiWorker.gsonConverter)
            .client(ApiWorker.client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    val routeApi: RouteApiInterface = getRetrofit("https://5yac54tula.execute-api.eu-central-1.amazonaws.com/").create(RouteApiInterface::class.java)
}