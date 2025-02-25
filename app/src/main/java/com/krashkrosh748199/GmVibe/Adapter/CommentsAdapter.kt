package com.krashkrosh748199.GmVibe.Adapter

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krashkrosh748199.GmVibe.Model.Comment
import com.krashkrosh748199.GmVibe.Model.User
import com.krashkrosh748199.GmVibe.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter(private val mContext: Context,
private val mComment: MutableList<Comment>?
):RecyclerView.Adapter<CommentsAdapter.ViewHolder>()
{
    private var firebaseUser:FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
          val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mComment!![position]
        holder.commentTV.text = comment.getComment()

        getUserInfo(holder.imageProfile,holder.userNameTV,comment.getPublisher())
    }



    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
       var imageProfile:CircleImageView
       var userNameTV:TextView
       var commentTV:TextView

       init {
           imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
           userNameTV = itemView.findViewById(R.id.user_name_comment)
           commentTV = itemView.findViewById(R.id.comment_comment)
       }
    }

    private fun getUserInfo(imageProfile: CircleImageView, userNameTV: TextView, publisher: String) {
      val userRef = FirebaseDatabase.getInstance().reference
          .child("Users")
          .child(publisher)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                   if (po.exists()){
                      val user = po.getValue(User::class.java)
                       Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)

                       userNameTV.text = user!!.getUsername()

                   }
            }

            override fun onCancelled(po: DatabaseError) {

            }
        })
    }

}