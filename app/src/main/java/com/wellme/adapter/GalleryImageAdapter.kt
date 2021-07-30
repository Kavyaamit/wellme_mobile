package com.wellme.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.wellme.R

class GalleryImageAdapter(var context : Context, var list : ArrayList<String>) : BaseAdapter(){
    var inflater : LayoutInflater? = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view : View = inflater!!.inflate(R.layout.gallery_item, parent, false)
        var imageView  : ImageView = view.findViewById(R.id.gallery_image)
        Log.v("Image Path", "==> "+list.get(position))
        Glide.with(context).load(list.get(position)).placeholder(R.drawable.app_info).into(imageView)

        return view
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }
}