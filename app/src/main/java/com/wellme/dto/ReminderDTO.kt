package com.wellme.dto

data class ReminderDTO(var title : String?,
                       var status : Boolean,
                       var time : String?,
                       var repeating_val : String?,
                       var image : Int,
                       var type : Int) {

}