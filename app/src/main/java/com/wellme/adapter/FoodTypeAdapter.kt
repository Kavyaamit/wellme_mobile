package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.CalorieIntakeDTO
import com.wellme.dto.FoodTypeDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class FoodTypeAdapter(var context: Context, var foodtype_list : List<FoodTypeDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<FoodTypeAdapter.ViewHolder>(){
    var linearLayoutManager : LinearLayoutManager? = null
    var regular : Typeface? = null
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var rv_food_type_cal : RecyclerView? = view.findViewById(R.id.rv_food_type_cal)
        var iv_add : ImageView? = view.findViewById(R.id.iv_add)
        var tv_food_type_quantity : TextView? = view.findViewById(R.id.tv_food_type_quantity)
        var tv_food_type : TextView? = view.findViewById(R.id.tv_food_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_food_type, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodtype_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        holder.rv_food_type_cal!!.layoutManager = linearLayoutManager
        regular = ResourcesCompat.getFont(context, R.font.poppins_regular)

        holder.tv_food_type!!.setTypeface(regular)
        holder.tv_food_type_quantity!!.setTypeface(regular)

        holder.iv_add!!.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
        var dto : FoodTypeDTO? = foodtype_list.get(position)
        if(dto!=null){
            var s_food_type : String? = dto!!.food_type_name
            var list : ArrayList<CalorieIntakeDTO> = dto!!.calorie_list
            if(!UtilMethod.instance.isStringNullOrNot(s_food_type)){
                holder.tv_food_type!!.setText(""+s_food_type)
                holder.tv_food_type!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_food_type!!.visibility = View.GONE
            }

            if(list!=null){
                holder.rv_food_type_cal!!.adapter = CalorieIntakeAdapter(context, list)
                var total_cal : Float = 0f
                for(i in 0..list.size-1){
                    var dto: CalorieIntakeDTO = list.get(i)
                    if(dto!=null){
                        var s_total_calorie : String = dto.total_calorie
                        if(!UtilMethod.instance.isStringNullOrNot(s_total_calorie)){
                            total_cal+=s_total_calorie.toFloat()
                        }
                    }
                }

                holder.tv_food_type_quantity!!.setText(UtilMethod.instance.getFormatedAmountString(total_cal)+" of 500 Cal")
            }
        }
    }
}