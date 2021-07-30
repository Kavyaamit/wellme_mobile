package com.wellme.utils

import android.app.Dialog
import android.content.DialogInterface
import android.view.View

class OnDialogClickListener(var status : Int) : DialogInterface.OnClickListener {
    public var callback : OnDialogClickCallback? = null

    constructor(status : Int, ondialog : OnDialogClickCallback) : this(status){
        this.callback = ondialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        callback!!.onDialogClicked(dialog, which)
    }

    interface OnDialogClickCallback {
        fun onDialogClicked(dialog: DialogInterface?, position: Int)
    }
}