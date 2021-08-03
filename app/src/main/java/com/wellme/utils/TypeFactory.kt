package com.wellme.utils

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.wellme.R


class TypeFactory(context1: Context) {
    var robotoBold: Typeface? = null
    var robotoLight:Typeface? = null
    var robotoRegular:Typeface? = null

    init {

        robotoBold = ResourcesCompat.getFont(context1, R.font.poppins_bold)
        robotoLight = ResourcesCompat.getFont(context1, R.font.poppins_bold)
        robotoRegular = ResourcesCompat.getFont(context1, R.font.poppins_bold)

    }






}