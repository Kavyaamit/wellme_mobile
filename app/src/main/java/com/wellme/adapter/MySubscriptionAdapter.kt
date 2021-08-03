package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.MySubscriptionDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class MySubscriptionAdapter(var context : Context, var subscription_list : List<MySubscriptionDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<MySubscriptionAdapter.ViewHolder>(){

    class ViewHolder(view1 : View) : RecyclerView.ViewHolder(view1){
        var iv_subscription : ImageView = view1.findViewById(R.id.iv_subscription)
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_amount : TextView = view1.findViewById(R.id.tv_amount)
        var tv_plan_type : TextView = view1.findViewById(R.id.tv_plan_type)
        var tv_subscription_date : TextView = view1.findViewById(R.id.tv_subscription_date)
        var tv_purchased_date : TextView = view1.findViewById(R.id.tv_purchased_date)
        var btn_repeat_plan : Button = view1.findViewById(R.id.btn_repeat_plan)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_mysubscription, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return subscription_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        var dto : MySubscriptionDTO? = subscription_list.get(position)
        var s_subscription_start_date: String? = ""
        var s_subscription_end_date: String? = ""
        var s_purchased_date: String? = ""
        holder.tv_title.setTypeface(bold)
        holder.tv_amount.setTypeface(regular)
        holder.tv_plan_type.setTypeface(regular)
        holder.tv_purchased_date.setTypeface(regular)
        holder.tv_subscription_date.setTypeface(regular)
        holder.btn_repeat_plan.setTypeface(regular)
        if(dto!=null) {
            holder.tv_title.setText("" + dto.title)
            holder.tv_plan_type.setText(""+dto.type_of_plan)
            s_subscription_start_date = dto.subscription_start
            s_subscription_end_date = dto.subscription_end
            s_purchased_date = dto.purchaced_on

            var s_image : String? = dto.image
            if(!UtilMethod.instance.isStringNullOrNot(s_image)) {
                Picasso.get().load(AppConstants.IMAGE_URL_NEW1 + "" + dto.image)
                    .placeholder(R.drawable.logo).into(holder.iv_subscription)
            }
            holder.tv_subscription_date.setText("From "+UtilMethod.instance.getDateWithoutTime1(s_subscription_start_date)+" to "+UtilMethod.instance.getDateWithoutTime1(s_subscription_end_date))
            holder.tv_purchased_date.setText("Buy on "+UtilMethod.instance.getDateWithTime1(s_purchased_date))
            var s_amount : String? = dto.total_amount
            if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
                var f1 : Float = s_amount!!.toFloat()
                holder.tv_amount.setText(context.resources!!.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(f1))
            }
            else{
                holder.tv_amount.setText("")
            }

            holder.btn_repeat_plan.setOnClickListener(OnItemClickListener(position, onItemClickCallback))

        }
    }
}