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
import com.wellme.dto.TestimonialDTO
import com.wellme.dto.TestimonialLikeDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod


class BlogLikeListAdapter(var context: Context, var like_list: List<TestimonialLikeDTO>) : RecyclerView.Adapter<BlogLikeListAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){

        var tv_name : TextView = view1.findViewById(R.id.tv_user_name)
        var iv_image : ImageView = view1.findViewById(R.id.iv_image)

//        var iv_testimonial : ImageView = view1.findViewById(R.id.iv_testimonial)
//        var rl_youtube : RelativeLayout = view1.findViewById(R.id.rl_youtube)
//        var teestinomial_video : ImageView = view1.findViewById(R.id.testinomial_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_testnomials_like_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return like_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var userDto : UserDTO? = UtilMethod.instance.getUser(context)
        Log.d("data",">>>"+userDto?.user_id)
        var s_media_type : String? = ""
        var dto : TestimonialLikeDTO = like_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        holder.tv_name.setTypeface(regular)

//        holder.tv_send_comment.clipToOutline = true
        if(dto!=null){
            var name : String? = ""
            var first_name : String? = dto.first_name
            var last_name : String? = dto.last_name
            var profile_image : String? = dto.profile_image


            if(!UtilMethod.instance.isStringNullOrNot(first_name)){
                name = first_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(last_name) && !UtilMethod.instance.isStringNullOrNot(name)){
                name +=" "+last_name
            }
            else if(!UtilMethod.instance.isStringNullOrNot(last_name)){
                name = last_name
            }

            if(!UtilMethod.instance.isStringNullOrNot(name)){
                holder.tv_name.setText(name)
            }

            if(!UtilMethod.instance.isStringNullOrNot(profile_image)){
                Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+profile_image).into(holder.iv_image)
                holder.iv_image.visibility =View.VISIBLE
            }
            else{
//                holder.iv_image.visibility =View.GONE
            }

            }
        }
//    }
//    fun setImage(iv : ImageView, url : String?){
//        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
//        Log.v("Blod Video ID ", "==> "+id)
//        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
//        iv.setOnClickListener(View.OnClickListener {
//            var intent : Intent = Intent(context, VideoActivity::class.java)
//
//            intent.putExtra("url", url)
//            context.startActivity(intent) })
//    }
}