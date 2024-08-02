package com.krashkrosh748199.GmVibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krashkrosh748199.GmVibe.Adapter.CommentsAdapter
import com.krashkrosh748199.GmVibe.Model.Comment
import com.krashkrosh748199.GmVibe.Model.User
import com.squareup.picasso.Picasso

class CommentsActivity : AppCompatActivity()
{
    private var postId = ""
    private  var publisherId = ""
    private var firebaseUser:FirebaseUser? = null
    private var commentAdapter: CommentsAdapter? = null
    private var commentList:MutableList<Comment>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)


        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView:RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapter(this,commentList)
        recyclerView.adapter = commentAdapter

        userInfo()
        readComments()
        getPostImage()

        findViewById<TextView>(R.id.post_comment).setOnClickListener(View.OnClickListener {
            if(findViewById<EditText>(R.id.add_comment)!!.text.toString()=="")
            {
                Toast.makeText(this@CommentsActivity,"Please write comment first.",Toast.LENGTH_LONG).show()

            }
            else
            {
                addComment()
            }
        })
    }

    private fun addComment()
    {
        val commentsRef= FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)

        val commentsMap = HashMap<String,Any>()
        commentsMap["comment"] = findViewById<EditText>(R.id.add_comment)!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid
        commentsRef.push().setValue(commentsMap)
         findViewById<EditText>(R.id.add_comment)!!.text.clear()
    }

    private fun userInfo(){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {

                if(pO.exists()){
                    val user=pO.getValue<User>(User::class.java)
                    val imageComment=findViewById<ImageView>(R.id.profile_image_comment)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageComment)

                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

    private fun getPostImage(){
        val postRef= FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId).child("postimage")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {

                if(pO.exists()){
                    val image=pO.value.toString()
                    val imageComment=findViewById<ImageView>(R.id.post_image_comment)
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(imageComment)

                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

    private fun readComments(){
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                   if (po.exists())
                   {
                       commentList!!.clear()

                       for(snapshot in po.children){
                          val comment = snapshot.getValue(Comment::class.java)
                           commentList!!.add(comment!!)
                       }
                       commentAdapter!!.notifyDataSetChanged()
                   }
            }

            override fun onCancelled(po: DatabaseError) {

            }
        })

    }


}