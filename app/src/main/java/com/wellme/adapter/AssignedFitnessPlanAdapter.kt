package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.AssignedFitnessPlanDTO
import com.wellme.dto.FitnessPlanDTO

class AssignedFitnessPlanAdapter(var context : Context, var assigned_list : ArrayList<AssignedFitnessPlanDTO>) : RecyclerView.Adapter<AssignedFitnessPlanAdapter.ViewHolder>(){
    var inflater : LayoutInflater = LayoutInflater.from(context)

    class ViewHolder(var view : View) : RecyclerView.ViewHolder(view){
        var tv_title : TextView = view.findViewById(R.id.tv_title)
        var rv_plan : RecyclerView = view.findViewById(R.id.rv_plan)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = inflater.inflate(R.layout.item_plan, parent, false);
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return assigned_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        var linearLayoutManager : LinearLayoutManager? = LinearLayoutManager(context)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        holder.rv_plan.layoutManager = linearLayoutManager
        holder.tv_title.setTypeface(regular)
        holder.tv_title.setText(assigned_list.get(position).exercise_type)
        var fitness_list : ArrayList<FitnessPlanDTO> = assigned_list.get(position).exercise_list
        if(fitness_list!=null){
            if(fitness_list.size>0){
                holder.rv_plan.adapter = MyFitnessPlanAdapter(context, fitness_list)
                holder.rv_plan.visibility = View.VISIBLE
            }
            else{
                holder.rv_plan.visibility = View.GONE
            }
        }
        else{
            holder.rv_plan.visibility = View.GONE
        }

    }
}