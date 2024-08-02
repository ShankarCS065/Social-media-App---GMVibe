package com.krashkrosh748199.GmVibe.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krashkrosh748199.GmVibe.AccountSettingsActivity
import com.krashkrosh748199.GmVibe.Adapter.MyImagesAdapter
import com.krashkrosh748199.GmVibe.Model.Post
import com.krashkrosh748199.GmVibe.Model.User
import com.krashkrosh748199.GmVibe.R
import com.krashkrosh748199.GmVibe.R.layout.fragment_profile
import com.squareup.picasso.Picasso
import java.util.Collections

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
      private lateinit var profileId:String
      private lateinit var firebaseUser: FirebaseUser

      var postList:List<Post>? = null
      var myImagesAdapter:MyImagesAdapter? = null

      var myImagesAdapterSavedImg:MyImagesAdapter? = null
      var postListSaved:List<Post>? = null
      var mySavesImg:List<String>? = null



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if(pref!=null){
            this.profileId = pref.getString("profileId", "none").toString()
        }
        if(profileId == firebaseUser.uid){
            view.findViewById<Button>(R.id.edit_account_setting_btn).text = "Edit Profile"
        }
        else if(profileId != firebaseUser.uid){
            checkFollowAndFollowingButtonStatus()
        }


        //recycler view for uploaded images

        var recyclerViewUploadImage:RecyclerView
        recyclerViewUploadImage = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImage.setHasFixedSize(true)
        val linearLayoutManager:LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewUploadImage.layoutManager = linearLayoutManager
        postList = ArrayList()
        myImagesAdapter = context?.let {MyImagesAdapter(it,postList as ArrayList<Post>)}
        recyclerViewUploadImage.adapter = myImagesAdapter


        //recycler view for saved images

        var recyclerViewSavedImage:RecyclerView
        recyclerViewSavedImage = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImage.setHasFixedSize(true)
        val linearLayoutManager2:LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewSavedImage.layoutManager = linearLayoutManager2
        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let {MyImagesAdapter(it,postListSaved as ArrayList<Post>)}
        recyclerViewSavedImage.adapter = myImagesAdapterSavedImg


        recyclerViewSavedImage.visibility = View.GONE
        recyclerViewUploadImage.visibility = View.VISIBLE


        var uploadedImagesBtn:ImageButton
        uploadedImagesBtn = view.findViewById(R.id.images_grid_view_btn)
        uploadedImagesBtn.setOnClickListener{
            recyclerViewSavedImage.visibility = View.GONE
            recyclerViewUploadImage.visibility = View.VISIBLE
        }

        var savedImagesBtn:ImageButton
        savedImagesBtn = view.findViewById(R.id.images_save_btn)
        savedImagesBtn.setOnClickListener{
            recyclerViewSavedImage.visibility = View.VISIBLE
            recyclerViewUploadImage.visibility = View.GONE
        }



        view.findViewById<Button>(R.id.edit_account_setting_btn).setOnClickListener {
            val getButtonText = view.findViewById<Button>(R.id.edit_account_setting_btn).text.toString()

            when{
                getButtonText == "Edit Profile" -> startActivity(Intent(context,AccountSettingsActivity::class.java))

                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }
            }

        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()


        return view

    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        if(followingRef!=null) {
            followingRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(pO: DataSnapshot) {
                    if (pO.child(profileId).exists()){

                        view?.findViewById<Button>(R.id.edit_account_setting_btn)?.text  = "Following"

                    }
                    else{
                        view?.findViewById<Button>(R.id.edit_account_setting_btn)?.text  = "Follow"

                    }
                }

                override fun onCancelled(pO: DatabaseError) {


                }
            })
        }
    }

    private fun getFollowers(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot) {

                if(pO.exists()){
                    view?.findViewById<TextView>(R.id.total_followers)?.text = pO.childrenCount.toString()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

    private fun getFollowings(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot) {

                if(pO.exists()){
                    view?.findViewById<TextView>(R.id.total_following)?.text = pO.childrenCount.toString()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

    private fun myPhotos(){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                if (po.exists())
                {
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in po.children){
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId)){
                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(po: DatabaseError) {

            }
        })

    }
    private fun userInfo(){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot) {

                if(pO.exists()){
                    val user=pO.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.findViewById(R.id.pro_image_profile_frag))
                    view?.findViewById<TextView>(R.id.profile_fragment_username)?.text = user!!.getUsername()
                    view?.findViewById<TextView>(R.id.full_name_profile_frag)?.text = user!!.getFullname()
                    view?.findViewById<TextView>(R.id.bio_profile_frag)?.text = user!!.getBio()

                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun getTotalNumberOfPosts(){

        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                 if (dataSnapshot.exists()){
                     var postCounter = 0
                     for (snapShot in dataSnapshot.children){
                         val post = snapShot.getValue(Post::class.java)!!
                         if(post.getPublisher() == profileId){
                             postCounter++
                         }

                     }

                     view!!.findViewById<TextView>(R.id.total_posts).text = " " + postCounter
                 }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun mySaves(){
          mySavesImg = ArrayList()
        val savedRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               if (dataSnapshot.exists()){
                   for (snapshot in dataSnapshot.children){
                      (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                   }
                   readSavedImagesData()
               }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }
        })
    }

    private fun readSavedImagesData() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    (postListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children){
                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavesImg!!){
                            if (post!!.getPostid() == key){
                                (postListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }
        })
    }

}