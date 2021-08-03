package com.wellme.adapter

import android.R.attr.path
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.dto.CommentListDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod


class CommentListAdapter(var context: Context, var comment_list: List<CommentListDTO>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var ll_reacive : LinearLayout = view1.findViewById(R.id.ll_reacive)
        var iv_image : ImageView = view1.findViewById(R.id.iv_image)
        var tv_name : TextView = view1.findViewById(R.id.tv_name)
        var tv_comment : TextView = view1.findViewById(R.id.tv_comment)
        var tv_time : TextView = view1.findViewById(R.id.tv_time)
        var iv_reacive_img : ImageView = view1.findViewById(R.id.iv_reacive_img)
        var rl_img_video : RelativeLayout = view1.findViewById(R.id.rl_img_video)
        var iv_video : ImageView = view1.findViewById(R.id.iv_video)

        var ll_send : LinearLayout = view1.findViewById(R.id.ll_send)
        var iv_send_image : ImageView = view1.findViewById(R.id.iv_send_image)
        var tv_send_name : TextView = view1.findViewById(R.id.tv_send_name)
        var tv_send_comment : TextView = view1.findViewById(R.id.tv_send_comment)
        var tv_send_time : TextView = view1.findViewById(R.id.tv_send_time)
        var iv_send_img : ImageView = view1.findViewById(R.id.iv_send_img)
        var rl_send_img_video : RelativeLayout = view1.findViewById(R.id.rl_send_img_video)
        var iv_send_video : ImageView = view1.findViewById(R.id.iv_send_video)

//        var iv_testimonial : ImageView = view1.findViewById(R.id.iv_testimonial)
//        var rl_youtube : RelativeLayout = view1.findViewById(R.id.rl_youtube)
//        var teestinomial_video : ImageView = view1.findViewById(R.id.testinomial_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_comment_reacive, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comment_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var userDto : UserDTO? = UtilMethod.instance.getUser(context)
        Log.d("data",">>>"+userDto?.user_id)
        var s_media_type : String? = ""
        var dto : CommentListDTO = comment_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        holder.tv_name.setTypeface(regular)
        holder.tv_comment.setTypeface(regular)
        holder.tv_send_name.setTypeface(regular)
        holder.tv_send_comment.setTypeface(regular)
        holder.tv_send_time.setTypeface(regular)
        holder.tv_time.setTypeface(regular)
//        holder.tv_send_comment.clipToOutline = true
        if(dto!=null){
            var name : String? = ""
            var first_name : String? = dto.first_name
            var last_name : String? = dto.last_name
            var comment : String? = dto.comment_text
            var comment_type : String? = dto.comment_type
            var added_on : String? = dto.added_on
            var comment_image : String? = dto.comment_image
            var comment_video : String? = dto.comment_video
            var comment_youtube_link : String? = dto.comment_youtube_link
//            var image_name : String? = dto.image
//            var s_youtube : String? = dto.youtube
//            s_media_type = dto.media_type


            if(!UtilMethod.instance.isStringNullOrNot(first_name)){
                name = first_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(last_name) && !UtilMethod.instance.isStringNullOrNot(name)){
                name +=" "+last_name
            }
            else if(!UtilMethod.instance.isStringNullOrNot(last_name)){
                name = last_name
            }





            if(dto.user_id.equals(userDto?.user_id)){
                holder.ll_send.visibility = View.VISIBLE
                holder.ll_reacive.visibility = View.GONE

                if(!UtilMethod.instance.isStringNullOrNot(name)){
                    holder.tv_send_name.setText(name)
                }

                if(!UtilMethod.instance.isStringNullOrNot(added_on)){
                    holder.tv_send_time.setText(UtilMethod.instance.getDate(added_on))
                }

                if (comment_type.equals("text")){
                    if(!UtilMethod.instance.isStringNullOrNot(comment)){
                        holder.tv_send_comment.setText(comment)
                        holder.tv_send_comment.visibility = View.VISIBLE
                        holder.iv_send_img.visibility = View.GONE
                        holder.rl_send_img_video.visibility = View.GONE
                    }else{
                        holder.tv_send_comment.visibility = View.GONE
                    }

                }else if (comment_type.equals("image")){
                    if(!UtilMethod.instance.isStringNullOrNot(comment_image)){
                        Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+comment_image)?.into(holder.iv_send_img)
                        holder.iv_send_img.visibility = View.VISIBLE
                        holder.rl_send_img_video.visibility = View.GONE
                        holder.tv_send_comment.visibility = View.GONE
                    }else{
                        holder.iv_send_img.visibility = View.GONE
                    }


                }else if (comment_type.equals("video")){

                    if(!UtilMethod.instance.isStringNullOrNot(comment_video)){

                        Glide.with(context)
                            .load(AppConstants.IMAGE_URL_NEW1+""+comment_video)
                            .thumbnail(Glide.with(context).load(AppConstants.IMAGE_URL_NEW1+""+comment_video))
                            .into(holder.iv_send_video)

//                        Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+comment_video)?.into(holder.iv_send_video)

                        holder.rl_send_img_video.visibility = View.VISIBLE
                        holder.iv_send_img.visibility = View.GONE
                        holder.tv_send_comment.visibility = View.GONE
                    }else{
                        holder.rl_send_img_video.visibility = View.GONE
                    }



                }else if (comment_type.equals("youtube")){


                }



            }else{
                holder.ll_send.visibility = View.GONE
                holder.ll_reacive.visibility = View.VISIBLE

                if(!UtilMethod.instance.isStringNullOrNot(name)){
                    holder.tv_name.setText(name)
                }

                if(!UtilMethod.instance.isStringNullOrNot(added_on)){
                    holder.tv_time.setText(UtilMethod.instance.getDate(added_on))
                }


                if (comment_type.equals("text")){

                    if(!UtilMethod.instance.isStringNullOrNot(comment)){
                        holder.tv_comment.setText(comment)

                        holder.tv_comment.visibility = View.VISIBLE
                        holder.iv_reacive_img.visibility = View.GONE
                        holder.rl_img_video.visibility = View.GONE

                    }else{
                        holder.tv_comment.visibility = View.VISIBLE
                    }

                }else if (comment_type.equals("image")){
                    if(!UtilMethod.instance.isStringNullOrNot(comment_image)){
                        Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+comment_image)?.into(holder.iv_reacive_img)

                        holder.iv_reacive_img.visibility = View.VISIBLE
                        holder.rl_img_video.visibility = View.GONE
                        holder.tv_comment.visibility = View.GONE

                    }else{
                        holder.iv_reacive_img.visibility = View.GONE
                    }

                }else if (comment_type.equals("video")){

                    if(!UtilMethod.instance.isStringNullOrNot(comment_video)){

                        Glide.with(context)
                            .load(AppConstants.IMAGE_URL_NEW1+""+comment_video)
                            .thumbnail(Glide.with(context).load(AppConstants.IMAGE_URL_NEW1+""+comment_video))
                            .into(holder.iv_video)


                        holder.rl_img_video.visibility = View.VISIBLE
                        holder.iv_reacive_img.visibility = View.GONE
                        holder.tv_comment.visibility = View.GONE
                    }else{
                        holder.rl_img_video.visibility = View.GONE
                    }



                }else if (comment_type.equals("youtube")){


                }

            }

        }
    }
    fun setImage(iv : ImageView, url : String?){
        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
        Log.v("Blod Video ID ", "==> "+id)
        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
        iv.setOnClickListener(View.OnClickListener {
            var intent : Intent = Intent(context, VideoActivity::class.java)

            intent.putExtra("url", url)
            context.startActivity(intent) })
    }
}