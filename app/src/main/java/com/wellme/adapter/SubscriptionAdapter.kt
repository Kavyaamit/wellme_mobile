package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.SubscriptionDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class SubscriptionAdapter(var context : Context, var list : List<SubscriptionDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback, var onItemClickCallbackBuy: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>(){
    var regular : Typeface? = null
    var bold : Typeface? = null
    class ViewHolder(view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_amount : TextView = view1.findViewById(R.id.tv_amount)
        var tv_validity : TextView = view1.findViewById(R.id.tv_no_of_days)
        var btn_view_details : Button = view1.findViewById(R.id.btn_view_details)
        var iv : ImageView = view1.findViewById(R.id.iv_subscription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_subscription, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        regular = ResourcesCompat.getFont(context, R.font.poppins_regular)
        bold =   ResourcesCompat.getFont(context, R.font.poppins_bold)
        holder.tv_amount.setTypeface(regular)
        holder.tv_validity.setTypeface(regular)
        holder.btn_view_details.setTypeface(regular)
        holder.tv_title.setTypeface(bold)


        holder.tv_title.setText(list.get(position).title)
        holder.tv_validity.setText("Upto "+list.get(position).no_of_days+" day(s)")
        var f1 : Float = 0f
        var s_amount = list.get(position).amount
        if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
            f1 = s_amount.toFloat()
        }
        Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+list.get(position).image).placeholder(R.drawable.logo).into(holder.iv)
        holder.tv_amount.setText(context.resources.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(f1))
        holder.btn_view_details.setOnClickListener(OnItemClickListener(position, onItemClickCallbackBuy))

    }
}