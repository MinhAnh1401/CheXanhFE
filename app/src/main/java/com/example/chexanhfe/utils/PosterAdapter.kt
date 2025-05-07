package com.example.chexanhfe.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.chexanhfe.R

class PosterPagerAdapter(private val posters: List<Int>) :
    RecyclerView.Adapter<PosterPagerAdapter.PosterViewHolder>() {

    inner class PosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val posterImage: ImageView = view.findViewById(R.id.posterImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_poster, parent, false)
        return PosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.posterImage.setImageResource(posters[position])
    }

    override fun getItemCount(): Int = posters.size
}