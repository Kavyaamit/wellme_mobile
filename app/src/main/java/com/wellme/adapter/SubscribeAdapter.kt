package com.wellme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.dto.SubscriptionDTO
import com.wellme.utils.AppConstants


class SubscribeAdapter(var context: Context, var subscribe_list: List<SubscriptionDTO>) : PagerAdapter(){


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun getCount(): Int {
        return subscribe_list.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view : View = LayoutInflater.from(context).inflate(R.layout.item_subscribe, container, false)
        var dto : SubscriptionDTO = subscribe_list.get(position)
        var iv : ImageView = view.findViewById(R.id.image)
        if(dto!=null){
            Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+dto.image).into(iv)
        }

        container.addView(view);

        return view
    }


}