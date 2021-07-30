package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.ReminderDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class ReminderAdapter(var context : Context, var list : List<ReminderDTO>, var onItemClickDetails : OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>(){
    var regular : Typeface? = null

    class ViewHolder(val view1 : View) : RecyclerView.ViewHolder(view1){
          var iv_img : ImageView = view1.findViewById(R.id.iv_img)
          var tv_title : TextView = view1.findViewById(R.id.tv_title)
          var tv_duration : TextView = view1.findViewById(R.id.tv_duration)
          var tv_edit : TextView = view1.findViewById(R.id.tv_edit)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_reminders, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        regular = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_title!!.setTypeface(regular)
        holder.tv_edit!!.setTypeface(regular)
        holder.tv_duration!!.setTypeface(regular)

        var dto : ReminderDTO = list.get(position)
        if(dto!=null){
            var flag : Boolean = dto.status
            var s_title : String? = dto.title
            var s_time : String? = dto.time
            var s_repeating_time : String? = dto.repeating_val
            if(flag){
                holder.tv_edit!!.setText(""+context.resources.getString(R.string.edit))
            }
            else{
                holder.tv_edit!!.setText(""+context.resources.getString(R.string.set))
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_time)){
                if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time)){
                    if(position == 2){
                        if(s_repeating_time.equals("day", true)){
                            holder.tv_duration.setText("Remind me once at "+s_time)
                        }
                        else if(s_repeating_time.equals("week", true)){
                            holder.tv_duration.setText("Remind me once every week on "+s_time)
                        }
                    }
                    else if(position == 4){
                        if(s_repeating_time.equals("day", true)){
                            holder.tv_duration.setText("Remind me once at "+s_time)
                        }
                        else if(s_repeating_time.equals("week", true)){
                            holder.tv_duration.setText("Remind me once every week on "+s_time)
                        }
                        else{
                            holder.tv_duration.setText("Remind me every "+s_time)
                        }
                    }
                    else{
                        if(s_repeating_time.equals("day", true)){
                            holder.tv_duration.setText("Remind me once at "+s_time)
                        }
                        else if(s_repeating_time.equals("week", true)){
                            holder.tv_duration.setText("Remind me once every week on "+s_time)
                        }
                        else{
                            var list : List<String> = s_repeating_time!!.split(",")
                            if(list!=null){
                                Log.v("Size ", "==> "+list.size)
                                holder.tv_duration.setText("Remind me once at "+s_time)
                            }
                            else{
                                holder.tv_duration.setText("")
                            }
                        }
                    }
                }
                else{
                    if(position == 0 || position == 1){
                        holder.tv_duration.setText("Remind me once at "+s_time)
                    }
                    else{
                        holder.tv_duration.setText("")
                    }


                }

                holder.tv_duration.visibility = View.VISIBLE
            }
            else{
                holder.tv_duration.setText("")
                holder.tv_duration.visibility = View.GONE
            }
            holder.tv_title!!.setText(s_title)

        }
        holder.itemView.setOnClickListener(OnItemClickListener(position, onItemClickDetails))
    }
}