package com.example.currentlocation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ConnectionEndPoint {
    @GET ("api/directions/json")
    fun getGoogleDirections (@Query("origin") origin:String,
    @Query("destination") destination:String,
    @Query("key") key:String):
//  optional, don't have to use it  @Query("mode") mode:String):
  Call<GoogleDirectionAPI> }