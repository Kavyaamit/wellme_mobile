package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.squareup.picasso.Picasso
import com.wellme.LeftSideMenuActivity
import com.wellme.PaymentActivity
import com.wellme.R
import com.wellme.databinding.FragmentSubscriptionDetailBinding
import com.wellme.dto.SubscriptionDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class SubscriptionDetailFragment : Fragment(), View.OnClickListener{
    var binding : FragmentSubscriptionDetailBinding? = null
    var regular : Typeface? = null
    var bold : Typeface? = null
    var access_token : String? = null
    var subscription_id : String? = ""
    var s_image : String? = ""
    var bundle : Bundle? = null
    var subscription_list : List<SubscriptionDTO> = ArrayList()
    var s_offer_id : String? = "0"
    var s_coupon_code : String? = ""
    var s_discount : String? = ""
    var s_discount_type : String? = ""
    var s_amount : String? = ""
    var dis1 : Float = 0f
    var reamining_amount : Float = 0f
    var s_end_date : String? = ""
    var s_validity : String? = ""
    var config : PayPalConfiguration = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .acceptCreditCards(false)
        .clientId("AVWFZNP8kbF_Yl7qfGpmbOSjcF9G9GQGwFfDYtsiaeYLE_wZzGsJ3yV_Cf8T9i6XOxhYG3293Bbtichq")
    var PAYPAL_REQUEST_CODE  : Int = 7777
    var s_total_wallet_amount : String? = ""
    var total_wallet_amount : Float = 0f
    override fun onClick(p0: View?) {
        if(p0 == binding!!.llAvailableOffer){
            var fragment : Fragment = OfferListThroughSubcriptionFragment()
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(p0 == binding!!.llApplyCoupon){
            var fragment : Fragment = CouponListThroughSubscriptionFragment()
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(p0 == binding!!.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
        else if(p0 == binding!!.btnBuyNow){
            if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
                var f1 = s_amount!!.toFloat()
                reamining_amount = f1 - dis1
                if(reamining_amount>0){
                    dialogSelectPaymentType(reamining_amount.toString())
                }
                else{
                    if(reamining_amount<0){
                        reamining_amount = 0f
                    }
                    setDiscountAmount()
                }

            }
        }
    }

    fun processPayment(f1 : Double){
        var paypalPayment : PayPalPayment = PayPalPayment(BigDecimal(f1.toString()), "USD", "Purchase Goods", PayPalPayment.PAYMENT_INTENT_SALE)
        var intnet : Intent = Intent(requireContext(), com.paypal.android.sdk.payments.PaymentActivity::class.java)
        intnet.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
        intnet.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT,paypalPayment)
        startActivityForResult(intnet, PAYPAL_REQUEST_CODE)
    }

    fun dialogSelectPaymentType(s1 : String){
        var dialog : Dialog? = Dialog(requireContext())
        var window : Window? = dialog!!.window
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_payment_type)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCanceledOnTouchOutside(true)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var tv_razorpay : TextView = dialog!!.findViewById(R.id.tv_razorpay)
        var tv_paypal : TextView = dialog!!.findViewById(R.id.tv_paypal)
        tv_paypal.setTypeface(regular1)
        tv_razorpay.setTypeface(regular1)
        dialog.show()
        tv_paypal.setOnClickListener (View.OnClickListener{ view ->
            dialog.dismiss()
            //callRateAPI(s1.toDouble())
            processPayment(s1.toDouble()/70)
        })


        tv_razorpay.setOnClickListener(View.OnClickListener {view ->
            dialog.dismiss()
            var intent : Intent = Intent(requireActivity(), PaymentActivity::class.java)
            intent.putExtra("amount", s1)
            startActivityForResult(intent, 1002)
        })

    }

    override fun onResume() {
        super.onResume()
        try{
            s_offer_id = UtilMethod.instance.getString(context!!, "offer_id")
            if(UtilMethod.instance.isStringNullOrNot(s_offer_id)){
                s_offer_id = "0"
            }
            s_coupon_code = UtilMethod.instance.getString(context!!, "coupon_code")
            s_discount = UtilMethod.instance.getString(context!!, "discount")
            s_discount_type = UtilMethod.instance.getString(context!!, "discount_type")
            getDiscount()
        }
        catch(e : java.lang.Exception){

        }

        (activity as LeftSideMenuActivity).disableBottomBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subscription_detail, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView1()

    }
    fun getView1(){
        regular = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        bold = ResourcesCompat.getFont(context!!, R.font.poppins_bold)
        binding!!.tvTitle.setTypeface(bold)
        binding?.header!!.setTypeface(regular)
        binding!!.tvDetails!!.setTypeface(bold)
        binding!!.btnBuyNow.setTypeface(regular)
        binding!!.tvApplyCoupon.setTypeface(regular)
        binding!!.tvAmount.setTypeface(regular)
        binding!!.tvAmountTitle.setTypeface(regular)
        binding!!.tvAvailableOffer.setTypeface(regular)
        binding!!.tvDescription.setTypeface(regular)
        binding!!.tvExpiredOn.setTypeface(regular)
        binding!!.tvExpiredOnTitle.setTypeface(regular)
        binding!!.tvNoOfCoach.setTypeface(regular)
        binding!!.tvNoOfCoachTitle.setTypeface(regular)
        binding!!.tvNoOfDays.setTypeface(regular)
        binding!!.tvNoOfDaysTitle.setTypeface(regular)
        binding!!.tvPlanTypeTitle.setTypeface(regular)
        binding!!.tvPlanType.setTypeface(regular)
        binding!!.cbWallet.setTypeface(regular)
        binding!!.tvDiscount.setTypeface(regular)
        binding!!.tvDiscountAmount.setTypeface(regular)
        binding!!.tvDiscountTitle.setTypeface(regular)
        binding!!.llAvailableOffer.setOnClickListener(this)
        binding!!.llApplyCoupon.setOnClickListener(this)
        binding!!.btnBuyNow.setOnClickListener(this)
        binding!!.back.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(context!!)
        bundle = arguments
        if(bundle!=null){
            subscription_id = bundle!!.getString("subscription_id")
        }
        callSubscriptionDetailAPI()

    }

    fun dialogForCheckNetworkError(){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(context)
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml(""+resources.getString(R.string.network_error)))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(context!!.resources!!.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialog, which -> callSubscriptionDetailAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun setDiscountAmount(){
        var calendar : Calendar = Calendar.getInstance()
        var end_calendar : Calendar = Calendar.getInstance()
        var purchase_calendar : Calendar = Calendar.getInstance()
        var validity: Int =0

        end_calendar.time = calendar.time
        if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
            validity = s_validity!!.toInt()
        }
        end_calendar.add(Calendar.DAY_OF_YEAR, validity)
        var s_start_date : String? = UtilMethod.instance.getDateTimeFormat(calendar.time)
        var s_end_date : String? = UtilMethod.instance.getDateTimeFormat(end_calendar.time)
        var s_purchased_date : String? = UtilMethod.instance.getDateTimeFormat(purchase_calendar.time)
        callAddTransaction(""+purchase_calendar.time, s_start_date!!, s_end_date!!, s_purchased_date!!, "Offer")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                var paymentConfirmation : PaymentConfirmation = data!!.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if(paymentConfirmation!=null){
                    var paymentDetails : String = paymentConfirmation.toJSONObject().toString(4)
                    if(!UtilMethod.instance.isStringNullOrNot(paymentDetails)){
                        var jsonObject : JSONObject = JSONObject(paymentDetails)
                        if(jsonObject!=null){
                            var responseJSON : JSONObject = jsonObject.getJSONObject("response")
                            if(responseJSON!=null){
                                var calendar : Calendar = Calendar.getInstance()
                                var end_calendar : Calendar = Calendar.getInstance()
                                var purchase_calendar : Calendar = Calendar.getInstance()
                                var validity: Int =0
                                var transaction_id : String = responseJSON.getString("id")
                                var state : String = responseJSON.getString("state")
                                if(!UtilMethod.instance.isStringNullOrNot(s_end_date)){
                                    var end_date : Date = UtilMethod.instance.getDate1(s_end_date)



                                    var end_ms : Long = 0
                                    var current_ms : Long = calendar.timeInMillis
                                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                                        validity = s_validity!!.toInt()
                                    }
                                    if(end_date!=null){
                                        end_ms = end_date.time
                                        if(end_ms>current_ms){
                                            calendar.time = end_date
                                            calendar.add(Calendar.SECOND, 1)

                                        }
                                        else{

                                        }

                                    }
                                    end_calendar.time = calendar.time
                                    end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                                    var s_start_date : String? = UtilMethod.instance.getDateTimeFormat(calendar.time)
                                    var s_end_date : String? = UtilMethod.instance.getDateTimeFormat(end_calendar.time)
                                    var s_purchased_date : String? = UtilMethod.instance.getDateTimeFormat(purchase_calendar.time)
                                    callAddTransaction(transaction_id, s_start_date!!, s_end_date!!, s_purchased_date!!, "Paypal")

                                }
                                else{
                                    end_calendar.time = calendar.time
                                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                                        validity = s_validity!!.toInt()
                                    }
                                    end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                                    var s_start_date : String? = UtilMethod.instance.getDateTimeFormat(calendar.time)
                                    var s_end_date : String? = UtilMethod.instance.getDateTimeFormat(end_calendar.time)
                                    var s_purchased_date : String? = UtilMethod.instance.getDateTimeFormat(purchase_calendar.time)
                                    callAddTransaction(transaction_id, s_start_date!!, s_end_date!!, s_purchased_date!!, "Paypal")
                                }

                            }
                        }
                    }
                }
            }
        }
        else if(requestCode == 1002){
            if(resultCode == Activity.RESULT_OK){
                var validity: Int =0
                var purchase_calendar : Calendar = Calendar.getInstance()
                var paymentDetails : String = data!!.getStringExtra("razorpay_id")
                if(!UtilMethod.instance.isStringNullOrNot(paymentDetails) && !UtilMethod.instance.isStringNullOrNot(s_end_date)){
                    var end_date : Date = UtilMethod.instance.getDate1(s_end_date)
                    var calendar : Calendar = Calendar.getInstance()
                    var end_calendar : Calendar = Calendar.getInstance()

                    var end_ms : Long = 0
                    var current_ms : Long = calendar.timeInMillis
                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                        validity = s_validity!!.toInt()
                    }
                    if(end_date!=null){
                        end_ms = end_date.time
                        if(end_ms>current_ms){
                            calendar.time = end_date
                            calendar.add(Calendar.SECOND, 1)

                        }
                        else{

                        }

                    }
                    end_calendar.time = calendar.time
                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                        validity = s_validity!!.toInt()
                    }
                    end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                    var s_start_date : String? = UtilMethod.instance.getDateTimeFormat(calendar.time)
                    var s_end_date : String? = UtilMethod.instance.getDateTimeFormat(end_calendar.time)
                    var s_purchased_date : String? = UtilMethod.instance.getDateTimeFormat(purchase_calendar.time)
                    Log.v("Call Appi", "yes")
                    callAddTransaction(paymentDetails, s_start_date!!, s_end_date!!, s_purchased_date!!, "Razorpay")

                }
                else if(!UtilMethod.instance.isStringNullOrNot(paymentDetails)){
                    var calendar : Calendar = Calendar.getInstance()
                    var end_calendar : Calendar = Calendar.getInstance()
                    end_calendar.time = calendar.time
                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                        validity = s_validity!!.toInt()
                    }
                    end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                    var s_start_date : String? = UtilMethod.instance.getDateTimeFormat(calendar.time)
                    var s_end_date : String? = UtilMethod.instance.getDateTimeFormat(end_calendar.time)
                    var s_purchased_date : String? = UtilMethod.instance.getDateTimeFormat(purchase_calendar.time)
                    Log.v("Call Appi", "yes")
                    callAddTransaction(paymentDetails, s_start_date!!, s_end_date!!, s_purchased_date!!, "Razorpay")
                }
            }
        }
    }

    fun callAddTransaction(trans_id: String, plan_start_date : String, plan_end_date : String, purchased_on : String, type : String){
        Log.v("Start Date", "==>"+s_offer_id)
        Log.v("Start Date", "==>"+plan_end_date)
        Log.v("Start Date", "==>"+purchased_on)
        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callAddTransactionAPI("Bearer "+access_token,
            ""+subscription_id,
            ""+reamining_amount,
            s_offer_id!!.toInt(),
            ""+s_amount,
            ""+s_coupon_code,
            "",
            0,
            plan_start_date,
            plan_end_date+"",
            ""+trans_id,
            purchased_on+"",
            type,
            dis1.toString()
        ).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.v("REquest ", "==> "+response.raw().body())
                Log.v("Response Code ", " ==> "+code+" "+response.raw())
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("REsponse == > ", "=="+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            requireActivity().onBackPressed()
                        }
                        else{

                        }

                    }
                }

            }
        })
    }

    fun callWalletDetailAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callUserWallet("Bearer "+access_token).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var response1 : String = response.body()!!.string()
                Log.v("Wallet Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        s_total_wallet_amount = object1.getString("total_wallet_amount")
                        if(!UtilMethod.instance.isStringNullOrNot(s_total_wallet_amount)){
                            total_wallet_amount = s_total_wallet_amount!!.toFloat()
                            if(total_wallet_amount>0){
                                binding!!.cbWallet.visibility = View.VISIBLE
                            }
                            else{
                                binding!!.cbWallet.visibility = View.GONE
                            }
                        }

//                        var gson : Gson = Gson()
//                        subscription_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<SubscriptionDTO>::class.java).toList()
////
//                        if(subscription_list!=null){
//                            getData(subscription_list.get(0))
//                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun callSubscriptionDetailAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getSubscriptionDetail("Bearer "+access_token, subscription_id).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var response1 : String = response.body()!!.string()
                Log.v("Subscription Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        subscription_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<SubscriptionDTO>::class.java).toList()
//
                        if(subscription_list!=null){
                                if(subscription_list.size > 0) {
                                    getData(subscription_list.get(0))
                                }
                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun getDiscount(){
        if(!UtilMethod.instance.isStringNullOrNot(s_amount) && !UtilMethod.instance.isStringNullOrNot(s_discount_type)){
            var f_amount : Float = s_amount!!.toFloat()
            var dis : Float = s_discount!!.toFloat()
            dis1 = 0f
            if(!UtilMethod.instance.isStringNullOrNot(s_discount_type)){
                if(s_discount_type.equals("0")){
                    var d1 = dis * f_amount/100
                    dis1 = d1
                    binding!!.tvDiscount.setText("-"+dis+"% Off")
                }
                else{
                    Log.v("Hello ", "==> "+dis)
                    dis1 = dis
                    binding!!.tvDiscount.setText("-"+context!!.resources!!.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(dis)+" Off")
                }
            }
            else{
                dis1 = dis
            }
            binding!!.tvDiscountAmount.setText(context!!.resources!!.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(dis1))


            binding!!.cvDiscountLayout.visibility = View.VISIBLE
            binding!!.llOffer.visibility = View.GONE
        }
        else{
            binding!!.cvDiscountLayout.visibility = View.GONE
            binding!!.llOffer.visibility = View.VISIBLE
        }
    }



    fun getData(dto : SubscriptionDTO){
        if(dto!=null){
            s_image = dto!!.image
            s_validity = dto!!.no_of_days
            binding!!.tvPlanType.setText(""+dto.type_of_plan)
            binding!!.tvTitle.setText(""+dto.title)
            binding!!.tvDescription.setText(""+dto.description)
            binding!!.tvNoOfDays.setText(""+dto.no_of_days)
            binding!!.tvNoOfCoach.setText(""+dto.no_of_coaches)
            binding!!.tvExpiredOn.setText(""+UtilMethod.instance.getDateWithoutSecond(dto.expire_date))
            s_amount = dto!!.amount
            if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
                binding!!.tvAmount.setText(context!!.resources!!.getString(R.string.rs_symbol)+""+UtilMethod.instance.getFormatedAmountString(s_amount!!.toFloat()))
                getDiscount()
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+s_image).placeholder(R.drawable.logo).into(binding!!.ivSubscription)
            }
            callWalletDetailAPI()

        }
    }
}