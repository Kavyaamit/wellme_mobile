package com.wellme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.wellme.dto.UserDTO
import com.wellme.utils.UtilMethod
import org.json.JSONObject

class PaymentActivity : Activity(), PaymentResultListener{
    var bundle : Bundle? = null
    var amount : Float = 0f
    var s_amount : String? = ""
    var userDTO : UserDTO? = null
    var s_email : String? = "na@gmail.com"
    var s_phone_number : String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        bundle = intent.extras
        userDTO = UtilMethod.instance.getUser(this)
        if(userDTO!=null){
            s_phone_number = userDTO!!.phone

        }
        if(bundle!=null){
            s_amount = bundle!!.getString("amount")
            if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
                amount = s_amount!!.toFloat()
            }

        }
        startPayment()
    }

    fun startPayment(){
        var co : Checkout = Checkout()
        co.setKeyID("rzp_test_wBYztKKebJbiIM")

        var object1 : JSONObject = JSONObject()
        object1.put("name", resources.getString(R.string.app_name))
        object1.put("description", "Fitness Subscription")
        object1.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png")
        object1.put("currency", "INR")
        amount *=100
        object1.put("amount", ""+amount)

        var profile_obj : JSONObject = JSONObject()
        profile_obj.put("email", s_email)
        profile_obj.put("contact", s_phone_number)

        object1.put("prefill", profile_obj)
        co.open(this, object1)


    }

    override fun onPaymentError(p0: Int, p1: String?) {
        var intent : Intent = Intent()
        intent.putExtra("razorpay_id", "")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.v("RazorPAy ID ", "==> "+p0)
        var intent : Intent = Intent()
        intent.putExtra("razorpay_id", p0)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}