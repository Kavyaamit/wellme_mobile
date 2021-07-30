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
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class GalleryImageListAdapter(var context: Context, var image_list: List<String?> , var onItemClickCallbackLike: OnItemClickListener.OnItemClickCallback , var onItemClickCallbackVideo: OnItemClickListener.OnItemClickCallback , var onItemClickCallbackFile: OnItemClickListener.OnItemClickCallback , var type : String?) : RecyclerView.Adapter<GalleryImageListAdapter.ViewHolder>(){

    class ViewHolder(var view1 : View) : RecyclerView.ViewHolder(view1){
        var iv_image : ImageView = view1.findViewById(R.id.iv_image)
        var iv_video : ImageView = view1.findViewById(R.id.iv_video)
        var rl_img_video : RelativeLayout = view1.findViewById(R.id.rl_img_video)
        var rl_img_file : LinearLayout = view1.findViewById(R.id.rl_img_file)
        var tv_file_name : TextView = view1.findViewById(R.id.tv_file_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return image_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var s_media_type : String? = ""
        var dto : String? = image_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)

        if(dto!=null){

            var image_name : String? = dto
            Log.d("type",">>>"+type)
            Log.d("dto",">>>"+dto)

            if (type.equals("image")){

                if(!UtilMethod.instance.isStringNullOrNot(image_name)){
//                        Picasso.get().load(image_name).into(holder.iv_image)
                    Glide.with(context).load(image_name).into(holder.iv_image)

                    holder.iv_image.visibility = View.VISIBLE
                    holder.rl_img_video.visibility = View.GONE
                }else{
                    holder.iv_image.visibility = View.GONE
                }
            }else if(type.equals("video")){

                if(!UtilMethod.instance.isStringNullOrNot(image_name)){
//                        Picasso.get().load(image_name).into(holder.iv_image)
                    Glide.with(context).load(image_name).into(holder.iv_video)

                    holder.iv_image.visibility = View.GONE
                    holder.rl_img_video.visibility = View.VISIBLE
                }else{
                    holder.rl_img_video.visibility = View.GONE
                }


            }else if(type.equals("pdf")){

                if(!UtilMethod.instance.isStringNullOrNot(image_name)){
//                        Picasso.get().load(image_name).into(holder.iv_image)
//                    Glide.with(context).load(image_name).into(holder.iv_video)
    var name = getName(image_name)

                    holder.tv_file_name.setText(name)
                    holder.iv_image.visibility = View.GONE
                    holder.rl_img_file.visibility = View.VISIBLE
                    holder.rl_img_video.visibility = View.GONE
                }else{
                    holder.rl_img_file.visibility = View.GONE
                }


            }


        }
        holder.iv_image.setOnClickListener(OnItemClickListener(position, onItemClickCallbackLike))
        holder.iv_video.setOnClickListener(OnItemClickListener(position, onItemClickCallbackVideo))
        holder.rl_img_file.setOnClickListener(OnItemClickListener(position, onItemClickCallbackFile))
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

    fun getName(title : String?):String?{

        var lStr = title
        lStr = lStr?.substring(lStr.lastIndexOf("/") + 1)

        return lStr

    }
}