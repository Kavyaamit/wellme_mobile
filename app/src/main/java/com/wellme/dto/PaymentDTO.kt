package com.wellme.dto

data class PaymentDTO(val id : String,
                      val payment_amount : String,
                      val plan_start : String,
                      val plan_end : String,
                      val payment_transaction_id : String,
                      val purchaced_on : String,
                      val payment_method : String,
                      val created_on : String,
                      val update_on: String,
                      val name : String,
                      val description: String,
                      val image : String,
                      val amount : String,
                      val validity : String,
                      val status : String,
                      val user : String,
                      val plan_id : String) {
}