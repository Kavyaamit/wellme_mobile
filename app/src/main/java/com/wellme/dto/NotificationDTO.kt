package com.wellme.dto

data class NotificationDTO(var id : String,
                           var title : String,
                           var description : String,
                           var image : String,
                           var video : String,
                           var added_on : String,
                           var updated_on : String,
                           var user_id : String,
                           var coach : String
                            ) {
}