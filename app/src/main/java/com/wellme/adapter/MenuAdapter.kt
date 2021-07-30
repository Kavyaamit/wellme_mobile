package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.MenuDTO
import com.wellme.utils.OnItemClickListener

class MenuAdapter(var context : Context, var list : ArrayList<MenuDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<MenuAdapter.ViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.setText(list.get(position).title)
        holder.iv_menu.setImageDrawable(list.get(position).d1)
        holder.full_layout.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
        var font : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_title.setTypeface(font)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tv_title : TextView = view.findViewById(R.id.tv_title)
        var full_layout : LinearLayout = view.findViewById(R.id.full_layout)
        var iv_menu : ImageView = view.findViewById(R.id.iv_menu)

    }
}