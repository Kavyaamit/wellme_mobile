package com.wellme.dto

data class AssignedDietPlanDTO(
        var time : String,
        var diet_plan : ArrayList<DietPlanDTO>
) {
}