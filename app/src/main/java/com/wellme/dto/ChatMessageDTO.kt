package com.wellme.dto

data class ChatMessageDTO(
                var id : String,
                var to_id : String,
                var from_id : String,
                var conversation_id : String,
                var message : String,
                var file : String,
                var type : String,
                var extension : String,
                var read_status : String,
                var msg_status : String,
                var read_at : String,
                var created_at : String,
                var delivered_at : String,
                var updated_at : String,
                var deleted_at : String
    ) {
}