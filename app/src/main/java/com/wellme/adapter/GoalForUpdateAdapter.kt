package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.GoalDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

public class GoalForUpdateAdapter(var context : Context, var goal_list : List<GoalDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback, var selected_id : String?) : RecyclerView.Adapter<GoalForUpdateAdapter.ViewHolder>(){

    class ViewHolder(var view : View) : RecyclerView.ViewHolder(view){
        var rb_title : RadioButton = view.findViewById(R.id.rb_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return goal_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : GoalDTO = goal_list.get(position)
        if(dto!=null){
            var id : String = dto.id
            var title : String = dto.goals
            var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
            holder.rb_title.setTypeface(regular)
            holder.rb_title.setText(title)
            if(!UtilMethod.instance.isStringNullOrNot(selected_id) && !UtilMethod.instance.isStringNullOrNot(id)){

                if(selected_id.equals(id, true)){
                    Log.v("Come ", "==> true")
                    holder.rb_title.isChecked = true
                }
                else{
                    Log.v("Come ", "==> false")
                    holder.rb_title?.isChecked = false
                }
            }
            else{
                Log.v("Come ", "==> false")
                holder.rb_title.isChecked = false
            }
        }
        holder.view.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    public fun setData(id1 : String?){
        this.selected_id = id1
        notifyDataSetChanged()
    }
}