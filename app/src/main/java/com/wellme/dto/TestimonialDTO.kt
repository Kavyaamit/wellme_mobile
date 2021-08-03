package com.wellme.dto

import java.io.Serializable


data class TestimonialDTO(
        var id : String?,
        var first_name : String?,
        var last_name : String?,
        var image : String?,
        var media_type : String?,
        var youtube : String?,
        var short_description : String,
        var description : String,
        var status : String,
        var created_on : String,
        var update_on : String,
        var total_like : String,
        var total_comment : String,
        var like_status : String
        ): Serializable {
}
