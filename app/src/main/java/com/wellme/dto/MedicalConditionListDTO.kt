package com.wellme.dto

data class MedicalConditionListDTO(var id : String,
                                   var medical_document : String,
                                   var document_type : String,
                                   var created_on : String,
                                   var updated_on : String,
                                   var user : String,
                                   var medical_condition : String
                               ) {
}