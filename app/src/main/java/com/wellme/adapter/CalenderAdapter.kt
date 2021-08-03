package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellme.R
import com.wellme.utils.UtilMethod
import java.util.*

class CalenderAdapter(var context: Context, var list: Array<String>,var s_month:Int,var s_year:Int) : RecyclerView.Adapter<CalenderAdapter.ViewHolder>(){


    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tv_date : TextView = view.findViewById(R.id.tv_date)
        var tv_title : TextView = view.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_date_calender, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : String = list.get(position)
        var regular: Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_date.setTypeface(regular)
        holder.tv_title.setTypeface(regular)
        var pos :Int
//        if(dto!=null){
//            holder.tv_date.setText(dto)
//
//        }

//        Log.d("dto",">>>"+dto)
        if (UtilMethod.instance.isStringNullOrNot(dto)){

            holder?.tv_date.setText("")
        }else{
            holder?.tv_date.setText(""+dto)
            getDay(dto,holder)

        }

        pos = if (position == 0) {
            6
        } else {
            (position - 1) % 7
        }

    }

fun getDay(date:String,holder:ViewHolder){

    var stringDate : String = date+"-"+this.s_month+1+"-"+this.s_year
//    Log.d("stringDate",">>>"+stringDate)
    var day :String = UtilMethod.instance.getDayString(stringDate)
//    var dayOfWeek :String = ""
//    if (Calendar.SUNDAY == day){
//        dayOfWeek = "Sun"
//    }
//    Log.d("stringDate",">>>"+day)

    holder?.tv_title.setText(""+day)



}
}