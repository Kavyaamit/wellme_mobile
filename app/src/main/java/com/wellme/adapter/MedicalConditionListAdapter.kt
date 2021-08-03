package com.wellme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.dto.MedicalConditionListDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class MedicalConditionListAdapter(var context1 : Context, var medical_list : List<MedicalConditionListDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<MedicalConditionListAdapter.ViewHolder>(){
    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_title : TextView? = view1.findViewById(R.id.tv_title)
        var tv_date : TextView? = view1.findViewById(R.id.tv_date)
        var tv_view : TextView? = view1.findViewById(R.id.tv_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context1).inflate(R.layout.item_medical_condition, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return medical_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : MedicalConditionListDTO = medical_list.get(position)

        var title = dto.medical_condition
        var date = dto.created_on


        if(!UtilMethod.instance.isStringNullOrNot(title)){
            holder.tv_title?.setText(title)
            holder.tv_title?.visibility = View.VISIBLE
        }else{
            holder.tv_title?.visibility = View.GONE
        }

        if(!UtilMethod.instance.isStringNullOrNot(date)){
            holder.tv_date?.setText(UtilMethod.instance.getDatenew(date))
            holder.tv_date?.visibility = View.VISIBLE
        }else{
            holder.tv_date?.visibility = View.GONE
        }

        holder.tv_view!!.setOnClickListener(OnItemClickListener(position, onItemClickCallback))


//        var dto : MedicalCondtionDTO = medical_list.get(position)
//        holder.tv_title!!.text = dto.medical_condition
//        if(id.equals(dto.id)){
//            holder.iv_check!!.visibility = View.VISIBLE
//        }
//        else{
//            holder.iv_check!!.visibility = View.GONE
//        }
//        if(dto.status){
//            holder.iv_check!!.visibility = View.VISIBLE
//        }
//        else{
//            holder.iv_check!!.visibility = View.GONE
//        }
//        var regular : Typeface? = ResourcesCompat.getFont(context1, R.font.poppins_regular)
//        holder.tv_title!!.setTypeface(regular)
//        holder.itemView!!.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }

//    public fun updateItem(medical_list1: ArrayList<MedicalCondtionDTO>, pos1 : Int){
//        this.medical_list = medical_list1
//        notifyDataSetChanged()
//
//    }


    public fun notifyData(){

        notifyDataSetChanged()
    }
}