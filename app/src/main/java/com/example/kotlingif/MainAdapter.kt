package com.example.kotlingif

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_row.view.*

class MainAdapter(val homeFeed: Models.HomeFeed): RecyclerView.Adapter<MainAdapter.CustomViewHolder>() {

    override fun getItemCount(): Int {
        return homeFeed.data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val callForRow = layoutInflater.inflate(R.layout.image_row, parent, false)
        return CustomViewHolder(callForRow)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val data = homeFeed.data[position]

        val gifImage = holder.view.imageView

        Glide.with(holder.view.context)
            .asGif()
            .load(data.images.fixed_width.url)
            .into(gifImage)

    }
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){

    }

}

