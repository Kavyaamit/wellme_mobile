package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.WorkoutDTO
import com.wellme.utils.OnItemClickListener

class WorkoutAdapter(val context: Context, var list : List<WorkoutDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>(){


    class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        var tv_title : TextView = view.findViewById(R.id.tv_title)
        var iv_plus : ImageView = view.findViewById(R.id.iv_plus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.text = list.get(position).exercise_name
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_title.setTypeface(regular)
        holder.iv_plus.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }
}