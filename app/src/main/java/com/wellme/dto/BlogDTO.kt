package com.wellme.dto

import java.io.Serializable

data class BlogDTO(var id : String,
                   var blog_title : String,
                   var blog_description : String,
                   var blog_type : String,
                   var blog_image : String,
                   var blog_link : String,
                   var added_on : String,
                   var like_status : String,
                   var total_comment : String,
                   var total_like : String
) : Serializable{
}