package com.wellme.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.ChatMessageDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod

class ChatAdapter(var context : Context, var chat_list : List<ChatMessageDTO>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    class ViewHolder(view1 : View) : RecyclerView.ViewHolder(view1){
        var from_layout : LinearLayout = view1.findViewById(R.id.from_layout)
        var to_layout : LinearLayout = view1.findViewById(R.id.to_layout)
        var tv_msg_from : TextView = view1.findViewById(R.id.tv_msg_from)
        var tv_msg_me : TextView = view1.findViewById(R.id.tv_msg_me)
        var iv_image_from : ImageView = view1.findViewById(R.id.iv_image_from)
        var iv_image_me : ImageView = view1.findViewById(R.id.iv_image_me)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chat_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : ChatMessageDTO = chat_list.get(position)
        var udto : UserDTO? = UtilMethod.instance.getUser(context)
        var user_id : String? = ""
        if(udto!=null){
            user_id = udto!!.user_id
        }
        if(dto!=null){
            var from_id : String = dto!!.from_id
            var type : String = dto!!.type
            var created_at : String = dto!!.created_at
            Log.v("Chat Created At", "==> "+created_at)
            Log.v("Chat Type ", "==> "+type)
            if(from_id.equals(user_id, true)){
                holder.from_layout.visibility = View.GONE
                holder.to_layout.visibility = View.VISIBLE

                if(type.equals("text")){
                    holder.tv_msg_me.setText(dto!!.message)
                    holder.tv_msg_me.visibility = View.VISIBLE
                    holder.iv_image_me.visibility = View.GONE
                }
                else if(type.equals("image", true)){
                    Picasso.get().load(AppConstants.SOCKET_IMAGE_URL+""+dto!!.message).into(holder.iv_image_me)
                    holder.tv_msg_me.visibility = View.GONE
                    holder.iv_image_me.visibility = View.VISIBLE
                }

            }
            else{
                holder.from_layout.visibility = View.VISIBLE
                holder.to_layout.visibility = View.GONE
                if(type.equals("text")){
                    holder.tv_msg_from.setText(dto!!.message)
                    holder.iv_image_from.visibility = View.GONE
                    holder.tv_msg_from.visibility = View.VISIBLE
                }
                else if(type.equals("image", true)){
                    Picasso.get().load(AppConstants.SOCKET_IMAGE_URL+""+dto!!.message).into(holder.iv_image_from)
                    holder.iv_image_from.visibility = View.VISIBLE
                    holder.tv_msg_from.visibility = View.GONE
                }
            }
        }
    }
}