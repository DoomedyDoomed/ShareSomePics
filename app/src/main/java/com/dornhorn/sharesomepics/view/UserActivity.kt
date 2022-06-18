package com.dornhorn.sharesomepics.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.dornhorn.sharesomepics.R
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val buttonLog = findViewById<Button>(R.id.buttonLog)
        buttonLog.setOnClickListener {
            login()
        }
        val buttonReg = findViewById<Button>(R.id.buttonReg)
        buttonReg.setOnClickListener{
            register()
        }

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun login(){
        val email = findViewById<TextView>(R.id.emailadress)
        val passw = findViewById<TextView>(R.id.password)
        auth.signInWithEmailAndPassword(email.text.toString(),passw.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Login Success!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }

    fun register(){
        val email = findViewById<TextView>(R.id.emailadress)
        val passw = findViewById<TextView>(R.id.password)

        auth.createUserWithEmailAndPassword(email.text.toString(),passw.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                // Go to FEED ACTIVITY
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{ exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_SHORT).show()
        }
    }
}