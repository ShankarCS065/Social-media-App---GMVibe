package com.krashkrosh748199.GmVibe.Adapter

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.krashkrosh748199.GmVibe.Fragments.PostDetailsFragment
import com.krashkrosh748199.GmVibe.Model.Post
import com.krashkrosh748199.GmVibe.R
import com.squareup.picasso.Picasso

class MyImagesAdapter(private val mContext:Context,mPost:List<Post>)
    :RecyclerView.Adapter<MyImagesAdapter.ViewHolder?>()
{
    private var mPost:List<Post>? = null

    init {
        this.mPost = mPost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout,parent,false)
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mPost!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost!![position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId",post.getPostid())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,PostDetailsFragment()).commit()
        }
    }

    inner class ViewHolder(@NonNull itemView:View)
        :RecyclerView.ViewHolder(itemView)
    {
        var postImage:ImageView

        init {
            postImage = itemView.findViewById(R.id.post_image)
        }
    }
}