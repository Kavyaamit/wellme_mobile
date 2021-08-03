package com.wellme.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.dto.TestimonialDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class TestinomialsViewAllAdapter(var context : Context, var list : List<TestimonialDTO>, var onItemClickCallbackLike: OnItemClickListener.OnItemClickCallback,
                                 var onItemClickCallbackComment: OnItemClickListener.OnItemClickCallback,
                                 var onItemClickCallback: OnItemClickListener.OnItemClickCallback,
                                 var onItemClickLikCount: OnItemClickListener.OnItemClickCallback,
                                 var onItemClickCommentCount: OnItemClickListener.OnItemClickCallback,
                                 var selected_id : String?) : RecyclerView.Adapter<TestinomialsViewAllAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
           var user_image : ImageView = view.findViewById(R.id.user_image)
           var iv_like_blue : ImageView = view.findViewById(R.id.iv_like_blue)
           var iv_comment : ImageView = view.findViewById(R.id.iv_comment)
           var testinomial_image : ImageView = view.findViewById(R.id.testinomial_image)
           var tv_name : TextView = view.findViewById(R.id.tv_name)
           var tv_posted_time : TextView = view.findViewById(R.id.tv_posted_time)
           var tv_like : TextView = view.findViewById(R.id.tv_like)
           var cv_youtube : CardView = view.findViewById(R.id.cv_youtube)
           var rl_img_video : RelativeLayout = view.findViewById(R.id.rl_img_video)
           var testinomial_video : ImageView = view.findViewById(R.id.testinomial_video)
           var ll_mainview : LinearLayout = view.findViewById(R.id.ll_mainview)
           var ll_header : LinearLayout = view.findViewById(R.id.ll_header)

        var ll_like_count : LinearLayout = view.findViewById(R.id.ll_like_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_testinomials_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : TestimonialDTO = list.get(position)
        var s_first_name : String? = ""
        var s_last_name : String? = ""
        var s_name : String? = ""
        var s_created_on : String? = ""
        var s_media_type : String? = ""
        var s_youtube : String? = ""
        var s_image : String? = ""
        var s_like_status : String? = ""
        var s_like_count : String? = ""
        var s_comment_count : String? = ""
        var typeface : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        holder.tv_name.setTypeface(typeface)
        holder.tv_posted_time.setTypeface(typeface)
        holder.tv_like.setTypeface(typeface)


        if(dto!=null){
            s_first_name = dto!!.first_name
            s_last_name = dto!!.last_name
            s_created_on = dto!!.created_on
            s_image = dto!!.image
            s_media_type = dto!!.media_type
            s_youtube = dto!!.youtube
            s_like_status = dto!!.like_status
            s_like_count = dto!!.total_like
            s_comment_count = dto!!.total_comment


            if(!UtilMethod.instance.isStringNullOrNot(s_first_name)){
                s_name = s_first_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_last_name)){
                if(UtilMethod.instance.isStringNullOrNot(s_name)){
                    s_name = s_last_name
                }
                else{
                    s_name+=" "+s_last_name
                }
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_media_type)){
                if(s_media_type.equals("image", true)){
                    if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                        Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+dto.image).into(holder.testinomial_image)
                        holder.testinomial_image.visibility =View.VISIBLE
                    }
                    else{
                        holder.testinomial_image.visibility =View.GONE
                    }
                    holder.rl_img_video.visibility = View.GONE
                    holder.testinomial_image.visibility = View.VISIBLE
                }
                else{
                    holder.rl_img_video.visibility = View.VISIBLE
                    holder.testinomial_image.visibility = View.GONE
                    setImage(holder.testinomial_video, s_youtube)
                }
            }
            else{
                holder.rl_img_video.visibility = View.GONE
                holder.testinomial_image.visibility = View.VISIBLE
                if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+dto.image).into(holder.testinomial_image)
                    holder.testinomial_image.visibility =View.VISIBLE
                }
                else{
                    holder.testinomial_image.visibility =View.GONE
                }
            }


            if(!UtilMethod.instance.isStringNullOrNot(s_name)){
                holder.tv_name.setText(""+s_name)
                holder.tv_name!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_name!!.visibility = View.GONE
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_like_status)){

                if (s_like_status.equals("1")){
                  //  holder.tv_like.setTextColor(context.resources.getColor(R.color.blue))
                    holder.iv_like_blue.setImageDrawable(context.resources.getDrawable(R.drawable.like_blue))
                }else {
                   // holder.tv_like.setTextColor(context.resources.getColor(R.color.grey))
                    holder.iv_like_blue.setImageDrawable(context.resources.getDrawable(R.drawable.like))
                }

            }

            if(!UtilMethod.instance.isStringNullOrNot(s_like_count)){
                var like_count = s_like_count.toInt()
                if(like_count> 1) {
                    holder.tv_like.setText(s_like_count + " Likes")
                }
                else{
                    holder.tv_like.setText(s_like_count + " Like")
                }
                holder.tv_like.visibility = View.VISIBLE
            }
            else{
                holder.tv_like.visibility = View.GONE
            }





//            if(!UtilMethod.instance.isStringNullOrNot(s_short_description)){
//                holder.tv_like.setText(s_short_description)
//                holder.tv_like!!.visibility = View.VISIBLE
//            }
//            else{
//                holder.tv_like!!.visibility = View.GONE
//            }

//            if (!UtilMethod.instance.isStringNullOrNot(s_description)){
//                holder.tv_comment!!.setText(s_description)
//                holder.tv_comment!!.visibility = View.VISIBLE
//            }
//            else{
//                holder.tv_comment!!.visibility = View.GONE
//            }

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
        holder.ll_header.setOnClickListener(OnItemClickListener(position, onItemClickCallback))

        holder.ll_like_count.setOnClickListener(OnItemClickListener(position, onItemClickLikCount))


    }

    fun setImage(iv : ImageView, url : String?){
        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
        Log.v("Blod Video ID ", "==> "+id)
        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
        iv.setOnClickListener(View.OnClickListener { var intent : Intent = Intent(context, VideoActivity::class.java)

            intent.putExtra("url", url)
            context.startActivity(intent) })
    }

    public fun setData(id1 : String?){
        this.selected_id = id1
        notifyDataSetChanged()
    }

    public fun setListValue(list : List<TestimonialDTO>){
            this.list = list
            notifyDataSetChanged()
    }
}