package com.wellme.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.dto.BlogDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod

class BlogAdapter(var context: Context, var list : List<BlogDTO>) : RecyclerView.Adapter<BlogAdapter.ViewHolder>(){

    class ViewHolder(view1 : View) : RecyclerView.ViewHolder(view1){
        var blog_image : ImageView = view1.findViewById(R.id.blog_image)
        var iv_play : ImageView = view1.findViewById(R.id.iv_play)
        var tv_title : TextView = view1.findViewById(R.id.tv_title)
        var tv_description : TextView = view1.findViewById(R.id.tv_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        var dto : BlogDTO = list.get(position)
        var s_title : String? = ""
        var s_description : String? = ""
        var s_image : String? = ""
        var s_type : String? = ""
        var s_link : String? = ""

        holder.tv_title.setTypeface(bold)
        holder.tv_description.setTypeface(regular)
        holder.blog_image.clipToOutline = true

        if(dto!=null){
            s_title = dto!!.blog_title
            s_description = dto!!.blog_description
            s_image = dto!!.blog_image
            s_type = dto!!.blog_type
            s_link = dto!!.blog_link
            if(!UtilMethod.instance.isStringNullOrNot(s_title)){
                holder.tv_title.setText(s_title)
                holder.tv_title!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_title!!.visibility = View.GONE
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_description)){
                holder.tv_description.setText(s_description)
                holder.tv_description!!.visibility = View.VISIBLE
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
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+dto.blog_image).into(holder.blog_image)
                    }
                }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setImage(iv : ImageView, url : String){
        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
        Log.v("Blod Video ID ", "==> "+id)
        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
        iv.setOnClickListener(View.OnClickListener { var intent : Intent = Intent(context, VideoActivity::class.java)

            intent.putExtra("url", url)
            context.startActivity(intent) })
    }
}