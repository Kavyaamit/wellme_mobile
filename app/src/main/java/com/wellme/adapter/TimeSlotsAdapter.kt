package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.TimeSlotsDTO
import com.wellme.utils.OnItemClickListener

class TimeSlotsAdapter(var context : Context, var goal_list : List<TimeSlotsDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback, var pos : Int) : RecyclerView.Adapter<TimeSlotsAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1) {
        var tv_title : TextView? = view1.findViewById(R.id.tv_time)
        var root : LinearLayout? = view1.findViewById(R.id.rootlayout)
//        var iv_check : ImageView? = view1.findViewById(R.id.iv_check)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_time_slots, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return goal_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : TimeSlotsDTO = goal_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var isDisable : Boolean = dto.disable
        holder.tv_title!!.text = dto.time
        holder.tv_title!!.setTypeface(regular)
        Log.d("time_pos ",">>>>>"+pos)

        if (isDisable){

            holder.root!!.setBackgroundResource(R.drawable.white_rounded_corner_new_bg)

        }else{
            holder.root!!.setBackgroundResource(R.drawable.grey_rounded_corner_bg)
        }

        if(pos == position){
            holder.root!!.setBackgroundResource(R.drawable.grey_rounded_corner_new_bg)
        }
        holder.view1.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }



    public fun setData(id1 : Int){
        this.pos = id1
        notifyDataSetChanged()
    }

    public fun setnotify(){

        notifyDataSetChanged()
    }
}