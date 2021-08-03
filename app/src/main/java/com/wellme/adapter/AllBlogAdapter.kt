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
import androidx.cardview.widget.CardView
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

class AllBlogAdapter(var context : Context, var list : List<BlogDTO>,
                     var onItemClickCallbackLike: OnItemClickListener.OnItemClickCallback,
                     var onItemClickCallbackComment: OnItemClickListener.OnItemClickCallback,
                     var onItemClickCallback: OnItemClickListener.OnItemClickCallback,
                     var onItemClickLikCount: OnItemClickListener.OnItemClickCallback,
                     var onItemClickCommentCount: OnItemClickListener.OnItemClickCallback,
                     var selected_id : String?) : RecyclerView.Adapter<AllBlogAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
           var iv_like_blue : ImageView = view.findViewById(R.id.iv_like_blue)
           var blog_image : ImageView = view.findViewById(R.id.blog_image)
           var tv_title : TextView = view.findViewById(R.id.tv_title)
           var tv_posted_time : TextView = view.findViewById(R.id.tv_posted_time)
           var tv_like : TextView = view.findViewById(R.id.tv_like)
           var rl_img_video : RelativeLayout = view.findViewById(R.id.rl_img_video)
           var video_blog : ImageView = view.findViewById(R.id.video_blog)
           var ll_mainview : LinearLayout = view.findViewById(R.id.ll_mainview)
           var ll_like_count : LinearLayout = view.findViewById(R.id.ll_like_count)
           var iv_comment : ImageView = view.findViewById(R.id.iv_comment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_blogall, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : BlogDTO = list.get(position)
        var s_title : String? = ""
        var s_created_on : String? = ""
        var s_media_type : String? = ""
        var s_youtube : String? = ""
        var s_image : String? = ""
        var s_like_status : String? = ""
        var s_like_count : String? = ""
        var typeface : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var typeface_bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        holder.tv_title.setTypeface(typeface_bold)
        holder.tv_posted_time.setTypeface(typeface)
        holder.tv_like.setTypeface(typeface)

        if(dto!=null){
            s_title = dto!!.blog_title
            s_created_on = dto!!.added_on
            s_image = dto!!.blog_image
            s_media_type = dto!!.blog_type
            s_youtube = dto!!.blog_link
            s_like_status = dto!!.like_status
            s_like_count = dto!!.total_like



            Log.v("Media Type ", "=> "+s_media_type)
            Log.v("Media Type ", "=> "+s_image)
            if(!UtilMethod.instance.isStringNullOrNot(s_media_type)){
                if(s_media_type.equals("image", true)){
                    if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                        Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_image).into(holder.blog_image)
                        holder.blog_image.visibility =View.VISIBLE

                    }
                    else{
                        holder.blog_image.visibility =View.GONE
                    }
                    holder.rl_img_video.visibility = View.GONE
                    holder.blog_image.visibility = View.VISIBLE
                }
                else{
                    holder.rl_img_video.visibility = View.VISIBLE
                    holder.blog_image.visibility = View.GONE
                    setImage(holder.video_blog, s_youtube)
                }
            }
            else{
                holder.rl_img_video.visibility = View.GONE
                holder.blog_image.visibility = View.VISIBLE
                if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_image).into(holder.blog_image)
                    holder.blog_image.visibility =View.VISIBLE
                }
                else{
                    holder.blog_image.visibility =View.GONE
                }
            }


            if(!UtilMethod.instance.isStringNullOrNot(s_title)){
                holder.tv_title.setText(""+s_title)
                holder.tv_title!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_title!!.visibility = View.GONE
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_like_status)){

                if (s_like_status.equals("1")){

                    holder.iv_like_blue.setImageDrawable(context.resources.getDrawable(R.drawable.like_blue))
                }else {
                    holder.iv_like_blue.setImageDrawable(context.resources.getDrawable(R.drawable.like))
                }

            }

            if(!UtilMethod.instance.isStringNullOrNot(s_like_count)){
                var total_like = s_like_count.toInt()
                if(total_like>1){
                    holder.tv_like.setText(s_like_count+" Likes")
                    holder.ll_like_count.visibility = View.VISIBLE
                }
                else if(total_like == 1){
                    holder.tv_like.setText(s_like_count+" Like")
                    holder.ll_like_count.visibility = View.VISIBLE
                }
                else{
                    holder.ll_like_count.visibility = View.GONE
                }
            }




            if(!UtilMethod.instance.isStringNullOrNot(s_created_on)){
                holder.tv_posted_time.setText("Posted on : "+UtilMethod.instance.getDate(s_created_on))
                holder.tv_posted_time.visibility = View.VISIBLE
            }
            else{
                holder.tv_posted_time.visibility = View.GONE
            }

        }

        holder.iv_like_blue.setOnClickListener(OnItemClickListener(position, onItemClickCallbackLike))
        holder.iv_comment.setOnClickListener(OnItemClickListener(position, onItemClickCallbackComment))
        holder.ll_mainview.setOnClickListener(OnItemClickListener(position, onItemClickCallback))
        holder.ll_like_count.setOnClickListener(OnItemClickListener(position, onItemClickLikCount))
//        holder.ll_mainview.setOnClickListener(OnItemClickListener(position, onItemClickCommentCount))

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

    public fun setListValue(list1 : List<BlogDTO>){
        this.list = list1
        notifyDataSetChanged()
    }
}