package com.example.currentlocation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    fun makeRetrofitService() : ConnectionEndPoint {
return Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ConnectionEndPoint::class.java)
    }

}