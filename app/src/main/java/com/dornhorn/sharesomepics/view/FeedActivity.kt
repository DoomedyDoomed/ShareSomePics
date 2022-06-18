package com.dornhorn.sharesomepics.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dornhorn.sharesomepics.R
import com.dornhorn.sharesomepics.SharePhotograph
import com.dornhorn.sharesomepics.adapter.FeedRecyclerAdapter
import com.dornhorn.sharesomepics.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private var postList = ArrayList<Post>()
    private lateinit var recyclerViewAdapter: FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getDataFromDatabase()

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = FeedRecyclerAdapter(postList)
        recyclerView.adapter = recyclerViewAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromDatabase(){
        database.collection("Post").orderBy("Post Date", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }else
            {
                if(snapshot != null)
                {
                    if (!snapshot.isEmpty)
                    {
                        val documents = snapshot.documents

                        postList.clear()

                        for(document in documents)
                        {
                            val userEmail  = document.get("User Email") as String
                            val userComment  = document.get("User Comment") as String
                            val imageUrl  = document.get("Image URL") as String
                            val downloadedPost = Post(userEmail,userComment,imageUrl)
                            postList.add(downloadedPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.share_photo){
            //share photo activity go
            val intent = Intent(this, SharePhotograph::class.java)
            startActivity(intent)
        }else if(item.itemId == R.id.logout){
            //Logout function called and go user activity
            auth.signOut()
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}