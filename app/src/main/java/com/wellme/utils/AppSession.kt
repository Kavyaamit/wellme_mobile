package com.wellme.utils

import android.content.SharedPreferences

class AppSession {

    private val mSharedPreferences: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null

    fun setCatIdBranch(categoryId: String?) {
        prefsEditor = mSharedPreferences?.edit()
        prefsEditor?.putString("getCatIdBranch", categoryId)
        prefsEditor?.commit()

    }

    fun getCatIdBranch(): String? {
        return mSharedPreferences!!.getString("getCatIdBranch", "")
    }


}