package com.krashkrosh748199.GmVibe.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krashkrosh748199.GmVibe.Fragments.ProfileFragment
import com.krashkrosh748199.GmVibe.Model.User
import com.krashkrosh748199.GmVibe.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private var mContext: Context,
                  private var mUser:List<User>,
                  private val isFragment: Boolean=false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser:FirebaseUser?=FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {

        val view =LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text= user.getUsername()
        holder.userFullNameTextView.text= user.getFullname()

        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)

        checkFollowingStatus(user.getUID(),holder.followButton)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("profileId",user.getUID())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,ProfileFragment()).commit()

        })

        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString()=="Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener {task ->
                            if(task.isSuccessful){

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {


                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener {task ->
                            if(task.isSuccessful){

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {


                                            }
                                        }
                                }
                            }
                        }
                }

            }
        }
    }


    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userNameTextView:TextView= itemView.findViewById(R.id.user_name_search)
        var userFullNameTextView:TextView= itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage:CircleImageView= itemView.findViewById(R.id.user_profile_image_search)
        var followButton:Button= itemView.findViewById(R.id.follow_btn_search)


    }

    private fun checkFollowingStatus(uid: String, followButton: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(uid).exists()) {
                    followButton.text = "Following"
                } else {
                    followButton.text = "Follow"
                }

            }

                override fun onCancelled(dataSnapshot: DatabaseError) {

                }

        })

        }

}
