package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R

class MyCoachExpertiseAdapter(var context1: Context) : RecyclerView.Adapter<MyCoachExpertiseAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_extertise : TextView = view1.findViewById(R.id.tv_expertise)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view1 : View = LayoutInflater.from(context1).inflate(R.layout.item_expertise, parent, false)
            return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context1, R.font.poppins_regular)
        holder.tv_extertise.setTypeface(regular)
    }
}