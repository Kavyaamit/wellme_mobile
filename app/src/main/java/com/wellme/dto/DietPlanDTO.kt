package com.wellme.dto

data class DietPlanDTO(var id : String,
                       var fullname : String,
                       var food_name : String,
                       var size : String,
                       var quantity  : String,
                       var diet_food_image : String,
                       var time : String,
                       var added_on : String,
                       var updated_on : String,
                       var coach : String,
                       var foodid : String) {

    override fun toString(): String {
        return time
    }
}