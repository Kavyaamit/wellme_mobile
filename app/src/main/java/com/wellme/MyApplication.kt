package com.wellme

import android.app.Application
import com.wellme.utils.AppConstants
import io.branch.referral.Branch
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


class MyApplication : Application(){

    var mSocket1 : Socket? = null

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext() : MyApplication {
            return instance as MyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            mSocket1 = IO.socket(AppConstants.SOCKET_URL)

            // Initialize the Branch object
            Branch.getAutoInstance(this)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }


    fun getSocket(): Socket? {
        return mSocket1
    }


}