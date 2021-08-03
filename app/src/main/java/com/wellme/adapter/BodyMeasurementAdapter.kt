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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.BodyMeasurentDTO
import com.wellme.dto.BodyTypeDTO
import com.wellme.dto.CalorieIntakeDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class BodyMeasurementAdapter(var context : Context, var body_type_list : List<BodyTypeDTO>?, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<BodyMeasurementAdapter.ViewHolder>() {
    var inflater : LayoutInflater? = LayoutInflater.from(context)
    var bodyMeasurementAdapter : BodyMeasurentListAdapter? = null
    var linearLayoutManager : LinearLayoutManager? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_body_type: TextView = itemView.findViewById(R.id.tv_body_type)
        var tv_body_type_quantity: TextView = itemView.findViewById(R.id.tv_body_type_quantity)
        var iv_add : ImageView = itemView.findViewById(R.id.iv_add)
        var rv_body_type_value : RecyclerView = itemView.findViewById(R.id.rv_body_type_value)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = inflater!!.inflate(R.layout.item_body_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        holder.rv_body_type_value!!.layoutManager = linearLayoutManager


        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var dto : BodyTypeDTO = body_type_list!!.get(position)
        var list : ArrayList<BodyMeasurentDTO> = dto!!.measurent_list

        holder.tv_body_type!!.setTypeface(regular)
        holder.tv_body_type_quantity.visibility = View.GONE

        if (dto.name.equals("Body fat")){
            holder.tv_body_type!!.setText(""+body_type_list!!.get(position).name+" (in %)")
        }else if (dto.name.equals("Muscle mass")){
            holder.tv_body_type!!.setText(""+body_type_list!!.get(position).name+" (in kg)")
        }else{
            holder.tv_body_type!!.setText(""+body_type_list!!.get(position).name+" (in cm)")
        }




        if(list!=null){
            holder.rv_body_type_value!!.adapter = BodyMeasurentListAdapter(context, list)
            var total_value : Float = 0f
            for(i in 0..list.size-1){
                var dto: BodyMeasurentDTO = list!!.get(i)

                if(dto!=null){
                    var s_measurent_value : String = dto.body_measurement_value
                    if(!UtilMethod.instance.isStringNullOrNot(s_measurent_value)){
                        total_value+=s_measurent_value.toFloat()
                        Log.d("11",">>>>"+list)
                    }
                }
            }

//            holder.tv_body_type_quantity!!.setText(UtilMethod.instance.getFormatedAmountString(total_value)+" of 500 Cal")
        }








        holder.iv_add!!.setOnClickListener(OnItemClickListener(position, onItemClickCallback))





    }

    override fun getItemCount(): Int {
        return body_type_list!!.size
    }

//    fun setAdapter(rv_measurent:RecyclerView){
//
//        linearLayoutManager = LinearLayoutManager(context)
//        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
//        rv_measurent!!.layoutManager = linearLayoutManager
//
//        rv_measurent!!.adapter = BodyMeasurentListAdapter(context, body_measurent_list)
//    }
}