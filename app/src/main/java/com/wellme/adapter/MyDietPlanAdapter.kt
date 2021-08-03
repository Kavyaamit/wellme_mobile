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
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.DietPlanDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod

class MyDietPlanAdapter(var cxt : Context, var dietplan_list : ArrayList<DietPlanDTO>) : RecyclerView.Adapter<MyDietPlanAdapter.ViewHolder>(){
    var regular : Typeface? = ResourcesCompat.getFont(cxt, R.font.poppins_regular)
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        var tv_diet_name : TextView = view.findViewById(R.id.tv_diet_name)
        var tv_diet_size : TextView = view.findViewById(R.id.tv_diet_size)
        var iv_or : ImageView = view.findViewById(R.id.iv_or)
        var iv_image : ImageView = view.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(cxt).inflate(R.layout.item_diet_plan_data, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dietplan_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var s_image : String = dietplan_list.get(position).diet_food_image
        holder.tv_diet_name.setText(dietplan_list.get(position).food_name)
        holder.tv_diet_size.setText("("+dietplan_list.get(position).quantity+" "+dietplan_list.get(position).size+")")
        holder.tv_diet_size.setTypeface(regular)
        holder.tv_diet_name.setTypeface(regular)
        if(position == dietplan_list.size-1){
            holder.iv_or.visibility = View.GONE
        }
        else{
            holder.iv_or.visibility = View.VISIBLE
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_image)){
            Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+s_image).into(holder.iv_image)
        }

    }
}