package com.wellme.dto

data class OfferListDTO(
        var id : String?,
        var offer_title : String?,
        var offer_type : String?,
        var image : String?,
        var discount_type : String?,
        var coupon_code : String?,
        var coupon_status : String?,
        var discount_value : String?,
        var description : String,
        var extend_days : String?,
        var start_offer_date : String?,
        var expire_offer_date : String?,
        var offer_status : String?,
        var status : String,
        var del_status : String?,
        var created_on : String,
        var added_on : String,
        var update_on : String,

        var media_type : String?,
        var youtube : String?,
        var short_description : String) {
}