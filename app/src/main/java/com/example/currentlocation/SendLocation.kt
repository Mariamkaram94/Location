

package com.example.currentlocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*

class SendLocation : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latText: Double = 0.0
    var longText: Double = 0.0
    lateinit var database: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: RecyclerViewAdapter
    lateinit var array : ArrayList<ModelClass>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_location)
        database = FirebaseDatabase.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userRef = database.getReference("Users")
        recyclerView = findViewById(R.id.recyclerId)
        array = ArrayList<ModelClass>()


        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    var name = ds.child("name").value.toString()
                    var phone = ds.child("phone").value.toString()
                    array.add(ModelClass(name , phone))
                }
                mAdapter = RecyclerViewAdapter(array)
                recyclerView.adapter=mAdapter
            }
        })

    }

    fun sendlocation(view: View) {
        userRef = database.getReference("UserLocation")
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    latText = "${location?.latitude}".toDouble()
                    longText = " ${location?.longitude}".toDouble()

                }
        val map = HashMap<String, String>()
        var phone =intent.getStringExtra("UserPhone")
        map["phone"] = phone.toString()
        map["lat"] = latText.toString()
        map["long"] = longText.toString()
        userRef.child(phone.toString()).setValue(map)
        Toast.makeText(this, "Location sent", Toast.LENGTH_SHORT).show()

    }

}