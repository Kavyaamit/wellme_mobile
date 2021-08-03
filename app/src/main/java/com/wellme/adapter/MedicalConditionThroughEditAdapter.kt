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
import com.wellme.R
import com.wellme.dto.MedicalConditionDTO
import com.wellme.utils.OnItemClickListener

class MedicalConditionThroughEditAdapter(var context1 : Context, var medical_list : List<MedicalConditionDTO>, var medical_filterlist : MutableList<MedicalConditionDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback, var pos : Int, var id : String) : RecyclerView.Adapter<MedicalConditionThroughEditAdapter.ViewHolder>(){
    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_title : TextView? = view1.findViewById(R.id.tv_title)
        var iv_check : ImageView? = view1.findViewById(R.id.iv_check)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context1).inflate(R.layout.item_selection, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return medical_filterlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : MedicalConditionDTO = medical_filterlist.get(position)
        holder.tv_title!!.text = dto.medical_condition
        if(id.equals(dto.id)){
            holder.iv_check!!.visibility = View.VISIBLE
        }
        else{
            holder.iv_check!!.visibility = View.GONE
        }
        var regular : Typeface? = ResourcesCompat.getFont(context1, R.font.poppins_regular)
        holder.tv_title!!.setTypeface(regular)
        holder.tv_title!!.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }

    public fun updateItem(medical_list1: ArrayList<MedicalConditionDTO>, pos1 : Int){
        this.medical_list = medical_list1
        this.pos = pos1
        notifyDataSetChanged()

    }


    public fun notifyData(){

        notifyDataSetChanged()
    }
}