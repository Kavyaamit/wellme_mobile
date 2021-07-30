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
import com.wellme.dto.CalorieIntakeDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod

class CalorieIntakeAdapter(var context: Context, var list: ArrayList<CalorieIntakeDTO>) : RecyclerView.Adapter<CalorieIntakeAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_quantity : TextView = view1.findViewById(R.id.tv_quantity)
        var tv_amount : TextView = view1.findViewById(R.id.tv_amount)
        var food_image : ImageView = view1.findViewById(R.id.food_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_calorie_intake, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular: Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_amount.setTypeface(regular)
        holder.tv_quantity.setTypeface(regular)
        holder.tv_title.setTypeface(regular)
        var dto: CalorieIntakeDTO = list.get(position)
        if (dto != null) {
            var s_title: String = dto.food_name
            var s_amount: String = dto.total_calorie
            var s_size: String = dto.size
            var s_weight: String = dto.quantity
            var s_food_image : String = dto.foodimage
            var cal: Float = 0.0f
            if (!UtilMethod.instance.isStringNullOrNot(s_amount)) {
                cal = s_amount.toFloat()
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_food_image)){
                Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_food_image).error(R.drawable.default_image).into(holder.food_image)
            }
            if (!UtilMethod.instance.isStringNullOrNot(s_size) && !UtilMethod.instance.isStringNullOrNot(
                    s_weight
                )
            ) {
                holder.tv_quantity.setText("(" + s_weight + " " + s_size + ")")
                holder.tv_quantity.visibility = View.VISIBLE
            } else if (!UtilMethod.instance.isStringNullOrNot(s_size)) {
                holder.tv_quantity.setText("(" + s_size + ")")
                holder.tv_quantity.visibility = View.VISIBLE
            } else if (!UtilMethod.instance.isStringNullOrNot(s_weight)) {
                holder.tv_quantity.setText("(" + s_weight + ")")
                holder.tv_quantity.visibility = View.VISIBLE
            } else {
                holder.tv_quantity.visibility = View.GONE
            }
            holder.tv_amount.setText(UtilMethod.instance.getFormatedAmountString(cal) + " cal")
            holder.tv_title.setText(s_title)

        }
    }
}