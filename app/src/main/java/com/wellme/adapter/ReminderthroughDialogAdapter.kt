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
import com.wellme.dto.ReminderDataDTO
import com.wellme.utils.OnItemClickListener

class ReminderthroughDialogAdapter(var context : Context, var list : ArrayList<ReminderDataDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<ReminderthroughDialogAdapter.ViewHolder>(){
    var regular : Typeface? = null
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tv_data : TextView = view.findViewById(R.id.tv_data)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_data, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var pos : Int = position % list.size
        regular = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_data.setTypeface(regular)
        if(pos>=0){
            var dto : ReminderDataDTO = list.get(pos)
            if(dto!=null){
                holder.tv_data.setText(""+dto.value)
            }

        }

        holder.itemView.setOnClickListener(OnItemClickListener(pos, onItemClickCallback))


    }


}