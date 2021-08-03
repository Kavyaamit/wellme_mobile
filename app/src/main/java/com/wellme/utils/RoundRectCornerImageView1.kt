package com.wellme.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

class RoundRectCornerImageView1(context : Context) : AppCompatImageView(context) {
    var radius : Float = 18.0f
    var path : Path? = null
    var rect : RectF? = null;


    constructor (app: Context, attrs : AttributeSet) : this(app) {
        //mApp = app
        //mContext = app
        init()
    }

    constructor (app: Context, attrs : AttributeSet, defStyle : Int) : this(app) {
        init()

    }


    fun init(){
        path = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        rect = RectF(0F, 0F, 100f, 100f)
        path!!.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas!!.clipPath(path!!)
        super.onDraw(canvas)

    }



}