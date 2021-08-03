package com.wellme.dto

data class AssignedFitnessPlanDTO(
    var exercise_type : String,
    var exercise_list : ArrayList<FitnessPlanDTO>
) {
    override fun toString(): String {
        return exercise_type
    }
}