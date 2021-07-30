package com.wellme.utils

import android.view.View


class OnItemClickListener(var pos : Int) : View.OnClickListener{
    private var position = 0
    private var parentId = -1
    private var onItemClickCallback: OnItemClickCallback? = null
    private var onItemClickCallbackParentPosition: OnItemClickCallbackParentPosition? = null

    constructor(pos1 : Int, onItemClickListener: OnItemClickCallback) : this(pos1){
        this.position = pos1
        this.onItemClickCallback = onItemClickListener

    }

    constructor(pos1 : Int, parentId : Int, onItemClickCallbackParentPosition: OnItemClickCallbackParentPosition) : this(pos1){
        this.position = pos1
        this.parentId = parentId
        this.onItemClickCallbackParentPosition = onItemClickCallbackParentPosition
    }

    override fun onClick(v: View?) {
        if (parentId == -1) {
            onItemClickCallback?.onItemClicked( v, position );
        } else {
            onItemClickCallbackParentPosition?.onItemClicked( v, position, parentId );
        }
    }


    interface OnItemClickCallback {
        fun onItemClicked(view: View?, position: Int)
    }

    interface OnItemClickCallbackParentPosition {
        fun onItemClicked(view: View?, position: Int, parentId: Int)
    }
}