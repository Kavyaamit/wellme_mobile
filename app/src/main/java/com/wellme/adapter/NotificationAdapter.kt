package com.wellme.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.NotificationDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class NotificationAdapter(var context: Context, var list : List<NotificationDTO>, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tv_title : TextView = view.findViewById(R.id.tv_title)
        var tv_description : TextView = view.findViewById(R.id.tv_description)
        var tv_date : TextView = view.findViewById(R.id.tv_date)
        var iv_notification : ImageView = view.findViewById(R.id.iv_notification)
        var iv_video : ImageView = view.findViewById(R.id.iv_video)
        var rl_video : RelativeLayout = view.findViewById(R.id.rl_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view1 : View = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : NotificationDTO = list.get(position)
        var regular: Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_date.setTypeface(regular)
        holder.tv_title.setTypeface(regular)
        holder.tv_description.setTypeface(regular)
        if(dto!=null){
            holder.tv_title.setText(dto.title)
            holder.tv_description.setText(dto.description)
            var s_date : String = dto.added_on
            var s_image : String = dto.image
            var s_video : String = dto.video


            if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                Log.v("Notification Image "+position, "==> "+s_image)
                Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+dto.image).into(holder.iv_notification)
                holder.iv_notification.visibility = View.VISIBLE
            }
            else{
                holder.iv_notification.visibility = View.GONE
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_date)){
                holder.tv_date.setText(UtilMethod.instance.getDate(s_date))
            }


            if(!UtilMethod.instance.isStringNullOrNot(s_video)){
                var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(s_video)
                if(!UtilMethod.instance.isStringNullOrNot(id)){
                    holder.iv_notification.visibility = View.GONE
                    Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").into(holder.iv_video)
                    holder.rl_video.visibility = View.VISIBLE
                    holder.rl_video.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
                }
                else{
                    holder.rl_video.visibility = View.GONE
                }

            }
            else{
                holder.rl_video.visibility = View.GONE
            }
        }
    }
}