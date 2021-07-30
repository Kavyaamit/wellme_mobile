package com.wellme.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.wellme.R


class CustomTextView(context: Context) : AppCompatTextView(context) {
    private var typefaceType = 0
    var mFontFactory : TypeFactory? = null
    init {

    }

    constructor (app: Context, attrs : AttributeSet) : this(app) {
        //mApp = app
        //mContext = app
        applyCustomFont(app, attrs)
    }

    constructor (app: Context, attrs : AttributeSet, defStyle : Int) : this(app) {
        applyCustomFont(app, attrs)

    }



    private fun applyCustomFont(
        context: Context,
        attrs: AttributeSet
    ) {
        val array = context.theme.obtainStyledAttributes(
            attrs,
            com.wellme.R.styleable.CustomTextView,
            0, 0
        )
        typefaceType = try {
            array.getInteger(R.styleable.CustomTextView_font_name, 0)
        } finally {
            array.recycle()
        }
        if (!isInEditMode) {
            setTypeface(getTypeFace(typefaceType, context))
        }
    }

    fun getTypeFace(type: Int, context: Context): Typeface? {
        if (mFontFactory == null) mFontFactory = TypeFactory(context)
        return when (type) {
            Constants.O_ROBOTO_BOLD -> mFontFactory?.robotoBold
            Constants.O_ROBOTO_LIGHT -> mFontFactory?.robotoLight
            Constants.O_ROBOTO_REGULAR -> mFontFactory?.robotoRegular
            else -> mFontFactory?.robotoRegular
        }
    }

    fun getTypeFace1(){

    }

    interface Constants {
        companion object {
            const val O_ROBOTO_BOLD = 1
            const val O_ROBOTO_LIGHT = 2
            const val O_ROBOTO_REGULAR = 3
        }
    }






}