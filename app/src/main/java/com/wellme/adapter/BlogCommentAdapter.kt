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
import com.wellme.dto.BlogCommentDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.UtilMethod

class BlogCommentAdapter(var context : Context, var comment_list : List<BlogCommentDTO>?) : RecyclerView.Adapter<BlogCommentAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            var ll_receiver : LinearLayout = view.findViewById(R.id.ll_receiver)
            var ll_owner : LinearLayout = view.findViewById(R.id.ll_owner)
            var tv_receiver : TextView = view.findViewById(R.id.tv_receiver)
            var tv_owner : TextView = view.findViewById(R.id.tv_owner)
            var tv_receiver_msg : TextView = view.findViewById(R.id.tv_receiver_msg)
            var tv_receiver_time : TextView = view.findViewById(R.id.tv_receiver_time)
            var tv_owner_msg : TextView = view.findViewById(R.id.tv_owner_msg)
            var tv_owner_time : TextView = view.findViewById(R.id.tv_owner_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_blog_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comment_list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        holder?.tv_owner.setTypeface(regular)
        holder?.tv_owner_msg.setTypeface(regular)
        holder?.tv_owner_time.setTypeface(regular)
        holder?.tv_receiver.setTypeface(regular)
        holder?.tv_receiver_msg.setTypeface(regular)
        holder?.tv_receiver_time.setTypeface(regular)
        var userDto : UserDTO? = UtilMethod.instance.getUser(context)
        var dto : BlogCommentDTO? = comment_list!!.get(position)
        if(dto!=null){
            var s_first_name : String? = dto!!.first_name
            var s_last_name : String? = dto!!.last_name
            var s_comment_text : String? = dto!!.comment_text
            var s_added_on : String? = dto!!.added_on
            var s_comment_type : String? = dto!!.comment_type
            var s_user_id : String? = dto!!.user_id
            var s_name : String? = ""
            if(!UtilMethod.instance.isStringNullOrNot(s_first_name)){
                s_name = s_first_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_last_name)){
                if(!UtilMethod.instance.isStringNullOrNot(s_name)){
                    s_name +=" "+s_last_name
                }
                else{
                    s_name = s_last_name
                }
            }

            if(userDto!=null && dto!=null){
                if(userDto!!.user_id == dto.user_id){
                    holder.ll_owner.visibility = View.VISIBLE
                    holder.ll_receiver.visibility = View.GONE
                    if(!UtilMethod.instance.isStringNullOrNot(s_name)){
                        holder.tv_owner.setText(""+s_name)
                    }
                    holder.tv_owner_msg.setText(""+s_comment_text)
                    if(!UtilMethod.instance.isStringNullOrNot(s_added_on)){
                        holder.tv_owner_time.setText(""+UtilMethod.instance.getDate(s_added_on))
                    }
                }
                else{
                    holder.ll_owner.visibility = View.GONE
                    holder.ll_receiver.visibility = View.VISIBLE
                    if(!UtilMethod.instance.isStringNullOrNot(s_name)){
                        holder.tv_receiver.setText(""+s_name)
                    }
                    if(!UtilMethod.instance.isStringNullOrNot(s_added_on)){
                        holder.tv_receiver_time.setText(""+UtilMethod.instance.getDate(s_added_on))
                    }
                    holder.tv_receiver_msg.setText(""+s_comment_text)
                }
            }
            else{
                holder.ll_owner.visibility = View.GONE
                holder.ll_receiver.visibility = View.GONE
            }

        }
    }
}