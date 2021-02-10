package com.example.currentlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Login : AppCompatActivity() {
    lateinit var email:EditText
    lateinit var password:EditText
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var userRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById (R.id.emailId)
        password = findViewById (R.id.passwordId)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("Users")
        }

    fun login(view: View) {
        if (email.text.toString().isEmpty()) {
            email.error ="Please Enter Email"
        }else if (password.text.toString().isEmpty()) {
        password.error = "Please Enter Password"
        }else {
           mAuth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {
               if (it.isSuccessful){
                   if (mAuth.currentUser?.isEmailVerified!!){
                       Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()

                       userRef.orderByChild("email").equalTo(email.text.toString()).addValueEventListener(object :ValueEventListener {
                           override fun onCancelled(error: DatabaseError) {
                               TODO("Not yet implemented")
                           }
                           override fun onDataChange(snapshot: DataSnapshot) {
                              for (ds in snapshot.children){
                                  var name = ds.child("name").value.toString()
                                  var phone = ds.child("phone").value.toString()
                                  var email = ds.child("email").value.toString()
                                  var intent= Intent(this@Login,SendLocation::class.java)
                                  intent.putExtra("UserPhone",phone)
                                  startActivity(intent)

                              }

                           }
                       })

                   }else{
                       Toast.makeText(this, "Please Verify Email First", Toast.LENGTH_SHORT).show()
                   }

               }else{
                   Toast.makeText(this, "Error Occurred ${it.exception?.message}", Toast.LENGTH_SHORT).show()
               }
           }
           }
        }

    fun movetosignup(view: View) {
        var intent = Intent(this, Signup::class.java)
        startActivity(intent)
    }
}