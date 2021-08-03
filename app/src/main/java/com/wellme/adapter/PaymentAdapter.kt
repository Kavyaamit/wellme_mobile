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
import com.wellme.dto.PaymentDTO
import com.wellme.utils.UtilMethod
import java.util.*

class PaymentAdapter(val context: Context, val list : List<PaymentDTO>) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tv_title : TextView = view.findViewById(R.id.tv_title)
        var tv_amount : TextView = view.findViewById(R.id.tv_amount)
        var tv_status : TextView = view.findViewById(R.id.tv_status)
        var tv_transaction_id : TextView = view.findViewById(R.id.tv_transaction_id)
        var tv_active_date : TextView = view.findViewById(R.id.tv_active_date)
        var tv_purchased_date : TextView = view.findViewById(R.id.tv_purchased_date)
        var ll_full : LinearLayout = view.findViewById(R.id.ll_full)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : PaymentDTO = list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_transaction_id.setTypeface(regular)
        holder.tv_amount.setTypeface(regular)
        holder.tv_purchased_date.setTypeface(regular)
        holder.tv_status.setTypeface(regular)
        holder.tv_title.setTypeface(regular)
        holder.tv_active_date.setTypeface(regular)
        if(dto!=null){
            var s_plan_start_date : String = dto.plan_start
            var s_plan_end_date : String = dto.plan_end
            var s_purchased_date : String = dto.purchaced_on
            var s_amount : String = dto.amount
            var current_date : Date? = Date()
            var start_date_ms : Long = 0
            var end_date_ms : Long = 0
            var current_date_ms : Long = 0
            var start_date : Date? = null
            var end_date : Date? = null
            var purchased_date : Date? = null
            if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
                holder.tv_amount.setText(context.resources.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(s_amount.toFloat()))
                holder.tv_amount.visibility = View.VISIBLE
            }
            else{
                holder.tv_amount.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_plan_start_date)){
                start_date = UtilMethod.instance.getDateWithoutLocalTime(s_plan_start_date)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_plan_end_date)){
                end_date = UtilMethod.instance.getDateWithoutLocalTime(s_plan_end_date)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_purchased_date)){
                purchased_date = UtilMethod.instance.getDateWithoutLocalTime(s_purchased_date)
            }
            if(start_date!=null && end_date!=null){
                start_date_ms = start_date.time
                end_date_ms = end_date.time
                current_date_ms = current_date!!.time

                if(current_date_ms > end_date_ms){
                    holder.ll_full.setBackgroundDrawable(context.resources.getDrawable(R.drawable.payment_expiry_bg))
                    holder.tv_status.setText("Expiry")
                    holder.tv_active_date.setText(context.resources.getString(R.string.expired_on)+" "+UtilMethod.instance.getDateFormat1(end_date))
                    holder.tv_status.visibility = View.VISIBLE
                    holder.tv_active_date.visibility = View.VISIBLE
                }
                else if(start_date_ms>current_date_ms){
                    holder.ll_full.setBackgroundDrawable(context.resources.getDrawable(R.drawable.payment_upcoming_bg))
                    holder.tv_status.setText("Upcoming")
                    holder.tv_active_date.setText(context.resources.getString(R.string.activate_from)+" "+UtilMethod.instance.getDateFormat1(start_date))
                    holder.tv_status.visibility = View.VISIBLE
                    holder.tv_active_date.visibility = View.VISIBLE
                }
                else if(start_date_ms<=current_date_ms && end_date_ms>current_date_ms){
                    holder.ll_full.setBackgroundDrawable(context.resources.getDrawable(R.drawable.payment_active_bg))
                    holder.tv_status.setText("Active")
                    holder.tv_active_date.setText(context.resources.getString(R.string.expiring_on)+" "+UtilMethod.instance.getDateFormat1(end_date))
                    holder.tv_status.visibility = View.VISIBLE
                    holder.tv_active_date.visibility = View.VISIBLE
                }
                else{
                    holder.ll_full.setBackgroundDrawable(context.resources.getDrawable(R.drawable.payment_transparent_bg))
                    holder.tv_status.visibility = View.GONE
                    holder.tv_active_date.visibility = View.GONE
                }
                holder.tv_title.setText(dto.name)
                holder.tv_transaction_id.setText("ID : "+dto.payment_transaction_id)
                holder.tv_purchased_date.setText(context.resources.getString(R.string.purchased_on)+" "+UtilMethod.instance.getDateFormat1(purchased_date))

            }
            else{
                holder.ll_full.visibility = View.GONE

            }
        }

    }
}