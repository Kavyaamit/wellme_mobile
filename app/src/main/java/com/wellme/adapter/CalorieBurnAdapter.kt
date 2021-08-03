package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.CalorieBurnDTO
import com.wellme.utils.UtilMethod

class CalorieBurnAdapter(var context: Context, var list : List<CalorieBurnDTO>) : RecyclerView.Adapter<CalorieBurnAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1) {
        var tv_amount1 : TextView = view1.findViewById(R.id.tv_amount1)
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_amount2 : TextView = view1.findViewById(R.id.tv_amount2)
        var tv_amount3 : TextView = view1.findViewById(R.id.tv_amount3)
        var tv_unit1 : TextView = view1.findViewById(R.id.tv_unit1)
        var tv_unit2 : TextView = view1.findViewById(R.id.tv_unit2)
        var tv_unit3 : TextView = view1.findViewById(R.id.tv_unit3)
        var ll_distance : LinearLayout = view1.findViewById(R.id.ll_distance)
        var ll_time : LinearLayout = view1.findViewById(R.id.ll_time)
        var ll_calorie : LinearLayout = view1.findViewById(R.id.ll_calorie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_calorie_burn, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_amount1.setTypeface(regular)
        holder.tv_amount2.setTypeface(regular)
        holder.tv_amount3.setTypeface(regular)
        holder.tv_unit1.setTypeface(regular)
        holder.tv_unit2.setTypeface(regular)
        holder.tv_unit3.setTypeface(regular)
        holder.tv_title.setTypeface(regular)
        var dto = list.get(position)
        if(dto!=null){
            var s_speed : String = dto.speed
            var s_distance : String = dto.distance
            var s_time : String = dto.time_duration
            var speed : Float = 0.0f
            var distance : Float = 0.0f
            var time : Int = 0
            var cal : Float = 0f
            var s_calorie = dto.total_calorie_burn
            var s_exercise_name : String = dto.exercise_name
            if(!UtilMethod.instance.isStringNullOrNot(s_speed)){
                speed = s_speed.toFloat()
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_distance)){
               distance = s_distance.toFloat()
                if(distance>0){
                    holder.tv_amount2!!.setText(UtilMethod.instance.getFormatedAmountString(distance))
                    holder.ll_distance.visibility = View.VISIBLE
                }
                else{
                    holder.ll_distance.visibility = View.GONE
                }
            }
            else{
                holder.ll_distance.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_time)){
               time = s_time.toInt()
                if(time>0) {
                    holder.tv_amount1!!.setText("" + time)
                    holder.ll_time.visibility = View.VISIBLE
                }
                else{
                    holder.ll_time.visibility = View.GONE
                }
            }
            else{
                holder.ll_time.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_calorie)){
                cal = s_calorie.toFloat()
                holder.tv_amount3!!.setText(UtilMethod.instance.getFormatedAmountString(cal))
                holder.ll_calorie.visibility = View.VISIBLE
            }
            else{
                holder.ll_calorie.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_exercise_name)){
                holder.tv_title!!.setText(s_exercise_name)
            }


        }

    }
}