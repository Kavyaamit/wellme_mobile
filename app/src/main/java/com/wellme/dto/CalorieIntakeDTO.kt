package com.wellme.dto

data class CalorieIntakeDTO(var food_id : String,
                            var food_name : String,
                            var quantity : String,
                            var foodtype : String,
                            var size : String,
                            var total_calorie : String,
                            var calorie_per_item : String,
                            var added_on : String,
                            var update_on : String,
                            var foodimage : String) {
}