package com.dornhorn.sharesomepics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dornhorn.sharesomepics.R
import com.dornhorn.sharesomepics.model.Post
import com.squareup.picasso.Picasso


class FeedRecyclerAdapter(private val postList : ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>(){

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.userEmailField).text = postList[position].userEmail
        holder.itemView.findViewById<TextView>(R.id.userCommentText).text = postList[position].userComment

        val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.get().load(postList[position].imageUrl).into(imageView)
    }

    //How many items will it have?
    override fun getItemCount(): Int {
        return postList.size
    }
}