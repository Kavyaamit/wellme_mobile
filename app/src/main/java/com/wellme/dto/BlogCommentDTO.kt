package com.wellme.dto

data class BlogCommentDTO(var id : String,
                          var profile_image : String,
                          var user_id : String,
                          var first_name : String,
                          var last_name : String,
                          var comment_text : String,
                          var comment_type : String,
                          var added_on : String
                          ) {
}