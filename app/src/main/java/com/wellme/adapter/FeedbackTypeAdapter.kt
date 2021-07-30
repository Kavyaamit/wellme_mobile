package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.FeedbackTypeDTO
import com.wellme.utils.OnItemClickListener

class FeedbackTypeAdapter(var context: Context, var list : List<FeedbackTypeDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<FeedbackTypeAdapter.ViewHolder>() {

    class ViewHolder(view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_data : TextView = view1.findViewById(R.id.tv_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_data, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        Log.v("Size Data", "==>"+list.size)
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_data.setTypeface(regular)
        holder.tv_data.setText(list.get(position).feedback_name)
        holder.itemView.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }
}