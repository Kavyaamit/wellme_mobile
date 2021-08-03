package com.wellme.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
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
import com.wellme.dto.DietPlanDTO
import com.wellme.dto.FitnessPlanDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.UtilMethod

class MyFitnessPlanAdapter(var cxt : Context, var fitnessplan_list : ArrayList<FitnessPlanDTO>) : RecyclerView.Adapter<MyFitnessPlanAdapter.ViewHolder>(){
    var regular : Typeface? = ResourcesCompat.getFont(cxt, R.font.poppins_regular)
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        var tv_fitness : TextView = view.findViewById(R.id.tv_fitness)
        var tv_duration : TextView = view.findViewById(R.id.tv_duration)
        var tv_set_repeat : TextView = view.findViewById(R.id.tv_set_repeat)
        var iv_or : ImageView = view.findViewById(R.id.iv_or)
        var iv_image : ImageView = view.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(cxt).inflate(R.layout.item_fitness_plan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fitnessplan_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : FitnessPlanDTO = fitnessplan_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(cxt, R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(cxt, R.font.poppins_bold)
        holder.tv_fitness.setTypeface(regular)
        holder.tv_duration.setTypeface(regular)
        holder.tv_set_repeat.setTypeface(regular)
        if(dto!=null){
            var s_exercise : String = dto.exercise
            var s_set : String = dto.exercise_set
            var s_repeat : String = dto.repeat
            var s_duration : String = dto.time
            var s_video : String = dto.video_url
            var set : String = "0"
            var repeat : String = "0"
            if(!UtilMethod.instance.isStringNullOrNot(s_exercise)){
                holder.tv_fitness.setText(s_exercise)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_set)){
                set = s_set
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_repeat)){
                repeat = s_repeat
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_duration)){
                holder.tv_duration.setText("(Duration : "+s_duration+")")
            }
            else{
                holder.tv_duration.setText("")
            }
            holder.tv_set_repeat.setText("(Set-"+set+",Repeat-"+repeat+")")
            setImage(holder.iv_image, s_video)
        }
        if(position == fitnessplan_list.size-1){
            holder.iv_or.visibility = View.GONE
        }
        else{
            holder.iv_or.visibility = View.VISIBLE
        }


    }

    fun setImage(iv : ImageView, url : String) {
        var id: String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
        Picasso.get().load("https://img.youtube.com/vi/" + id + "/0.jpg")
            .error(R.drawable.default_image).into(iv)
        if (!UtilMethod.instance.isStringNullOrNot(id)) {
            iv.setOnClickListener(View.OnClickListener {
                var intent: Intent = Intent(cxt, VideoActivity::class.java)

                intent.putExtra("url", url)
                cxt.startActivity(intent)
            })
        }
    }
}