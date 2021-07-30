package com.wellme.adapter

import android.content.Context
import android.content.Intent
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
import com.wellme.VideoActivity
import com.wellme.dto.TestimonialDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod

class TestinomialsAdapter(var context: Context, var testimonial_list: List<TestimonialDTO>) : RecyclerView.Adapter<TestinomialsAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var tv_name : TextView = view1.findViewById(R.id.tv_name)
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_description : TextView = view1.findViewById(R.id.tv_description)
        var iv_testimonial : ImageView = view1.findViewById(R.id.iv_testimonial)
        var rl_youtube : RelativeLayout = view1.findViewById(R.id.rl_youtube)
        var teestinomial_video : ImageView = view1.findViewById(R.id.testinomial_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_testimonials, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testimonial_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var s_media_type : String? = ""
        var dto : TestimonialDTO = testimonial_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        holder.tv_name.setTypeface(regular)
        holder.tv_title.setTypeface(bold)
        holder.tv_description.setTypeface(regular)
        holder.iv_testimonial.clipToOutline = true
        if(dto!=null){
            var name : String? = ""
            var first_name : String? = dto.first_name
            var last_name : String? = dto.last_name
            var title : String? = dto.short_description
            var description : String? = dto.description
            var image_name : String? = dto.image
            var s_youtube : String? = dto.youtube
            s_media_type = dto.media_type
            if(!UtilMethod.instance.isStringNullOrNot(s_media_type)){
                if(s_media_type.equals("image", true)){
                    holder.rl_youtube.visibility = View.GONE
                    holder.iv_testimonial.visibility = View.VISIBLE
                    if(!UtilMethod.instance.isStringNullOrNot(image_name)){
                        Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+image_name).into(holder.iv_testimonial)
                    }
                }
                else{
                    holder.rl_youtube.visibility = View.VISIBLE
                    holder.iv_testimonial.visibility = View.GONE
                    setImage(holder.teestinomial_video, s_youtube)
                }
            }
            else{
                holder.rl_youtube.visibility = View.GONE
                holder.iv_testimonial.visibility = View.VISIBLE
                if(!UtilMethod.instance.isStringNullOrNot(image_name)){
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+image_name).into(holder.iv_testimonial)
                }
            }
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

            if(!UtilMethod.instance.isStringNullOrNot(title)){
                holder.tv_title.setText(title)
            }

            if(!UtilMethod.instance.isStringNullOrNot(description)){
                holder.tv_description.setText(description)
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