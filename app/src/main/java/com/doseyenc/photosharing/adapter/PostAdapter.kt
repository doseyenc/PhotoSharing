package com.doseyenc.photosharing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.doseyenc.photosharing.R
import com.doseyenc.photosharing.databinding.PostItemDesignBinding
import com.doseyenc.photosharing.model.PostModel

class PostAdapter(

):  RecyclerView.Adapter< PostAdapter.PostViewHolder>() {
    private val list = mutableListOf<PostModel>()
    class PostViewHolder(val binding: PostItemDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(post.imageUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageView)

                textViewUserName.text = post.userEmail
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            PostItemDesignBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = list.size
    fun updateList(newList: MutableList<PostModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }


}