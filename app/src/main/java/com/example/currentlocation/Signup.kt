package com.example.currentlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    lateinit var name:EditText
    lateinit var email:EditText
    lateinit var password:EditText
    lateinit var phone:EditText
    lateinit var mAuth: FirebaseAuth
    lateinit var database :FirebaseDatabase
    lateinit var userRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        name = findViewById (R.id.nameId)
        email = findViewById (R.id.emailId)
        password = findViewById (R.id.passwordId)
        phone = findViewById (R.id.phoneId)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("Users")
    }
    fun signup(view: View){
        mAuth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {
            if (it.isSuccessful){
                mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                    var map = HashMap<String,String> ()
                    map["name"]= name.text.toString()
                    map["email"]= email.text.toString()
                    map["phone"]= phone.text.toString()
                    map["password"]= password.text.toString()
                    userRef.push().setValue(map)
                }
            } else {
                Toast.makeText(this, "Error Occurred ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }
}