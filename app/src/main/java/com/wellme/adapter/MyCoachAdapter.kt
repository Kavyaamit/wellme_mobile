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
import com.wellme.dto.MyCoachDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class MyCoachAdapter(var context1 : Context, var list : List<MyCoachDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<MyCoachAdapter.ViewHolder>() {


    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_request_id_txt : TextView = view1.findViewById(R.id.tv_request_id_txt)
        var tv_request_datetime_txt : TextView = view1.findViewById(R.id.tv_request_datetime_txt)
        var tv_request_for_txt : TextView = view1.findViewById(R.id.tv_request_for_txt)
        var tv_status : TextView = view1.findViewById(R.id.tv_status)
        var tv_request_id : TextView = view1.findViewById(R.id.tv_request_id)
        var tv_request_datetime : TextView = view1.findViewById(R.id.tv_request_datetime)
        var tv_request_for : TextView = view1.findViewById(R.id.tv_request_for)
        var ll_full : LinearLayout = view1.findViewById(R.id.ll_full)
        var tv_view_profile : TextView = view1.findViewById(R.id.tv_view_profile)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context1).inflate(R.layout.item_my_coach, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var regular : Typeface? = ResourcesCompat.getFont(context1, R.font.poppins_regular)
        holder.tv_request_id.setTypeface(regular)
        holder.tv_request_id_txt.setTypeface(regular)
        holder.tv_request_datetime.setTypeface(regular)
        holder.tv_request_datetime_txt.setTypeface(regular)
        holder.tv_request_for.setTypeface(regular)
        holder.tv_request_for_txt.setTypeface(regular)
        holder.tv_status.setTypeface(regular)
        holder.tv_view_profile.setTypeface(regular)

        var dto : MyCoachDTO = list.get(position)
        if(dto!=null){
            var s_fullname : String = dto.coach_fullname
            var s_request_datetime : String = dto.coach_language
            var s_request_id : String = dto.id
            var s_status : String = dto.status
            var s_request_booktime : String = dto.booking_time

            if(!UtilMethod.instance.isStringNullOrNot(s_fullname)){
                holder.tv_request_for.setText(s_fullname)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_request_id)){
                holder.tv_request_id.setText(s_request_id)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_status)){
                holder.tv_status.visibility = View.VISIBLE
                if(s_status.equals("0", false)){
                    holder.tv_status.setText(context1.resources.getString(R.string.pending))
                    holder.ll_full.setBackgroundDrawable(context1.resources.getDrawable(R.drawable.payment_upcoming_bg))
                }
                else if(s_status.equals("1", false)){
                    holder.tv_status.setText(context1.resources.getString(R.string.accept))
                    holder.ll_full.setBackgroundDrawable(context1.resources.getDrawable(R.drawable.payment_active_bg))
                }
                else if(s_status.equals("2", false)){
                    holder.tv_status.setText(context1.resources.getString(R.string.completed))
                    holder.ll_full.setBackgroundDrawable(context1.resources.getDrawable(R.drawable.payment_active_bg))
                }
                else if(s_status.equals("3", false)){
                    holder.tv_status.setText(context1.resources.getString(R.string.reject))
                    holder.ll_full.setBackgroundDrawable(context1.resources.getDrawable(R.drawable.payment_expiry_bg))
                }
                else{
                    holder.tv_status.setText(context1.resources.getString(R.string.cancel))
                    holder.ll_full.setBackgroundDrawable(context1.resources.getDrawable(R.drawable.payment_expiry_bg))
                }
            }
            else{
                holder.tv_status.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_request_booktime)){
                holder.tv_request_datetime.setText(UtilMethod.instance.getDateTime(s_request_booktime))
            }

        }
        holder.tv_view_profile.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
    }
}