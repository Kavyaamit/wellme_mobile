package com.wellme.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AppService {

    @GET("data_list/")
    fun getDataList() : Call<ResponseBody>

    @GET("City_List/")
    fun getCityList(@Query("state") state : String?,@Query("search") search : String?) : Call<ResponseBody>

    @GET("exercise_list/")
    fun getExerciseList() : Call<ResponseBody>

    @GET("coach_list/")
    fun getCoachList() : Call<ResponseBody>

    @GET("get_testimonial_comment_list/")
    fun callTestimonialCommentList(@Header("Authorization") accesstoken: String?, @Query("testimonial_id") testimonial_id: String? ) : Call<ResponseBody>

    @POST("add_testimonial_comment/")
    @Multipart
    fun callTestimonialCommentText(@Header("Authorization") accesstoken: String?,
                                   @Part ("testimonial_id") testimonial_id:RequestBody? ,
                                   @Part("comment_text") comment_text:RequestBody?,
                                   @Part("comment_type") comment_type:RequestBody?

    ) : Call<ResponseBody>

    @POST("add_testimonial_comment/")
    @Multipart
    fun callTestimonialImage(@Header("Authorization") accesstoken: String?,
                             @Part ("testimonial_id") testimonial_id:RequestBody? ,
                             @Part("comment_type") comment_type:RequestBody?,
                             @Part image:MultipartBody.Part
    ) : Call<ResponseBody>

    @POST("add_testimonial_comment/")
    @Multipart
    fun callTestimonialVideo(@Header("Authorization") accesstoken: String?,
                             @Part ("testimonial_id") testimonial_id:RequestBody? ,
                             @Part("comment_type") comment_type:RequestBody?,
                             @Part video:MultipartBody.Part
    ) : Call<ResponseBody>


    @GET("book_coach_list/")
    fun getMyCoachList(@Header("Authorization") accesstoken: String?) : Call<ResponseBody>

    @GET("complain_list/")
    fun getComplaintTypeList() : Call<ResponseBody>

    @POST("coach_profile/")
    @FormUrlEncoded
    fun getCoachProfile(@Header("Authorization") accesstoken: String?, @Field("coach_id") coach_id : String) : Call<ResponseBody>

    @POST("add_booking_detail/")
    @FormUrlEncoded
    fun callBookCoach(@Header("Authorization") accesstoken: String?, @Field("coach_id") coach_id : String?, @Field("coach_fullname") coach_fullname : String?, @Field("date") date : String?,@Field("time") time : String) : Call<ResponseBody>

    @GET("subscription_list/")
    fun getSubscriptionList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @GET("user_transaction_list/")
    fun getMySubscriptionList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @GET("food_list/")
    fun getTrackedFoodList(@Query("food_type") food_type : String?, @Query("search") search : String?) : Call<ResponseBody>

    @POST("notification_list/")
    fun getNotificationList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @POST("subscription_detail/")
    @FormUrlEncoded
    fun getSubscriptionDetail(@Header("Authorization") accesstoken : String? , @Field("subscription_id") subscription_id : String?) : Call<ResponseBody>

    @POST("offer_list/")
    @FormUrlEncoded
    fun getOfferList(@Header("Authorization") accesstoken : String? , @Field("offer_type") offer_type : String?) : Call<ResponseBody>

    @POST("check_user_offer_validity/")
    @FormUrlEncoded
    fun checkUserOfferValidity(@Header("Authorization") accesstoken : String? , @Field("offer_id") offer_id : String?) : Call<ResponseBody>

    @POST("check_user_coupan_validity/")
    @FormUrlEncoded
    fun checkUserCouponValidity(@Header("Authorization") accesstoken : String? , @Field("coupan_code") coupan_code : String?) : Call<ResponseBody>

    @GET("Testimonials/")
    fun getTestinomialList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @POST("add_testimonial_like/")
    @FormUrlEncoded
    fun callTestimonialLike(@Header("Authorization") accesstoken: String?, @Field("testimonial_id") testimonial_id: String? ,@Field("like_status") like_status: String?) : Call<ResponseBody>

    @POST("add_blog_like/")
    @FormUrlEncoded
    fun callBlogLike(@Header("Authorization") accesstoken: String?, @Field("blog_id") blog_id: String? ,@Field("like_status") like_status: String?) : Call<ResponseBody>

    @POST("add_blog_comment/")
    @FormUrlEncoded
    fun callBlogComment(@Header("Authorization") accesstoken: String?, @Field("blog_id") blog_id: String? ,@Field("comment_type") comment_type: String? ,@Field("comment_text") comment_text: String?) : Call<ResponseBody>

    @GET("get_blog_comment_list/")
    fun callBlogCommentList(@Header("Authorization") accesstoken: String?, @Query("blog_id") blog_id: String? ) : Call<ResponseBody>

    @GET("user_medical_condition_list/")
//    fun getTestinomialList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>
    fun getMedicalConditionList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>


    @GET("get_testimonial_comment_list/")
    fun callTestimonialComment(@Header("Authorization") accesstoken: String?, @Query("testimonial_id") testimonial_id: String? ) : Call<ResponseBody>

    @GET("blog_list/")
    fun getBlogList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @POST("add_user_medical_condition/")
    @Multipart
    fun callUploadMedicalDoc(@Header("Authorization") accesstoken: String?,
                             @Part ("medical_condition_id") testimonial_id:RequestBody? ,
                             @Part("document_type") comment_type:RequestBody?,
                             @Part image:MultipartBody.Part
    ) : Call<ResponseBody>

    @GET("food_feedback_type/")
    fun getFoodFeedbackTypeList() : Call<ResponseBody>

    @POST("transaction_list/")
    fun getTransactionList(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @GET("latest/")
    fun getRateAPI() : Call<ResponseBody>

    @POST("register_user/")
    @FormUrlEncoded
    fun callRegistration(@Field("username") username : String?, @Field("email") email : String?, @Field("password") password : String?, @Field("password2") password2 : String?) : Call<ResponseBody>

    @GET("get_testimonial_like_list/")
    fun getTestinomialLikeList(@Header("Authorization") accesstoken : String?,@Query("testimonial_id") testimonial_id : String?) : Call<ResponseBody>

    @POST("login_user/")
    @FormUrlEncoded
    fun callLogin(@Field("username") username: String?, @Field("password") password: String?) : Call<ResponseBody>

    @POST("change_password/")
    @FormUrlEncoded
    fun callChangePassword(@Field("username") username: String?, @Field("password") password: String?) : Call<ResponseBody>

    @POST("check_user_exist/")
    @FormUrlEncoded
    fun callUserExist(@Field("username") username: String?) : Call<ResponseBody>

    @POST("add_user_complain/")
    @FormUrlEncoded
    fun callAddComplaintAPI(@Header("Authorization") accesstoken : String?, @Field("complain_title") complain_title: String?, @Field("complain_description") description: String?) : Call<ResponseBody>

    @POST("add_user_feedback/")
    @FormUrlEncoded
    fun callFeedbackAPI(@Header("Authorization") accesstoken : String?, @Field("feedback_title") feedback_title: String?, @Field("feedback_description") feedback_description: String?, @Field("feedback_type") feedback_type: String?) : Call<ResponseBody>

    @POST("add_notification_id/")
    @FormUrlEncoded
    fun updateNotification(@Header("Authorization") accesstoken : String?, @Field("notification_id") notification_id: String?) : Call<ResponseBody>


    @POST("create_user_profile/")
    @FormUrlEncoded
    fun callCreateProfile(@Header("Authorization") accesstoken : String?,
                          @Field("first_name") first_name : String?,
                          @Field("last_name") last_name : String?,
                          @Field("phone") phone : String?,
                          @Field("age") age : String?,
                          @Field("gender") gender : String?,
                          @Field("city") city : String?,
                          @Field("state") state : String?,
                          @Field("initial_logged_weight") weight : String?,
                          @Field("medical_condition") medical_condition : String?,
                          @Field("occupation") occupation : String?,
                          @Field("initial_logged_height") initial_logged_height : String?,
                          @Field("goal") goal : String?,
                          @Field("app_version") app_version : String?,
                          @Field("notification_id") notification_id : String?) : Call<ResponseBody>


    @GET("user_weight_list/")
    fun callUserWeight(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @GET("user_wallet_detail/")
    fun callUserWallet(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @POST("user_body_measurement_log_list/")
    @FormUrlEncoded
    fun callUserBodyMeasurement(@Header("Authorization") accesstoken : String?, @Field("body_measurement_type_id") body_measurement_type_id : String?) : Call<ResponseBody>


    @POST("user_exercise_calorie_burn_list/")
    @FormUrlEncoded
    fun callUserExercise(@Header("Authorization") accesstoken : String?, @Field("date") date : String?) : Call<ResponseBody>

    @GET("user_Calorie_Intakes_list/")
    fun callCalorieIntakeList(@Header("Authorization") accesstoken : String?, @Query("date") date : String?) : Call<ResponseBody>

    @POST("user_diet_plan/")
    @FormUrlEncoded
    fun callDietPlanList(@Header("Authorization") accesstoken : String?, @Field("date") date : String?) : Call<ResponseBody>


    @GET("User_Water_Intakes/")
    fun callWaterIntake(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @GET("fitness_exercise_planner_list/")
    fun callFitnessPlanList(@Header("Authorization") accesstoken : String?, @Query("date") date : String?) : Call<ResponseBody>


    @POST("user_profile/")
    fun callGetProfileTask(@Header("Authorization") accesstoken : String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateFitnessLevel(@Header("Authorization") accesstoken: String?, @Field("fitness_level") fitness_level: String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateBio(@Header("Authorization") accesstoken: String?, @Field("bio") bio: String?) : Call<ResponseBody>


    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateBodyMeasurement(@Header("Authorization") accesstoken: String?, @Field("body_fat") body_fat: String?, @Field("muscle_mass") muscle_mass: String?, @Field("arm_right") arm_right: String?, @Field("arm_left") arm_left: String?, @Field("chest") chest: String?, @Field("thigh") thigh: String?, @Field("calf") calf: String?, @Field("hip") hip: String?, @Field("waist") waist: String?) : Call<ResponseBody>

    @POST("add_user_weight/")
    @FormUrlEncoded
    fun callAddWeight(@Header("Authorization") accesstoken: String?, @Field("weight") weight: String?) : Call<ResponseBody>

    @POST("add_water_intake/")
    @FormUrlEncoded
    fun callAddWaterIntake(@Header("Authorization") accesstoken: String?, @Field("water_amount") weight: String?, @Field("date") date : String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateFitnessPurpose(@Header("Authorization") accesstoken: String?, @Field("fitness_purpose") fitness_level: String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateFitnessTarget(@Header("Authorization") accesstoken: String?, @Field("fitness_target") fitness_target: String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateGoal(@Header("Authorization") accesstoken: String?, @Field("goal_id") goal: String?) : Call<ResponseBody>

    @POST("home_data/")
    @FormUrlEncoded
    fun callHomeAPI(@Header("Authorization") accesstoken: String?, @Field("date") date: String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateFoodPreference(@Header("Authorization") accesstoken: String?, @Field("diet_preference") diet_preference: String?, @Field("allergies") allergies : String?, @Field("preference_cuisine") preference_cuisine : String?) : Call<ResponseBody>

    @POST("update_user_profile/")
    @FormUrlEncoded
    fun updateProfile(@Header("Authorization") accesstoken: String?, @Field("first_name") first_name: String?, @Field("last_name") last_name: String?, @Field("age") age: String?, @Field("gender") gender: String?, @Field("current_weight") weight: String?, @Field("initial_logged_height") initial_logged_height: String?, @Field("medical_condition") medical_condition: String?, @Field("occupation") occupation: String?) : Call<ResponseBody>

    @POST("add_target_weight/")
    @FormUrlEncoded
    fun updateTargetWeight(@Header("Authorization") accessToken : String?, @Field("target_weight") target_weight : String?, @Field("target_weight_date") target_weight_date : String?, @Field("target_weight_type") target_weight_type : String) : Call<ResponseBody>


    @POST("add_calorie_burn/")
    @FormUrlEncoded
    fun callAddWorkout(@Header("Authorization") accesstoken: String?, @Field("exercise_id") exercise_id : String, @Field("cb_id") cb_id : String, @Field("time_duration") time_duration : String, @Field("calorie_per_item_burn") calorie_per_item_burn : String, @Field("total_calorie_burn") total_calorie_burn : String, @Field("speed") speed : String, @Field("distance") distance : String, @Field("etype") etype : String, @Field("exercise_name") exercise_name : String) : Call<ResponseBody>

    @POST("add_transaction/")
    @FormUrlEncoded
    fun callAddTransaction(@Header("Authorization") accesstoken: String?,
                           @Field("plan_id") plan_id : String,
                           @Field("payment_amount") payment_amount : String,
                           @Field("plan_start") plan_start : String,
                           @Field("plan_end") plan_end : String,
                           @Field("payment_transaction_id") payment_transaction_id : String,
                           @Field("purchaced_on") purchaced_on : String,
                           @Field("payment_method") payment_method : String) : Call<ResponseBody>


    @POST("add_calorie_intake/")
    @FormUrlEncoded
    fun callAddCalorieIntake(@Header("Authorization") accesstoken : String,
                             @Field("calorie_intake_id") calorie_intake_id : String,
                             @Field("food_id") food_id : String,
                             @Field("quantity") quantity : String,
                             @Field("total_calorie") total_calorie : String,
                             @Field("food_name") food_name : String,
                             @Field("calorie_per_item") calorie_per_item : String,
                             @Field("foodtype") food_type : String,
                             @Field("size") size : String,
                             @Field("date") date : String?) : Call<ResponseBody>

    @POST("privacy_policy_about_us/")
    @FormUrlEncoded
    fun callAboutUsAndPrivacy(@Header("Authorization") accesstoken: String?, @Field("type") type: String?) : Call<ResponseBody>

    @Multipart
    @POST("user_chat_data")
    fun uploadData(@Header("Authorization") accesstoken: String?, @Part("filename") image : MultipartBody.Part ) : Call<ResponseBody>


    @POST("add_transaction/")
    @FormUrlEncoded
    fun callAddTransactionAPI(@Header("Authorization") accesstoken: String?,
                              @Field("subscription_id") subscription_id: String?,
                              @Field("payment_amount") payment_amount: String?,
                              @Field("offer_id") offer_id: Int?,
                              @Field("total_payment") total_payment: String?,
                              @Field("coupon_code") coupon_code: String?,
                              @Field("referral_id") referral_id: String?,
                              @Field("wallet_amount") wallet_amount: Int,
                              @Field("subscription_start") subscription_start: String?,
                              @Field("subscription_end") subscription_end: String?,
                              @Field("payment_transaction_id") payment_transaction_id: String?,
                              @Field("purchaced_on") purchaced_on: String?,
                              @Field("payment_method") payment_method: String?,
                              @Field("discount_amount") discount_amount : String?) : Call<ResponseBody>


    @POST("add_user_body_measurement_log/")
    @FormUrlEncoded
    fun callAddBodyFat(@Header("Authorization") accesstoken: String?, @Field("body_measurement_type_id") body_measurement_type_id: String?,@Field("body_measurement_value") body_measurement_value: String?,@Field("added_on") added_on: String?) : Call<ResponseBody>



}