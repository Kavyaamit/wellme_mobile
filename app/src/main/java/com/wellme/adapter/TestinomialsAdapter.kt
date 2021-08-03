package com.wellme.adapter

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
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.dto.BlogDTO
import com.wellme.dto.TestimonialDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class TestinomialsAdapter(var context: Context,
                          var testimonial_list: List<TestimonialDTO>,
                          var onItemClickLikCount: OnItemClickListener.OnItemClickCallback,
                          var onItemClickCommentCount: OnItemClickListener.OnItemClickCallback,
                          var selected_id : String?
                          ) : RecyclerView.Adapter<TestinomialsAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){

        var blog_image : ImageView = view1.findViewById(R.id.blog_image)
        var iv_play : ImageView = view1.findViewById(R.id.iv_play)
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_description : TextView = view1.findViewById(R.id.tv_description)
        var tv_like_count : TextView = view1.findViewById(R.id.tv_like_count)
        var tv_comment_count : TextView = view1.findViewById(R.id.tv_comment_count)
        var ll_like_count : LinearLayout = view1.findViewById(R.id.ll_like_count)
        var ll_comment_count : LinearLayout = view1.findViewById(R.id.ll_comment_count)
        var iv_like_blue : ImageView = view1.findViewById(R.id.iv_like_blue)
        var iv_comment : ImageView = view1.findViewById(R.id.iv_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_testimonials, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testimonial_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        var dto : TestimonialDTO = testimonial_list.get(position)
        var s_title : String? = ""
        var s_description : String? = ""
        var s_image : String? = ""
        var s_type : String? = ""
        var s_link : String? = ""
        var s_like_count : String? = ""
        var s_comment_count : String? = ""
        var s_like_status : String? = ""

        holder.tv_title.setTypeface(bold)
        holder.tv_description.setTypeface(regular)
        holder.blog_image.clipToOutline = true

        if(dto!=null){
            s_title = dto!!.short_description
            s_description = dto!!.description
            s_image = dto!!.image
            s_type = dto!!.media_type
            s_link = dto!!.youtube
            s_like_count = dto!!.total_like
            s_comment_count = dto!!.total_comment
            s_like_status = dto!!.like_status

            if(!UtilMethod.instance.isStringNullOrNot(s_title)){
                holder.tv_title.setText(s_title)
                holder.tv_title!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_title!!.visibility = View.GONE
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_like_count)){
                holder.tv_like_count.setText(s_like_count)
                holder.tv_like_count!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_like_count!!.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_comment_count)){
                holder.tv_comment_count.setText(s_comment_count)
                holder.tv_comment_count!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_comment_count!!.visibility = View.GONE
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_description)){
                holder.tv_description.setText(s_description)
                holder.tv_description!!.visibility = View.GONE
            }
            else{
                holder.tv_description!!.visibility = View.GONE
            }

            if(s_type.equals("youtube")){
                holder.iv_play.visibility = View.VISIBLE
                setImage(holder.blog_image, s_link)
            }
            else{
                if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                    holder.iv_play.visibility = View.GONE
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+dto.image).into(holder.blog_image)
                }
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_like_status)){

                if (s_like_status.equals("1")){
                    //  holder.tv_like.setTextColor(context.resources.getColor(R.color.blue))
                    holder.iv_like_blue.setImageDrawable(context.resources.getDrawable(R.drawable.like_))
                }else {
                    // holder.tv_like.setTextColor(context.resources.getColor(R.color.grey))
                    holder.iv_like_blue.setImageDrawable(context.resources.getDrawable(R.drawable.like))
                }

            }
        }



        holder.ll_like_count.setOnClickListener(OnItemClickListener(position, onItemClickLikCount))
        holder.ll_comment_count.setOnClickListener(OnItemClickListener(position, onItemClickCommentCount))
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

    public fun setData(id1 : String?){
        this.selected_id = id1
        notifyDataSetChanged()
    }

    public fun setListValue(list : List<TestimonialDTO>){
        this.testimonial_list = list
        notifyDataSetChanged()
    }
}