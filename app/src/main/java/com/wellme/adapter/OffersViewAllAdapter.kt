package com.wellme.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.dto.OfferListDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class OffersViewAllAdapter(var context : Context, var list : List<OfferListDTO>, var purpose : Int, var onItemClickCallback: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<OffersViewAllAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

           var offer_image : ImageView = view.findViewById(R.id.iv_offer)
           var tv_title : TextView = view.findViewById(R.id.tv_offer_title)
           var tv_expire_on : TextView = view.findViewById(R.id.tv_expire_on)
           var tv_discount : TextView = view.findViewById(R.id.tv_discount)
           var btn_read_more : Button = view.findViewById(R.id.btn_read_more)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_offerslist_viewall, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dto : OfferListDTO = list.get(position)
        var s_offer_title : String? = ""
        var s_discount_type : String? = ""
        var s_discount_amount : String? = ""
        var s_expire_on : String? = ""
        var s_image : String? = ""

        var typeface : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_regular)
        var typeface_bold : Typeface? = ResourcesCompat.getFont(context, R.font.poppins_bold)
        holder.tv_title.setTypeface(typeface_bold)
        holder.tv_expire_on.setTypeface(typeface)
        holder.tv_discount.setTypeface(typeface)
        holder.btn_read_more.setTypeface(typeface)

        if(purpose == 1){
            holder.btn_read_more.setText("Apply")
        }
        else{
            holder.btn_read_more.setText("Read More")
        }

        if(dto!=null){
            s_offer_title = dto!!.offer_title
            s_discount_type = dto!!.discount_type
            s_discount_amount = dto!!.discount_value
            s_expire_on = dto!!.expire_offer_date
            s_image = dto!!.image


            if(!UtilMethod.instance.isStringNullOrNot(s_discount_type) && !UtilMethod.instance.isStringNullOrNot(s_discount_amount)){
                if(s_discount_type.equals("0")){
                    holder.tv_discount.setText(s_discount_amount+"% Off")
                }
                else{
                    var f1 : Float = s_discount_amount!!.toFloat()
                    holder.tv_discount.setText(context!!.resources.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(f1)+" Off")
                }
            }


                    if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                        Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+dto.image).into(holder.offer_image)
                        holder.offer_image.visibility =View.VISIBLE
                    }
                    else{
                        holder.offer_image.visibility =View.GONE
                    }

                }





            if(!UtilMethod.instance.isStringNullOrNot(s_offer_title)){
                holder.tv_title.setText(""+s_offer_title)
                holder.tv_title!!.visibility = View.VISIBLE
            }
            else{
                holder.tv_title!!.visibility = View.GONE
            }







//            if (!UtilMethod.instance.isStringNullOrNot(s_description)){
//                holder.tv_description!!.setText(s_description)
//                holder.tv_description!!.visibility = View.VISIBLE
//            }
//            else{
//                holder.tv_description!!.visibility = View.GONE
//            }

            if(!UtilMethod.instance.isStringNullOrNot(s_expire_on)){
                holder.tv_expire_on.setText("Expire on : "+UtilMethod.instance.getDate(s_expire_on))
                holder.tv_expire_on.visibility = View.VISIBLE
            }
            else{
                holder.tv_expire_on.visibility = View.GONE
            }

            holder.btn_read_more.setOnClickListener(OnItemClickListener(position, onItemClickCallback))

        }
    }

//    fun setImage(iv : ImageView, url : String?){
//        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
//        Log.v("Blod Video ID ", "==> "+id)
//        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
//        iv.setOnClickListener(View.OnClickListener { var intent : Intent = Intent(context, VideoActivity::class.java)
//
//            intent.putExtra("url", url)
//            context.startActivity(intent) })
//    }
//}