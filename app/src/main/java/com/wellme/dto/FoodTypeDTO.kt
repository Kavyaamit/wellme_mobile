package com.wellme.dto

data class FoodTypeDTO(var id : String,
                       var food_type_name : String,
                       var calorie_list : ArrayList<CalorieIntakeDTO>) {
}