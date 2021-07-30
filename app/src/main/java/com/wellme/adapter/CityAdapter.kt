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
import com.wellme.dto.CityDTO
import com.wellme.utils.OnItemClickListener

class CityAdapter(var context: Context, var city_list : List<CityDTO>,var city_filterlist : MutableList<CityDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback, var pos : Int,var id : String) :  RecyclerView.Adapter<CityAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var iv_check : ImageView = view1.findViewById(R.id.iv_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_selection, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return city_filterlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : CityDTO = city_filterlist.get(position)
        holder.tv_title!!.text = dto.name
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_title.setTypeface(regular)
        if(id.equals(dto.id)){
            holder.iv_check!!.visibility = View.VISIBLE
        }
        else{
            holder.iv_check!!.visibility = View.GONE
        }
        holder.view1.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }

    public fun notifyData(){

        notifyDataSetChanged()
    }
}