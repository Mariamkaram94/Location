package com.example.currentlocation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var database: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var getTime: TextView
    lateinit var getDistance: TextView
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("UserLocation")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getTime = findViewById(R.id.timeId)
        getDistance = findViewById(R.id.distanceId)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        var phone = intent.getStringExtra("UserNumber")
        userRef.orderByChild("phone").equalTo(phone.toString()).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    var lat = ds.child("lat").value.toString().toDouble()
                    var long = ds.child("long").value.toString().toDouble()
                    mMap = googleMap
                    // Add a marker in Position and move the camera
                    val location = LatLng(lat, long)
                    mMap.addMarker(MarkerOptions().position(location).title("Marker in User Position"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                }
            }
        })
//          if( ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
//
//        }else{
//
//            mMap.isMyLocationEnabled = true
//        }
    }

    fun callretrofit(view: View) {
        mMap.clear()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                var currentLocation = "${location?.latitude},${location?.longitude}"
               // mMap.addMarker(MarkerOptions().position(LatLng(location?.latitude!!.toDouble(), location?.longitude!!.toDouble())).title("Marker in Current Position"))

                var Userphone = intent.getStringExtra("UserNumber")
                userRef.orderByChild("phone").equalTo(Userphone.toString()).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            var latitude = ds.child("lat").value.toString()
                            var longitude = ds.child("long").value.toString()
                            var userLocation = "${latitude},${longitude}"

                            ServiceBuilder.makeRetrofitService().getGoogleDirections(currentLocation, userLocation, "AIzaSyDFTBnzxyAXbNWiOW5LKmDJr7flZPTCXfs")
                                    .enqueue(object : Callback<GoogleDirectionAPI> {
                                        override fun onResponse(call: Call<GoogleDirectionAPI>, response: Response<GoogleDirectionAPI>) {
                                            try {
                                                drawDirections(location?.latitude!!.toDouble(), location?.longitude.toDouble(), latitude.toDouble(), longitude.toDouble(), mMap)
                                                getDistance.text = response.body()!!.routes[0].legs[0].distance.text
                                                getTime.text = response.body()!!.routes[0].legs[0].duration.text

                                            } catch (t: Exception) {

                                                Toast.makeText(applicationContext, "${t.message.toString()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<GoogleDirectionAPI>, t: Throwable) {
                                            Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                    })
                        }
                    }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                })
            }
        }
    }
    fun drawDirections(startLat: Double, startLon: Double, endLat: Double, endLon: Double, map: GoogleMap) {

        val path: MutableList<LatLng> = ArrayList()
        val context = GeoApiContext().setQueryRateLimit(3).setApiKey("AIzaSyDFTBnzxyAXbNWiOW5LKmDJr7flZPTCXfs")
                .setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS)
        var latLngOrigin = LatLng(startLat, startLon)
        var latLngDestination = LatLng(endLat, endLon)
        map.addMarker(MarkerOptions().position(latLngOrigin).title("Origin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        map.addMarker(MarkerOptions().position(latLngDestination).title("Destintaion"))

        // animate camera to show map with 2 points only
        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        builder.include(latLngOrigin)
        builder.include(latLngDestination)
        val bounds: LatLngBounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 0)
        map.animateCamera(cu)

        Log.i("Draw", "$startLat,$startLon")
        val req = DirectionsApi.getDirections(context, "$startLat,$startLon", "$endLat,$endLon")
        try {
            val res = req.await()

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.size > 0) {
                val route = res.routes[0]
                if (route.legs != null) {
                    for (i in route.legs.indices) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in leg.steps.indices) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.size > 0) {
                                    for (k in step.steps.indices) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
        }
        if (path.size > 0) {
            val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(8f)
            map.addPolyline(opts)
        }
    }
}
