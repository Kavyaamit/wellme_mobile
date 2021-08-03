package com.wellme.dto

data class BodyMeasurentDTO(
    var id: String,
    var body_measurement_value : String,
    var created_on : String,
    var updated_on : String,
    var user : String,
    var body_measurement_type : String

) {
}