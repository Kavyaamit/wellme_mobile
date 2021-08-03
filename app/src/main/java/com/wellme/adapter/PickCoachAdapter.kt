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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.CoachDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class PickCoachAdapter(var context1 : Context, var list : List<CoachDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<PickCoachAdapter.ViewHolder>() {


    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_coach_name : TextView = view1.findViewById(R.id.tv_coach_name)
        var tv_language : TextView = view1.findViewById(R.id.tv_language)
        var iv_image : ImageView = view1.findViewById(R.id.iv_image);
        var rv_expert : RecyclerView =view1.findViewById(R.id.rv_expert)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context1).inflate(R.layout.item_coach, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }



    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var gridLayoutManager : GridLayoutManager = GridLayoutManager(context1,2)
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        holder.rv_expert.layoutManager = gridLayoutManager

        var regular : Typeface? = ResourcesCompat.getFont(context1, R.font.poppins_regular)
        holder.tv_coach_name.setTypeface(regular)
        holder.tv_language.setTypeface(regular)
        var dto : CoachDTO = list.get(position)
        if(dto!=null){
            var s_fullname : String = dto.fullname
            var s_group_name : String = dto.name
            var s_language_known : String = dto.coach_language
            var s_coach_department : String = dto.coach_department
            var s_image : String = dto.profile_Image

            if(!UtilMethod.instance.isStringNullOrNot(s_coach_department)){
                val list: List<String> = s_coach_department.split(",")
                if(list!=null){
                    if(list.size>0){
                        holder.rv_expert.visibility = View.VISIBLE
                        holder.rv_expert.adapter = ExpertiseAdapter(context1, list)
                    }
                    else{
                        holder.rv_expert.visibility = View.GONE
                    }
                }
                else{
                    holder.rv_expert.visibility = View.GONE
                }
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_fullname)){
                if(!UtilMethod.instance.isStringNullOrNot(s_group_name)){
                    s_fullname+=" ("+s_group_name+")"
                }
                holder.tv_coach_name.setText(s_fullname)
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_language_known)){
                holder.tv_language.setText(s_language_known)
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_image).error(R.drawable.default_image).into(holder.iv_image)
            }
        }
        holder.itemView.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }
}