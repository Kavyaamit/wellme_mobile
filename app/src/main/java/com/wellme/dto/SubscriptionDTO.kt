package com.wellme.dto

data class SubscriptionDTO(var id : String,
                           var title : String,
                           var description : String,
                           var image : String,
                           var amount : String,
                           var expire_date : String,
                           var type_of_plan : String,
                           var no_of_days : String,
                           var no_of_coaches : String
                            ) {

}