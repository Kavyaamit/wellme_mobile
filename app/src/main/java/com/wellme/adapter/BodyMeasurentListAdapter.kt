package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.BodyMeasurentDTO
import com.wellme.dto.UserWeightDTO
import com.wellme.utils.UtilMethod

class BodyMeasurentListAdapter(val context: Context, val list : List<BodyMeasurentDTO>?) : RecyclerView.Adapter<BodyMeasurentListAdapter.ViewHolder>(){


    class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        var layout : LinearLayout = view.findViewById(R.id.layout)
        var tv_weight_date : TextView = view.findViewById(R.id.tv_weight_date)
        var tv_weight_amount : TextView = view.findViewById(R.id.tv_weight_amount)
        var image : ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_body_measurement, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : BodyMeasurentDTO = list!!.get(position)
        if(position == list.size - 1) {
            holder.layout.visibility = View.GONE
        }
        else{
            holder.layout.visibility = View.VISIBLE
        }

        if(position % 2 == 0){
            holder.image.setImageDrawable(context.resources.getDrawable(R.drawable.white_rounded_bg1))
        }
        else{
            holder.image.setImageDrawable(context.resources.getDrawable(R.drawable.theme_rounded_bg))
        }

        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_weight_amount.setTypeface(regular)
        holder.tv_weight_date.setTypeface(regular)
        if(dto!=null){
            var s_weight : String? = dto.body_measurement_value;
            if(!UtilMethod.instance.isStringNullOrNot(s_weight)){
                var f_weight : Float = s_weight!!.toFloat()
                var index : Int = dto.created_on.lastIndexOf(".")
                holder.tv_weight_amount.setText(UtilMethod.instance.getFormatedAmountString(f_weight))
                if(index>-1){
                    holder.tv_weight_date.setText(UtilMethod.instance.getDate(dto.created_on))
                }
                else{
                    holder.tv_weight_date.setText(UtilMethod.instance.getDate(dto.created_on))
                }

            }

        }

    }
}