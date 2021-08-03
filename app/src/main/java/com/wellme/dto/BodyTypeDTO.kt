package com.wellme.dto

data class BodyTypeDTO(
    var id: String,
    var name : String,
    var slug : String,
    var created_on : String,
    var updated_on : String,
    var measurent_list : ArrayList<BodyMeasurentDTO>
) {
}